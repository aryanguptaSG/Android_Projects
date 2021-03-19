package com.example.socialmedia.Daos

import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*

class DeletePost {
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore

    public fun deletepost(postid:String) {
        storageReference = FirebaseStorage.getInstance().getReference()
        firestore = FirebaseFirestore.getInstance()

        firestore.collection("Posts").document(postid).get().addOnSuccessListener {documentSnapshot ->
                if (documentSnapshot != null) {

                    val postref = documentSnapshot.getString("postref")

                        storageReference.child("Post_images").child(postref!!).delete().addOnSuccessListener {
                           deleteformfirestore(postid)
                        }

                    Log.d("main activity tag", "DocumentSnapshot data: ${documentSnapshot.data}")
                } else {
                    Log.d("main activity tag", "No such document")
                }

        }.addOnFailureListener { exception ->
            Log.d("main activity tag", "get failed with ", exception)
        }

    }

    private fun deleteformfirestore(postid: String){

        firestore.collection("Posts").document(postid).delete().addOnSuccessListener {
        }

    }

}