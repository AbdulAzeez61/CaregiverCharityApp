
package com.example.caregiver.ui.model

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

data class Login(
    val firebaseAuth: FirebaseAuth,
    val observer: LogInListener
) {

    fun logIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    observer.logInSuccess(email, password)
                } else {
                    observer.logInFailure(task.exception, email, password)
                }
            })
    }

}