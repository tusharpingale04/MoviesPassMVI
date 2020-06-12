package com.tushar.movieappmvi.ui.main.favorites

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
import java.lang.Exception
import javax.inject.Inject

abstract class BaseFavoritesFragment : DaggerFragment() {

    lateinit var stateChangeListener: DataStateChangeListener

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: FavoritesViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.favoritesFragment, activity as AppCompatActivity)

        viewModel = activity?.run {
            ViewModelProvider(this,providerFactory).get(FavoritesViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        cancelAllActiveJobs()
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
        try{
            stateChangeListener = context as DataStateChangeListener
        }catch (e: Exception){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }
    }

    companion object{
        const val TAG = "BaseFavoritesFragment"
    }
}