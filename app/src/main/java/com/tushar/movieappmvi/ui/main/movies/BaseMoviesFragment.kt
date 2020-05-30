package com.tushar.movieappmvi.ui.main.movies

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.RequestManager
import com.tushar.movieappmvi.R
import com.tushar.movieappmvi.ui.DataStateChangeListener
import com.tushar.movieappmvi.ui.main.movies.viewmodel.MoviesViewModel
import com.tushar.movieappmvi.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseMoviesFragment : DaggerFragment() {

    lateinit var stateChangeListener: DataStateChangeListener

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: MoviesViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.movieListFragment, activity as AppCompatActivity)

        viewModel = activity?.run {
            ViewModelProvider(this,providerFactory).get(MoviesViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        cancelAllActiveJobs()

        if(this is MovieDetailsFragment){
            stateChangeListener.showToolbar(false)
            //stateChangeListener.collapseBottomBar()
        }else{
            stateChangeListener.showToolbar(true)
        }
    }

    fun cancelAllActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    /**
    @fragmentId is id of fragment from graph to be EXCLUDED from action back bar nav
     */
    private fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity) {
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: Exception) {
            Log.e(TAG, "$context must implement DataStateChangeListener")
        }
    }

    companion object {
        const val TAG = "BaseMoviesFragment"
    }
}