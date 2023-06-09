package servify.hackathon.mumbaihacks.gyanibot

import android.content.Context

/**
 * Created by Adarsh Revankar on 03/06/23.
 */
interface TopicSelectionContract {

    interface View {
        fun showLoader()

        fun hideLoader()

        fun getContext(): Context?

        fun onGPTResponseReceived(response: String?)

        fun showErrorToast(message: String?)
    }

    interface Presenter {

        fun fetchTopicsForSubject(subject: String)

    }
}