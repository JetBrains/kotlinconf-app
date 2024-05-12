package org.jetbrains.kotlinconf.androidx.annotation

/**
 * Denotes that the annotated element should be a float or double in the given range
 *
 * Example:
 * ```
 * @FloatRange(from=0.0,to=1.0)
 * public float getAlpha() {
 *     ...
 * }
 * ```
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.ANNOTATION_CLASS
)
internal actual annotation class FloatRange actual constructor(
    actual val from: Double,
    actual val to: Double,
    actual val fromInclusive: Boolean,
    actual val toInclusive: Boolean
)