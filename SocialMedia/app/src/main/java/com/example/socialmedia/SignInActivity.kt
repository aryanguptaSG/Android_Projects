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
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG="Sign In Tag"
    override fun onCreate(savedInstanceState: Bundle?) {
        auth=Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signinprogressbar.visibility=View.INVISIBLE

        signuphint.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }
        signinbutton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val email=signinemail.text.toString().trim()
        val pass = signinpass.text.toString().trim()

        if (email.isEmpty() && pass.isEmpty()) {
            Toast.makeText(this, "Pleas Enter email and password  ", Toast.LENGTH_SHORT).show()
        }
        else {
            signinprogressbar.visibility=View.VISIBLE
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        Toast.makeText(this, "Log In Successful", Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed. Please Fill Correct Information",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                        signinprogressbar.visibility = View.INVISIBLE

                        // ...
                    }

                    // ...
                }
        }

    }

    private fun updateUI(user: FirebaseUser?) {
        if(user!==null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }


    }

    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        if(currentUser!==null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}