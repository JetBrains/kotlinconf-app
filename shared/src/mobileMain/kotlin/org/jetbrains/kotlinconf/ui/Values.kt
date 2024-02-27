package org.jetbrains.kotlinconf.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import org.jetbrains.kotlinconf.ui.theme.blackWhite
import org.jetbrains.kotlinconf.ui.theme.violet

val MOBILE_APP_DESCRIPTION: AnnotatedString
    @Composable get() = buildAnnotatedString {
        append("The KotlinConf application is developed by the JetBrains team with Kotlin Multiplatform shared logic. Compose Multiplatform is used for both apps on Android and iOS. \n\nCheck out the ")
        appendLink("GitHub repository", "https://github.com/JetBrains/kotlinconf-app")
        append(" for the source code and more technical details about the application.\n\n")
        append("Enjoy the app, and please share your feedback to help us make it even better!")
    }

@Composable
internal fun AnnotatedString.Builder.appendLink(text: String, link: String = text) {
    pushStringAnnotation("link", link)
    val style = SpanStyle(
        color = MaterialTheme.colors.blackWhite,
        textDecoration = TextDecoration.Underline
    )
    withStyle(style) {
        append(text)
    }
    pop()
}

internal fun AnnotatedString.Builder.appendBold(text: String) {
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append(text)
    }
}

val CODE_OF_CONDUCT = buildAnnotatedString {
    append(
        """
        This event is dedicated to providing a harassment-free experience for everyone, regardless of gender, sexual orientation, ability, physical appearance, body size, race, or religion. 

        We do not tolerate harassment of event participants in any form. Sexual language and imagery is not appropriate for any event venue, including talks. Event participants violating these rules may be sanctioned or expelled from the event without a refund at the discretion of the event organizers.

        Any form of written or verbal communication that can be harassing to any attendee, speaker, or staff member is not allowed at this event. Harassment includes offensive verbal comments related to gender, sexual orientation, ability, physical appearance, body size, race, or religion; sexual images in public spaces; deliberate intimidation; stalking; following; photography or recording without the subject's consent photography or recording; sustained disruption of talks or other activities; inappropriate physical contact; and unwelcome sexual attention.

        Exhibitors in the expo hall, sponsor or vendor booths, or similar areas are also subject to the anti-harassment policy. Exhibitors should not use sexualized images, activities, or other materials. Booth staff, including volunteers, should not wear sexualized clothing, uniforms, or costumes, or otherwise create a sexualized environment.

        We expect participants to follow these rules at all event venues and event-related social gatherings. Please inform an event staff member (identified by their official t-shirts and/or special badges) if you feel a violation has taken place. Participants asked to stop any harassing behavior are expected to comply immediately. Event staff will be happy to help participants contact hotel/venue security or local law enforcement, provide escorts, or otherwise ensure those experiencing harassment feel safe for the duration of the event.

        We value your attendance.
    """.trimIndent()
    )
}