package com.example.firebaseexample

import android.Manifest
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseexample.databinding.ActivityUploadBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    private val pathArray = ArrayList<String>()
    private var arrayPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        checkFilePermissions()
        addFilePath()

        binding.btnBackImage.setOnClickListener {
            if (arrayPosition > 0) {
                Timber.d("OnClick: Back an Image")
                arrayPosition -= 1
                loadImageFromStorage()
            }
        }

        binding.btnNextImage.setOnClickListener {
            if (arrayPosition < pathArray.size - 1) {
                Timber.d("OnClick: Next Image")
                arrayPosition += 1
                loadImageFromStorage()
            }
        }

        onClickUploadImage()

    }

    private fun checkFilePermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            var permissionCheck = checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE")
            permissionCheck += checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE")
            if (permissionCheck != 0) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), 1001
                )
            }
        } else {
            Timber.d("checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.")
        }
    }

    private fun addFilePath() {
        Timber.d("addFilePath: Adding file paths")
        val path = System.getenv("EXTERNAL_STORAGE")
        pathArray.add("$path/Pictures/ForApp/image1.jpg")
        pathArray.add("$path/Pictures/ForApp/image2.jpg")
        pathArray.add("$path/Pictures/ForApp/image3.jpg")
        loadImageFromStorage()
    }

    private fun loadImageFromStorage() {
        try {
            val path = pathArray[arrayPosition]
            val file = File(path, "")
            val bitMap = BitmapFactory.decodeStream(FileInputStream(file))
            binding.uploadImage.setImageBitmap(bitMap)
        } catch (e: FileNotFoundException) {
            Timber.e("loadImageFromStorage: FileNotFoundException ${e.message}")
        }
    }

    private fun onClickUploadImage() {
        binding.btnUploadImage.setOnClickListener {
            Timber.d("OnClick: Uploading Image...")
            Snackbar.make(
                binding.parentLayout,
                "OnClick: Uploading Image...",
                Snackbar.LENGTH_SHORT
            ).show()
            binding.progressBar.visibility = View.VISIBLE

            val user = auth.currentUser
            val userID = user?.uid

            val name = binding.imageName.text.toString()
            if (name != "") {
                val uri = Uri.fromFile(File(pathArray[arrayPosition]))
                val storageReference = storageRef.child("images/users/$userID/$name.jpg")
                storageReference.putFile(uri)
                    .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                        override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                            val downloadUrl = p0?.storage?.downloadUrl
                            binding.progressBar.visibility = View.GONE
                        }
                    })
                    .addOnFailureListener {
                        Snackbar.make(
                            binding.parentLayout, "Upload Failed", Snackbar.LENGTH_SHORT
                        ).show()
                        binding.progressBar.visibility = View.GONE
                    }
            }
        }
    }
}