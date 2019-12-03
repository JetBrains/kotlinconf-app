package org.jetbrains.kotlinconf.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_partner.*
import android.content.Intent
import android.net.Uri
import org.jetbrains.kotlinconf.R
import org.jetbrains.kotlinconf.*

internal val PARTNER_LOGOS = mapOf(
    "android" to R.drawable.logo_android_big,
    "47" to R.drawable.logo_47_big,
    "freenow" to R.drawable.logo_freenow_big,
    "bitrise" to R.drawable.logo_bitrise_big,
    "instill" to R.drawable.logo_instl_big,
    "gradle" to R.drawable.logo_gradle_big,
    "n26" to R.drawable.logo_n26_big,
    "kodein" to R.drawable.logo_kodein_big,
    "badoo" to R.drawable.logo_badoo,
    "jetbrains" to R.drawable.logo_jetbrains
)

class PartnerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_partner)

        val partnerName = intent.getStringExtra("partner") ?: return
        displayPartner(partnerName)
    }

    private fun displayPartner(name: String) {
        PARTNER_LOGOS[name]?.let {
            partner_logo.setImageResource(it)
        }

        val partner = Partners.partner(name) ?: return
        partner_name.text = partner.title
        partner_description.text = Partners.descriptionByName(name)
        partner_link.text = partner.url

        partner_link.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(partner.url))
            startActivity(browserIntent)
        }
    }
}
