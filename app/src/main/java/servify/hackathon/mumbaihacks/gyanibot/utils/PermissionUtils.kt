package servify.hackathon.mumbaihacks.gyanibot.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Created by Adarsh Revankar on 03/06/23.
 */
class PermissionUtils {
    companion object {
        public fun hasPermission(context: Context, permissions: ArrayList<String>): Boolean {
            permissions.forEach {
                if (ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }
    }

}