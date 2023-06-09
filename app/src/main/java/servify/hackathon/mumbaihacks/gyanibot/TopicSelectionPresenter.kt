package servify.hackathon.mumbaihacks.gyanibot

import servify.hackathon.mumbaihacks.gyanibot.common.NETWORK_CHAT_GPT_QUERY
import servify.hackathon.mumbaihacks.gyanibot.model.RetrofitClient
import servify.hackathon.mumbaihacks.gyanibot.model.SerivfyGPTResponse
import servify.hackathon.mumbaihacks.gyanibot.network.ApiCallback
import servify.hackathon.mumbaihacks.gyanibot.network.ApiService
import servify.hackathon.mumbaihacks.gyanibot.network.NetworkUtils

/**
 * Created by Adarsh Revankar on 03/06/23.
 */
class TopicSelectionPresenter(private val view: TopicSelectionContract.View?) :
    TopicSelectionContract.Presenter, ApiCallback {
    override fun fetchTopicsForSubject(subject: String) {
        view?.showLoader()
        val query = "Please provide me with subtopics or headings related to $subject for kids aged below 6"
        val requestBody = HashMap<String, Any?>()
        requestBody["messages"] = ArrayList<HashMap<String, String?>>().apply {
            this.add(hashMapOf("role" to "user", "content" to query))
        }
        NetworkUtils.makeNetworkCall(
            NETWORK_CHAT_GPT_QUERY,
            RetrofitClient.instance?.getRepository()?.queryGPT(requestBody),
            this
        )
    }

    override fun onSuccess(tag: String, response: SerivfyGPTResponse?) {
        when (tag) {
            NETWORK_CHAT_GPT_QUERY -> {
                if ((response?.choices?.size?: 0) > 0 && response?.choices?.get(0)?.message?.content != null) {
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