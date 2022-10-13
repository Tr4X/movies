package com.tr4x.movies

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.tr4x.movies.api.MovieService
import com.tr4x.movies.data.MovieRepository
import com.tr4x.movies.db.MovieDatabase
import com.tr4x.movies.ui.ViewModelFactory


object Injection {
    private fun provideGithubRepository(context: Context): MovieRepository {
        return MovieRepository(MovieService.create(), MovieDatabase.getInstance(context))
    }

    fun provideViewModelFactory(
        context: Context,
        owner: SavedStateRegistryOwner
    ): ViewModelProvider.Factory {
        return ViewModelFactory(owner, provideGithubRepository(context))
    }
}