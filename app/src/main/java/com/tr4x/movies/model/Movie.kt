package com.tr4x.movies.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movies")
data class Movie (
    @PrimaryKey @field:SerializedName("title") val title: String,
    @field:SerializedName("year") val year: Long,
    @field:SerializedName("director") val director: String,
    @field:SerializedName("posterUrl") val posterUrl: String,
)