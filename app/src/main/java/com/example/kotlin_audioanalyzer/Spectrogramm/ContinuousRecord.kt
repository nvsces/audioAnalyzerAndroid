package com.example.kotlin_audioanalyzer.Spectrogramm

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import android.util.Log
import kotlin.reflect.KFunction1

class ContinuousRecord
/**
 * Constructor
 */ internal constructor(private val samplingRate: Int) {
    // Attributes
    private var audioRecord: AudioRecord? = null
    public var recordLength = 0
    private var thread: Thread? = null
    private var run = false

    /**
     * Initiate the recording service
     * The service is then ready to start recording
     * The buffer size can be forced to be multiple of @param multiple (size in sample count)
     * @param multiple is ineffective if set to 1
     */
    fun prepare(multiple: Int) {
        var BYTES_PER_SHORT = 2
        // Setup buffer size
        recordLength = AudioRecord.getMinBufferSize(
            samplingRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ) / BYTES_PER_SHORT

        // Increase buffer size so that it is a multiple of the param
        val r = recordLength % multiple
        if (r > 0) recordLength += (multiple - r)

        // Log value
        //Log.d("ContinuousRecord","Buffer size = "+recordLength+" samples");

        // Init audio recording from MIC
        audioRecord = AudioRecord(
            AudioSource.MIC,
            samplingRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            recordLength * BYTES_PER_SHORT
        )
    }

    /**
     * Start recording in a independent thread
     * @param listener is call every time a sample is ready
     */

    fun start(function:(recordBuffer:ShortArray)->Unit) {
        if (!run && audioRecord != null) {
            run = true
            audioRecord!!.startRecording()
            val recordBuffer = ShortArray(recordLength)
            thread = Thread {
                while (run) {
                    audioRecord!!.read(recordBuffer, 0, recordLength)
                    function(recordBuffer)
                }
            }
            thread!!.start()
        }
    }

    /**
     * Stop recording
     * Notifies the thread to stop and wait until it stops
     * Also stops the recording service
     */
    fun stop() {
        if (run && audioRecord != null) {
            //Log.d("ContinuousRecord","Stopping service...");
            run = false
            while (thread!!.isAlive) try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            audioRecord!!.stop()
            //Log.d("ContinuousRecord","Service stopped");
        }
    }

    /**
     * Destroys the recording service
     * @method start and @method stop should then not be called
     */
    fun release() {
        if (!run && audioRecord != null) audioRecord!!.release()
    }
}