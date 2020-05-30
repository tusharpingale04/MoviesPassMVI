package com.tushar.movieappmvi.ui

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.tushar.movieappmvi.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity: DaggerAppCompatActivity(), DataStateChangeListener {

    companion object{
        const val TAG = "BaseActivity"
    }

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            GlobalScope.launch(Main) {
                showProgress(it.loading.isLoading)
            }

            dataState.data?.let {
                it.response?.let { responseEvent ->
                    handleResponseType(responseEvent)
                }
            }

            dataState.error?.let { errorEvent ->
                handleErrorEvent(errorEvent)
            }
        }
    }

    private fun handleErrorEvent(errorEvent: Event<StateError>) {
        errorEvent.getContentIfNotHandled()?.let {
            when(it.response.responseType){
                is ResponseType.Toast ->{
                    it.response.message?.let{message ->
                        displayToast(message)
                    }
                }

                is ResponseType.Dialog ->{
                    it.response.message?.let{ message ->
                        displayErrorDialog(message)
                    }
                }

                is ResponseType.None -> {
                    Log.i(TAG, "handleStateError: ${it.response.message}")
                }
            }
        }
    }

    private fun handleResponseType(responseEvent: Event<Response>) {
        responseEvent.getContentIfNotHandled()?.let {
            when(it.responseType){
                is ResponseType.Toast ->{
                    it.message?.let{message ->
                        displayToast(message)
                    }
                }

                is ResponseType.Dialog ->{
                    it.message?.let{ message ->
                        displaySuccessDialog(message)
                    }
                }

                is ResponseType.None -> {
                    Log.i(TAG, "handleStateResponse: ${it.message}")
                }
            }
        }
    }

    override fun hideKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    abstract fun showProgress(isVisible: Boolean)
}