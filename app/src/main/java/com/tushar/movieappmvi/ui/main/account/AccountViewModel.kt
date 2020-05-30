package com.tushar.movieappmvi.ui.main.account

import androidx.lifecycle.LiveData
import com.tushar.movieappmvi.models.AccountProperties
import com.tushar.movieappmvi.repository.main.AccountRepository
import com.tushar.movieappmvi.session.SessionManager
import com.tushar.movieappmvi.ui.BaseViewModel
import com.tushar.movieappmvi.ui.DataState
import com.tushar.movieappmvi.ui.main.account.state.AccountStateEvent
import com.tushar.movieappmvi.ui.main.account.state.AccountViewState
import com.tushar.movieappmvi.util.AbsentLiveData
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    private val accountRepository: AccountRepository
): BaseViewModel<AccountStateEvent, AccountViewState>(){

    override fun getViewState() = AccountViewState()

    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when(stateEvent){
            is AccountStateEvent.GetAccountDetails ->{
                val authToken = sessionManager.cachedToken.value
                return if(authToken?.token != null){
                    accountRepository.getAccountDetails(authToken.token)
                }else{
                    AbsentLiveData.create()
                }
            }
            is AccountStateEvent.None ->{
                return object : LiveData<DataState<AccountViewState>>(){
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

    fun setAccountProperties(accountProperties: AccountProperties){
        val update = getCurrentViewStateOrNew()
        if(update.accountProperties == accountProperties){
            return
        }
        update.accountProperties = accountProperties
        _viewState.value = update
    }

    fun logout(){
        sessionManager.logout()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    fun cancelActiveJobs(){
        setStateEvent(AccountStateEvent.None)
        accountRepository.cancelActiveJobs()
    }

}