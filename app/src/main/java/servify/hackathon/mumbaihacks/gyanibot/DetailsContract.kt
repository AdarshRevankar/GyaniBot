package servify.hackathon.mumbaihacks.gyanibot

import android.content.Context
import servify.hackathon.mumbaihacks.gyanibot.model.ImageResponse

/**
 * Created by Adarsh Revankar on 03/06/23.
 */
interface DetailsContract {

    interface View {
        fun showLoader()

        fun hideLoader()

        fun getContext(): Context?

        fun onGPTResponseReceived(response: String?)

        fun showErrorToast(message: String?)

        fun onS3UploadFetched(url: ImageResponse?)
    }

    interface Presenter {

        fun fetchTopicDetails(topic: String)

        fun fetchWallpaperUrl(wallpaperDescription: String, isVectorArt: Boolean)

    }
}