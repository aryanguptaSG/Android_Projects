package com.example.socialmedia

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.socialmedia.Daos.DeletePost
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_delete_account.*
import kotlinx.android.synthetic.main.activity_set_up.*

class DeleteAccountActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        setSupportActionBar(deleteaccounttoolbar)
        supportActionBar?.title = "Delete Account"

        auth = Firebase.auth
        firestore= FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference()

        deleteprogressbar.visibility = View.INVISIBLE

        DeleteButton.setOnClickListener {
            deleteAccount(auth.currentUser!!.uid.toString())
        }

    }

    private fun deleteAccount(uid: String) {
        if (deleteemail.text.trim().isNotEmpty() && deletepassword.text.trim().isNotEmpty()){
            val user = Firebase.auth.currentUser!!
            val credential = EmailAuthProvider
                .getCredential(deleteemail.text.toString().trim(), deletepassword.text.toString().trim())

            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    Log.d("re-authenticated", "User re-authenticated.")
                            if (task.isSuccessful) {
                                val deletepost = DeletePost()
                                val alert = AlertDialog.Builder(this)
                                alert.setTitle("Are You Sure")
                                alert.setMessage("After Deleting, It Will No Longer Remain")
                                alert.setPositiveButton("Yes",{dialogInterface: DialogInterface?, i: Int ->
                                    deleteprogressbar.visibility = View.VISIBLE

                                    firestore.collection("Posts")
                                        .whereEqualTo("user", uid)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            for (document in documents) {
                                                Log.d(
                                                    "delete account tag",
                                                    "${document.id} => ${document.data}"
                                                )
                                                deletepost.deletepost(document.id)
                                            }
                                            Toast.makeText(
                                                this,
                                                "All Posts has been deleted",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.w(
                                                "delete account tag",
                                                "Error getting documents: ",
                                                exception
                                            )
                                        }

                                    storageReference.child("Profile_Images").child(uid + ".jpg")
                                        .delete().addOnCompleteListener {
                                            firestore.collection("Users").document(uid).delete()
                                                .addOnCompleteListener {
                                                    Toast.makeText(
                                                        this,
                                                        "Profile Has Been Deleted!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    val user = Firebase.auth.currentUser!!
                                                    val credential = EmailAuthProvider
                                                        .getCredential(
                                                            deleteemail.text.toString(),
                                                            deletepassword.text.toString()
                                                        )

// Prompt the user to re-provide their sign-in credentials
                                                    user.reauthenticate(credential)
                                                        .addOnCompleteListener {
                                                            Log.d(
                                                                "re-authenticated",
                                                                "User re-authenticated."
                                                            )
                                                            user.delete()
                                                                .addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        Toast.makeText(
                                                                            this,
                                                                            "Your Account is Deleted !!",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                        Log.d(
                                                                            "Delete User",
                                                                            "User account deleted."
                                                                        )
                                                                        auth.signOut()
                                                                        startActivity(
                                                                            Intent(
                                                                                this,
                                                                                SignUpActivity::class.java
                                                                            )
                                                                        )
                                                                        finish()
                                                                    }
                                                                }.addOnFailureListener {
                                                                    Log.d(
                                                                        "Delete User",
                                                                        "User account deleted Not."
                                                                    )
                                                                }
                                                        }


                                                }
                                        }

                                })
                                alert.setNegativeButton("No",{ dialogInterface : DialogInterface?, i: Int ->  })
                                alert.show()


                            }
                            else{
                                Toast.makeText(this, "Incorrect Information", Toast.LENGTH_SHORT).show()
                            }
                        }

        }
        else{
            Toast.makeText(this, "Please Fill Email And Password.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
        super.onBackPressed()
    }

}
