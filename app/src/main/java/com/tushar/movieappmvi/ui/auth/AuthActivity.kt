package com.tushar.movieappmvi.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.tushar.movieappmvi.R
import com.tushar.movieappmvi.ui.BaseActivity
import com.tushar.movieappmvi.ui.auth.state.AuthStateEvent
import com.tushar.movieappmvi.ui.main.MainActivity
import com.tushar.movieappmvi.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import com.tushar.movieappmvi.viewmodels.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : BaseActivity(), NavController.OnDestinationChangedListener{

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)

        subscribeObservers()
    }

    override fun onResume() {
        super.onResume()
        checkForAuthToken()
    }

    private fun checkForAuthToken() {
        viewModel.setStateEvent(
            AuthStateEvent.CheckPreviousAuthEvent
        )
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(this, Observer { dataState ->
            onDataStateChange(dataState)
            dataState?.data?.let { data ->
                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let { authViewState ->
                        authViewState.requestToken?.let {requestToken ->
                            viewModel.setRequestToken(requestToken)
                        }
                        authViewState.loginResponse?.let { loginResponse ->
                            viewModel.setLoginResponse(loginResponse)
                        }
                        authViewState.authToken?.let { authToken ->
                            viewModel.setAuthToken(authToken)
                        }
                    }
                }
                data.response?.let{event ->
                    event.peekContent().let{ response ->
                        response.message?.let{ message ->
                            if(message == RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE){
                                onFinishCheckPreviousAuthUser()
                            }
                        }
                    }
                }
            }

        })

        viewModel.viewState.observe(this, Observer{
            it.requestToken?.let { requestToken ->
                if(it.authToken == null && it.loginResponse == null){
                    viewModel.setStateEvent(
                        AuthStateEvent.Login(
                            username = it.loginFields?.username!!,
                            password = it.loginFields?.password!!,
                            requestToken = requestToken.requestToken!!
                        )
                    )
                }
            }

            it.loginResponse?.let { loginDetails ->
                if(loginDetails.success!! && it.authToken == null){
                    viewModel.setStateEvent(
                        AuthStateEvent.CreateSession(
                            it.requestToken?.requestToken!!,
                            it.loginFields?.username!!
                        )
                    )
                }

            }

            it.authToken?.let{ authToken ->
                sessionManager.login(authToken)
            }
        })

        sessionManager.cachedToken.observe(this, Observer{ dataState ->
            dataState?.let{ authToken ->
                if(!authToken.token.isNullOrEmpty()){
                    navMainActivity()
                }
            }
        })
    }

    private fun navMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onFinishCheckPreviousAuthUser(){
        fragment_container.visibility = View.VISIBLE
    }

    companion object{
        const val TAG = "AuthActivity"
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        viewModel.cancelActiveJobs()
    }

    override fun showProgress(isVisible: Boolean) {
        if(isVisible){
            progress_bar.visibility = View.VISIBLE
        }else{
            progress_bar.visibility = View.GONE
        }
    }

    override fun expandAppBar() {
        //Do Nothing
    }

    override fun collapseBottomBar() {

    }

    override fun showToolbar(showToolbar: Boolean) {

    }
}
