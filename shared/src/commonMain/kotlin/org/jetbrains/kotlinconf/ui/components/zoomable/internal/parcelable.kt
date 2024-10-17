package org.jetbrains.kotlinconf.ui.components.zoomable.internal

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class AndroidParcelize()

expect interface AndroidParcelable

