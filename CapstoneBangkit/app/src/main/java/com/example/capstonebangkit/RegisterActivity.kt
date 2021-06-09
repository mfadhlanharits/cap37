package com.example.capstonebangkit

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase


class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var editTextFullName: EditText? = null
    private var editTextEmail: EditText? = null
    private var editTextPassword: EditText? = null
    private var editTextReTypePassword: EditText? = null
    private var mRegister: Button? = null
    private var progressBar: ProgressBar? = null
    private var mAuth: FirebaseAuth? = null
    private var tvtoLogin: TextView? = null

    override fun onCreate(
            savedInstanceState: Bundle?
    ) {
        supportActionBar!!.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        mRegister = findViewById<View>(R.id.btn_register) as Button
        mRegister!!.setOnClickListener(this)
        editTextFullName = findViewById<View>(R.id.tv_register_name) as EditText
        editTextEmail = findViewById<View>(R.id.tv_register_email) as EditText
        editTextPassword = findViewById<View>(R.id.tv_register_password) as EditText
        editTextReTypePassword = findViewById<View>(R.id.tv_register_repassword) as EditText
        progressBar = findViewById<View>(R.id.pb_register) as ProgressBar
        progressBar!!.visibility = View.INVISIBLE
        tvtoLogin = findViewById<View>(R.id.tv_register_tologin) as TextView
        tvtoLogin!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_register -> startActivity(Intent(this, MainActivity::class.java))
            R.id.btn_register -> registerUser()
            R.id.tv_register_tologin -> launchtoLogin()
        }
    }

    private fun launchtoLogin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun registerUser() {
        val email = editTextEmail!!.text.toString().trim { it <= ' ' }
        val password = editTextPassword!!.text.toString().trim { it <= ' ' }
        val fullName = editTextFullName!!.text.toString().trim { it <= ' ' }
        val repassword = editTextReTypePassword!!.text.toString().trim { it <= ' ' }
        if (fullName.isEmpty()) {
            editTextFullName!!.error = "Full name is required"
            editTextFullName!!.requestFocus()
            return
        }
        if (email.isEmpty()) {
            editTextEmail!!.error = "Email is required"
            editTextEmail!!.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail!!.error = "Please provide valid email"
            editTextEmail!!.requestFocus()
            return
        }
        if (password.isEmpty()) {
            editTextPassword!!.error = "Password is required"
            editTextPassword!!.requestFocus()
            return
        }
        if (password.length < 6) {
            editTextPassword!!.error = "Minimum Password length is 6"
            editTextPassword!!.requestFocus()
            return
        }
        if (repassword.isEmpty()) {
            editTextReTypePassword!!.error = "Retype the password is required"
            editTextReTypePassword!!.requestFocus()
            return
        }
        if (!repassword.contentEquals(password)) {
            editTextReTypePassword!!.error = "Password Must Match"
            editTextReTypePassword!!.requestFocus()
            return
        }
        progressBar!!.visibility = View.VISIBLE
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(fullName, email)
                    UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName).build()
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(user).addOnCompleteListener(OnCompleteListener<Void?> { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "User has been registered successfully and try to login", Toast.LENGTH_LONG).show()
                                    progressBar!!.visibility = View.GONE
                                } else {
                                    Toast.makeText(this, "Please try to login", Toast.LENGTH_LONG
                                    ).show()
                                    progressBar!!.visibility = View.GONE
                                }
                            })
                } else {
                    Toast.makeText(this, "Try to register again or user already registered", Toast.LENGTH_LONG).show()
                    progressBar!!.visibility = View.GONE
                }
            }
    }
}