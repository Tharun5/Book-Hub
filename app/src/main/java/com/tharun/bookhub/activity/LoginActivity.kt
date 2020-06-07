package com.tharun.bookhub.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.tharun.bookhub.R

class LoginActivity : AppCompatActivity() {
    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    val validMobileNum = "0123456789"
    val validPassword = "1234"
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val intent= Intent(this@LoginActivity,MainActivity::class.java)
        setContentView(R.layout.activity_login)
        if (isLoggedIn) {
            startActivity(intent)
            finish()
        }
    title = "Log In"
        etMobileNumber = findViewById(R.id.etMobileNum)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {

            val mobileNumber = etMobileNumber.text.toString()
            val password = etPassword.text.toString()
            if (mobileNumber == validMobileNum && password == validPassword){
                savePreferences()
                startActivity(intent)
            }else{
                Toast.makeText(this@LoginActivity,"Incorrect Credentials",Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onPause() {
        super.onPause()
        finish()
    }

    fun savePreferences() {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
    }
}
