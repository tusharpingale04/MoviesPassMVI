package com.tushar.movieappmvi.ui.main.account.state


sealed class AccountStateEvent{
    object GetAccountDetails : AccountStateEvent()
    object None: AccountStateEvent()
}