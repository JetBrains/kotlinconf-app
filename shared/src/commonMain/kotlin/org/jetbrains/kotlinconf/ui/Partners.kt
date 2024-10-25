package org.jetbrains.kotlinconf.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinconfapp.shared.generated.resources.Res
import kotlinconfapp.shared.generated.resources.academy
import kotlinconfapp.shared.generated.resources.android
import kotlinconfapp.shared.generated.resources.btsystems
import kotlinconfapp.shared.generated.resources.cloud_inject
import kotlinconfapp.shared.generated.resources.express
import kotlinconfapp.shared.generated.resources.foundation
import kotlinconfapp.shared.generated.resources.google
import kotlinconfapp.shared.generated.resources.gradle
import kotlinconfapp.shared.generated.resources.kodein
import kotlinconfapp.shared.generated.resources.kt_weekly
import kotlinconfapp.shared.generated.resources.mercari
import kotlinconfapp.shared.generated.resources.monta
import kotlinconfapp.shared.generated.resources.partner_academy
import kotlinconfapp.shared.generated.resources.partner_academy_description
import kotlinconfapp.shared.generated.resources.partner_android
import kotlinconfapp.shared.generated.resources.partner_android_description
import kotlinconfapp.shared.generated.resources.partner_btsystems
import kotlinconfapp.shared.generated.resources.partner_btsystems_description
import kotlinconfapp.shared.generated.resources.partner_cloud_inject
import kotlinconfapp.shared.generated.resources.partner_cloud_inject_description
import kotlinconfapp.shared.generated.resources.partner_express
import kotlinconfapp.shared.generated.resources.partner_express_description
import kotlinconfapp.shared.generated.resources.partner_foundation
import kotlinconfapp.shared.generated.resources.partner_foundation_description
import kotlinconfapp.shared.generated.resources.partner_google
import kotlinconfapp.shared.generated.resources.partner_google_description
import kotlinconfapp.shared.generated.resources.partner_gradle
import kotlinconfapp.shared.generated.resources.partner_gradle_description
import kotlinconfapp.shared.generated.resources.partner_kodein
import kotlinconfapp.shared.generated.resources.partner_kodein_description
import kotlinconfapp.shared.generated.resources.partner_kt_weekly
import kotlinconfapp.shared.generated.resources.partner_kt_weekly_description
import kotlinconfapp.shared.generated.resources.partner_mercari
import kotlinconfapp.shared.generated.resources.partner_mercari_description
import kotlinconfapp.shared.generated.resources.partner_monta
import kotlinconfapp.shared.generated.resources.partner_monta_description
import kotlinconfapp.shared.generated.resources.partner_pretix
import kotlinconfapp.shared.generated.resources.partner_pretix_description
import kotlinconfapp.shared.generated.resources.partner_sentry
import kotlinconfapp.shared.generated.resources.partner_sentry_description
import kotlinconfapp.shared.generated.resources.partner_shape
import kotlinconfapp.shared.generated.resources.partner_shape_description
import kotlinconfapp.shared.generated.resources.partner_sticker_mule
import kotlinconfapp.shared.generated.resources.partner_sticker_mule_description
import kotlinconfapp.shared.generated.resources.partner_touchlab
import kotlinconfapp.shared.generated.resources.partner_touchlab_description
import kotlinconfapp.shared.generated.resources.partner_uber
import kotlinconfapp.shared.generated.resources.partner_uber_description
import kotlinconfapp.shared.generated.resources.partner_worldline
import kotlinconfapp.shared.generated.resources.partner_worldline_description
import kotlinconfapp.shared.generated.resources.pretix
import kotlinconfapp.shared.generated.resources.sentry
import kotlinconfapp.shared.generated.resources.shape
import kotlinconfapp.shared.generated.resources.stickermule
import kotlinconfapp.shared.generated.resources.touchlab
import kotlinconfapp.shared.generated.resources.uber
import kotlinconfapp.shared.generated.resources.worldline
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.kotlinconf.ui.components.NavigationBar
import org.jetbrains.kotlinconf.ui.theme.grey5Black
import org.jetbrains.kotlinconf.ui.theme.greyWhite
import org.jetbrains.kotlinconf.ui.theme.whiteGrey

enum class Type {
    Gold, Silver, Bronze, AudioVisual, Media, GameZone, Supporter
}

