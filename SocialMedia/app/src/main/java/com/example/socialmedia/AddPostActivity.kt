package com.example.socialmedia

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.socialmedia.Model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add_post.*
import java.time.Clock
import java.time.LocalDateTime

class AddPostActivity : AppCompatActivity() {
    private  var postphotouri: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var uid:String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        setSupportActionBar(addposttoolbar)
        supportActionBar?.title = " Add Post"

        uid = Firebase.auth.uid.toString()
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference()


        addpostprogressbar.visibility = View.INVISIBLE

        addpostbackbutton.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        postimage.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3,2)
                .start(this);
        }

       postbutton.setOnClickListener {
           val postcaption = postcaption.text.trim()
           if (postcaption.isNotEmpty() && postphotouri!=null){
               addpostprogressbar.visibility= View.VISIBLE
               val time = FieldValue.serverTimestamp().toString()+".jpg"
               val postref = storageReference.child("Post_images").child(time)
               postref.putFile(postphotouri!!).addOnSuccessListener { task ->
                   // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                   savepostToFirestore(postcaption,postref,time)
                   // ...
               }.addOnFailureListener {
                   Toast.makeText(this, "Post not Saved", Toast.LENGTH_SHORT)
                       .show()
                   addpostprogressbar.visibility= View.INVISIBLE
               }
           }
           else{
               Toast.makeText(this, "Pleas Select Image and add Caption.", Toast.LENGTH_SHORT).show()
           }
       }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun savepostToFirestore(postcaption: CharSequence, postref: StorageReference, time: String) {
        postref.downloadUrl.addOnSuccessListener {uri ->
            val post = Post(postcaption.toString(),uri.toString(),uid,LocalDateTime.now().toString(),time)
//            val post = hashMapOf(
//                "Post_caption" to "$postcaption",
//                "Post_image" to "${uri.toString()}",
//                "User" to "$uid",
//                "Time" to "${LocalDateTime.now()}",
//                "Postref" to "${time}"
//            )

            firestore.collection("Posts")
                .add(post)
                .addOnSuccessListener {
                    addpostprogressbar.visibility= View.INVISIBLE
                    Toast.makeText(this, "Post  Saved", Toast.LENGTH_SHORT).show()
                    Log.d("Post Add Tag", "DocumentSnapshot successfully written!")
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e -> Log.w("Post Add Tag", "Error writing document", e)
                    postref.delete()
                    addpostprogressbar.visibility= View.INVISIBLE
                    Toast.makeText(this, "Post Not Saved", Toast.LENGTH_SHORT).show()
                }

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)
            if (resultCode== Activity.RESULT_OK){
                postphotouri = result.uri
                postimage.setImageURI(postphotouri)
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toast.makeText(this,"${result.error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
        super.onBackPressed()
    }
}