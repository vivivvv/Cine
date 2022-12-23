package com.app.cinema.history.adapter

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
import com.app.cinema.R
import com.app.cinema.model.Search
import com.app.cinema.roomdp.HistoryData
import java.util.concurrent.Executors

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryListViewHolder>() {

    private var historyList = ArrayList<HistoryData>()

    @SuppressLint("NotifyDataSetChanged")
    fun setMovieList(
        historyList: ArrayList<HistoryData>
    ) {
        this.historyList = historyList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryListViewHolder {
        // Initialize view
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.history_card_view, parent, false
        )
        // Pass holder view
        return HistoryListViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryListViewHolder, position: Int) {
        // Set text on radio button
        holder.setData(position)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    inner class HistoryListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Assign variable
        var movieImage: ImageView = view.findViewById(R.id.imageview)
        var movieTitleText: TextView = view.findViewById(R.id.title_textView)
        var descriptionText: TextView = view.findViewById(R.id.description_textView)

        @SuppressLint("SetTextI18n")
        fun setData(position: Int) {
            movieTitleText.text = historyList[position].title
            descriptionText.text = historyList[position].description
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
                val imageURL = historyList[position].image
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

        }
    }

    private var historyListClickListener: HistoryListClickListener? = null

    fun setOnClickListener(listener: HistoryListClickListener) {
        historyListClickListener = listener
    }

    interface HistoryListClickListener {
        fun onClicked(listPosition: Int, movieList: ArrayList<Search>)
    }
}