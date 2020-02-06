package com.example.tvshowreminder.screen.detail.tabsfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.tvshowreminder.R
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.util.KEY_TV_SHOW_DETAIL
import com.example.tvshowreminder.util.TEXT_NO
import com.example.tvshowreminder.util.TEXT_YES
import kotlinx.android.synthetic.main.fragment_description.*


class DescriptionFragment : Fragment() {

    var tvShowDetails : TvShowDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tvShowDetails = it.getParcelable(KEY_TV_SHOW_DETAIL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvShowDetails?.apply {
            inProduction?.let {
                text_view_in_production.text = if (it) TEXT_YES else TEXT_NO
            }
            text_view_first_air_date.text = firstAirDate
            text_view_homepage.text = homepage
            text_view_overview.text = "\t$overview"
        }
    }

    fun newInstance(tvShowDetails: TvShowDetails): DescriptionFragment {
        return DescriptionFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_TV_SHOW_DETAIL, tvShowDetails)
            }
        }
    }
}
