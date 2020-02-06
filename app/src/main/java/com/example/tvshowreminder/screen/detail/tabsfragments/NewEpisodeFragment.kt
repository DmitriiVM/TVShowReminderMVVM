package com.example.tvshowreminder.screen.detail.tabsfragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.tvshowreminder.R
import com.example.tvshowreminder.data.pojo.episode.NextEpisodeToAir
import com.example.tvshowreminder.util.KEY_NEXT_EPISODE
import kotlinx.android.synthetic.main.fragment_new_episode.*

class NewEpisodeFragment : Fragment() {

    var nextEpisodeToAir: NextEpisodeToAir? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nextEpisodeToAir = it.getParcelable(KEY_NEXT_EPISODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_episode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextEpisodeToAir?.apply {
            text_view_name.text = nameNextEpisode
            text_view_season_number.text = seasonNumber.toString()
            text_view_episode_number.text = episodeNumber.toString()
            text_view_air_date.text = airDate
            text_view_overview_next_episode.text = overviewNextEpisode
        }
    }

    fun newInstance(nextEpisodeToAir: NextEpisodeToAir): NewEpisodeFragment {
        return NewEpisodeFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_NEXT_EPISODE, nextEpisodeToAir)
            }
        }
    }
}
