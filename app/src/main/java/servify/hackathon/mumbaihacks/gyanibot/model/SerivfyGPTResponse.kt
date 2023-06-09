package servify.hackathon.mumbaihacks.gyanibot.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Adarsh Revankar on 03/06/23.
 */
@Parcelize
class SerivfyGPTResponse(
    val id: String? = null,
    val `object`: String? = null,
    val created: Int? = null,
    val choices: ArrayList<Choice>? = null
) : Parcelable

@Parcelize
class Choice(
    val message: Message? = null,
    val index: Int? = null
) : Parcelable

@Parcelize
class Message(
    val role: String? = null,
    val content: String? = null
) : Parcelable


@Parcelize
class ImageResponse(
    val img_path: String? = null
): Parcelable