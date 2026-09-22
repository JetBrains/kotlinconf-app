package org.jetbrains.kotlinconf

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AgendaTest {

    private val now = LocalDateTime(2025, 3, 18, 8, 0, 0)

    private fun ldt(hour: Int, minute: Int = 0) =
        LocalDateTime(2025, 3, 18, hour, minute, 0)

    private fun session(
        id: String,
        startsAt: LocalDateTime,
        endsAt: LocalDateTime,
        speakerIds: List<SpeakerId> = emptyList(),
        tags: List<String>? = null,
    ) = Session(
        id = SessionId(id),
        title = id,
        description = "",
        speakerIds = speakerIds,
        location = "Room",
        startsAt = startsAt,
        endsAt = endsAt,
        tags = tags,
        videoUrl = null,
    )

    private fun speaker(id: String) = Speaker(
        id = SpeakerId(id),
        name = id,
        position = "",
        description = "",
        photoUrl = "",
    )

    private fun conference(sessions: List<Session>, speakers: List<Speaker> = emptyList()) =
        Conference(sessions = sessions, speakers = speakers)

    private fun allSessionIds(timeSlots: List<TimeSlot>): List<SessionId> =
        timeSlots.flatMap { it.sessions.map { s -> s.id } }

    /* ---- Full-day workshop + service events at same start ---- */

    @Test
    fun `full-day workshop keeps own slot separate from service event at same start`() {
        val s1 = speaker("s1")
        val workshop = session("workshop", ldt(9), ldt(17), speakerIds = listOf(s1.id))
        val breakfast = session("breakfast", ldt(9), ldt(10)) // service: no speakers, no tags

        val conf = conference(listOf(workshop, breakfast), speakers = listOf(s1))
        val timeSlots = listOf(workshop, breakfast).groupByTime(conf, now, emptySet(), emptyMap())

        assertEquals(2, timeSlots.size)

        val breakfastSlot = timeSlots.find { it.endsAt == ldt(10) }
        val workshopSlot = timeSlots.find { it.endsAt == ldt(17) }

        assertNotNull(breakfastSlot)
        assertNotNull(workshopSlot)
        assertEquals(listOf(SessionId("breakfast")), breakfastSlot.sessions.map { it.id })
        assertEquals(listOf(SessionId("workshop")), workshopSlot.sessions.map { it.id })
    }

    /* ---- Service events get own exact slots in chronological order ---- */

    @Test
    fun `service events have own exact time slots in chronological order`() {
        val breakfast = session("breakfast", ldt(8, 30), ldt(9))
        val coffeeBreak = session("coffee", ldt(10, 30), ldt(10, 40)) // 10 min — lightning duration but service
        val lunch = session("lunch", ldt(12), ldt(13))
        val afternoonCoffee = session("afternoon-coffee", ldt(14, 15), ldt(14, 25)) // 10 min

        val workshop = session("workshop", ldt(9), ldt(17), tags = listOf("Workshop"))
        val conf = conference(listOf(lunch, workshop, afternoonCoffee, breakfast, coffeeBreak))
        val timeSlots = conf.sessions
            .groupByTime(conf, now, emptySet(), emptyMap())

        assertEquals(5, timeSlots.size)
        val breaks = timeSlots.filter { it.sessions.none { session -> session.id == workshop.id } }

        // chronological by start time
        assertEquals(ldt(8, 30), breaks[0].startsAt)
        assertEquals(SessionId("breakfast"), breaks[0].sessions[0].id)

        assertEquals(ldt(10, 30), breaks[1].startsAt)
        assertEquals(SessionId("coffee"), breaks[1].sessions[0].id)

        assertEquals(ldt(12), breaks[2].startsAt)
        assertEquals(SessionId("lunch"), breaks[2].sessions[0].id)

        assertEquals(ldt(14, 15), breaks[3].startsAt)
        assertEquals(SessionId("afternoon-coffee"), breaks[3].sessions[0].id)
    }

    /* ---- Shorter normal talk keeps own slot inside longer session ---- */

    @Test
    fun `shorter normal talk keeps own slot when nested inside longer session`() {
        val s1 = speaker("s1")
        val s2 = speaker("s2")
        val workshop = session("workshop", ldt(9), ldt(17), speakerIds = listOf(s1.id))
        val shortTalk = session("short", ldt(10), ldt(11), speakerIds = listOf(s2.id))

        val conf = conference(listOf(workshop, shortTalk), speakers = listOf(s1, s2))
        val timeSlots = listOf(workshop, shortTalk).groupByTime(conf, now, emptySet(), emptyMap())

        assertEquals(2, timeSlots.size)
        assertEquals(setOf(SessionId("workshop")), timeSlots[0].sessions.map { it.id }.toSet())
        assertEquals(setOf(SessionId("short")), timeSlots[1].sessions.map { it.id }.toSet())
    }

    /* ---- Lightning talks group into containing regular talk ---- */

    @Test
    fun `lightning talks group into the containing regular talk slot`() {
        val s1 = speaker("s1")
        val s2 = speaker("s2")
        val s3 = speaker("s3")
        val talk = session("talk", ldt(10), ldt(11), speakerIds = listOf(s1.id))
        val lightning1 = session("l1", ldt(10, 5), ldt(10, 15), speakerIds = listOf(s2.id))
        val lightning2 = session("l2", ldt(10, 20), ldt(10, 30), speakerIds = listOf(s3.id))

        val conf = conference(
            listOf(talk, lightning1, lightning2),
            speakers = listOf(s1, s2, s3),
        )
        val timeSlots = listOf(talk, lightning1, lightning2)
            .groupByTime(conf, now, emptySet(), emptyMap())

        assertEquals(1, timeSlots.size)
        val slot = timeSlots[0]
        val ids = slot.sessions.map { it.id }.toSet()
        assertTrue(ids.containsAll(listOf(SessionId("talk"), SessionId("l1"), SessionId("l2"))))
        // non-lightning first
        assertEquals(SessionId("talk"), slot.sessions[0].id)
    }

    @Test
    fun `lightning talk picks shortest containing slot`() {
        val s1 = speaker("s1")
        val s2 = speaker("s2")
        val s3 = speaker("s3")
        val longTalk = session("long", ldt(9), ldt(17), speakerIds = listOf(s1.id))
        val shortTalk = session("short", ldt(10), ldt(11), speakerIds = listOf(s2.id))
        val lightning = session("l1", ldt(10, 5), ldt(10, 15), speakerIds = listOf(s3.id))

        val conf = conference(
            listOf(longTalk, shortTalk, lightning),
            speakers = listOf(s1, s2, s3),
        )
        val timeSlots = listOf(longTalk, shortTalk, lightning)
            .groupByTime(conf, now, emptySet(), emptyMap())

        // 2 talk-slots: 09-17 and 10-11; lightning goes into shortest containing = 10-11
        assertEquals(2, timeSlots.size)
        val shortSlot = timeSlots.find { it.endsAt == ldt(11) }!!
        val shortIds = shortSlot.sessions.map { it.id }.toSet()
        assertTrue(shortIds.contains(SessionId("short")))
        assertTrue(shortIds.contains(SessionId("l1")))

        val longSlot = timeSlots.find { it.endsAt == ldt(17) }!!
        assertEquals(setOf(SessionId("long")), longSlot.sessions.map { it.id }.toSet())
    }

    /* ---- Standalone lightning talks must not disappear ---- */

    @Test
    fun `standalone lightning talk does not disappear`() {
        val s1 = speaker("s1")
        val lightning = session("lightning", ldt(8), ldt(8, 10), speakerIds = listOf(s1.id))

        val conf = conference(listOf(lightning), speakers = listOf(s1))
        val timeSlots = listOf(lightning).groupByTime(conf, now, emptySet(), emptyMap())

        assertEquals(1, timeSlots.size)
        assertEquals(SessionId("lightning"), timeSlots[0].sessions[0].id)
    }

    @Test
    fun `standalone lightning talk not lost alongside regular sessions`() {
        val s1 = speaker("s1")
        val s2 = speaker("s2")
        val lightning = session("lightning", ldt(8), ldt(8, 10), speakerIds = listOf(s1.id))
        val talk = session("talk", ldt(10), ldt(11), speakerIds = listOf(s2.id))

        val conf = conference(listOf(lightning, talk), speakers = listOf(s1, s2))
        val timeSlots = listOf(lightning, talk).groupByTime(conf, now, emptySet(), emptyMap())

        assertEquals(2, timeSlots.size)
        val ids = allSessionIds(timeSlots).toSet()
        assertTrue(ids.contains(SessionId("lightning")))
        assertTrue(ids.contains(SessionId("talk")))
    }

    /* ---- Each session appears exactly once ---- */

    @Test
    fun `each session appears exactly once across all time slots`() {
        val s1 = speaker("s1")
        val s2 = speaker("s2")
        val s3 = speaker("s3")

        val all = listOf(
            session("workshop", ldt(9), ldt(17), speakerIds = listOf(s1.id)),
            session("breakfast", ldt(8, 30), ldt(9)),
            session("talk", ldt(10), ldt(11), speakerIds = listOf(s2.id)),
            session("lightning", ldt(10, 5), ldt(10, 15), speakerIds = listOf(s3.id)),
            session("lunch", ldt(12), ldt(13)),
        )

        val conf = conference(all, speakers = listOf(s1, s2, s3))
        val timeSlots = all.groupByTime(conf, now, emptySet(), emptyMap())

        val inputIds = all.map { it.id }
        val outputIds = allSessionIds(timeSlots)

        assertEquals(inputIds.toSet(), outputIds.toSet()) // all present
        assertEquals(outputIds.size, outputIds.toSet().size) // no duplicates
    }

    /* ---- buildAgenda end-to-end ---- */

    @Test
    fun `buildAgenda groups sessions by day and produces correct time slots`() {
        val s1 = speaker("s1")
        val day1 = LocalDate(2025, 3, 18)
        val day2 = LocalDate(2025, 3, 19)
        fun d1t(hour: Int) = LocalDateTime(2025, 3, 18, hour, 0)
        fun d2t(hour: Int) = LocalDateTime(2025, 3, 19, hour, 0)

        val sessions = listOf(
            session("d1-talk", d1t(10), d1t(11), speakerIds = listOf(s1.id)),
            session("d2-talk", d2t(14), d2t(15), speakerIds = listOf(s1.id)),
        )

        val conf = conference(sessions, speakers = listOf(s1))
        val agenda = conf.buildAgenda(emptySet(), emptyList(), now)

        assertEquals(2, agenda.size)
        assertEquals(day1, agenda[0].date)
        assertEquals(1, agenda[0].timeSlots.size)
        assertEquals(SessionId("d1-talk"), agenda[0].timeSlots[0].sessions[0].id)

        assertEquals(day2, agenda[1].date)
        assertEquals(1, agenda[1].timeSlots.size)
        assertEquals(SessionId("d2-talk"), agenda[1].timeSlots[0].sessions[0].id)
    }
}
