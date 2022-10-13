package com.tr4x.movies.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.tr4x.movies.api.MovieService
import com.tr4x.movies.db.MovieDatabase
import com.tr4x.movies.model.Movie
import retrofit2.HttpException
import java.io.IOException


@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val service: MovieService,
    private val repoDatabase: MovieDatabase
) : RemoteMediator<Int, Movie>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Movie>): MediatorResult {
        try {
            val apiResponse = service.getMovies()

            val repos = apiResponse.items
            val endOfPaginationReached = repos.isEmpty()
            repoDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    repoDatabase.moviesDao().clearMovies()
                }
                repoDatabase.moviesDao().insertAll(repos)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }
}