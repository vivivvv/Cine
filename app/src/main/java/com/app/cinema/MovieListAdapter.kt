package com.app.cinema

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.cinema.model.Search
import java.util.concurrent.Executors

class MovieListAdapter : RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder>() {

    private var movieList = ArrayList<Search>()

    @SuppressLint("NotifyDataSetChanged")
    fun setMovieList(
        movieList: ArrayList<Search>
    ) {
        this.movieList = movieList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder {
        // Initialize view
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.movie_card_view, parent, false
        )
        // Pass holder view
        return MovieListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {
        // Set text on radio button
        holder.setData(position)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    inner class MovieListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Assign variable
        var movieImage: ImageView = view.findViewById(R.id.movie_imageview)
        var movieTitleText: TextView = view.findViewById(R.id.movie_title)
        var yearText: TextView = view.findViewById(R.id.year)

        fun setData(position: Int) {
            movieTitleText.text = movieList[position].Title
            yearText.text = movieList[position].Year
            // Declaring executor to parse the URL
            val executor = Executors.newSingleThreadExecutor()
            // Once the executor parses the URL
            // and receives the image, handler will load it
            // in the ImageView
            val handler = Handler(Looper.getMainLooper())
            // Initializing the image
            var image: Bitmap? = null
            // Only for Background process (can take time depending on the Internet speed)
            executor.execute {
                // Image URL
                val imageURL = movieList[position].Poster
                // Tries to get the image and post it in the ImageView
                // with the help of Handler
                try {
                    val `in` = java.net.URL(imageURL).openStream()
                    image = BitmapFactory.decodeStream(`in`)

                    // Only for making changes in UI
                    handler.post {
                        movieImage.setImageBitmap(image)
                    }
                }
                // If the URL doesnot point to
                // image or any other kind of failure
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            itemView.setOnClickListener {
                movieListClickListener?.onClicked(position, movieList)
            }
        }
    }

    private var movieListClickListener: MovieListClickListener? = null

    // Initializing TimeSlotIconClickListener Interface
    fun setOnClickListener(listener: MovieListClickListener) {
        movieListClickListener = listener
    }

    // Interface For TimeSlot Icon Click
    interface MovieListClickListener {
        fun onClicked(listPosition: Int, movieList: ArrayList<Search>)
    }


}