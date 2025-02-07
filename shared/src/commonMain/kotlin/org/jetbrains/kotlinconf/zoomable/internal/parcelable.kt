package org.jetbrains.kotlinconf.zoomable.internal

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
internal expect annotation class AndroidParcelize()

internal expect interface AndroidParcelable
