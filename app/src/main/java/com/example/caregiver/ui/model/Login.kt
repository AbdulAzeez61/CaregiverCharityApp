
package com.example.caregiver.ui.model

import android.util.Log
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
                    Log.d("Login","logged in succesfully")
                } else {
                    observer.logInFailure(task.exception, email, password)
                    Log.d("Login","login unsuccesful")
                }
            })
    }
}
//interface LogInListener {
//    fun logInSuccess(email: String, password: String)
//    fun logInFailure(exception: Exception?, email: String, password: String)
//}