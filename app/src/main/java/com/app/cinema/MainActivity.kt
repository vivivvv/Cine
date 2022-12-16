package com.app.cinema

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.cinema.databinding.ActivityMainBinding
import com.app.cinema.model.Search
import com.app.cinema.movieDetails.MovieDetailsActivity
import com.app.cinema.retrofit.RetrofitClient.apiInterface

class MainActivity : AppCompatActivity(), MovieListAdapter.MovieListClickListener {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    lateinit var movieListRecycler: RecyclerView
    lateinit var movieListAdapter: MovieListAdapter
    private val movieList = ArrayList<Search>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.mainViewModel = viewModel
        binding.lifecycleOwner = this@MainActivity

        init()
    }

    private fun init() {
        // Initialize recycler view
        movieListRecycler = binding.movieListRc
        // Set movieList recyclerView Data
        setTimeSlotsRecycler()
        searchMovieList("harry", "a0783fa9")
    }

    private fun setTimeSlotsRecycler() {
        movieListAdapter = MovieListAdapter()
        movieListRecycler.layoutManager =
            GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        movieListRecycler.adapter = movieListAdapter
        movieListAdapter.setOnClickListener(this)
    }

    private fun searchMovieList(movieName: String, apiKey: String) {
        lifecycleScope.launchWhenCreated {
            try {
                val response = apiInterface.searchMovieList(movieName, apiKey)
                if (response.isSuccessful) {
                    movieList.clear()
                    response.body()?.Search?.forEach {
                        movieList.add(it)
                    }
                    Log.d("TAG", "searchMovieList: $movieList")
                    movieListAdapter.setMovieList(movieList)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        response.errorBody().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (Ex: Exception) {
                Log.d("TAG", "getUserList: ${Ex.localizedMessage}")
            }
        }
    }

    override fun onClicked(listPosition: Int, movieList: ArrayList<Search>) {
        Log.d("TAG", "onClicked: ${movieList[listPosition].imdbID}")
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra("imdbID", movieList[listPosition].imdbID ?: 0)
        startActivity(intent)
    }
}