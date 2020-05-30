package com.tushar.movieappmvi.util

object ImagePathGenerator {

    fun getImagePath(path: String) : String{
        return "https://image.tmdb.org/t/p/w500$path"
    }

    fun getBackDropImagePath(path: String) : String{
        return "https://image.tmdb.org/t/p/w780$path"
    }
}