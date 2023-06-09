package servify.hackathon.mumbaihacks.gyanibot.utils

import android.app.Activity
import android.view.Window
import android.view.WindowManager
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Created by Adarsh Revankar on 03/06/23.
 */
fun setStatusBarColor(color: Int, activity: Activity) {
    // min version android M checks removed
    val window: Window = activity.window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
        ColorUtils.calculateLuminance(color) > 0.5
    WindowCompat.getInsetsController(window, window.decorView).systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    window.statusBarColor = color
}