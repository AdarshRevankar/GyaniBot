package servify.hackathon.mumbaihacks.gyanibot

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import servify.hackathon.mumbaihacks.gyanibot.common.REQUEST_SPEECH_PERMISSION
import servify.hackathon.mumbaihacks.gyanibot.common.SELECTED_SUBJECT
import servify.hackathon.mumbaihacks.gyanibot.common.SELECTED_WALLPAPER
import servify.hackathon.mumbaihacks.gyanibot.common.SPEECH_PERMISSIONS
import servify.hackathon.mumbaihacks.gyanibot.customView.ClickListener
import servify.hackathon.mumbaihacks.gyanibot.customView.TopicsRecyclerViewAdapter
import servify.hackathon.mumbaihacks.gyanibot.databinding.ActivityTopicSelectionActiviyBinding
import servify.hackathon.mumbaihacks.gyanibot.utils.PermissionUtils
import servify.hackathon.mumbaihacks.gyanibot.utils.setStatusBarColor
import java.util.Arrays
import java.util.Locale

class TopicSelectionActivity : AppCompatActivity(), TopicSelectionContract.View {
    private lateinit var binding: ActivityTopicSelectionActiviyBinding
    private var speechRecognizer: SpeechRecognizer? = null
    private var selectedSubject: String? = null
    private var speechRecognizerIntent: Intent? = null
    private lateinit var presenter: TopicSelectionPresenter
    private lateinit var adapter: TopicsRecyclerViewAdapter
    private var items = ArrayList<String>()
    private var topics: Array<String>? = null
    private var selectedWallpaper: Int? = null

    companion object {
        fun createInstance(context: Context, subject: String, resourceId: Int?): Intent {
            return Intent(context, TopicSelectionActivity::class.java).apply {
                putExtra(SELECTED_SUBJECT, subject)
                putExtra(SELECTED_WALLPAPER, resourceId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_topic_selection_activiy)
        presenter = TopicSelectionPresenter(this)
        getIntentData()
        setUpSpeechRecognizer()
        fetchGPTQueryList()
        initView()
    }

    private fun fetchGPTQueryList() {
        showLoader()
        selectedSubject?.let { presenter.fetchTopicsForSubject(it) }
    }

    override fun showLoader() {
        binding.loader.visibility = View.VISIBLE
        binding.loaderBackground.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        binding.loader.visibility = View.GONE
        binding.loaderBackground.visibility = View.GONE
    }

    override fun showErrorToast(message: String?) {
        binding.tvError.visibility = View.VISIBLE
        binding.rlError.visibility = View.VISIBLE
        binding.clContainer.visibility = View.VISIBLE
        binding.tvError.text = message?: "Something went wrong"
        Handler(Looper.getMainLooper()).postDelayed({
            binding.tvError.visibility = View.GONE
        }, 2000)
    }

    override fun getContext(): Context? {
        return this
    }

    override fun onGPTResponseReceived(response: String?) {
        val lines = processResponseText(response)
        if (lines != null) {
            topics = lines
            adapter.addAll(lines.toCollection(ArrayList()))
            binding.btnSpeak.visibility = View.VISIBLE
            binding.tvTitle.visibility = View.VISIBLE
            binding.tvDescription.visibility = View.VISIBLE
            binding.ivTopic.visibility = View.VISIBLE
        } else {
            showErrorToast(null)
        }
    }

    private fun processResponseText(responseText: String?): Array<String>? {
        try {
            return responseText?.split("\n")
                ?.filter { it.isNotEmpty() && it.isNotBlank() }
                ?.map { it.replace("^\\d{0,2}\\. ".toRegex(), "") }
                ?.toTypedArray()
        } catch (e: Exception) {
            Logger.d("Error while parsing the text : $e")
        }
        return null
    }

    private fun getIntentData() {
        selectedSubject = intent.getStringExtra(SELECTED_SUBJECT)
        selectedWallpaper = intent.getIntExtra(SELECTED_WALLPAPER, 0)
    }

    private fun initView() {
        setStatusBarColor(ContextCompat.getColor(this, R.color.light_gray), this)
        binding.tvTitle.text = selectedSubject
        selectedWallpaper?.let {
            binding.ivTopic.setImageDrawable(AppCompatResources.getDrawable(this, it))
        }
        adapter = TopicsRecyclerViewAdapter(items).apply {
            this.setClickListener(object: ClickListener {
                override fun onClickListener(item: String) {
                    checkAndProceedNext(item)
                }
            })
        }
        binding.rvTopics.visibility = View.VISIBLE
        binding.rvTopics.adapter = adapter
        binding.rvTopics.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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

    private fun checkAndProceedNext(item: String) {
        if (topics?.contains(item) == true) {
            selectedSubject?.let { subject ->
                startActivity(DetailsActivity.createInstance(this, subject, item))
                overridePendingTransition(R.anim.enter_from_right, R.anim.stay)
            }
        }
    }

    private fun changeSpeechListenState(shouldListen: Boolean) {
        if (shouldListen) {
            speechRecognizer?.startListening(speechRecognizerIntent)
        } else {
            speechRecognizer?.stopListening()
        }
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

    private fun updateListenButtonIcon(shouldListen: Boolean) {
        Glide.with(this@TopicSelectionActivity)
            .load(if (shouldListen) R.drawable.start_listen else R.drawable.stop_listen)
            .into(binding.btnSpeak)
    }

    private val recognitionListener = object : RecognitionListener {
        public var isListening = false

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
                Toast.makeText(this@TopicSelectionActivity, "Couldn't hear you !...", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onResults(bundle: Bundle?) {
            val data = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            data?.let {
                checkAndProceedNext(it.joinToString(" "))
            } ?: {
                Toast.makeText(this@TopicSelectionActivity, "Couldn't hear you !...", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onPartialResults(bundle: Bundle?) {

        }

        override fun onEvent(i: Int, bundle: Bundle?) {

        }

    }
}