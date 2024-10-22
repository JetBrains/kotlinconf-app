package org.jetbrains.kotlinconf.ui.components.zoomable.internal

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class AndroidParcelize

expect interface AndroidParcelable

