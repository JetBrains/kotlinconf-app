package org.jetbrains.kotlinconf

import android.content.*
import android.content.res.*
import android.net.*
import android.os.*
import android.os.Build.VERSION_CODES.*
import android.provider.*
import android.support.annotation.*
import android.text.*
import android.util.*
import android.view.*
import net.opacapp.multilinecollapsingtoolbar.*
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.*

inline fun ViewManager.multilineCollapsingToolbarLayout(
    theme: Int = 0,
    init: CollapsingToolbarLayout.() -> Unit
): CollapsingToolbarLayout {
    return ankoView({ CollapsingToolbarLayout(it) }, theme = theme, init = init)
}

fun Context.getResourceId(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.resourceId
}

@ColorInt
fun Resources.Theme.getColor(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    if (resolveAttribute(attribute, typedValue, true)) {
        return typedValue.data
    }

    return 0
}

fun Context.getHtmlText(resId: Int): Spanned {
    return if (android.os.Build.VERSION.SDK_INT >= N) {
        Html.fromHtml(getText(resId).toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(getText(resId).toString())
    }
}

val AnkoContext<*>.theme: Resources.Theme
    get() = this.ctx.theme

val Context.connectivityManager
    get() = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

val Context.isConnected: Boolean?
    get() = connectivityManager?.activeNetworkInfo?.isConnected

val Context.isAirplaneModeOn: Boolean
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    get() = try {
        Settings.System.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0
    } catch (error: Throwable) {
        false
    }