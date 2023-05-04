package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.myapplication.databinding.ActivityOpeningBinding
import com.example.myapplication.view.login.LoginActivity
import com.example.myapplication.view.order_customer.OrderCustomerActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OpeningActivity : ActivityMessage() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOpeningBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        auth = Firebase.auth

        try {
            binding.tvOpeningVersion.text.apply {
                packageManager.getPackageInfo(packageName, 0).versionName
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

      //  Handler(Looper.getMainLooper()).postDelayed({checkUser()}, 2000)

    }

    private fun checkUser() {
        toMain()
        if (auth.currentUser != null) {
            toMain()
        } else {
            toLogin()
        }
    }

    private fun toMain() {
        val intent = Intent(this, OrderCustomerActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun toLogin() {
        val intent = Intent(this, LoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    }