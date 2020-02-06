package com.example.tvshowreminder.di

import android.app.Application
import android.content.Context
import com.example.tvshowreminder.TvShowApplication
import com.example.tvshowreminder.backgroundwork.BackgroundWorker
import com.example.tvshowreminder.screen.detail.DetailFragment
import com.example.tvshowreminder.screen.detail.tabsfragments.SeasonsFragment
import com.example.tvshowreminder.screen.main.MainActivity
import com.example.tvshowreminder.screen.main.MainFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, ViewModelModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(mainFragment: MainFragment)
    fun inject(detailFragment: DetailFragment)
    fun inject(seasonFragment: SeasonsFragment)
    fun inject(backgroundWorker: BackgroundWorker)
}