package org.jetbrains.kotlinconf

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import android.content.Context
import android.content.res.Resources
import android.os.Build.VERSION_CODES.N
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.ViewManager
import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView
import ru.gildor.coroutines.retrofit.ErrorResult
import ru.gildor.coroutines.retrofit.Result
import ru.gildor.coroutines.retrofit.getOrNull

inline fun ViewManager.multilineCollapsingToolbarLayout(theme: Int = 0, init: CollapsingToolbarLayout.() -> Unit): CollapsingToolbarLayout {
    return ankoView({ CollapsingToolbarLayout(it) }, theme = theme, init = init)
}

fun Context.getResourceId(@AttrRes attribute: Int) : Int {
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
        Html.fromHtml(getText(resId).toString())
    }
}

val AnkoContext<*>.theme: Resources.Theme
    get() = this.ctx.theme


// Needed for better type inference
inline fun <X, Y> map(source: LiveData<X>, noinline func: (X?) -> Y): LiveData<Y> =
        Transformations.map(source, func)

inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (T?) -> Unit) =
        observe(owner, Observer { observer(it) })


inline fun <T : Any> Result<T>.ifFailed(handler: () -> Unit): Result<T> {
    if (this is ErrorResult) handler()
    return this
}

inline fun <T : Any> Result<T>.ifSucceeded(handler: (data: T) -> Unit): Result<T> {
    (this as? Result.Ok)?.getOrNull()?.let { handler(it) }
    return this
}

inline fun <T : Any> Result<T>.ifError(handler: (code: Int) -> Unit): Result<T> {
    (this as? Result.Error)?.response?.code()?.let { handler(it) }
    return this
}

inline fun <T : Any> Result<T>.ifException(handler: (exception: Throwable) -> Unit): Result<T> {
    (this as? Result.Exception)?.exception?.let { handler(it) }
    return this
}