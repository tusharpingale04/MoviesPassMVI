package com.tushar.movieappmvi.ui

interface DataStateChangeListener {

    fun onDataStateChange(dataState: DataState<*>?)

    fun expandAppBar()

    fun collapseBottomBar()

    fun showToolbar(showToolbar: Boolean)

    fun hideKeyboard()
}