package com.example.firebaseexample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseexample.databinding.ActivityViewDatabaseBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import timber.log.Timber

class ViewDatabaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewDatabaseBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var authListener: FirebaseAuth.AuthStateListener
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_database)
        binding = ActivityViewDatabaseBinding.inflate(layoutInflater)
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
                        this@ViewDatabaseActivity,
                        "Successfully signed in with ${user.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Timber.d("onAuthStateChanged signed out")
                    Toast.makeText(
                        this@ViewDatabaseActivity,
                        "Successfully signed out",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                showData(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun showData(snapshot: DataSnapshot) {
        for (ds in snapshot.children) {
            val userID = auth.currentUser?.uid
            if (userID != null) {
                val userName = ds.child(userID).getValue(UserInformation::class.java)?.name
                val userEmail = ds.child(userID).getValue(UserInformation::class.java)?.email
                val userPhone = ds.child(userID).getValue(UserInformation::class.java)?.phoneNum

                binding.tvUserName.text = userName
                binding.tvUserEmail.text = userEmail
                binding.tvUserPhone.text = userPhone

                Timber.d("showData: name $userName")
                Timber.d("showData: email $userEmail")
                Timber.d("showData: phone $userPhone")
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