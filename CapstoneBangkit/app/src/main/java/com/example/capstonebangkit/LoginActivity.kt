package com.example.capstonebangkit

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private var editTextEmail: EditText? = null
    private var editTextPassword: EditText? = null
    private var mLogin: Button? = null
    private var progressBar: ProgressBar? = null
    private var mAuth: FirebaseAuth? = null
    private var tvtoRegister: TextView? = null
    private var tvtoForgot: TextView? = null

    var auth = FirebaseAuth.getInstance();
     override fun onCreate(savedInstanceState: Bundle?){
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_login)
         supportActionBar!!.hide()
        mAuth = FirebaseAuth.getInstance()
        mLogin = findViewById<View>(R.id.btn_login) as Button
        mLogin!!.setOnClickListener(this)
        editTextEmail = findViewById<View>(R.id.tv_login_email) as EditText
        editTextPassword = findViewById<View>(R.id.tv_login_password) as EditText
        progressBar = findViewById<View>(R.id.pb_login) as ProgressBar
        progressBar!!.visibility = View.INVISIBLE
         tvtoRegister = findViewById<View>(R.id.tv_login_toregister) as TextView
         tvtoRegister!!.setOnClickListener(this)
         tvtoForgot = findViewById<View>(R.id.tv_forgotpassword) as TextView
         tvtoForgot!!.setOnClickListener(this)
         if (auth.getCurrentUser() != null) {
         val intent = Intent(this@LoginActivity, MainActivity::class.java);
         startActivity(intent)
             finish()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_welcome -> startActivity(Intent(this, MainActivity::class.java))
            R.id.btn_login -> userLogin()
            R.id.tv_login_toregister -> launchtoRegister()
            R.id.tv_forgotpassword -> launchtoForgot()
        }
    }

    private fun launchtoRegister() {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
    }
    private fun launchtoForgot()
    {
        val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }


    private fun userLogin() {
        val email = editTextEmail!!.text.toString().trim { it <= ' ' }
        val password = editTextPassword!!.text.toString().trim { it <= ' ' }
        if (email.isEmpty()) {
            editTextEmail!!.error = "Email is required"
            editTextEmail!!.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail!!.error = "Please provide a valid email"
            editTextEmail!!.requestFocus()
            return
        }
        if (password.isEmpty()) {
            editTextPassword!!.error = "Password is required"
            editTextPassword!!.requestFocus()
            return
        }
        if (password.length < 6) {
            editTextPassword!!.error = "Password at least 6 characters"
            editTextPassword!!.requestFocus()
            return
        }
        progressBar!!.visibility = View.VISIBLE
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user!!.isEmailVerified) {
                        startActivity(Intent(this, MainActivity::class.java))
                        Toast.makeText(this, "Login successfull", Toast.LENGTH_LONG)
                            .show()
                        finish()
                    } else {
                        user.sendEmailVerification()
                        Toast.makeText(this, "Check your email for Verification", Toast.LENGTH_LONG)
                            .show()
                        progressBar!!.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Failed to login, please check your email and password again",
                        Toast.LENGTH_LONG
                    ).show()
                    progressBar!!.visibility = View.GONE
                }
            }
    }

}