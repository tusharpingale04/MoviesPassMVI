package com.tushar.movieappmvi.ui.main.favorites

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.tushar.movieappmvi.R
import com.tushar.movieappmvi.ui.DataStateChangeListener
import dagger.android.support.DaggerFragment
import java.lang.Exception

abstract class BaseFavoritesFragment : DaggerFragment() {

    lateinit var stateChangeListener: DataStateChangeListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.favoritesFragment, activity as AppCompatActivity)

        cancelAllActiveJobs()
    }

    fun cancelAllActiveJobs(){
        //viewModel.cancelActiveJobs()
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