package com.example.tvshowreminder.screen.detail.tabsfragments.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.tvshowreminder.R
import com.example.tvshowreminder.TvShowApplication
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.screen.detail.tabsfragments.DescriptionFragment
import com.example.tvshowreminder.screen.detail.tabsfragments.NewEpisodeFragment
import com.example.tvshowreminder.screen.detail.tabsfragments.SeasonsFragment

class TabFragmentPageAdapter(
    fragmentManager: FragmentManager,
    val tvShowDetails: TvShowDetails
): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment = when (position){
        0 -> DescriptionFragment().newInstance(tvShowDetails)
        1 ->  tvShowDetails.nextEpisodeToAir?.let {
                NewEpisodeFragment().newInstance(it)
            } ?: NewEpisodeFragment()
        else -> SeasonsFragment().newInstance(tvShowDetails.id, tvShowDetails.numberOfSeasons ?: 0)
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence?  = when (position){
        0 -> TvShowApplication.context.getString(R.string.title_description)
        1 -> TvShowApplication.context.getString(R.string.title_next_episode)
        else -> TvShowApplication.context.getString(R.string.title_season)
    }
}