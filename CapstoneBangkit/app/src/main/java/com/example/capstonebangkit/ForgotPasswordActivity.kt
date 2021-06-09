package com.example.capstonebangkit

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordActivity : AppCompatActivity() {
    private var emailEditText: EditText? = null
    private var resetPassword: Button? = null
    private var progessBar: ProgressBar? = null
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        supportActionBar!!.hide()
        emailEditText = findViewById<View>(R.id.tv_forgot_email) as EditText
        resetPassword = findViewById<View>(R.id.btn_forgot) as Button
        progessBar = findViewById<View>(R.id.progressBar) as ProgressBar
        mAuth = FirebaseAuth.getInstance()
        progessBar!!.visibility = View.INVISIBLE
        resetPassword!!.setOnClickListener { resetPassword() }
    }

    private fun resetPassword() {
        val email = emailEditText!!.text.toString().trim { it <= ' ' }
        if (email.isEmpty()) {
            emailEditText!!.error = "Email is required"
            emailEditText!!.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText!!.error = "Please provide valid email"
            emailEditText!!.requestFocus()
            return
        }
        progessBar!!.visibility = View.VISIBLE
        mAuth!!.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Check Your email to reset Password",
                    Toast.LENGTH_LONG
                ).show()
                progessBar!!.visibility = View.GONE
            } else {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Try Again, Something Wrong Happend",
                    Toast.LENGTH_LONG
                ).show()
                progessBar!!.visibility = View.GONE
            }
        }
    }
}