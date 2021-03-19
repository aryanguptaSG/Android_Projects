package com.example.socialmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private val TAG="SignUp Tag"
    override fun onCreate(savedInstanceState: Bundle?) {

        auth=Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        signupprogressbar.visibility=View.INVISIBLE

        signinhint.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }
        signupbutton.setOnClickListener {
            signUp()
        }

    }

    private fun signUp() {
        val email = signupemail.text.toString().trim()
        val pass=signuppass.text.toString().trim()
        if (email.isEmpty() && pass.isEmpty()) {
            Toast.makeText(this, "Pleas Enter email and password  ", Toast.LENGTH_SHORT).show()
        }
        else{
            signupprogressbar.visibility=View.VISIBLE
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                        signupprogressbar.visibility = View.INVISIBLE
                    }

                    // ...
                }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user!==null){
            startActivity(Intent(this,SetUpActivity::class.java))
            finish()
        }
    }


    override fun onStart() {
        super.onStart()
        val currentuser = auth.currentUser
        if(currentuser != null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}