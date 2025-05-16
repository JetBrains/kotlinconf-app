package org.jetbrains.kotlinconf

class LocalNotificationId(
    val type: Type,
    val id: String,
) {
    enum class Type(private val key: String) {
        SessionStart("session-start"),
        SessionEnd("session-end"),
        ;

        override fun toString(): String = key

        companion object {
            fun parse(key: String): Type? = entries.find { it.key == key }
        }
    }

    override fun equals(other: Any?): Boolean {
        other as? LocalNotificationId ?: return false
        return toString() == other.toString()
    }

    override fun hashCode(): Int = toString().hashCode()

    override fun toString(): String = "$type:$id"

    companion object {
        fun parse(string: String): LocalNotificationId? {
            val split = string.split(":")
            if (split.size != 2) return null

            val (typeString, id) = split
            val type = Type.parse(typeString) ?: return null
            return LocalNotificationId(type, id)
        }
    }
}
