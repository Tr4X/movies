package com.tr4x.movies.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tr4x.movies.model.Movie

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<Movie>)

    @Query(
        "SELECT * FROM movies"
    )
    fun movies(): PagingSource<Int, Movie>

    @Query("DELETE FROM movies")
    suspend fun clearMovies()
}