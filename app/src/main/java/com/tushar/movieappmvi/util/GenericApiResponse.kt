package com.tushar.movieappmvi.util

import android.util.Log
import com.google.gson.Gson
import com.tushar.movieappmvi.api.ApiError
import retrofit2.Response

sealed class GenericApiResponse<T> {

    class EmptyApiResponse<T> : GenericApiResponse<T>()

    data class ApiErrorResponse<T>(val error: String) : GenericApiResponse<T>()

    data class ApiSuccessResponse<T>(val data: T) : GenericApiResponse<T>()

    companion object{

        private const val TAG = "GenericApiResponse"

        fun <T> create(error: Throwable) : GenericApiResponse<T>{
            return ApiErrorResponse(error.message ?: "unknown error")
        }

        fun <T> create(response: Response<T>) : GenericApiResponse<T>{

            Log.d(TAG, "GenericApiResponse: response: $response")
            Log.d(TAG, "GenericApiResponse: raw: ${response.raw()}")
            Log.d(TAG, "GenericApiResponse: headers: ${response.headers()}")
            Log.d(TAG, "GenericApiResponse: message: ${response.message()}")

            if(response.isSuccessful){
                val body = response.body()
                return when {
                    body == null -> {
                        EmptyApiResponse()
                    }
                    response.code() == 401 -> {
                        ApiErrorResponse("401 Unauthorized. Token may be invalid.")
                    }
                    else -> {
                        Log.d(TAG, "GenericApiResponse: Body: ${response.message()}")
                        ApiSuccessResponse(data = body)
                    }
                }
            } else{
                val apiError: ApiError = Gson().fromJson(response.errorBody()?.charStream(), ApiError::class.java)
                val errorMsg = if (!apiError.errorMessage.isNullOrEmpty()) {
                    apiError.errorMessage
                } else{
                    null
                }
                return ApiErrorResponse(errorMsg ?: "unknown error")
            }

        }

    }

}