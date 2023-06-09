package servify.hackathon.mumbaihacks.gyanibot.common

import android.Manifest

/**
 * Created by Adarsh Revankar on 03/06/23.
 */
val SPEECH_PERMISSIONS = arrayListOf(Manifest.permission.RECORD_AUDIO)
const val REQUEST_SPEECH_PERMISSION = 1000

const val SELECTED_SUBJECT = "subject"
const val SELECTED_TOPIC = "topic"
const val SELECTED_WALLPAPER = "wallpaper"

const val NETWORK_CHAT_GPT_QUERY = "chatServifyGPT"
const val NETWORK_IMAGE_FETCH = "imageFetch"