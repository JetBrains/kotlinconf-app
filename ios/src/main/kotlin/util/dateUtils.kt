import platform.Foundation.*

private fun createDateFormatter(format: String) = NSDateFormatter().apply {
    dateFormat = format
    timeZone = NSTimeZone.timeZoneWithAbbreviation("UTC")!!
}

private val FULL_DATE_FORMATTER = createDateFormatter("EEEE, MMMM d h:mm a")
private val ONLY_TIME_FORMATTER = createDateFormatter("h:mm a")
private val WEEKDAY_TIME_FORMATTER = createDateFormatter("EEEE h:mm a")

private val IEEE_DATE_PARSER = createDateFormatter("yyyy-MM-dd'T'HH:mm:ss")

fun NSDate?.orDefault() = this ?: NSDate.dateWithTimeIntervalSinceReferenceDate(0.0)

fun parseDate(str: String?): NSDate? {
    return str?.let { IEEE_DATE_PARSER.dateFromString(it) }
}

fun renderDate(date: NSDate?): String {
    return FULL_DATE_FORMATTER.stringFromDate(date.orDefault())
}

fun renderWeekdayTime(date: NSDate?): String {
    return WEEKDAY_TIME_FORMATTER.stringFromDate(date.orDefault())
}

fun renderInterval(startDate: NSDate?, endDate: NSDate?): String {
    val start = startDate.orDefault()
    val end = endDate.orDefault()
    return FULL_DATE_FORMATTER.stringFromDate(start) + " – " + ONLY_TIME_FORMATTER.stringFromDate(end)
}