package com.tr4x.movies.api

import com.google.gson.annotations.SerializedName
import com.tr4x.movies.model.Movie

class MovieResponse {
    @SerializedName("movies") val items: List<Movie> = emptyList()
}