package com.tushar.movieappmvi.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.tushar.movieappmvi.R
import com.tushar.movieappmvi.ui.auth.state.AuthStateEvent
import com.tushar.movieappmvi.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()

        btn_login.setOnClickListener {
            login()
        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.loginFields?.let { loginFields ->
                loginFields.username?.let { userName ->
                    input_email.setText(userName)
                }
                loginFields.username?.let { password ->
                    input_password.setText(password)
                }
            }
        })
    }

    private fun login() {
        viewModel.setStateEvent(
            AuthStateEvent.GetRequestToken
        )
        setLoginFields()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setLoginFields()
    }

    private fun setLoginFields() {
        viewModel.setLoginFields(
            LoginFields(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }

}
