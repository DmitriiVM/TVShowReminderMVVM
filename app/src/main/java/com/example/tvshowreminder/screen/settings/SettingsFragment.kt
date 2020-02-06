package com.example.tvshowreminder.screen.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.tvshowreminder.R

class SettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}