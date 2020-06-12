package com.tushar.movieappmvi.ui.main.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.tushar.movieappmvi.R
import com.tushar.movieappmvi.models.FavoriteMovie
import com.tushar.movieappmvi.ui.main.favorites.state.FavoritesStateEvent
import kotlinx.android.synthetic.main.fragment_favorites.*

class FavoritesFragment : BaseFavoritesFragment() {

    private lateinit var recyclerAdapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        setUpFavoritesMoviesRV()
    }

    private fun setUpFavoritesMoviesRV() {
        rv_favorites_movies.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            isNestedScrollingEnabled = false
            recyclerAdapter = FavoritesAdapter(requestManager)
            adapter = recyclerAdapter
        }
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState?.data?.let { data ->
                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let { movieViewState ->
                        movieViewState.movieList.let {
                            viewModel.setFavoritesMovies(it)
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if(!viewState.movieList.isNullOrEmpty()){
                setFavoriteMovies(viewState.movieList)
            }
        })
    }

    private fun setFavoriteMovies(movieList: List<FavoriteMovie>) {
        recyclerAdapter.submitList(movieList)
    }

    override fun onResume() {
        super.onResume()
        viewModel.setStateEvent(FavoritesStateEvent.FetchFavoritesMovies)
    }

}
