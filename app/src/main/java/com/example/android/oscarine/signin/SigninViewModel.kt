package com.example.android.oscarine.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.oscarine.SharedPreference
import com.example.android.oscarine.network.OscarineApi
import com.example.android.oscarine.network.models.login_user.LoginServerResponse
import com.example.android.oscarine.network.models.login_user.LoginUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigninViewModel(private val sharedPreference: SharedPreference): ViewModel() {

    private val _messages = MutableLiveData<String>()
    val messages: LiveData<String>
        get() = _messages

    val username = MutableLiveData<String>()

    val password = MutableLiveData<String>()


    private fun postForSigningInUser(userDetails: LoginUser) {
        OscarineApi.retrofitService.loginUser(userDetails).enqueue(object: Callback<LoginServerResponse> {
            override fun onFailure(call: Call<LoginServerResponse>, t: Throwable) {
                _messages.value = "Failure: " + t.message
            }

            override fun onResponse(
                call: Call<LoginServerResponse>,
                response: Response<LoginServerResponse>
            ) {
                val responseCode = response.code()
                if (responseCode == 200) {
                    _messages.value = "You are now logged in"
                    val token = response.body()!!.access_token
                    saveToken(token)
                    resetValuesOnSigninSuccess()
                } else {
                    _messages.value = "Failed to login"
                }
            }

        })
    }

    fun login() {
        val userDetails =
            LoginUser(
                username = username.value.toString(),
                password = password.value.toString()
            )
        postForSigningInUser(userDetails)
    }

    fun resetValuesOnSigninSuccess() {
        username.value = ""
        password.value = ""
    }

    fun saveToken(token: String) {
        sharedPreference.setToken(token)
    }
}