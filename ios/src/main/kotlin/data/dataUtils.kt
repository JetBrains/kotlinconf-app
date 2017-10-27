import libs.*
import platform.UIKit.*
import platform.Foundation.*

enum class KSessionRating(val rawValue: Int) {
    GOOD(1),
    SO_SO(0),
    BAD(-1)
}

val KVote.sessionRating: KSessionRating?
    get() = when (rating) {
        1 -> KSessionRating.GOOD
        0 -> KSessionRating.SO_SO
        -1 -> KSessionRating.BAD
        else -> null
    }

operator fun KSession.compareTo(other: KSession): Int {
    val thisDate = this.startsAtDate ?: NSDate.dateWithTimeIntervalSinceReferenceDate(0.0)
    val otherDate = other.startsAtDate ?: NSDate.dateWithTimeIntervalSinceReferenceDate(0.0)

    if (thisDate == otherDate) {
        return (this.title ?: "").compareTo(other.title ?: "")
    }

    return thisDate.compareTo(otherDate)
}

operator fun NSDate.compareTo(other: NSDate): Int {
    val diff = this.timeIntervalSince1970 - other.timeIntervalSince1970
    return when {
        diff < 0 -> -1
        diff > 0 -> 1
        else -> 0
    }
}

fun KAll.findSpeaker(id: String): KSpeaker? {
    return speaker.toList<KSpeaker>().firstOrNull { it.id == id }
}

fun KAll.findRoom(id: Long): KRoom? {
    return room.toList<KRoom>().firstOrNull { it.id == id }
}

private val PLACEHOLDER_IMAGE = UIImage.imageNamed("user_default")?.circularImage()

fun UIImageView.loadUserIcon(url: String?) {
    val nsURL = url?.let { NSURL.URLWithString(it) }
    sd_setImageWithURL(nsURL, placeholderImage = PLACEHOLDER_IMAGE)
}
