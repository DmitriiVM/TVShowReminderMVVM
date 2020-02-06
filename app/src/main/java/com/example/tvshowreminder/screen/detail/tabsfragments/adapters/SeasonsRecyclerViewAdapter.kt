package com.example.tvshowreminder.screen.detail.tabsfragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshowreminder.R
import com.example.tvshowreminder.data.pojo.season.SeasonDetails
import kotlinx.android.synthetic.main.season_item.view.*

class SeasonsRecyclerViewAdapter : RecyclerView.Adapter<SeasonsRecyclerViewAdapter.SeasonsViewHolder>() {

    private var seasonsList: List<SeasonDetails> = mutableListOf()

    fun setSeasonsList(newSeasonsList: List<SeasonDetails>){
        seasonsList = newSeasonsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonsViewHolder = SeasonsViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.season_item, parent, false)
    )

    override fun getItemCount(): Int = seasonsList.size

    override fun onBindViewHolder(holder: SeasonsViewHolder, position: Int) {
        holder.onBind(seasonsList[position])
    }

    class SeasonsViewHolder(val view: View): RecyclerView.ViewHolder(view){
        fun onBind(seasonDetails: SeasonDetails) {

            var isOpen = false

            view.apply {
                text_view_season_name.text = seasonDetails.name
                recycler_view_seasons.apply {
                    layoutManager = LinearLayoutManager(view.context)
                    seasonDetails.episodes?.let {
                        val sortedList = it.sortedBy {
                            episode -> episode.episodeNumber
                        }
                        adapter = EpisodesRecyclerViewAdapter(sortedList)
                    }
                    visibility = View.GONE
                }
                setOnClickListener {
                    if (isOpen){
                        view.recycler_view_seasons.visibility = View.GONE
                        view.image_view_open.setImageResource(R.drawable.ic_add)
                        isOpen = false
                    } else {
                        view.recycler_view_seasons.visibility = View.VISIBLE
                        view.image_view_open.setImageResource(R.drawable.ic_remove)
                        isOpen = true
                    }
                }
            }
        }
    }
}