package com.example.kotlin_audioanalyzer

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.example.kotlin_audioanalyzer.utils.currentRun
import com.example.kotlin_audioanalyzer.utils.etalonRun

class VoiceRecord(val fs:Int) {

    private var audioRecord: AudioRecord? = null
    public var recordLength = 0
    private var thread: Thread? = null
    var etalonList=ArrayList<ShortArray>()

    fun prepare(multiple: Int) {
        var BYTES_PER_SHORT = 2
        // Setup buffer size
        recordLength = AudioRecord.getMinBufferSize(fs, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) / BYTES_PER_SHORT

        // Increase buffer size so that it is a multiple of the param
        val r = recordLength % multiple
        if (r > 0) recordLength += (multiple - r)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            fs,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            recordLength * BYTES_PER_SHORT
        )
    }

    fun start(function:()->Unit) {
            audioRecord?.startRecording()
            thread = Thread {
                while (etalonRun) {
                    val recordBuffer = ShortArray(recordLength)
                    audioRecord!!.read(recordBuffer, 0, recordLength)
                    etalonList.add(recordBuffer)
                    function()
                }
            }
            thread!!.start()
        }

    fun startTime(function:(recordBuffer:ShortArray)->Unit) {
        audioRecord?.startRecording()
        thread = Thread {
            while (currentRun) {
                val recordBuffer = ShortArray(recordLength)
                audioRecord!!.read(recordBuffer, 0, recordLength)
                etalonList.add(recordBuffer)
                function(recordBuffer)
            }
        }
        thread!!.start()
    }

    /**
     * Stop recording
     * Notifies the thread to stop and wait until it stops
     * Also stops the recording service
     */
    fun stop() {
            while (thread!!.isAlive) try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            audioRecord!!.stop()
            //Log.d("ContinuousRecord","Service stopped");
        }
    /**
     * Destroys the recording service
     * @method start and @method stop should then not be called
     */
    fun release() {
        if (!etalonRun && audioRecord != null) audioRecord!!.release()
    }


}