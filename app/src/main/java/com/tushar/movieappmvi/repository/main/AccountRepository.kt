package com.tushar.movieappmvi.repository.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.tushar.movieappmvi.api.main.MainApiService
import com.tushar.movieappmvi.models.AccountProperties
import com.tushar.movieappmvi.db.AccountPropertiesDao
import com.tushar.movieappmvi.repository.JobManager
import com.tushar.movieappmvi.repository.NetworkBoundResource
import com.tushar.movieappmvi.session.SessionManager
import com.tushar.movieappmvi.ui.DataState
import com.tushar.movieappmvi.ui.main.account.state.AccountViewState
import com.tushar.movieappmvi.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountPropertiesDao: AccountPropertiesDao,
    val mainApiService: MainApiService
) : JobManager(TAG) {

    fun getAccountDetails(sessionId: String): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                false,
                true
            ) {
            /**
             * This method is called, when the network request is successful. The data is first inserted into the DB.
             * After inserting into DB, The Job is set to completed and the data is loaded from updated cache.
             */
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<AccountProperties>) {
                updateLocalDb(response.data)
                createCacheRequestAndReturn()
            }

            /**
             * This method is called to make the network request
             */
            override suspend fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return mainApiService.getAccountDetails(sessionId)
            }

            /**
             * This method is called when we require to fetch data from local DB as well as network.
             * Unlike login request, Data must be only fetch from network.
             * This method queries the local DB and fetch data if available and set the data to view state.
             * The view state update the UI before the network data is fetched.
             */
            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountPropertiesDao.searchById(0).switchMap {
                    object : LiveData<AccountViewState>() {
                        override fun onActive() {
                            super.onActive()
                            value = AccountViewState(it)
                        }
                    }
                }

            }

            /**
             * This method will update the Local DB
             */
            override suspend fun updateLocalDb(cachedObject: AccountProperties?) {
                cachedObject?.let {
                    accountPropertiesDao.updateAccountProperties(it.id, it.username)
                }
            }

            override fun setJob(job: Job) {
                addJob("getAccountDetails", job)
            }

            /**
             * If Internet is not available, So this method will be called from network bound resource.
             * This method queries the local DB and fetch the data from it.
             * If any data is available, then the data is set to the view state and the UI is updated based on cached data.
             */
            override suspend fun createCacheRequestAndReturn() {
                withContext(Dispatchers.Main) {
                    result.addSource(loadFromCache()) { accountViewState ->
                        onCompleteJob(
                            DataState.data(
                                data = accountViewState,
                                response = null
                            )
                        )
                    }
                }
            }

        }.asLiveData()
    }

    companion object {
        const val TAG = "AccountRepository"
    }
}