package com.example.tvshowreminder.screen.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tvshowreminder.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager.apply {
            findFragmentById(R.id.setting_fragment_container) as SettingsFragment?
                ?: beginTransaction().replace(R.id.setting_fragment_container, SettingsFragment()).commit()
        }
    }
}
