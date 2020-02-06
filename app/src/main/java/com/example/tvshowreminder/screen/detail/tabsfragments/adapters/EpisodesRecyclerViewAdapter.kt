package com.example.tvshowreminder.screen.detail.tabsfragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshowreminder.R
import com.example.tvshowreminder.data.pojo.episode.Episode
import kotlinx.android.synthetic.main.episode_item.view.*

class EpisodesRecyclerViewAdapter(
    private val episodesList: List<Episode>
) : RecyclerView.Adapter<EpisodesRecyclerViewAdapter.EpisodeViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder = EpisodeViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.episode_item, parent, false)
    )

    override fun getItemCount(): Int = episodesList.size

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        holder.onBind(episodesList[position])
    }

    class EpisodeViewHolder(val view: View): RecyclerView.ViewHolder(view){
        fun onBind(episode: Episode) {
            view.text_view_episode_name.text = "${episode.episodeNumber}. ${episode.name}"
            view.text_view_episode_date.text = episode.airDate
        }

    }
}