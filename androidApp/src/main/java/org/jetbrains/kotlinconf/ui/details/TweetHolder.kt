package org.jetbrains.kotlinconf.ui.details

import android.content.*
import android.net.*
import android.view.*
import androidx.recyclerview.widget.*
import com.bumptech.glide.*
import kotlinx.android.synthetic.main.view_tweet_card.view.*
import org.jetbrains.kotlinconf.*
import org.jetbrains.kotlinconf.presentation.*

class TweetHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun showPost(post: FeedPost) {
        view.setOnClickListener {
            val url = buildString {
                append("https://twitter.com/")
                append(post.user.id_str)
                append("/status/")
                append(post.id_str)
            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            view.context.startActivity(intent)
        }
        with(view) {
            Glide.with(view)
                .load(post.user.profile_image_url_https)
                .into(tweet_avatar)

            tweet_account.text = "@${post.user.screen_name}"
            tweet_name.text = post.user.name
            tweet_text.text = post.text
            tweet_time.text = post.displayDate()

            val photoUrl = post.entities.media.map { it.media_url_https }.firstOrNull()
            if (photoUrl != null) {
                Glide.with(view)
                    .load(photoUrl)
                    .into(tweet_photo)
            } else {
                tweet_photo.setImageResource(android.R.color.transparent)
            }
        }
    }
}
