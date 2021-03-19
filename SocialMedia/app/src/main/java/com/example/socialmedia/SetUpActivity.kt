package com.example.socialmedia

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_set_up.*


class SetUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private  lateinit var profileuri:Uri
    private lateinit var storageReference:StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var uid:String
    private val TAG="firesotre adding data"
    private var isphotoselected = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_up)

        setSupportActionBar(setuptoolbar)
        supportActionBar?.title = "Profile"

        setupprogressbar.visibility= View.VISIBLE

        storageReference=FirebaseStorage.getInstance().getReference()
        firestore= FirebaseFirestore.getInstance()
        uid = Firebase.auth.uid.toString()
        auth=Firebase.auth


        firestore.collection("Users").document(uid).get().addOnSuccessListener {documentSnapshot ->
            if (documentSnapshot!=null){
                if (documentSnapshot != null) {
                    if (documentSnapshot.getString("Name")!=null && documentSnapshot.getString("Tag Line")!=null &&documentSnapshot.getString("Profile Photo")!=null){
                        username.setText(documentSnapshot.getString("Name"))
                        tagline.setText(documentSnapshot.getString("Tag Line"))
                        if (documentSnapshot.getString("Profile Photo")!= "No Profile"){
                        Glide.with(this).load(documentSnapshot.getString("Profile Photo")).into(proflephoto)
                        }
                    }

                    Log.d(TAG, "DocumentSnapshot data: ${documentSnapshot.data}")
                    setupprogressbar.visibility=View.INVISIBLE
                } else {
                    setupprogressbar.visibility=View.INVISIBLE
                    Log.d(TAG, "No such document")
                }
            }
        }.addOnFailureListener { exception ->
            setupprogressbar.visibility=View.INVISIBLE
                    Log.d(TAG, "get failed with ", exception)
                }



        savechange.setOnClickListener {
            setupprogressbar.visibility = View.VISIBLE
            val username = username.text.trim()
            val tagline = tagline.text.trim()
            val imageref = storageReference.child("Profile_Images").child(uid + ".jpg")
            if (isphotoselected) {
                if (username.isNotEmpty() && profileuri != null && tagline.isNotEmpty()) {
                    imageref.putFile(profileuri)
                        .addOnSuccessListener { task ->
                            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                            saveToFirestore(username as Editable, tagline as Editable, imageref)
                            // ...
                        }.addOnFailureListener {
                            Toast.makeText(this, "Profile Photo not Saved", Toast.LENGTH_SHORT)
                                .show()
                            setupprogressbar.visibility = View.INVISIBLE
                        }
                } else {
                    Toast.makeText(this, "Please Select Profile Picture and Fill All Entries", Toast.LENGTH_SHORT).show()
                    setupprogressbar.visibility = View.INVISIBLE
                }
            }
            else{
                if (username.isNotEmpty() && imageref!=null && tagline.isNotEmpty()){
                saveToFirestore(username as Editable, tagline as Editable,imageref)
                }
                else{
                    setupprogressbar.visibility = View.INVISIBLE
                    Toast.makeText(this, "Please Select Profile Picture and Fill All Entries", Toast.LENGTH_SHORT).show()

                }
            }
        }

        addprofile.setOnClickListener {
            if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(this , android.Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
                }
                else{
                    CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
                }
            }
        }

        setupbackbutton.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    private fun saveToFirestore(username: Editable, tagline: Editable, imageref: StorageReference) {
        imageref.downloadUrl.addOnSuccessListener {uri ->
            val users = hashMapOf(
                "Name" to "$username",
                "Tag Line" to "$tagline",
                "Profile Photo" to "${uri.toString()}"
            )

            firestore.collection("Users").document(uid)
                .set(users)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e)
                    Toast.makeText(this, "Profile Not Saved", Toast.LENGTH_SHORT).show()
                }
            setupprogressbar.visibility= View.INVISIBLE
        }.addOnFailureListener {
            val users = hashMapOf(
                "Name" to "$username",
                "Tag Line" to "$tagline",
                "Profile Photo" to "No Profile"
            )

            firestore.collection("Users").document(uid)
                .set(users)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e)
                    Toast.makeText(this, "Profile Not Saved", Toast.LENGTH_SHORT).show()
                }
            setupprogressbar.visibility= View.INVISIBLE
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)
            if (resultCode== Activity.RESULT_OK){
                profileuri = result.uri
                proflephoto.setImageURI(profileuri)
                isphotoselected=true
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