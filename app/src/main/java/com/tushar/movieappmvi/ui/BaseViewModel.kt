package com.tushar.movieappmvi.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<StateEvent,ViewState> : ViewModel(){

    private val _stateEvent : MutableLiveData<StateEvent> = MutableLiveData()
    protected val _viewState : MutableLiveData<ViewState> = MutableLiveData()

    val viewState: LiveData<ViewState>
        get() = _viewState

    val dataState : LiveData<DataState<ViewState>> = Transformations
        .switchMap(_stateEvent){ stateEvent ->
            stateEvent?.let {
                handleStateEvent(it)
            }
        }

    fun setStateEvent(stateEvent: StateEvent){
        _stateEvent.value = stateEvent
    }

    fun setViewState(viewState: ViewState){
        _viewState.value = viewState
    }

    fun getCurrentViewStateOrNew() : ViewState{
        val viewState = viewState.value?.let {
            it
        } ?: getViewState()
        return viewState
    }

    abstract fun getViewState(): ViewState

    abstract fun handleStateEvent(stateEvent: StateEvent): LiveData<DataState<ViewState>>
}