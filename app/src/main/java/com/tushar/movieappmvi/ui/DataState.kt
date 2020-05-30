package com.tushar.movieappmvi.ui

data class DataState<T>(
    val loading: Loading = Loading(false),
    val data: Data<T>? = null,
    val error: Event<StateError>? = null
) {
    companion object {

        fun <T> loading(isLoading: Boolean, cachedData: T? = null): DataState<T> {
            return DataState(
                loading = Loading(isLoading),
                data = Data(Event.dataEvent(cachedData), null),
                error = null
            )
        }

        fun <T> data(data: T? = null, response: Response? = null): DataState<T> {
            return DataState(
                loading = Loading(false),
                data = Data(data = Event.dataEvent(data), response = Event.responseEvent(response)),
                error = null
            )
        }

        fun <T> error(response: Response): DataState<T> {
            return DataState(
                loading = Loading(false),
                data = null,
                error = Event(StateError(response = response))
            )
        }
    }
}