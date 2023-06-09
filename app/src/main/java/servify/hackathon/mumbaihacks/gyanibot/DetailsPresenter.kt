package servify.hackathon.mumbaihacks.gyanibot

import servify.hackathon.mumbaihacks.gyanibot.common.NETWORK_CHAT_GPT_QUERY
import servify.hackathon.mumbaihacks.gyanibot.common.NETWORK_IMAGE_FETCH
import servify.hackathon.mumbaihacks.gyanibot.model.ImageResponse
import servify.hackathon.mumbaihacks.gyanibot.model.RetrofitClient
import servify.hackathon.mumbaihacks.gyanibot.model.RetrofitClient_v2
import servify.hackathon.mumbaihacks.gyanibot.model.SerivfyGPTResponse
import servify.hackathon.mumbaihacks.gyanibot.network.ApiCallback
import servify.hackathon.mumbaihacks.gyanibot.network.ApiCallbackV2
import servify.hackathon.mumbaihacks.gyanibot.network.ApiService
import servify.hackathon.mumbaihacks.gyanibot.network.NetworkUtils
import java.util.logging.Logger

/**
 * Created by Adarsh Revankar on 03/06/23.
 */
class DetailsPresenter(private val view: DetailsContract.View?) : DetailsContract.Presenter,
    ApiCallback {

    override fun fetchTopicDetails(topic: String) {
        view?.showLoader()
        val query = "Explain $topic in a way that a 7-year-old child would understand"
        val requestBody = HashMap<String, Any?>()
        requestBody["messages"] = ArrayList<HashMap<String, String?>>().apply {
            this.add(hashMapOf("role" to "user", "content" to query))
        }
        NetworkUtils.makeNetworkCall(
            NETWORK_CHAT_GPT_QUERY,
            RetrofitClient.instance?.getRepository()
                ?.queryGPT(requestBody),
            this
        )
    }

    override fun fetchWallpaperUrl(wallpaperDescription: String, isVectorArt: Boolean) {
        val requestBody = HashMap<String, Any?>()
        requestBody["text"] = wallpaperDescription
        requestBody["is_vector_art"] = isVectorArt
        NetworkUtils.makeNetworkCall_v2(
            NETWORK_IMAGE_FETCH,
            RetrofitClient_v2.instance?.getRepository()
                ?.queryImage(requestBody),
            object : ApiCallbackV2 {
                override fun onSuccess(tag: String, response: ImageResponse?) {
                    when (tag) {
                        NETWORK_IMAGE_FETCH -> {
                            view?.onS3UploadFetched(response)
                        }
                        else -> {

                        }
                    }
                }

                override fun onError(tag: String) {

                }

                override fun onFailure(tag: String, response: ImageResponse?) {

                }

            }
        )
    }

    override fun onSuccess(tag: String, response: SerivfyGPTResponse?) {
        when (tag) {
            NETWORK_CHAT_GPT_QUERY -> {
                if ((response?.choices?.size ?: 0) > 0 &&
                    response?.choices?.get(0)?.message?.content != null
                ) {
                    view?.hideLoader()
                    view?.onGPTResponseReceived(response.choices[0].message?.content)
                }
            }

            else -> {

            }
        }
    }

    override fun onError(tag: String) {
        view?.hideLoader()
        view?.showErrorToast(null)
    }

    override fun onFailure(tag: String, response: SerivfyGPTResponse?) {
        view?.hideLoader()
        view?.showErrorToast(null)
    }
}