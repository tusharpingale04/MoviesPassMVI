package com.tushar.movieappmvi.ui.auth.state


sealed class AuthStateEvent{
    data class Login(val username: String, val password: String, val requestToken: String) : AuthStateEvent()
    object GetRequestToken : AuthStateEvent()
    data class CreateSession(val requestToken: String, val username: String) : AuthStateEvent()
    object CheckPreviousAuthEvent: AuthStateEvent()
    object None: AuthStateEvent()
}