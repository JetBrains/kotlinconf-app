package org.jetbrains.kotlinconf.ui.details

import androidx.fragment.app.*

internal class PagerAdapter(
    private val fragments: List<Fragment>,
    manager: FragmentManager
) : FragmentPagerAdapter(manager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getCount(): Int = fragments.size
    override fun getItem(position: Int): Fragment = fragments[position]
}
