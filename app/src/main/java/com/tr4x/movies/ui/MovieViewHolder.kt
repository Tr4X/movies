package com.tr4x.movies.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tr4x.movies.R
import com.tr4x.movies.model.Movie

class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val title: TextView = view.findViewById(R.id.title)
    private val year: TextView = view.findViewById(R.id.year)
    private val director: TextView = view.findViewById(R.id.director)
    private val poster: ImageView = view.findViewById(R.id.poster)

    private var movie: Movie? = null

    init {
        view.setOnClickListener {
            movie?.posterUrl?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                view.context.startActivity(intent)
            }
        }
    }

    fun bind(movie: Movie?) {
        if (movie == null) {
            val resources = itemView.resources
            title.text = resources.getString(R.string.loading)
            year.text = resources.getString(R.string.unknown)
            director.text = resources.getString(R.string.unknown)
        } else {
            showRepoData(movie)
        }
    }

    private fun showRepoData(movie: Movie) {
        this.movie = movie
        title.text = movie.title
        year.text = movie.year.toString()
        director.text = movie.director

        Picasso.get()
            .load(movie.posterUrl)
            .placeholder(R.drawable.ic_baseline_image_24)
            .error(R.drawable.ic_baseline_error_24)
            .into(poster);

    }

    companion object {
        fun create(parent: ViewGroup): MovieViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_view_item, parent, false)
            return MovieViewHolder(view)
        }
    }
}
