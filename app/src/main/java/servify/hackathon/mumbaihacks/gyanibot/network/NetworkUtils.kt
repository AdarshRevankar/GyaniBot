package servify.hackathon.mumbaihacks.gyanibot.network

import com.orhanobut.logger.Logger
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subscribers.DisposableSubscriber
import servify.hackathon.mumbaihacks.gyanibot.model.ImageResponse
import servify.hackathon.mumbaihacks.gyanibot.model.SerivfyGPTResponse

class NetworkUtils {
    companion object {
        fun makeNetworkCall(
            tag: String,
            flowable: Flowable<SerivfyGPTResponse>?,
            callback: ApiCallback?
        ): DisposableSubscriber<SerivfyGPTResponse>? {
            return flowable?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread(), true)
                ?.subscribeWith(object : DisposableSubscriber<SerivfyGPTResponse>() {
                    override fun onNext(response: SerivfyGPTResponse) {
                        Logger.d("ON NEXT")
                        Logger.d("ON NEXT")
                        if (response.choices == null) {
                            callback?.onFailure(tag, response)
                        } else {
                            callback?.onSuccess(tag, response)
                        }
                    }

                    override fun onError(t: Throwable?) {
                        Logger.d("ON ERROR")
                        Logger.d("Error Message : ${t?.localizedMessage}}")
                        callback?.onError(tag)
                    }

                    override fun onComplete() {
                        Logger.d("ON COMPLETE")
                    }

                })
        }

        fun makeNetworkCall_v2(
            tag: String,
            flowable: Flowable<ImageResponse>?,
            callback: ApiCallbackV2?
        ): DisposableSubscriber<ImageResponse>? {
            return flowable?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread(), true)
                ?.subscribeWith(object : DisposableSubscriber<ImageResponse>() {
                    override fun onNext(response: ImageResponse) {
                        Logger.d("ON NEXT")
                        Logger.d("ON NEXT")
                        callback?.onSuccess(tag, response)
                    }

                    override fun onError(t: Throwable?) {
                        Logger.d("ON ERROR")
                        Logger.d("Error Message : ${t?.localizedMessage}}")
                        callback?.onError(tag)
                    }

                    override fun onComplete() {
                        Logger.d("ON COMPLETE")
                    }

                })
        }
    }
}