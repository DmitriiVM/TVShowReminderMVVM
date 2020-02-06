package com.example.tvshowreminder.screen.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tvshowreminder.R
import com.example.tvshowreminder.util.INTENT_EXTRA_TV_SHOW_ID
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val tvShowId = intent.getIntExtra(INTENT_EXTRA_TV_SHOW_ID, 0)

        supportFragmentManager.apply {
            findFragmentById(R.id.fragment_container_detail) as DetailFragment?
                ?: beginTransaction()
                    .replace(R.id.fragment_container_detail, DetailFragment.newInstance(tvShowId))
                    .commit()
        }

        image_view_navigate_back.setOnClickListener {
            onBackPressed()
        }
    }
}
