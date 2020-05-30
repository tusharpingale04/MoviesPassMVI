package com.tushar.movieappmvi.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.tushar.movieappmvi.ui.DataState
import com.tushar.movieappmvi.ui.Response
import com.tushar.movieappmvi.ui.ResponseType
import com.tushar.movieappmvi.util.Constants
import com.tushar.movieappmvi.util.Constants.Companion.NETWORK_TIMEOUT
import com.tushar.movieappmvi.util.ErrorHandling
import com.tushar.movieappmvi.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.tushar.movieappmvi.util.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.tushar.movieappmvi.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import com.tushar.movieappmvi.util.GenericApiResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

abstract class NetworkBoundResource<ResponseObject, CachedObject, ViewStateType>
    (
    isConnected: Boolean, // is their a network connection?
    isNetworkRequest: Boolean, // is this a network request?
    shouldCancelIfNoInternet: Boolean, //Cancel Request if No Internet(Request Depends Totally On Internet eg: Login)
    shouldLoadFromCache: Boolean // should the cached data be loaded?
) {

    private val TAG = NetworkBoundResource::class.java.simpleName
    lateinit var job: CompletableJob
    lateinit var coroutineScope: CoroutineScope
    val result = MediatorLiveData<DataState<ViewStateType>>()

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true))
        if (shouldLoadFromCache) {
            val cache = loadFromCache()
            result.addSource(cache) {
                result.removeSource(cache)
                setValue(DataState.loading(isLoading = true, cachedData = it))
            }
        }

        if (isNetworkRequest) {
            if (isConnected) {
                handleNetworkRequest()
            } else {
                if (shouldCancelIfNoInternet) {
                    onErrorReturn(
                        UNABLE_TODO_OPERATION_WO_INTERNET,
                        showDialog = true,
                        showToast = false
                    )
                } else {
                    doCacheRequest()
                }
            }
        } else {
            doCacheRequest()
        }

    }

    private fun doCacheRequest() {
        GlobalScope.launch(Main) {
            createCacheRequestAndReturn()
        }
    }

    private fun handleNetworkRequest() {
        coroutineScope.launch(Main) {
            delay(Constants.TESTING_NETWORK_DELAY)
            val apiResponse = withContext(IO) { createCall() }
            result.addSource(apiResponse) { response ->
                result.removeSource(apiResponse)
                coroutineScope.launch {
                    handleNetworkCall(response = response)
                }
            }

        }
        GlobalScope.launch(IO) {
            delay(NETWORK_TIMEOUT)
            if (!job.isCompleted) {
                Log.e(TAG, "NetworkBoundResource: JOB NETWORK TIMEOUT.")
                job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
            }
        }
    }

    private suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when (response) {
            is GenericApiResponse.ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is GenericApiResponse.ApiErrorResponse -> {
                Log.e(TAG, "NetworkBoundResource: ${response.error}")
                onErrorReturn(response.error, showDialog = true, showToast = false)
            }
            is GenericApiResponse.EmptyApiResponse -> {
                Log.e(TAG, "NetworkBoundResource: Request returned NOTHING (HTTP 204)")
                onErrorReturn("HTTP 204. Returned nothing.", showDialog = true, showToast = false)
            }
        }
    }


    private fun initNewJob(): Job {
        job = Job()
        job.invokeOnCompletion {
            if (job.isCancelled) {
                Log.d(TAG, "invoke: Job Cancelled")
                it?.let {
                    onErrorReturn(it.message, showDialog = false, showToast = true)
                } ?: onErrorReturn(
                    ErrorHandling.ERROR_UNKNOWN,
                    showDialog = false,
                    showToast = true
                )
            } else if (job.isCompleted) {
                Log.d(TAG, "invoke: Job is Completed")
            }
        }
        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    private fun onErrorReturn(errorMessage: String?, showDialog: Boolean, showToast: Boolean) {
        var responseType: ResponseType = ResponseType.None
        var msg = errorMessage
        var useDialog = showDialog
        if (msg == null) {
            msg = ErrorHandling.ERROR_UNKNOWN
        } else if (ErrorHandling.isNetworkError(msg)) {
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (showToast) {
            responseType = ResponseType.Toast
        }
        if (useDialog) {
            responseType = ResponseType.Dialog
        }
        onCompleteJob(
            DataState.error(
                response = Response(
                    message = msg,
                    responseType = responseType
                )
            )
        )
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<ResponseObject>)

    abstract suspend fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun loadFromCache(): LiveData<ViewStateType>

    abstract suspend fun updateLocalDb(cachedObject: CachedObject?)

    abstract fun setJob(job: Job)

    abstract suspend fun createCacheRequestAndReturn()

    // extension function
    fun <T> CoroutineScope.asyncIO(ioFun: () -> T) = async(IO) { ioFun() }
}