package com.tushar.movieappmvi.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.tushar.movieappmvi.api.auth.TMDBAuthService
import com.tushar.movieappmvi.api.auth.request.LoginRequest
import com.tushar.movieappmvi.api.auth.request.SessionRequest
import com.tushar.movieappmvi.api.auth.response.LoginResponse
import com.tushar.movieappmvi.api.auth.response.RequestResponse
import com.tushar.movieappmvi.api.auth.response.SessionResponse
import com.tushar.movieappmvi.models.AccountProperties
import com.tushar.movieappmvi.models.AuthToken
import com.tushar.movieappmvi.db.AccountPropertiesDao
import com.tushar.movieappmvi.db.AuthTokenDao
import com.tushar.movieappmvi.repository.NetworkBoundResource
import com.tushar.movieappmvi.session.SessionManager
import com.tushar.movieappmvi.ui.DataState
import com.tushar.movieappmvi.ui.Response
import com.tushar.movieappmvi.ui.ResponseType
import com.tushar.movieappmvi.ui.auth.state.AuthViewState
import com.tushar.movieappmvi.ui.auth.state.LoginFields
import com.tushar.movieappmvi.util.AbsentLiveData
import com.tushar.movieappmvi.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.tushar.movieappmvi.util.GenericApiResponse
import com.tushar.movieappmvi.util.PreferenceKeys.Companion.PREVIOUS_AUTH_USER
import com.tushar.movieappmvi.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Job

class AuthRepository constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val tmdbAuthService: TMDBAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
) {

    private var repositoryJob: Job? = null

    fun getRequestToken(): LiveData<DataState<AuthViewState>> {
        return object :
            NetworkBoundResource<RequestResponse,Any, AuthViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<RequestResponse>) {
                onCompleteJob(
                    dataState = DataState.data(
                        data = AuthViewState(
                            requestToken = RequestResponse(
                                response.data.requestToken
                            )
                        )
                    )
                )
            }

            override suspend fun createCall(): LiveData<GenericApiResponse<RequestResponse>> {
                return tmdbAuthService.getRequestToken()
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            override suspend fun createCacheRequestAndReturn() {

            }

            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cachedObject: Any?) {

            }

        }.asLiveData()
    }

    fun login(
        email: String,
        password: String,
        requestToken: String
    ): LiveData<DataState<AuthViewState>> {
        val loginFieldErrors = LoginFields(email, password).isValid()
        if (loginFieldErrors != LoginFields.LoginError.none()) {
            return returnErrorResponse(loginFieldErrors, ResponseType.Dialog)
        }
        return object :
            NetworkBoundResource<LoginResponse,Any, AuthViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<LoginResponse>) {
                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            loginResponse = LoginResponse(
                                success = response.data.success,
                                requestToken = response.data.requestToken
                            )
                        )
                    )
                )
            }

            override suspend fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return tmdbAuthService.login(LoginRequest(email, password, requestToken))
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            override suspend fun createCacheRequestAndReturn() {

            }

            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cachedObject: Any?) {

            }

        }.asLiveData()
    }

    fun createSession(requestToken: String, username: String): LiveData<DataState<AuthViewState>> {
        return object :
            NetworkBoundResource<SessionResponse,Any, AuthViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true,
                false
            ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<SessionResponse>) {

                //For now as Server doesn't send unique id, we save at 0th row only
                accountPropertiesDao.insertAndReplace(
                    AccountProperties(
                        0,
                        "",
                        username
                    )
                )

                val result = authTokenDao.insert(
                    AuthToken(
                        0,
                        response.data.sessionId
                    )
                )

                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            response = Response(
                                message = ERROR_SAVE_AUTH_TOKEN,
                                responseType = ResponseType.Dialog
                            )
                        )
                    )
                }

                saveUserNameToPrefs(username)

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(
                                token = response.data.sessionId
                            )
                        )

                    )
                )
            }

            override suspend fun createCall(): LiveData<GenericApiResponse<SessionResponse>> {
                return tmdbAuthService.createSession(SessionRequest(requestToken))
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            override suspend fun createCacheRequestAndReturn() {

            }

            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cachedObject: Any?) {

            }

        }.asLiveData()

    }

    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>> {
        val previousAuthUser = sharedPreferences.getString(PREVIOUS_AUTH_USER, null) ?: return noTokenFoundError()
        return object : NetworkBoundResource<Unit,Any, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            false,
            false,
            false
        ) {
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<Unit>) {

            }

            override suspend fun createCall(): LiveData<GenericApiResponse<Unit>> {
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            override suspend fun createCacheRequestAndReturn() {
                accountPropertiesDao.searchByUserName(previousAuthUser).let { accountProperties ->
                    accountProperties?.let {
                        if (accountProperties.id > -1) {
                            authTokenDao.searchById(accountProperties.id).let { authToken ->
                                authToken?.let { token ->
                                    token.token?.let {
                                        onCompleteJob(
                                            DataState.data(
                                                AuthViewState(authToken = authToken)
                                            )
                                        )
                                        return
                                    }
                                }
                            }
                        }
                    }
                }
                onCompleteJob(
                    DataState.data(
                        null,
                        Response(
                            RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                            ResponseType.None
                        )
                    )
                )
            }

            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cachedObject: Any?) {

            }

        }.asLiveData()
    }

    private fun noTokenFoundError(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    null,
                    Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None)
                )
            }
        }
    }

    private fun saveUserNameToPrefs(username: String) {
        sharedPrefsEditor.putString(PREVIOUS_AUTH_USER, username)
        sharedPrefsEditor.apply()
    }

    private fun returnErrorResponse(
        errorMsg: String,
        responseType: ResponseType
    ): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    response = Response(
                        message = errorMsg,
                        responseType = responseType
                    )
                )
            }
        }
    }

    fun cancelActiveJobs() {
        Log.d(TAG, "AuthRepository: Cancelling on-going jobs...")
        repositoryJob?.cancel()
    }

    companion object {
        const val TAG = "AuthRepository"
    }

}