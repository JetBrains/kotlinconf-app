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
internal expect annotation class FloatRange(
    /** Smallest value. Whether it is inclusive or not is determined by [.fromInclusive] */
    val from: Double = Double.NEGATIVE_INFINITY,
    /** Largest value. Whether it is inclusive or not is determined by [.toInclusive] */
    val to: Double = Double.POSITIVE_INFINITY,
    /** Whether the from value is included in the range */
    val fromInclusive: Boolean = true,
    /** Whether the to value is included in the range */
    val toInclusive: Boolean = true
)

