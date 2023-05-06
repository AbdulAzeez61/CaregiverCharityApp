import android.app.Activity
import com.example.caregiver.ui.model.LogInListener
import com.example.caregiver.ui.model.Login
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.lang.Exception
import java.util.concurrent.Executor

class LoginUnitTest {

    @Mock
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var login: Login

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        login = Login(firebaseAuth, object : LogInListener {
            override fun logInSuccess(email: String, password: String) {
                // success scenario
            }

            override fun logInFailure(exception: Exception?, email: String, password: String) {
                // failure scenario
            }
        })
    }

    @Test
    fun logInSuccess() {
        val email = "cool@cool.com"
        val password = "123456"
        val successTask = mockTask<AuthResult>(true)
        val listener: LogInListener = mock(LogInListener::class.java)
        `when`(firebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(successTask)
        login.logIn(email, password)
        // assert that logInSuccess() method is called in the success scenario
        verify(listener).logInSuccess(email, password)
    }

    @Test
    fun logInFailure() {
        // Create a mock LogInListener object
        val mockListener = mock(LogInListener::class.java)

        // Set up the mock to expect the logInFailure() method call
        val email = "cool@cool.com"
        val password = "123456"
        val failureTask = mockTask<AuthResult>(false)
        `when`(firebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(failureTask)
        login.logIn(email, password)
        verify(mockListener).logInFailure(any(Exception::class.java), eq(email), eq(password))
    }



    private fun <T> mockTask(isSuccessful: Boolean): Task<T> {
        return object : Task<T>() {
            override fun isComplete(): Boolean = true

            override fun isSuccessful(): Boolean = isSuccessful

            override fun getResult(): T? = null

            override fun getException(): Exception? = null

            override fun <X : Throwable> getResult(p0: Class<X>): T? {
                return null
            }

            override fun addOnCompleteListener(listener: OnCompleteListener<T>): Task<T> {
                listener.onComplete(this)
                return this
            }

            override fun isCanceled(): Boolean {
                TODO("Not yet implemented")
            }

            override fun addOnFailureListener(p0: OnFailureListener): Task<T> {
                // Simply return this same Task object since we don't need to actually perform any action
                return this
            }

            override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<T> {
                TODO("Not yet implemented")
                return this
            }

            override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<T> {
                TODO("Not yet implemented")
                return this
            }

            override fun addOnSuccessListener(p0: Executor, p1: OnSuccessListener<in T>): Task<T> {
                TODO("Not yet implemented")
                return this
            }

            override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in T>): Task<T> {
                TODO("Not yet implemented")
                return this
            }

            override fun addOnSuccessListener(p0: OnSuccessListener<in T>): Task<T> {
                TODO("Not yet implemented")
                return this
            }

        }
    }
}
