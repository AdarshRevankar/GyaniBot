package servify.hackathon.mumbaihacks.gyanibot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.SpeechRecognizer
import android.speech.tts.UtteranceProgressListener
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import servify.hackathon.mumbaihacks.gyanibot.common.REQUEST_SPEECH_PERMISSION
import servify.hackathon.mumbaihacks.gyanibot.common.SELECTED_SUBJECT
import servify.hackathon.mumbaihacks.gyanibot.common.SELECTED_TOPIC
import servify.hackathon.mumbaihacks.gyanibot.common.SPEECH_PERMISSIONS
import servify.hackathon.mumbaihacks.gyanibot.customView.DetailsPhraseRecyclerViewAdapter
import servify.hackathon.mumbaihacks.gyanibot.databinding.ActivityDetailsActiviyBinding
import servify.hackathon.mumbaihacks.gyanibot.model.ImageResponse
import servify.hackathon.mumbaihacks.gyanibot.model.Speak
import servify.hackathon.mumbaihacks.gyanibot.utils.PermissionUtils
import servify.hackathon.mumbaihacks.gyanibot.utils.setStatusBarColor

class DetailsActivity : AppCompatActivity(), DetailsContract.View {
    private lateinit var lines: List<String>
    private var utteranceListener: UtteranceProgressListener? = null
    private var isCharacterSpeaking: Boolean = false
    private lateinit var binding: ActivityDetailsActiviyBinding

    private var selectedSubject: String? = null
    private var selectedTopic: String? = null

    private lateinit var presenter: DetailsPresenter
    private lateinit var adapter: DetailsPhraseRecyclerViewAdapter
    private var items = ArrayList<String>()

    companion object {
        fun createInstance(context: Context, subject: String, topic: String): Intent {
            return Intent(context, DetailsActivity::class.java).apply {
                putExtra(SELECTED_SUBJECT, subject)
                putExtra(SELECTED_TOPIC, topic)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details_activiy)
        presenter = DetailsPresenter(this)
        getIntentData()
        Speak(this.applicationContext)
        fetchGPTQueryList()
        initView()
    }

    private fun fetchGPTQueryList() {
        showLoader()
        selectedTopic?.let { presenter.fetchTopicDetails(it) }
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
        binding.tvError.text = message ?: "Something went wrong"
        Handler(Looper.getMainLooper()).postDelayed({
            binding.tvError.visibility = View.GONE
        }, 2000)
    }

    override fun onS3UploadFetched(responseData: ImageResponse?) {
        responseData?.img_path?.let { url -> updateWallpaper(url) }
    }

    override fun getContext(): Context? {
        return this
    }

    var index = 0

    override fun onGPTResponseReceived(response: String?) {
        if (response != null) {
            Speak.stop()
            lines = response.split("\n", ".").filter { it.isNotEmpty() }.toList()
            adapter.addAll(lines.toCollection(ArrayList()))
            binding.tvTitle.visibility = View.VISIBLE
            utteranceListener = object : UtteranceProgressListener() {
                override fun onStart(id: String?) {

                }

                override fun onDone(id: String?) {
                    id?.toInt()?.plus(1)?.let { newInt ->
                        sayContent(newInt, lines, this)
                    }
                }

                override fun onError(id: String?) {

                }
            }
            index = 0
            changePlayPauseStatus(true)
        } else {
            showErrorToast(null)
        }
    }

    private fun sayContent(newInt: Int, lines: List<String>, listener: UtteranceProgressListener?) {
        if (newInt <= lines.size - 1) {
            runOnUiThread {
                adapter.currentPhrase = newInt
                adapter.notifyDataSetChanged()
                presenter.fetchWallpaperUrl(lines[newInt], false)
                Speak.say(lines[newInt], newInt.toString(), listener)
            }
        } else {
            runOnUiThread {
                changePlayPauseStatus(false)
            }
        }
    }

    private fun updateWallpaper(url: String?) {
        url?.let {
            Glide.with(this@DetailsActivity).load(it).into(binding.ivContent)
        }
    }

    private fun getIntentData() {
        selectedSubject = intent.getStringExtra(SELECTED_SUBJECT)
        selectedTopic = intent.getStringExtra(SELECTED_TOPIC)
    }

    private fun initView() {
        setStatusBarColor(ContextCompat.getColor(this, R.color.light_gray), this)
        changeCharacterAnim(false)
        binding.tvTitle.text = selectedTopic
        adapter = DetailsPhraseRecyclerViewAdapter(this, items)
        binding.rvPhrases.visibility = View.VISIBLE
        binding.rvPhrases.adapter = adapter
        adapter.clearAll()
        binding.rvPhrases.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.btnPlayPause.setOnClickListener {
            if (isCharacterSpeaking) {
                runOnUiThread {
                    changePlayPauseStatus(false)
                }
            } else {
                if (!PermissionUtils.hasPermission(this, SPEECH_PERMISSIONS)) {
                    checkAndAskPermissions()
                } else {
                    changePlayPauseStatus(true)
                }
            }
        }
    }

    private fun changeCharacterAnim(isSpeaking: Boolean) {
        isCharacterSpeaking = isSpeaking
        Glide.with(this@DetailsActivity)
            .load(if (isSpeaking) R.drawable.character else R.drawable.low)
            .transform(RoundedCorners(20))
            .into(binding.ivDictator)
    }

    private fun changePlayPauseStatus(play: Boolean) {
        if (play) {
            binding.btnPlayPause.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.pause
                )
            )
            changeCharacterAnim(true)
            sayContent(index, lines, utteranceListener)
        } else {
            binding.btnPlayPause.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.play
                )
            )
            changeCharacterAnim(false)
            Speak.stop()
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

    override fun onDestroy() {
        Speak.destroy()
        super.onDestroy()
    }
}