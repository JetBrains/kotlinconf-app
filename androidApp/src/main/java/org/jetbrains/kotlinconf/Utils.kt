package org.jetbrains.kotlinconf

import android.app.*
import android.content.*
import android.content.res.*
import android.net.*
import android.os.Build.VERSION_CODES.*
import android.provider.*
import android.text.*
import android.util.*
import android.view.*
import androidx.annotation.*
import androidx.core.content.*
import kotlin.math.*

internal fun Context.getResourceId(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.resourceId
}

@ColorInt
internal fun View.color(@ColorRes attribute: Int): Int = ContextCompat.getColor(context, attribute)

internal inline fun <reified T : Activity> showActivity(block: Intent.() -> Unit = {}) {
    val context = KotlinConf.service.context.activity
    val intent = Intent(context, T::class.java).apply(block)
    context.startActivity(intent)
}

internal fun Context.getHtmlText(resId: Int): Spanned {
    return if (android.os.Build.VERSION.SDK_INT >= N) {
        Html.fromHtml(getText(resId).toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(getText(resId).toString())
    }
}

internal val Context.connectivityManager
    get() = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

internal val Context.isConnected: Boolean?
    get() = connectivityManager?.activeNetworkInfo?.isConnected

internal val Context.isAirplaneModeOn: Boolean
    @RequiresApi(JELLY_BEAN_MR1)
    get() = try {
        Settings.System.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0
    } catch (error: Throwable) {
        false
    }

internal val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

internal val Int.px: Int get() = (this / Resources.getSystem().displayMetrics.density).roundToInt()

internal fun View.setPressedColor(event: MotionEvent, normal: Int, pressed: Int) {
    val action = event.action
    val color = if (action != MotionEvent.ACTION_DOWN) {
        normal
    } else {
        pressed
    }

    setBackgroundColor(color(color))
}

internal fun View.autoclear() {
    setOnTouchListener { view, event ->
        view.clearFocus()
        false
    }
}