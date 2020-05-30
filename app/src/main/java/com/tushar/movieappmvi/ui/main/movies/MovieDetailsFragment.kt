package com.tushar.movieappmvi.ui.main.movies

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.tushar.movieappmvi.R
import com.tushar.movieappmvi.models.Keyword
import com.tushar.movieappmvi.models.Movie
import com.tushar.movieappmvi.models.SimilarMovie
import com.tushar.movieappmvi.ui.main.movies.state.MovieStateEvent
import com.tushar.movieappmvi.ui.main.movies.viewmodel.setKeywords
import com.tushar.movieappmvi.ui.main.movies.viewmodel.setSimilarMovies
import com.tushar.movieappmvi.util.ImagePathGenerator
import kotlinx.android.synthetic.main.fragment_movie_details.*

class MovieDetailsFragment : BaseMoviesFragment() {

    private lateinit var recyclerAdapter: SimilarMoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        setUpSimilarMoviesRV()
        viewModel.viewState.value?.movieFields?.movieDetail?.id?.let {
            fetchKeywords(it.toString())
            Handler().postDelayed(
                {fetchSimilarMovies(it.toString())},1000
            )

        }
    }

    private fun setUpSimilarMoviesRV() {
        rv_similar_movies.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            isNestedScrollingEnabled = false
            recyclerAdapter = SimilarMoviesAdapter(requestManager)
            adapter = recyclerAdapter
        }
    }

    private fun fetchKeywords(id: String) {
        viewModel.setStateEvent(
            MovieStateEvent.FetchKeywords(id)
        )
    }

    private fun fetchSimilarMovies(id: String) {
        viewModel.setStateEvent(
            MovieStateEvent.FetchSimilarMovies(id, "1")
        )
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState?.data?.let { data ->
                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let { movieViewState ->
                        movieViewState.movieFields.keywordsList?.let {
                            viewModel.setKeywords(it)
                        }
                        movieViewState.movieFields.similarMoviesList?.let {
                            viewModel.setSimilarMovies(it)
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.movieFields.movieDetail?.let { movie ->
                setMovieDetails(movie)
            }
            if(!viewState.movieFields.keywordsList.isNullOrEmpty()){
                setKeywords(viewState.movieFields.keywordsList!!)
            }
            if(!viewState.movieFields.similarMoviesList.isNullOrEmpty()){
                setSimilarMovies(viewState.movieFields.similarMoviesList!!)
            }
        })
    }

    private fun setSimilarMovies(movies: List<SimilarMovie>) {
        recyclerAdapter.submitList(movies)
    }

    private fun setKeywords(keywords: List<Keyword>) {
        val list: MutableList<String> = keywords.map {
            it.name!!
        } as MutableList<String>
        tagView.isUseRandomColor = true
        tagView.setItems(list)
    }

    private fun setMovieDetails(movie: Movie) {
        tvMovieName.text = movie.originalTitle
        movie_desc.text = movie.overview

        requestManager
            .load(ImagePathGenerator.getBackDropImagePath(movie.backdropPath!!))
            .into(image)

        requestManager
            .load(ImagePathGenerator.getImagePath(movie.posterPath!!))
            .into(imgMain)
    }



}
