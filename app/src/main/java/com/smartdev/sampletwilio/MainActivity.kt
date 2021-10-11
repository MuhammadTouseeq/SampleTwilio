package com.smartdev.sampletwilio

import android.Manifest
import android.content.Context
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.TextView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.koushikdutta.async.future.FutureCallback
import com.koushikdutta.ion.Ion
import com.smartdev.sampletwilio.databinding.ActivityMainBinding
import com.twilio.voice.Call
import com.twilio.voice.CallException
import com.twilio.voice.Voice
import java.util.HashMap

class MainActivity : AppCompatActivity() {


    val audioManager: AudioManager?=null
lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        soundPoolManager = SoundPoolManager.getInstance(this)
        /*
         * Needed for setting/abandoning audio focus during a call
         */
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // audioManager.setSpeakerphoneOn(true)

        binding.btnSpeaker?.setOnClickListener {

            if(!audioManager.isSpeakerphoneOn) {
                binding.btnSpeaker.setColorFilter(getResources().getColor(R.color.purple_200));
                audioManager.setSpeakerphoneOn(true)
            }
            else{
                binding.btnSpeaker.setColorFilter(getResources().getColor(R.color.purple_700));
                audioManager.setSpeakerphoneOn(false)
            }


        }
        /*
         * Enable changing the volume using the up/down keys during a conversation
         */

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */

        volumeControlStream = AudioManager.STREAM_VOICE_CALL

        binding?.chronometer?.hide()

        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    getTwilloAccessToken()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

                }

            })

            // .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.RECORD_AUDIO)
            .check();

        binding.btnEndCall.setOnClickListener {
            try {
                twilloCall?.disconnect()
                binding.chronometer.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finish()
        }
        binding.EdtDigits.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            hideKeyboard(binding.EdtDigits)
            toast(binding.EdtDigits.text.toString())
            if(!binding.EdtDigits.text.toString().isNullOrEmpty()) {
                twilloCall?.sendDigits(binding.EdtDigits.text.toString())
            }
            binding.EdtDigits.clearFocus()
            true
        })

    }
    private fun configureAudio(enable: Boolean) {
        if (enable) {
            audioManager?.requestAudioFocus(
                null, AudioManager.STREAM_VOICE_CALL,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )
            audioManager?.setMode(AudioManager.MODE_IN_COMMUNICATION)
            audioManager?.setSpeakerphoneOn(true)
        } else {
            audioManager?.setMode(AudioManager.MODE_NORMAL)
            audioManager?.abandonAudioFocus(null)
            audioManager?.setSpeakerphoneOn(false)
        }
    }

    fun getTwilloAccessToken() {
        Ion.with(this)
            .load(APIConstant.TWILIO_ACCESS_TOKEN_SERVER_URL + "?identity=app")
            .asString().setCallback(FutureCallback<String> { e, accessToken ->
                if (accessToken != null) {

//                 placeCall(accessToken, "+922134915006")  //8012
                    placeCall(accessToken, "+923208220496")  //8012
//                    placeCall(accessToken, model?.phone)
                } else {

                   // showErrorMessage("Error retrieving access token. Unable to make calls")
                }
            })
    }

    lateinit var soundPoolManager: SoundPoolManager
    lateinit var twilloCall: Call
    fun placeCall(token: String, number: String) {

        soundPoolManager?.playRinging()
        val twiMLParams = HashMap<String, String>()
        twiMLParams.put("to", number)
//        twiMLParams.put("to", "+15005550006")
        twilloCall = Voice.call(this, token, twiMLParams, object : Call.Listener {
            override fun onConnectFailure(p0: Call?, p1: CallException?) {
                //toast("Failed to make call")
                soundPoolManager?.release()
                binding.chronometer?.stop()
                binding.callState?.text = "Failed to connect"
                binding.EdtDigits?.hide()
            }

            override fun onConnected(p0: Call?) {
                // toast("call connected")
                soundPoolManager?.stopRinging()
                binding.chronometer?.show()
                binding.chronometer.base = SystemClock.elapsedRealtime()
                binding.chronometer?.start()

                binding.callState?.text = "Connected"
                binding.EdtDigits?.show()
            }

            override fun onDisconnected(p0: Call?, p1: CallException?) {

                soundPoolManager?.release()
                binding.callState?.text = "Disconnected"
                binding.chronometer?.stop()
                binding.EdtDigits?.hide()
            }

        })


    }
}