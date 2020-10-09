package com.example.kotlin_audioanalyzer.Spectrogramm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kotlin_audioanalyzer.R
import net.galmiza.android.engine.sound.SoundEngine
import java.text.DecimalFormat
import java.util.*


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class SpectrogramActivity : AppCompatActivity() {
    // Attributes
    private var actionBar: ActionBar? = null
//    private var frequencyView: FrequencyView? = null
    private lateinit var timeView: TimeView
    private var recorder: ContinuousRecord? = null
    private lateinit var nativeLib: SoundEngine
    private var menu: Menu? = null
    private val samplingRate = 44100
    private var fftResolution = 0

    // Buffers
    private lateinit var bufferStack // Store trunks of buffers
            : MutableList<ShortArray>
    private lateinit var fftBuffer // buffer supporting the fft process
            : ShortArray
    private lateinit var re // buffer holding real part during fft process
            : FloatArray
    private lateinit var im // buffer holding imaginary part during fft process
            : FloatArray

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Share core
        Misc.setAttribute("activity", this)

        // Load preferences
        loadPreferences()

        // JNI interface
        this.nativeLib = SoundEngine()
        this.nativeLib.initFSin()

        // Recorder & player
        recorder = ContinuousRecord(samplingRate)

        // Create view for frequency display
        this.setContentView(R.layout.main)
        //frequencyView = findViewById(R.id.frequency_view)
        timeView = findViewById(R.id.time_view)
       // if (Misc.getPreference(this, "keep_screen_on", false)) frequencyView.setKeepScreenOn(true)
       // frequencyView.setFFTResolution(fftResolution)
        timeView.setFFTResolution(fftResolution)
        //frequencyView.setSamplingRate(samplingRate)

        // Color mode
        val nightMode: Boolean = Misc.getPreference(this, "night_mode", true)
        if (!nightMode) {
          //  frequencyView.setBackgroundColor(Color.WHITE)
            timeView.setBackgroundColor(Color.WHITE)
        } else {
           // frequencyView.setBackgroundColor(Color.BLACK)
            timeView.setBackgroundColor(Color.BLACK)
        }

        /*// Prepare screen
        getSupportActionBar().hide();
        if (util.Misc.getPreference(this, "hide_status_bar", false))
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        // Action bar
        actionBar = getSupportActionBar()
        actionBar?.setTitle(getString(R.string.app_name))
        actionBar?.setSubtitle(getString(R.string.app_subtitle))

        // Request record audio permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) === PackageManager.PERMISSION_GRANTED
        ) {
            loadEngine()
            //updateHeaders()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                MY_PERMISSIONS_REQUEST_RECORD_AUDIO
            )
        }
    }
    /**
     * Control recording service
     */
    fun startRecording() {
        recorder!!.start(object:ContinuousRecord.OnBufferReadyListener{
            override fun onBufferReady(buffer: ShortArray) {
                val n = fftResolution
                Log.d("TESTstart",buffer[0].toFloat().toString())
                // Trunks are consecutive n/2 length samples
                for (i in 0 until  bufferStack.size-1)
                    System.arraycopy(buffer, n / 2 * i, bufferStack[i + 1], 0, n / 2)

                // Build n length buffers for processing
                // Are build from consecutive trunks
                for (i in 0 until  bufferStack.size-1 ) {
                    System.arraycopy(bufferStack[i], 0, fftBuffer, 0, n / 2)
                    System.arraycopy(bufferStack[i + 1], 0, fftBuffer, n / 2, n / 2)
                    process()
                }

                // Last item has not yet fully be used (only its first half)
                // Move it to first position in arraylist so that its last half is used
                val first = bufferStack[0]
                val last = bufferStack[bufferStack.size - 1]
                System.arraycopy(last, 0, first, 0, n / 2)
            }

        })
    }

    fun stopRecording() {
        recorder!!.stop()
    }

    /**
     * Handles response to permission request
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_RECORD_AUDIO -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadEngine()
                   // updateHeaders()
                }
                return
            }
        }
    }
  override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        //if (resultCode == Activity.RESULT_OK) {
        if (requestCode == INTENT_SETTINGS) {

            // Stop and release recorder if running
            recorder!!.stop()
            recorder!!.release()

            // Update preferences
            loadPreferences()

            // Notify view
           // frequencyView.setFFTResolution(fftResolution)
            timeView.setFFTResolution(fftResolution)
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) === PackageManager.PERMISSION_GRANTED
            ) {
                loadEngine()
               // updateHeaders()
            }

            // Update color mode
            val nightMode: Boolean = Misc.getPreference(this, "night_mode", false)
            if (!nightMode) {
              //  frequencyView.setBackgroundColor(Color.WHITE)
                timeView.setBackgroundColor(Color.WHITE)
            } else {
                //frequencyView.setBackgroundColor(Color.BLACK)
                timeView.setBackgroundColor(Color.BLACK)
            }
        }
        //}
    }

 override fun onDestroy() {
        super.onDestroy()

        // Stop input streaming
        recorder!!.stop()
        recorder!!.release()
    }

    private fun loadPreferences() {
        fftResolution = Integer.parseInt(Misc.getPreference(this, "fft_resolution", getString(R.string.preferences_fft_resolution_default_value)));
    }

    /**
     * Initiates the recording service
     * Creates objects to handle recording and FFT processing
     */
    private fun loadEngine() {

        // Stop and release recorder if running
        recorder!!.stop()
        recorder!!.release()

        // Prepare recorder
        recorder!!.prepare(fftResolution) // Record buffer size if forced to be a multiple of the fft resolution

        // Build buffers for runtime
        val n = fftResolution
        fftBuffer = ShortArray(n)
        re = FloatArray(n)
        im = FloatArray(n)
        bufferStack = ArrayList()
        val l: Int = recorder!!.recordLength / (n / 2)
        for (i in 0 until l + 1)  //+1 because the last one has to be used again and sent to first position
            (bufferStack as ArrayList<ShortArray>).add(ShortArray(n / 2)) // preallocate to avoid new within processing loop

        // Start recording
        startRecording()
    }

    /**
     * Called every time the microphone record a sample
     * Divide into smaller buffers (of size=resolution) which are overlapped by 50%
     * Send these buffers for FFT processing (call to process())
     */
    /**
     * Processes the sound waves
     * Computes FFT
     * Update views
     */
    private fun process() {
        val n = fftResolution
        Log.d("re",fftBuffer[0].toString())
        val log2_n = (Math.log(n.toDouble()) / Math.log(2.0)).toInt()
        nativeLib.shortToFloat(fftBuffer, re, n)
        nativeLib.clearFloat(im, n) // Clear imaginary part
        //Log.d("re",re.toString())
        timeView.setWave(re)

        // Windowing to reduce spectrum leakage
        val window: String? = Misc.getPreference(
            this,
            "window_type",
            getString(R.string.preferences_window_type_default_value)
        )
        if (window == "Rectangular") nativeLib.windowRectangular(
            re,
            n
        ) else if (window == "Triangular") nativeLib.windowTriangular(
            re,
            n
        ) else if (window == "Welch") nativeLib.windowWelch(
            re,
            n
        ) else if (window == "Hanning") nativeLib.windowHanning(
            re,
            n
        ) else if (window == "Hamming") nativeLib.windowHamming(
            re,
            n
        ) else if (window == "Blackman") nativeLib.windowBlackman(
            re,
            n
        ) else if (window == "Nuttall") nativeLib.windowNuttall(
            re,
            n
        ) else if (window == "Blackman-Nuttall") nativeLib.windowBlackmanNuttall(
            re,
            n
        ) else if (window == "Blackman-Harris") nativeLib.windowBlackmanHarris(re, n)
        nativeLib.fft(re, im, log2_n, 0) // Move into frquency domain
        nativeLib.toPolar(re, im, n) // Move to polar base
        //frequencyView.setMagnitudes(re)
        runOnUiThread {
            //frequencyView.invalidate()
            timeView.invalidate()
        }
    }

    /**
     * Switch visibility of the views as user click on view headers
     */
    fun onTimeViewHeaderClick(view: View?) {
        System.out.println(timeView.visibility)
        if (timeView.visibility === View.GONE) timeView.visibility =
            View.VISIBLE else timeView.visibility =
            View.GONE
    }

    companion object {
        // Constant
        const val PI = Math.PI.toFloat()
        const val INTENT_SETTINGS = 0
        const val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0
    }
}
