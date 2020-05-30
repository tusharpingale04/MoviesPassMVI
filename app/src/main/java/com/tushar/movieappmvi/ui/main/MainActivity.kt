package com.tushar.movieappmvi.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tushar.movieappmvi.R
import com.tushar.movieappmvi.ui.BaseActivity
import com.tushar.movieappmvi.ui.auth.AuthActivity
import com.tushar.movieappmvi.ui.main.account.BaseAccountFragment
import com.tushar.movieappmvi.ui.main.favorites.BaseFavoritesFragment
import com.tushar.movieappmvi.ui.main.favorites.WatchListFragment
import com.tushar.movieappmvi.ui.main.movies.BaseMoviesFragment
import com.tushar.movieappmvi.ui.main.movies.MovieDetailsFragment
import com.tushar.movieappmvi.util.BottomNavController
import com.tushar.movieappmvi.util.setUpNavigation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(),
    BottomNavController.NavGraphProvider,
    BottomNavController.OnNavigationGraphChanged,
    BottomNavController.OnNavigationReselectedListener
{

    private lateinit var bottomNavigationView: BottomNavigationView

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_nav_host_fragment,
            R.id.nav_movie,
            this,
            this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setUpNavigation(bottomNavController, this)
        if (savedInstanceState == null) {
            bottomNavController.onNavigationItemSelected()
        }
        subscribeObservers()
    }

    private fun cancelAllActiveJobs(){
        val fragments = bottomNavController.fragmentManager
            .findFragmentById(bottomNavController.containerId)
            ?.childFragmentManager
            ?.fragments

        if(fragments != null && fragments.size > 0){
            for(fragment in fragments){
                if(fragment is BaseAccountFragment){
                    fragment.cancelAllActiveJobs()
                }
                if(fragment is BaseMoviesFragment){
                    fragment.cancelAllActiveJobs()
                }
                if(fragment is BaseFavoritesFragment){
                    fragment.cancelAllActiveJobs()
                }
            }
        }
    }

    override fun getNavGraphId(itemId: Int) = when(itemId){
        R.id.nav_movie -> {
            R.navigation.nav_movie
        }
        R.id.nav_favorite -> {
            R.navigation.nav_favorite
        }
        R.id.nav_account -> {
            R.navigation.nav_account
        }
        else -> {
            R.navigation.nav_movie
        }
    }

    override fun onGraphChange() {
        cancelAllActiveJobs()
        expandAppBar()
        showToolbar(true)
    }

    override fun onReselectNavItem(
        navController: NavController,
        fragment: Fragment
    ) = when(fragment){

        is MovieDetailsFragment -> {
            navController.navigate(R.id.action_movieDetailsFragment_to_movieListFragment)
        }

        is WatchListFragment -> {
            navController.navigate(R.id.action_watchListFragment_to_favoritesFragment)
        }

        else -> {
            // do nothing
        }
    }

    override fun onBackPressed() = bottomNavController.onBackPressed()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }

    override fun collapseBottomBar() {
        hideBottomNavigationView(bottomNavigationView)
    }

    private fun hideBottomNavigationView(view: BottomNavigationView) {
        view.clearAnimation()
        view.animate().translationY(view.height.toFloat()).duration = 300
    }

    override fun showToolbar(showToolbar: Boolean) {
        if(showToolbar){
            tool_bar.visibility = View.VISIBLE
        }else{
            tool_bar.visibility = View.GONE
        }
    }

    private fun subscribeObservers(){
        sessionManager.cachedToken.observe(this, Observer{ authToken ->
            if(authToken?.token.isNullOrEmpty()){
                navAuthActivity()
                finish()
            }
        })
    }

    private fun setupActionBar(){
        setSupportActionBar(tool_bar)
    }

    private fun navAuthActivity(){
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showProgress(isVisible: Boolean) {
        if(isVisible){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.GONE
        }
    }
}
