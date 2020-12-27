package com.example.firebaseexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebaseexample.databinding.ActivityAddUserInformationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import timber.log.Timber

class AddUserInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserInformationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var authListener: FirebaseAuth.AuthStateListener
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user_information)
        binding = ActivityAddUserInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        myRef = firebaseDatabase.reference

        authListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val user = p0.currentUser
                if (user != null) {
                    Timber.d("onAuthStateChanged: signed in ${user.uid}")
                    Toast.makeText(
                        this@AddUserInformationActivity,
                        "Successfully signed in with ${user.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Timber.d("onAuthStateChanged signed out")
                    Toast.makeText(
                        this@AddUserInformationActivity,
                        "Successfully signed out",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.d("onDataChange: Added information to database ${snapshot.value}")
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        binding.btnSubmit.setOnClickListener {
            Timber.d("OnClick: Submit pressed")
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val phoneNum = binding.etPhone.text.toString()
            val userID = auth.currentUser?.uid

            if (name != "" && email != "" && phoneNum != "" && userID != null) {
                val userInfo = UserInformation(name, email, phoneNum)
                myRef.child("users").child(userID).setValue(userInfo)
                Toast.makeText(this, "New information has been saved", Toast.LENGTH_SHORT).show()
                binding.etName.setText("")
                binding.etEmail.setText("")
                binding.etPhone.setText("")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authListener)
    }
}