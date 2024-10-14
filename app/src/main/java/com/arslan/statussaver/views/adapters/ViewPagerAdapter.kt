package com.arslan.statussaver.views.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arslan.statussaver.downloadedStatuses.SavedStatusesFragment
import com.arslan.statussaver.views.fragments.FragmentWhatsappBusiness
import com.arslan.statussaver.directMessage.fragments.DirectChatFragment
import com.arslan.statussaver.views.fragments.FragmentWhatsAppStatus

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    private val fragmentList = listOf(
        FragmentWhatsAppStatus(),
        FragmentWhatsappBusiness(),
        SavedStatusesFragment(),
        DirectChatFragment()
    )

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]
}