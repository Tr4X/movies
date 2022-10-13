package com.tr4x.movies.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.tr4x.movies.api.MovieService
import com.tr4x.movies.db.MovieDatabase
import com.tr4x.movies.model.Movie
import kotlinx.coroutines.flow.Flow

class MovieRepository(
    private val service: MovieService,
    private val database: MovieDatabase
) {

    fun getSearchResultStream(): Flow<PagingData<Movie>> {
        Log.d("MovieRepository", "New query")

        val pagingSourceFactory = { database.moviesDao().movies() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = MovieRemoteMediator(
                service,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }
}