package com.example.tvshowreminder.screen.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshowreminder.R
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.util.ErrorImageOrientation
import com.example.tvshowreminder.util.setImage
import kotlinx.android.synthetic.main.tvshow_item.view.*

class TvShowListAdapter: PagedListAdapter<TvShow, TvShowListAdapter.TvShowViewHolder>(
    tvShowDiffCallback
) {

    interface OnTvShowClickListener {
        fun onTvShowClick(tvId: Int)
    }

    private var onTvShowClickListener: OnTvShowClickListener? = null

    fun setOnShowClickListener(onTvShowClickListener: OnTvShowClickListener) {
        this.onTvShowClickListener = onTvShowClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowViewHolder =
        TvShowViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.tvshow_item, parent, false))

    override fun onBindViewHolder(holder: TvShowViewHolder, position: Int){
        getItem(position)?.let {
            holder.onBind(it)
        }
    }

    inner class TvShowViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun onBind(tvShow: TvShow) {
            view.apply {
                image_view_compact_type.setImage(tvShow.posterPath ?: "", ErrorImageOrientation.VERTICAL)
                text_view_title.text = tvShow.name
                text_view_raiting.text = tvShow.voteAverage.toString()
                text_view_popularuty.text = tvShow.popularity?.toInt().toString()
                setOnClickListener {
                    onTvShowClickListener?.onTvShowClick(tvShow.id)
                }
            }
        }
    }

    companion object{
        private val tvShowDiffCallback = object : DiffUtil.ItemCallback<TvShow>(){

            override fun areItemsTheSame(oldItem: TvShow, newItem: TvShow) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: TvShow, newItem: TvShow) = oldItem == newItem
        }
    }
}