package com.example.firebaseexample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseexample.databinding.ActivityAddToDatabaseBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import timber.log.Timber


class AddToDatabaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddToDatabaseBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var authListener: FirebaseAuth.AuthStateListener
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to_database)
        binding = ActivityAddToDatabaseBinding.inflate(layoutInflater)
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
                        this@AddToDatabaseActivity,
                        "Successfully signed in with ${user.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Timber.d("onAuthStateChanged signed out")
                    Toast.makeText(
                        this@AddToDatabaseActivity,
                        "Successfully signed out",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.child("Favorite Foods").getValue(String::class.java)
                Timber.d("Value is $value")
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.w("Failed to read value: ${error.toException()}")
            }
        })

        binding.buttonAddNewFood.setOnClickListener {
            Timber.d("OnClick: Attempting to add object to database")
            val newFood = binding.editTextAddNewFood.text.toString()
            if (newFood != "") {
                val user = auth.currentUser
                val userID = user?.uid
                if (userID != null) {
                    myRef.child(userID).child("Food").child("Favorite Foods").child(newFood)
                        .setValue(true)
                    Toast.makeText(this, "Adding $newFood to Database", Toast.LENGTH_SHORT).show()
                    binding.editTextAddNewFood.setText("")
                }
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