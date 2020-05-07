package com.example.tvshowreminder.screen.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tvshowreminder.R
import com.example.tvshowreminder.TvShowApplication
import com.example.tvshowreminder.backgroundwork.cancelAlarm
import com.example.tvshowreminder.backgroundwork.setAlarm
import com.example.tvshowreminder.data.pojo.general.TvShowDetails
import com.example.tvshowreminder.screen.detail.tabsfragments.adapters.TabFragmentPageAdapter
import com.example.tvshowreminder.util.*
import kotlinx.android.synthetic.main.fragment_detail.*
import javax.inject.Inject

class DetailFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<DetailsViewModel> { viewModelFactory }

    private var tvShowDetails: TvShowDetails? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as TvShowApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvShowId = arguments?.getInt(TV_SHOW_ID)

        button_add_delete.isEnabled = false

        tvShowId?.let {
            if (savedInstanceState == null){
                subscribeObserver(it, false)
            } else {
                subscribeObserver(it, true)
                handleProcessDeath(it, savedInstanceState)
            }
        }
    }

    private fun handleProcessDeath(tvShowId: Int, savedInstanceState: Bundle){
        val success = savedInstanceState.getParcelable<TvShowDetails>(KEY_SUCCESS_STATE)
        success?.let {
            viewModel.detailsResult.value = Resource.Success(it)
        }
    }

    private fun subscribeObserver(tvId: Int, isRestored: Boolean) {
        viewModel.getTvShowDetails(tvId, isRestored).observe(viewLifecycleOwner, Observer { resource ->
            when (resource){
                is Resource.Loading -> {
                    progress_bar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    progress_bar.visibility = View.INVISIBLE
                    tvShowDetails = resource.data
                    displayTvShow(resource.data)
                    checkForButtonFunction(resource.data.id)
                }
            }
        })
    }

    private fun checkForButtonFunction(tvShowId: Int){
        viewModel.isPresentInList(tvShowId).observe(viewLifecycleOwner, Observer {
            if (it){
                setButtonWithDeleteFunction()
            } else {
                setButtonWithAddFunction()
            }
        })
    }

    private fun setButtonWithAddFunction() {
        button_add_delete.apply {
            text = getString(R.string.button_add)
            isEnabled = true
            setOnClickListener {
                val tvShow = tvShowDetails
                tvShow?.let {
                    viewModel.insertTvShow(it)
                    requireActivity().setAlarm(it)
                    setButtonWithDeleteFunction()
                }
            }
        }
    }

     private fun setButtonWithDeleteFunction() {
        button_add_delete.apply {
            text = getString(R.string.button_delete)
            isEnabled = true
            setOnClickListener {
                val tvShow = tvShowDetails
                tvShow?.let {
                    viewModel.deleteTvShow(it)
                    requireActivity().cancelAlarm(tvShow.id)
                    setButtonWithAddFunction()
                }
            }
        }
    }

    private fun displayTvShow(tvShowDetails: TvShowDetails) {
        text_view_title.text = tvShowDetails.name
        rating_bar.progress = tvShowDetails.voteAverage?.toInt() ?: 0
        image_view_tvshow_detail.setImage(tvShowDetails.backdropPath ?: "", ErrorImageOrientation.HORIZONTAL)

        view_pager.adapter =
            TabFragmentPageAdapter(
                requireActivity().supportFragmentManager,
                tvShowDetails
            )
        tab_layout.setupWithViewPager(view_pager)
    }

    fun showError(errorMessage: String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_SUCCESS_STATE, (viewModel.detailsResult.value as Resource.Success<TvShowDetails>).data)
    }

    companion object {
        fun newInstance(tvShowId: Int): Fragment {
            val fragment = DetailFragment()
            fragment.arguments = Bundle().apply {
                putInt(TV_SHOW_ID, tvShowId)
            }
            return fragment
        }
    }
}
