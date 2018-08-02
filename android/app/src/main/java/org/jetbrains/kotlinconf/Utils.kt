package org.jetbrains.kotlinconf

import android.arch.lifecycle.*
import android.content.*
import android.content.res.*
import android.os.Build.VERSION_CODES.*
import android.support.annotation.*
import android.text.*
import android.util.*
import android.view.*
import net.opacapp.multilinecollapsingtoolbar.*
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.*
import ru.gildor.coroutines.retrofit.*

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