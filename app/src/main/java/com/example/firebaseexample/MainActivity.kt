package com.example.firebaseexample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseexample.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var authListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        auth = FirebaseAuth.getInstance()
        authListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val user = p0.currentUser
                if (user != null) {
                    Timber.d("onAuthStateChanged: signed in ${user.uid}")
                    Toast.makeText(this@MainActivity, "Successfully signed in with ${user.email}", Toast.LENGTH_SHORT).show()
                } else {
                    Timber.d("onAuthStateChanged signed out")
                    Toast.makeText(this@MainActivity, "Successfully signed out", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.createUserButton.setOnClickListener {
            val email = binding.textEmail.text.toString()
            val pass = binding.textPassword.text.toString()
            if (email != "" && pass != "") {
                auth.createUserWithEmailAndPassword(email, pass)
                Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(this, "You didn't fill in all the fields", Toast.LENGTH_SHORT).show()
        }

        binding.emailSignInButton.setOnClickListener {
            val email = binding.textEmail.text.toString()
            val pass = binding.textPassword.text.toString()
            if (email != "" && pass != "") {
                auth.signInWithEmailAndPassword(email, pass)
            } else Toast.makeText(this, "You didn't fill in all the fields", Toast.LENGTH_SHORT).show()
        }

        binding.emailSignOutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this@MainActivity, "Signing out", Toast.LENGTH_SHORT).show()
        }

        binding.addItemScreen.setOnClickListener {
            val intent = Intent(this, AddToDatabaseActivity::class.java)
            startActivity(intent)
        }

        binding.viewItemsScreen.setOnClickListener {
            val intent = Intent(this, ViewDatabaseActivity::class.java)
            startActivity(intent)
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

