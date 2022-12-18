package com.app.cinema

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.cinema.databinding.ActivityMainBinding
import com.app.cinema.model.Search
import com.app.cinema.model.SearchMovieListResponse
import com.app.cinema.movieDetails.MovieDetailsActivity
import com.app.cinema.retrofit.RetrofitClient.apiInterface
import com.app.cinema.utills.SharedPrefsHelper
import com.app.cinema.utills.hideKeyboard
import retrofit2.Response

class MainActivity : AppCompatActivity(), MovieListAdapter.MovieListClickListener {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    lateinit var movieListRecycler: RecyclerView
    lateinit var movieListAdapter: MovieListAdapter
    var cursorAdapter: SimpleCursorAdapter? = null

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
        // Initialize searchbar
        initializeSearchView()
        // Set movieList recyclerView Data
        setMovieListRecycler()
        // Initial Content visibility
        setMovieRcVisibility()
        // Update history data
        viewModel.historyList = SharedPrefsHelper.loadData(this@MainActivity)
        // Initialize search suggestion
        setSearchSuggestion()
        // Suggestion Listener
        setSuggestionListener()
    }

    private fun initializeSearchView() {
        // Avoid searchbar auto focus
        binding.searchView.clearFocus()
        // Enable submit button
        binding.searchView.isSubmitButtonEnabled = true
        // SearchView submit button listener
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Hide keyboard
                hideKeyboard(binding.root)
                // Verify query exist in sharedPreference
                if (!viewModel.historyList.contains(query)) {
                    // Add query to history
                    viewModel.historyList.add(query)
                }
                // Save history to sharedPreference
                SharedPrefsHelper.saveData(this@MainActivity, viewModel.historyList)
                // Search Movie
                searchMovieList(query, "a0783fa9")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val cursor =
                    MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                newText?.let {
                    viewModel.historyList.forEachIndexed { index, suggestion ->
                        if (suggestion.contains(newText, true))
                            cursor.addRow(arrayOf(index, suggestion))
                    }
                }
                cursorAdapter?.changeCursor(cursor)
                return true
            }
        })

    }

    private fun doApiCall() {

    }

    private fun setSuggestionListener() {
        binding.searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            @SuppressLint("Range")
            override fun onSuggestionClick(position: Int): Boolean {
                hideKeyboard(binding.root)
                val cursor = binding.searchView.suggestionsAdapter.getItem(position) as Cursor
                val selection =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                binding.searchView.setQuery(selection, false)
                // Search Movie
                searchMovieList(selection, "a0783fa9")
                return true
            }
        })
    }

    private fun setSearchSuggestion() {
        binding.searchView.findViewById<AutoCompleteTextView>(androidx.appcompat.R.id.search_src_text)
            .threshold = 1

        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.item_label)
        cursorAdapter = SimpleCursorAdapter(
            this,
            R.layout.suggestion_layout,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
        binding.searchView.suggestionsAdapter = cursorAdapter
    }

    private fun setMovieListRecycler() {
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
                    // Update movieList
                    updateMovieList(response)
                } else {
                    Toast.makeText(
                        this@MainActivity, response.errorBody().toString(), Toast.LENGTH_LONG
                    ).show()
                }
            } catch (Ex: Exception) {
                viewModel.movieList.clear()
                // Update movie recyclerview visibility
                setMovieRcVisibility()
                Log.d("TAG", "getUserList: ${Ex.localizedMessage}")
            }
        }
    }

    private fun updateMovieList(response: Response<SearchMovieListResponse>) {
        viewModel.movieList.clear()
        response.body()?.Search?.forEach {
            viewModel.movieList.add(it)
        }
        Log.d("TAG", "searchMovieList: ${viewModel.movieList}")
        movieListAdapter.setMovieList(viewModel.movieList)
        // Update movie recyclerview visibility
        setMovieRcVisibility()
    }

    override fun onClicked(listPosition: Int, movieList: ArrayList<Search>) {
        Log.d("TAG", "onClicked: ${movieList[listPosition].imdbID}")
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra("imdbID", movieList[listPosition].imdbID ?: 0)
        startActivity(intent)
    }

    private fun setMovieRcVisibility() {
        if (viewModel.movieList.isEmpty()) {
            binding.movieListRc.visibility = View.GONE
            binding.message.visibility = View.VISIBLE
        } else {
            binding.movieListRc.visibility = View.VISIBLE
            binding.message.visibility = View.GONE
        }
    }

}