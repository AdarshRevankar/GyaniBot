package servify.hackathon.mumbaihacks.gyanibot.model

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.TextUtils
import com.orhanobut.logger.Logger
import io.reactivex.rxjava3.core.Flowable
import java.lang.ref.SoftReference
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class Speak(appContext: Context) {

    init {
        textToSpeech = SoftReference(TextToSpeech(appContext, {
            if (it == TextToSpeech.SUCCESS) {
                val avlLocales: Set<Locale>? = textToSpeech?.get()?.availableLanguages
                val isSupported = AtomicBoolean(false)
                if (avlLocales != null && avlLocales.contains(Locale.getDefault())) {
                    Flowable.fromIterable(avlLocales).forEach { locale: Locale ->
                        if (locale.language == Locale.getDefault().language) {
                            isSupported.set(true)
                            textToSpeech?.get()?.language = locale
                        }
                    }.dispose()
                    if (!isSupported.get()) setAvailableLanguage()
                } else setAvailableLanguage()
                textToSpeech?.get()?.setPitch(1f)
            }
        }, "com.google.android.tts"))
    }


    private fun setAvailableLanguage() {
        if (textToSpeech != null && textToSpeech!!.get() != null && !textToSpeech!!.get()!!.availableLanguages.isNullOrEmpty()) {
            val isSupported = AtomicBoolean(false)
            Flowable.fromIterable(textToSpeech!!.get()!!.availableLanguages)
                .forEach {
                    if (it.language == Locale.US.language || it.language == Locale.UK.language) {
                        isSupported.set(true)
                        textToSpeech?.get()?.language = it
                    }
                }.dispose()
            if (!isSupported.get()) {
                val result: Int? = textToSpeech?.get()?.setLanguage(
                    Flowable.fromIterable(textToSpeech!!.get()!!.availableLanguages)
                        .blockingFirst(Locale.getDefault())
                )
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Logger.e("TextToSpeech" + "This Language is not supported")
                }
            }
        }
    }

    companion object {
        var textToSpeech: SoftReference<TextToSpeech?>? = null

        fun say(text: String, utteranceId: String, utteranceProgressListener: UtteranceProgressListener? = null) {
            if (textToSpeech?.get() != null && !TextUtils.isEmpty(text)) {
                Logger.d("TextToSpeech: Say: $text")
                textToSpeech?.get()?.setOnUtteranceProgressListener(utteranceProgressListener)
                textToSpeech?.get()?.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId)
            }
        }

        fun stop() {
            if (textToSpeech?.get() != null) {
                textToSpeech?.get()?.stop()
            }
        }

        fun destroy() {
            if (textToSpeech?.get() != null) {
                textToSpeech?.get()?.stop()
                textToSpeech?.get()?.shutdown()
                textToSpeech = null
            }
        }
    }
}
