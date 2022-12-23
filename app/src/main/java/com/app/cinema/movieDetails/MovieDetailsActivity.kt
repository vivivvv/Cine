package com.app.cinema.movieDetails

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.cinema.App
import com.app.cinema.R
import com.app.cinema.databinding.ActivityMovieDetailsBinding
import com.app.cinema.model.MovieDescriptionResponse
import com.app.cinema.retrofit.RetrofitClient
import com.app.cinema.roomdp.HistoryData
import com.app.cinema.utills.SharedPrefsHelper
import java.util.concurrent.Executors

class MovieDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailsBinding
    private val viewModel: MovieDetailsViewModel by lazy {
        val factory =
            MovieDetailsViewModel.MovieDetailsViewModelFactory((application as App).repository)
        ViewModelProvider(this, factory)[MovieDetailsViewModel::class.java]
    }
    var imdbID: String = ""
    lateinit var hologramListRecycler: RecyclerView
    lateinit var hologramAdapter: HologramAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details)
        binding.movieDetailsViewModel = viewModel
        binding.lifecycleOwner = this@MovieDetailsActivity

        init()
    }

    private fun init() {
        hologramListRecycler = binding.hologramRc
        imdbID = intent.getStringExtra("imdbID").toString()
        // Set movieList recyclerView Data
        setHologramRecycler()
        getMovieDescription(imdbID, "a0783fa9")

    }

    private fun setHologramRecycler() {
        hologramAdapter = HologramAdapter()
        hologramListRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        hologramListRecycler.adapter = hologramAdapter
    }

    private fun getMovieDescription(imdbID: String, apiKey: String) {
        lifecycleScope.launchWhenCreated {
            try {
                val response = RetrofitClient.apiInterface.getMovieDescription(imdbID, apiKey)
                if (response.isSuccessful) {
                    Log.d("TAG", "getMovieDescription: ${response.body()}")
                    // Save title to verify title available in local database
                    if (!SharedPrefsHelper.loadData(this@MovieDetailsActivity, "Title")
                            .contains(response.body()?.Title)
                    ) {
                        SharedPrefsHelper.saveData(
                            this@MovieDetailsActivity,
                            "Title",
                            ArrayList<String>().apply { response.body()?.Title?.let { add(it) } }
                        )
                        // Save data to local database
                        viewModel.insert(
                            HistoryData(
                                response.body()!!.Title,
                                response.body()!!.Poster,
                                response.body()!!.Plot,
                                imdbID
                            )
                        )
                    }
                    // Loading image to UI
                    loadImage(response.body()?.Poster)
                    // Setting details to UI
                    afterApiCall(response.body())
                } else {
                    Toast.makeText(
                        this@MovieDetailsActivity,
                        response.errorBody().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (Ex: Exception) {
                Log.d("TAG", "getUserList: ${Ex.localizedMessage}")
            }
        }

    }

    private fun afterApiCall(body: MovieDescriptionResponse?) {
        setMovieTitle(body)
        setRating(body)
        setTime(body)
        setGenre(body)
        setPlot(body)
        setDirectorName(body)
        setWriterName(body)
        setActorsName(body)

    }

    private fun setActorsName(body: MovieDescriptionResponse?) {
        binding.actorsName.text = body?.Actors
    }

    private fun setWriterName(body: MovieDescriptionResponse?) {
        binding.writerName.text = body?.Writer
    }

    private fun setDirectorName(body: MovieDescriptionResponse?) {
        binding.directorName.text = body?.Director
    }

    private fun setPlot(body: MovieDescriptionResponse?) {
        binding.plot.text = body?.Plot
    }

    private fun setTime(body: MovieDescriptionResponse?) {
        binding.time.text = body?.Runtime
    }

    private fun setRating(body: MovieDescriptionResponse?) {
        binding.rating.text = body?.imdbRating
    }

    private fun setMovieTitle(body: MovieDescriptionResponse?) {
        binding.movieName.text = body?.Title
    }

    private fun setGenre(body: MovieDescriptionResponse?) {
        val hologramList = ArrayList<String>()
        val hologramTotalList = ArrayList<String>().apply {
            body?.Genre?.split(", ")?.forEach {
                this.add(it)
            }
        }
        if (hologramTotalList.size >= 3) {
            (hologramTotalList.subList(0, 3)).forEach {
                hologramList.add(it)
            }
        } else {
            hologramTotalList.forEach { hologramList.add(it) }
        }

        Log.d("TAG", "afterApiCall: ${body?.Genre} $hologramList")
        hologramAdapter.setHologramList(hologramList)
    }

    private fun loadImage(poster: String?) {
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
            val imageURL = poster
            // Tries to get the image and post it in the ImageView
            // with the help of Handler
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)

                // Only for making changes in UI
                handler.post {
                    binding.movieImageview.setImageBitmap(image)
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