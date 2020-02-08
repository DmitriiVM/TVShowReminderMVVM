package com.example.tvshowreminder.screen.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.tvshowreminder.R
import com.example.tvshowreminder.screen.settings.SettingsActivity
import com.example.tvshowreminder.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_layout.view.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var searchView: SearchView
    private var query: String? = null
    private var menuItemId = R.id.menu_item_popular
    private var isRestored = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        restoreState(savedInstanceState)

        bottom_nav_view.setOnNavigationItemSelectedListener(this)
        bottom_nav_view.selectedItemId = menuItemId

        isRestored = false
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            isRestored = true
            query = savedInstanceState.getString(KEY_QUERY)
            menuItemId = savedInstanceState.getInt(KEY_MENU_ITEM_ID)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        menuItemId = item.itemId
        when (item.itemId) {
            R.id.menu_item_popular -> showFragment(FRAGMENT_POPULAR, null)
            R.id.menu_item_latest -> showFragment(FRAGMENT_LATEST, null)
            R.id.menu_item_shows_to_follow -> showFragment(FRAGMENT_FAVOURITE, null)
        }
        return true
    }

    private fun showFragment(fragmentType: String, query: String?) {
        if (!isRestored) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment.newInstance(fragmentType, query))
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        val searchItem = menu?.findItem(R.id.app_bar_search)
        searchView = searchItem?.actionView as SearchView

        if (!query.isNullOrBlank()) {
            searchItem.expandActionView()
            searchView.setQuery(query, false)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query1: String?): Boolean {
                searchItem.collapseActionView()
                query1?.let {
                    query = query1
                    if (menuItemId == R.id.menu_item_shows_to_follow){
                        showFragment(FRAGMENT_SEARCH_IN_FAVOURITE, query1)
                    } else {
                        showFragment(FRAGMENT_SEARCH, query1)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.popup_menu_settings -> showSettings()
            R.id.popup_menu_about -> showAboutInfo()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_SETTINGS)
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
}