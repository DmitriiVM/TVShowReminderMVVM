package com.example.tvshowreminder.screen.main

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.example.tvshowreminder.R
import com.example.tvshowreminder.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_layout.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow

class MainActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private var query: String? = null
    private var menuItemId = R.id.menu_item_popular
    private var isRestored = false
    private var isAfterSearch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        restoreState(savedInstanceState)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setBottomNavigation()
        } else {
            setNavigationDrawer()
        }
        isRestored = false
    }

    @Suppress("PLUGIN_WARNING")
    private fun setNavigationDrawer() {
        nav_view.setNavigationItemSelectedListener { item ->
            drawer.closeDrawers()
            setNavigation(item.itemId)
        }
        val toogle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toogle)
        toogle.syncState()
        showFragment(FRAGMENT_POPULAR, null)
        nav_view.setCheckedItem(menuItemId)
    }

    @Suppress("PLUGIN_WARNING")
    private fun setBottomNavigation() {
        bottom_nav_view.setOnNavigationItemSelectedListener { item ->
            setNavigation(item.itemId)
        }
        showFragment(FRAGMENT_POPULAR, null)
        bottom_nav_view.selectedItemId = menuItemId
    }

    private fun setNavigation(item: Int): Boolean {
        if (menuItemId == item && !isAfterSearch) return true
        isAfterSearch = false
        menuItemId = item
        when (item) {
            R.id.menu_item_popular -> showFragment(FRAGMENT_POPULAR, null)
            R.id.menu_item_latest -> showFragment(FRAGMENT_LATEST, null)
            R.id.menu_item_shows_to_follow -> showFragment(FRAGMENT_FAVOURITE, null)
        }
        return true
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            isRestored = true
            query = savedInstanceState.getString(KEY_QUERY)
            menuItemId = savedInstanceState.getInt(KEY_MENU_ITEM_ID)
        }
    }

    private fun showFragment(fragmentType: String, query: String?) {
        if (!isRestored) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment.newInstance(fragmentType, query))
                .commit()
        }
    }

    @InternalCoroutinesApi
    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        val searchItem = menu?.findItem(R.id.app_bar_search)
        searchView = searchItem?.actionView as SearchView

        if (!query.isNullOrBlank()) {
            searchItem.expandActionView()
            searchView.setQuery(query, false)
        }

        val flow = callbackFlow<String> {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        offer(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        offer(newText)
                    }
                    return true
                }

            })
            awaitClose()
        }
        lifecycleScope.launchWhenResumed {
            flow.debounce(400).collect { currentQuery ->
                if (currentQuery.isEmpty()) return@collect
                Log.d("mmm", "MainActivity :  onCreate --  $currentQuery")
                query = currentQuery
                if (menuItemId == R.id.menu_item_shows_to_follow) {
                    showFragment(FRAGMENT_SEARCH_IN_FAVOURITE, currentQuery)
                } else {
                    showFragment(FRAGMENT_SEARCH, currentQuery)
                }
                isAfterSearch = true
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.popup_menu_about -> showAboutInfo()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAboutInfo() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_layout, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .show()
        dialogView.button_dialog.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_QUERY, searchView.query.toString())
        outState.putInt(KEY_MENU_ITEM_ID, menuItemId)
    }

    @Suppress("PLUGIN_WARNING")
    override fun onBackPressed() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
            drawer.isDrawerOpen(GravityCompat.START)
        ) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}