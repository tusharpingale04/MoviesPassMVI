package com.tushar.movieappmvi.ui.auth

import androidx.lifecycle.LiveData
import com.tushar.movieappmvi.api.auth.response.LoginResponse
import com.tushar.movieappmvi.api.auth.response.RequestResponse
import com.tushar.movieappmvi.models.AuthToken
import com.tushar.movieappmvi.repository.auth.AuthRepository
import com.tushar.movieappmvi.ui.BaseViewModel
import com.tushar.movieappmvi.ui.Data
import com.tushar.movieappmvi.ui.DataState
import com.tushar.movieappmvi.ui.auth.state.AuthStateEvent
import com.tushar.movieappmvi.ui.auth.state.AuthViewState
import com.tushar.movieappmvi.ui.auth.state.LoginFields
import com.tushar.movieappmvi.util.AbsentLiveData
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): BaseViewModel<AuthStateEvent,AuthViewState>() {

    override fun getViewState(): AuthViewState {
        return AuthViewState()
    }

    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        return when(stateEvent){
            AuthStateEvent.GetRequestToken -> {
                authRepository.getRequestToken()
            }
            is AuthStateEvent.Login ->{
                authRepository.login(
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.requestToken
                )
            }
            is AuthStateEvent.CreateSession -> {
                authRepository.createSession(
                    stateEvent.requestToken,
                    stateEvent.username
                )
            }
            is AuthStateEvent.CheckPreviousAuthEvent ->{
                authRepository.checkPreviousAuthUser()
            }
            is AuthStateEvent.None ->{
                object : LiveData<DataState<AuthViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState.data(
                            data = null,
                            response = null
                        )
                    }
                }
            }
        }
    }

    fun setLoginFields(loginFields: LoginFields){
        val update = getCurrentViewStateOrNew()
        if(update.loginFields == loginFields){
            return
        }
        update.loginFields = loginFields
        _viewState.value = update
    }

    fun setRequestToken(requestToken: RequestResponse){
        val update = getCurrentViewStateOrNew()

        if(update.requestToken == requestToken){
            return
        }
        update.requestToken = requestToken
        _viewState.value = update
    }

    fun setLoginResponse(loginResponse: LoginResponse){
        val update = getCurrentViewStateOrNew()

        if(update.loginResponse == loginResponse){
            return
        }
        update.loginResponse = loginResponse
        _viewState.value = update
    }

    fun setAuthToken(authToken: AuthToken){
        val update = getCurrentViewStateOrNew()

        if(update.authToken == authToken){
            return
        }
        update.authToken = authToken
        _viewState.value = update
    }

    fun cancelActiveJobs(){
        setStateEvent(AuthStateEvent.None)
        authRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }


}