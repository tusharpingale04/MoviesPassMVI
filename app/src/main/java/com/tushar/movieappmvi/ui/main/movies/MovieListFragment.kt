package com.tushar.movieappmvi.ui.main.movies

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tushar.movieappmvi.R
import com.tushar.movieappmvi.models.Movie
import com.tushar.movieappmvi.ui.DataState
import com.tushar.movieappmvi.ui.main.movies.state.MovieViewState
import com.tushar.movieappmvi.ui.main.movies.viewmodel.*
import kotlinx.android.synthetic.main.fragment_movie_list.*

class MovieListFragment : BaseMoviesFragment(), MoviesAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener {

    lateinit var recyclerAdapter: MoviesAdapter
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        swipe_refresh.setOnRefreshListener(this)
        setUpRecyclerView()
        subscribeObservers()
        if(savedInstanceState == null){
            viewModel.loadFirstPage()
        }
    }

    private fun setUpRecyclerView() {
        rv_movies.apply {
            layoutManager = GridLayoutManager(context,2)
            (layoutManager as GridLayoutManager).spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val type = recyclerAdapter.getItemViewType(position)
                    return if (type == MoviesAdapter.NO_MORE_RESULTS) 2 else 1
                }
            }
            recyclerAdapter = MoviesAdapter(requestManager,  this@MovieListFragment)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == recyclerAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "MovieListFragment: attempting to load next page...")
                        viewModel.loadNextPage()
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if(dataState != null){
                handlePagination(dataState)
                stateChangeListener.onDataStateChange(dataState)
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { movieViewState ->
            //Submit RecyclerList
            movieViewState?.let {
                recyclerAdapter.submitList(
                    movieList = it.movieListFields.movieList,
                    isQueryExhausted = viewModel.isSearchQueryExhausted()
                )
            }

        })
    }

    private fun handlePagination(dataState: DataState<MovieViewState>) {
        dataState.data?.let {
            it.data?.let{ event ->
                event.getContentIfNotHandled()?.let{ viewState ->
                    if(viewState.movieListFields.movieList.isNotEmpty()){
                        viewModel.handleMoviesList(viewState)
                    }else{
                        viewModel.setIsQueryExhausted(true)
                    }
                }
            }
        }
    }

    private fun onMovieSearch(){
        viewModel.loadFirstPage().run {
            resetUI()
        }
    }

    private fun resetUI(){
        rv_movies.smoothScrollToPosition(0)
        stateChangeListener.hideKeyboard()
        focusable_view.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rv_movies.adapter = null
    }

    override fun onItemSelected(position: Int, item: Movie) {
        viewModel.setMovie(item)
        val action = MovieListFragmentDirections.actionMovieListFragmentToMovieDetailsFragment()
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        initSearchView(menu)
    }

    private fun initSearchView(menu: Menu){
        activity?.apply {
            val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true
        }

        // ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
        val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
        searchPlate.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_ACTION_SEARCH ) {
                val searchQuery = v.text.toString()
                Log.e(TAG, "SearchView: (keyboard or arrow) executing search...: $searchQuery")
                viewModel.setQuery(searchQuery).run{
                    onMovieSearch()
                }
            }
            true
        }

        // SEARCH BUTTON CLICKED (in toolbar)
        val searchButton = searchView.findViewById(R.id.search_go_btn) as View
        searchButton.setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            Log.e(TAG, "SearchView: (button) executing search...: $searchQuery")
            viewModel.setQuery(searchQuery).run {
                onMovieSearch()
            }

        }
    }

    override fun onRefresh() {
        onMovieSearch()
        swipe_refresh.isRefreshing = false
    }
}
