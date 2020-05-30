package com.tushar.movieappmvi.ui.auth.state

import com.tushar.movieappmvi.api.auth.response.LoginResponse
import com.tushar.movieappmvi.api.auth.response.RequestResponse
import com.tushar.movieappmvi.models.AuthToken

class AuthViewState(
    var loginFields: LoginFields? = LoginFields(),
    var authToken: AuthToken? = null,
    var requestToken: RequestResponse? = null,
    var loginResponse: LoginResponse? = null
)

data class LoginFields(
    val username: String? = null,
    val password: String? = null
) {
    class LoginError {
        companion object {

            fun mustFillAllFields(): String {
                return "Username/Password is Empty."
            }

            fun none(): String {
                return "None"
            }

        }
    }

    fun isValid(): String {
        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            return LoginError.mustFillAllFields()
        }
        return LoginError.none()
    }
}

