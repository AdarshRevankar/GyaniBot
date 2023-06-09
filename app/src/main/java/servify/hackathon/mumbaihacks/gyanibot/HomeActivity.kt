package servify.hackathon.mumbaihacks.gyanibot

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import servify.hackathon.mumbaihacks.gyanibot.common.REQUEST_SPEECH_PERMISSION
import servify.hackathon.mumbaihacks.gyanibot.common.SPEECH_PERMISSIONS
import servify.hackathon.mumbaihacks.gyanibot.customView.ClickListener
import servify.hackathon.mumbaihacks.gyanibot.customView.SubjectRecyclerViewAdapter
import servify.hackathon.mumbaihacks.gyanibot.databinding.ActivityHomeBinding
import servify.hackathon.mumbaihacks.gyanibot.utils.PermissionUtils
import servify.hackathon.mumbaihacks.gyanibot.utils.setStatusBarColor
import java.util.Locale


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var speechRecognizer: SpeechRecognizer? = null
    private var speechRecognizerIntent: Intent? = null

    companion object {
        private val SUBJECTS_MAP = mapOf(
            "Social Science" to R.drawable.nature,
            "Science" to R.drawable.science,
            "Computers" to R.drawable.computer,
            "Finance" to R.drawable.save
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setUpSpeechRecognizer()
        initView()
    }

    private fun changeSpeechListenState(shouldListen: Boolean) {
        if (shouldListen) {
            speechRecognizer?.startListening(speechRecognizerIntent)
        } else {
            speechRecognizer?.stopListening()
        }
    }

    private fun updateListenButtonIcon(shouldListen: Boolean) {
        Glide.with(this@HomeActivity)
            .load(if (shouldListen) R.drawable.start_listen else R.drawable.stop_listen)
            .into(binding.btnSpeak)
    }

    private fun setUpSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
        }
        speechRecognizer?.setRecognitionListener(recognitionListener)
    }

    private fun checkAndAskPermissions() {
        if (!PermissionUtils.hasPermission(this, SPEECH_PERMISSIONS)) {
            ActivityCompat.requestPermissions(
                this,
                SPEECH_PERMISSIONS.toTypedArray(),
                REQUEST_SPEECH_PERMISSION
            )
        }
    }

    private fun initView() {
        setStatusBarColor(ContextCompat.getColor(this, R.color.shadow_colour_dark), this)
        binding.rvSubjects.adapter = SubjectRecyclerViewAdapter(this, SUBJECTS_MAP.toList()).apply {
            this.setClickListener(object: ClickListener {
                override fun onClickListener(item: String) {
                    checkAndProceedNext(item)
                }
            })
        }
        binding.rvSubjects.layoutManager = GridLayoutManager(this, if (getDisplayMetrics().widthPixels >= 1000) 2 else 1, GridLayoutManager.VERTICAL, false)
        binding.btnSpeak.setOnClickListener {
            if (recognitionListener.isListening) {
                changeSpeechListenState(false)
            } else {
                if (!PermissionUtils.hasPermission(this, SPEECH_PERMISSIONS)) {
                    checkAndAskPermissions()
                } else {
                    changeSpeechListenState(true)
                }
            }
        }
    }

    private fun getDisplayMetrics(): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_SPEECH_PERMISSION -> checkAndAskPermissions()
                else -> {}
            }
        }
    }

    private fun checkAndProceedNext(selectedSubject: String?) {
        SUBJECTS_MAP.forEach { (subject, _) ->
            if (subject.lowercase() == selectedSubject?.lowercase()) {
                startActivity(TopicSelectionActivity.createInstance(this, subject, SUBJECTS_MAP[subject]))
                overridePendingTransition(R.anim.enter_from_right, R.anim.stay)
                return@forEach
            }
        }
    }

    private val recognitionListener = object: RecognitionListener {
        var isListening = false

        override fun onReadyForSpeech(bundle: Bundle?) {
            updateListenButtonIcon(false)
        }

        override fun onBeginningOfSpeech() {
            isListening = true
        }

        override fun onRmsChanged(v: Float) {

        }

        override fun onBufferReceived(bytes: ByteArray?) {

        }

        override fun onEndOfSpeech() {
            isListening = false
            updateListenButtonIcon(true)
        }

        override fun onError(i: Int) {
            runOnUiThread {
                isListening = false
                updateListenButtonIcon(true)
                Toast.makeText(this@HomeActivity, "Couldn't hear you !...", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onResults(bundle: Bundle?) {
            val data = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            data?.let {
                checkAndProceedNext(it.joinToString(" "))
            }?: {
                runOnUiThread { Toast.makeText(this@HomeActivity, "Couldn't hear you !...", Toast.LENGTH_SHORT).show() }
            }
        }

        override fun onPartialResults(bundle: Bundle?) {

        }

        override fun onEvent(i: Int, bundle: Bundle?) {

        }

    }
}