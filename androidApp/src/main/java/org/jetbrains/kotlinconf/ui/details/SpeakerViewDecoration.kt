package org.jetbrains.kotlinconf.ui.details

import android.graphics.*
import android.view.*
import androidx.recyclerview.widget.*
import org.jetbrains.kotlinconf.*

class SpeakerViewDecoration(
    private val spacing: Int = 8
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = spacing.dp
        outRect.top = spacing.dp
    }
}
