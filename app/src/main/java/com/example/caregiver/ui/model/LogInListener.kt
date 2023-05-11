package com.example.caregiver.ui.model

interface LogInListener {
    fun logInSuccess(email: String?, password: String?)
    fun logInFailure(exception: Exception?, email: String?, password: String?)
}
