package com.tushar.movieappmvi.ui.main.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.tushar.movieappmvi.R
import com.tushar.movieappmvi.models.AccountProperties
import com.tushar.movieappmvi.ui.main.account.state.AccountStateEvent
import com.tushar.movieappmvi.ui.main.account.state.AccountViewState
import kotlinx.android.synthetic.main.fragment_account_detail.*

class AccountDetailFragment : BaseAccountFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnLogout.setOnClickListener {
            viewModel.logout()
        }
        subscribeObservers()
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState?.data.let { data ->
                data?.data.let { event ->
                    event?.getContentIfNotHandled()?.let { accountViewState ->
                        accountViewState.accountProperties?.let { accountProperties ->
                            viewModel.setAccountProperties(accountProperties)
                        }
                    }
                }
            }
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.accountProperties?.let {
                setAccountDetails(it)
            }
        })
    }

    private fun setAccountDetails(accountProperties: AccountProperties){
        username.text = accountProperties.username
    }

    override fun onResume() {
        super.onResume()
        viewModel.setStateEvent(AccountStateEvent.GetAccountDetails)
    }

}
