package com.tushar.movieappmvi.util

class Constants {

    companion object{

        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val NETWORK_TIMEOUT = 60000L
        const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing
        const val PAGINATION_PAGE_SIZE = 20
    }
}