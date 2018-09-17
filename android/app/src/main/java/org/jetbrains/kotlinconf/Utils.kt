package org.jetbrains.kotlinconf

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Build
import android.os.Build.VERSION_CODES.N
import android.provider.Settings
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.annotation.RequiresApi
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.ViewManager
import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView

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