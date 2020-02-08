package com.example.tvshowreminder.screen.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tvshowreminder.R
import com.example.tvshowreminder.TvShowApplication
import com.example.tvshowreminder.data.pojo.general.TvShow
import com.example.tvshowreminder.screen.detail.DetailActivity
import com.example.tvshowreminder.util.*
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class MainFragment : Fragment(),
    TvShowListAdapter.OnTvShowClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    private lateinit var adapter: TvShowListAdapter
    private var toast: Toast? = null
    private var fragmentType: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as TvShowApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentType = arguments?.getString(FRAGMENT_TYPE)
        val query = arguments?.getString(QUERY)

        setRecyclerView()


        var isRequiredToLoad: Boolean = true
        if (savedInstanceState != null){
            isRequiredToLoad = false
        }

        when (fragmentType) {
            FRAGMENT_POPULAR -> subscribe(
                viewModel.getPopularTvShowList(isRequiredToLoad)
            )
            FRAGMENT_LATEST -> subscribe(
                viewModel.getLatestTvShowList(isRequiredToLoad)
            )
            FRAGMENT_FAVOURITE -> subscribe(
                    viewModel.getFavouriteTvShowList(isRequiredToLoad)
                )
            FRAGMENT_SEARCH -> query?.let {
                subscribe(
                    viewModel.searchTvShowsList(query, isRequiredToLoad)
                )
            }
            FRAGMENT_SEARCH_IN_FAVOURITE -> query?.let {
                subscribe(
                    viewModel.searchTvShowsListInFavourite(query, isRequiredToLoad)
                )
            }
        }
    }

    private fun subscribe(liveData: LiveData<Resource<PagedList<TvShow>>>) {
        liveData
            .observe(viewLifecycleOwner, Observer { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        progress_bar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        progress_bar.visibility = View.INVISIBLE
                        adapter.submitList(resource.data)

                    }
                    is Resource.SuccessWithMessage -> {
                        progress_bar.visibility = View.INVISIBLE
                        adapter.submitList(resource.data)
                        showMessage(resource.networkErrorMessage)
                    }
                    is Resource.Error -> {
                        progress_bar.visibility = View.INVISIBLE
                        showMessage(resource.message)
                    }
                }
            })
    }

    private fun setRecyclerView() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recycler_view.layoutManager = LinearLayoutManager(requireContext())
        } else {
            recycler_view.layoutManager = GridLayoutManager(requireContext(), 2)
        }
        adapter = TvShowListAdapter()
        recycler_view.adapter = adapter
        recycler_view.itemAnimator = null
        adapter.setOnShowClickListener(this)
    }

    override fun onTvShowClick(tvId: Int) {
        if (ConnectivityHelper.isOnline(requireContext())  || fragmentType == FRAGMENT_FAVOURITE) {
            startDetailActivity(tvId)
        } else {
            showMessage(MESSAGE_DETAILS_WITH_NO_INTERNET)
        }
    }

    private fun showMessage(message: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast?.apply {
            setGravity(Gravity.CENTER, 0, 300)
            show()
        }
    }

    private fun startDetailActivity(tvId: Int) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra(INTENT_EXTRA_TV_SHOW_ID, tvId)
        startActivity(intent)
    }

    companion object {
        fun newInstance(fragmentType: String, query: String?): Fragment{
            val fragment = MainFragment()
            fragment.arguments = Bundle().apply {
                putString(FRAGMENT_TYPE, fragmentType)
                putString(QUERY, query)
            }
            return fragment
        }
    }
}