@OptIn(ExperimentalResourceApi::class)
enum class Partner(
    val title: StringResource,
    val description: StringResource,
    val logo: DrawableResource,
    val type: Type
) {
    // Gold
    Google(
        title = Res.string.partner_google,
        description = Res.string.partner_google_description,
        logo = Res.drawable.google,
        type = Type.Gold
    ),
    Monta(
        title = Res.string.partner_monta,
        description = Res.string.partner_monta_description,
        logo = Res.drawable.monta,
        type = Type.Gold
    ),

    // Silver
    Gradle(
        title = Res.string.partner_gradle,
        description = Res.string.partner_gradle_description,
        logo = Res.drawable.gradle,
        type = Type.Silver
    ),
    Sentry(
        title = Res.string.partner_sentry,
        description = Res.string.partner_sentry_description,
        logo = Res.drawable.sentry,
        type = Type.Silver
    ),
    TouchLab(
        title = Res.string.partner_touchlab,
        description = Res.string.partner_touchlab_description,
        logo = Res.drawable.touchlab,
        type = Type.Silver
    ),
    CloudInject(
        title = Res.string.partner_cloud_inject,
        description = Res.string.partner_cloud_inject_description,
        logo = Res.drawable.cloud_inject,
        type = Type.Silver
    ),

    // Bronze
    Uber(
        title = Res.string.partner_uber,
        description = Res.string.partner_uber_description,
        logo = Res.drawable.uber,
        type = Type.Bronze
    ),
    Kodein(
        title = Res.string.partner_kodein,
        description = Res.string.partner_kodein_description,
        logo = Res.drawable.kodein,
        type = Type.Bronze
    ),
    Mercari(
        title = Res.string.partner_mercari,
        description = Res.string.partner_mercari_description,
        logo = Res.drawable.mercari,
        type = Type.Bronze
    ),
    WorldLine(
        title = Res.string.partner_worldline,
        description = Res.string.partner_worldline_description,
        logo = Res.drawable.worldline,
        type = Type.Bronze
    ),
    BTSystems(
        title = Res.string.partner_btsystems,
        description = Res.string.partner_btsystems_description,
        logo = Res.drawable.btsystems,
        type = Type.Bronze
    ),

    // Audio / Visual
    AmericanExpress(
        title = Res.string.partner_express,
        description = Res.string.partner_express_description,
        logo = Res.drawable.express,
        type = Type.AudioVisual
    ),
    Android(
        title = Res.string.partner_android,
        description = Res.string.partner_android_description,
        logo = Res.drawable.android,
        type = Type.AudioVisual
    ),

    // Media
    Pretix(
        title = Res.string.partner_pretix,
        description = Res.string.partner_pretix_description,
        logo = Res.drawable.pretix,
        type = Type.Media
    ),
    StickerMule(
        title = Res.string.partner_sticker_mule,
        description = Res.string.partner_sticker_mule_description,
        logo = Res.drawable.stickermule,
        type = Type.Media,
    ),
    KotlinWeekly(
        title = Res.string.partner_kt_weekly,
        description = Res.string.partner_kt_weekly_description,
        logo = Res.drawable.kt_weekly,
        type = Type.Media
    ),
    KtAcademy(
        title = Res.string.partner_academy,
        description = Res.string.partner_academy_description,
        logo = Res.drawable.academy,
        type = Type.Media
    ),

    // GameZone
    Shape(
        title = Res.string.partner_shape,
        description = Res.string.partner_shape_description,
        logo = Res.drawable.shape,
        type = Type.GameZone
    ),

    // Supporter
    KotlinFoundation(
        title = Res.string.partner_foundation,
        description = Res.string.partner_foundation_description,
        logo = Res.drawable.foundation,
        type = Type.Supporter
    ),
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Partners(showPartner: (Partner) -> Unit, back: () -> Unit) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.whiteGrey)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        NavigationBar(
            title = "Partners",
            isLeftVisible = true,
            onLeftClick = { back() },
            isRightVisible = false
        )
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
        ) {
            SectionTitle("Gold")
            PartnerGrid(showPartner, Partner.Google, Partner.Monta)
            SectionTitle("Silver")

            PartnerGrid(
                showPartner,
                Partner.Gradle,
                Partner.Sentry,
                Partner.TouchLab,
                Partner.CloudInject,
            )
            SectionTitle("Bronze")
            PartnerGrid(
                showPartner,
                Partner.Uber,
                Partner.Kodein,
                Partner.Mercari,
                Partner.WorldLine,
                Partner.BTSystems
            )
            SectionTitle("Audio/visual")
            PartnerGrid(showPartner, Partner.AmericanExpress, Partner.Android)
            SectionTitle("Media")
            PartnerGrid(
                showPartner,
                Partner.Pretix,
                Partner.StickerMule,
                Partner.KotlinWeekly,
                Partner.KtAcademy
            )
            SectionTitle("Game Zone")
            PartnerGrid(showPartner, Partner.Shape)
            SectionTitle("Supporter")
            PartnerGrid(showPartner, Partner.KotlinFoundation)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().height(74.dp).background(MaterialTheme.colors.grey5Black),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.greyWhite),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        HDivider()
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun RowScope.PartnerCard(partner: Partner, onClick: (partner: Partner) -> Unit) {
    Box(
        Modifier.background(MaterialTheme.colors.whiteGrey).fillMaxWidth().weight(1f)
            .clickable { onClick(partner) }.height(160.dp), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = partner.logo.painter(), contentDescription = "image"
        )
    }
}

@Composable
private fun PartnerGrid(onClick: (Partner) -> Unit, vararg partners: Partner) {
    var index = 0
    val rowsCount = partners.size / 2 + partners.size % 2
    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(rowsCount) {
            Row(Modifier.fillMaxWidth()) {
                PartnerCard(partners[index++], onClick)
                VDivider(Modifier.height(160.dp))
                if (index < partners.size) {
                    PartnerCard(partners[index++], onClick)
                } else {
                    Box(Modifier.fillMaxWidth().weight(1f))
                }
            }
            HDivider()
        }
    }
}
