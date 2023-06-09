package servify.hackathon.mumbaihacks.gyanibot.network

import io.reactivex.rxjava3.core.Flowable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url
import servify.hackathon.mumbaihacks.gyanibot.model.ImageResponse
import servify.hackathon.mumbaihacks.gyanibot.model.SerivfyGPTResponse


interface ApiService {
    companion object {
        const val QUERY_GPT_BASE_URL = "http://172.21.0.124:8100/"
        const val QUERY_STABLE_DIFFUSION_BASE_URL = "http://172.21.0.124:8102/"
    }

    @POST("chat/")
    fun queryGPT(@Body params: HashMap<String, Any?>): Flowable<SerivfyGPTResponse>

    @POST("get_images/")
    fun queryImage(@Body params: HashMap<String, Any?>): Flowable<ImageResponse>?
}