package com.example.tvshowreminder.screen.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tvshowreminder.R
import com.example.tvshowreminder.screen.detail.DetailActivity
import com.example.tvshowreminder.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_main.*
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tvshowreminder.TvShowApplication
import com.example.tvshowreminder.screen.settings.SettingsActivity
import kotlinx.android.synthetic.main.dialog_layout.view.*
import javax.inject.Inject

class MainFragment : Fragment(),
    TvShowListAdapter.OnTvShowClickListener,
    BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    private lateinit var adapter: TvShowListAdapter
    private lateinit var searchView: SearchView
    private var menuItemId = R.id.menu_item_popular
    private var toast: Toast? = null
    private var query : String? = null
    private var afterSearch = false
    private var isSearching = false

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

        (requireActivity() as MainActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        setRecyclerView()
        bottom_nav_view.setOnNavigationItemSelectedListener(this)

        if (savedInstanceState == null){
            subscribeObservers(true)
        } else {
            query = savedInstanceState.getString(KEY_QUERY)
            menuItemId = savedInstanceState.getInt(KEY_MENU_ITEM_ID)
            subscribeObservers(false)
        }
    }

    private fun subscribeObservers(isRequiredToLoad: Boolean){
        when (menuItemId) {
            R.id.menu_item_popular -> {
                subscribeForPopularTvShows(isRequiredToLoad)
            }
            R.id.menu_item_latest -> {
                subscribeForLatestTvShows(isRequiredToLoad)
            }
            R.id.menu_item_shows_to_follow -> {
                subscribeForFavouriteTvShows(isRequiredToLoad)
            }
        }
    }

    private fun setRecyclerView() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        } else {
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        }
        adapter = TvShowListAdapter()
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
        adapter.setOnShowClickListener(this)
    }

    private fun subscribeForPopularTvShows(isRequiredToLoad: Boolean) {
        viewModel.getPopularTvShowList(isRequiredToLoad).observe(viewLifecycleOwner, Observer { resource ->
            when (resource){
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.INVISIBLE
                    adapter.submitList(resource.data)

                }
                is Resource.SuccessWithMessage -> {
                    progressBar.visibility = View.INVISIBLE
                    adapter.submitList(resource.data)
                    showMessage(resource.networkErrorMessage)
                }
            }
        })
    }

    private fun subscribeForLatestTvShows(isRequiredToLoad: Boolean){
        viewModel.getLatestTvShowList(isRequiredToLoad).observe(viewLifecycleOwner, Observer { resource ->
            when (resource){
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.INVISIBLE
                    adapter.submitList(resource.data)

                }
                is Resource.SuccessWithMessage -> {
                    progressBar.visibility = View.INVISIBLE
                    adapter.submitList(resource.data)
                    showMessage(resource.networkErrorMessage)
                }
            }
        })
    }

    private fun subscribeForFavouriteTvShows(isRequiredToLoad: Boolean){
        viewModel.getFavouriteTvShowList(isRequiredToLoad).observe(viewLifecycleOwner, Observer { resource ->
            when (resource){
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.INVISIBLE
                    if (menuItemId == R.id.menu_item_shows_to_follow){
                        adapter.submitList(resource.data)
                    }
                }
            }
        })
    }

    private fun subscribeForSearchResult(query: String){
        isSearching = true
        viewModel.searchTvShowsList(query).observe(viewLifecycleOwner, Observer { resource ->
            when (resource){
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.INVISIBLE
                    adapter.submitList(resource.data)
                    isSearching = false
                }
            }
        })
    }

    private fun subscribeForFavouriteSearchResult(query: String){
        viewModel.searchTvShowsListInFavourite(query).observe(viewLifecycleOwner, Observer { resource ->
            when (resource){
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.INVISIBLE
                    adapter.submitList(resource.data)
                }
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (menuItemId != item.itemId || afterSearch){
            afterSearch = false
            menuItemId = item.itemId
            when (menuItemId){
                R.id.menu_item_popular -> {
                    subscribeForPopularTvShows(true)
                }
                R.id.menu_item_latest -> {
                    subscribeForLatestTvShows(true)
                }
                R.id.menu_item_shows_to_follow -> {
                    subscribeForFavouriteTvShows(true)
                }
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)

        val searchItem = menu.findItem(R.id.app_bar_search)
        searchView = searchItem.actionView as SearchView

        if (!query.isNullOrBlank()) {
                searchItem.expandActionView()
                searchView.setQuery(query, false)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchItem.collapseActionView()
                query?.let {
                    if (menuItemId == R.id.menu_item_shows_to_follow){
                        subscribeForFavouriteSearchResult(it)
                    } else {
                        subscribeForSearchResult(it)
                    }
                }
                afterSearch = true
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.popup_menu_settings -> showSettings()
            R.id.popup_menu_about -> showAboutInfo()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSettings() {
        val intent = Intent(requireContext(), SettingsActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_SETTINGS)
    }

    private fun showAboutInfo() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .show()
        dialogView.button_dialog.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onTvShowClick(tvId: Int) {
        if (ConnectivityHelper.isOnline(requireContext())) {
            startDetailActivity(tvId)
        } else {
            if (menuItemId == R.id.menu_item_shows_to_follow) {
                startDetailActivity(tvId)
            } else
                showMessage(MESSAGE_DETAILS_WITH_NO_INTERNET)
        }
    }

    private fun startDetailActivity(tvId: Int){
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra(INTENT_EXTRA_TV_SHOW_ID, tvId)
        startActivity(intent)
    }

    private fun showMessage(message: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast?.apply {
            setGravity(Gravity.CENTER, 0, 300)
            show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_MENU_ITEM_ID, menuItemId)
        outState.putString(KEY_QUERY, searchView.query.toString())
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}