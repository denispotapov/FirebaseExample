package com.example.firebaseexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseexample.databinding.ActivityAddUserInformationBinding
import com.example.firebaseexample.databinding.ActivityCrashReportBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrashReportBinding
    private lateinit var text: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash_report)
        binding = ActivityCrashReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnError1.setOnClickListener {
            binding.editText.setText(text)
        }
    }
}