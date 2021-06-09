package com.example.capstonebangkit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class AccountSettingsActivity : AppCompatActivity() {
    private var logoutButton: Button? = null
    private var uploadButton: Button? = null

    private lateinit var user: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var userID: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)
        setTitle("Account Detail")

        logoutButton = findViewById<View>(R.id.btn_account_logout) as Button
        uploadButton = findViewById<View>(R.id.btn_account_upload) as Button
        logoutButton!!.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountSettingsActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        uploadButton!!.setOnClickListener {
            val intent = Intent(this@AccountSettingsActivity, UploadActivity::class.java)
            startActivity(intent)
        }


        user = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users")
        userID = user.uid

        val email = user!!.email
        val username = user!!.displayName

        val emailTextView: TextView = findViewById(R.id.account_email)
        val fullNameTextView: TextView = findViewById(R.id.account_fullName)

        emailTextView.setText(email)
        fullNameTextView.setText(username)

        reference.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile = snapshot.getValue(
                        User::class.java
                )
                if (userProfile != null) {
                    val fullName = userProfile.fullName
                    val email = userProfile.email
                    fullNameTextView.text = fullName
                    emailTextView.text = email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AccountSettingsActivity, "Something wrong Happened!", Toast.LENGTH_SHORT)
                        .show()
            }
        })
    }
}
