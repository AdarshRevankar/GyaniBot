package servify.hackathon.mumbaihacks.gyanibot.network

import servify.hackathon.mumbaihacks.gyanibot.model.ImageResponse
import servify.hackathon.mumbaihacks.gyanibot.model.SerivfyGPTResponse

interface ApiCallback {

    fun onSuccess(tag: String, response: SerivfyGPTResponse?)

    fun onError(tag: String)

    fun onFailure(tag: String, response: SerivfyGPTResponse?)
}

interface ApiCallbackV2 {
    fun onSuccess(tag: String, response: ImageResponse?)

    fun onError(tag: String)

    fun onFailure(tag: String, response: ImageResponse?)
}