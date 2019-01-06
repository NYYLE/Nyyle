package com.nyyle

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    // Firebase references
    private var mUserDatabaseReference: DatabaseReference? = null
    private var mParamDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    // Global Variables
    private val TAG = "RegisterActivity"
    private var email: String? = null
    private var password: String? = null
    private var currentUserCount: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initialise()
    }

    fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mParamDatabaseReference = mDatabase!!.reference!!.child("Parameters")
        mUserDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()

        mParamDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUserCount = snapshot.child("currentUserCount").value.toString().toInt()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun createAccount(view: View) {
        email = emailText.text.toString()
        password = passwordText.text.toString()

        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            mAuth!!
                    .createUserWithEmailAndPassword(email!!, password!!)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")

                            val userId = mAuth!!.currentUser!!.uid

                            // Verify Email
                            verifyEmail()

                            // Update System Parameters
                            mParamDatabaseReference!!.child("currentUserCount").setValue(currentUserCount!!.plus(1))

                            // Update user profile information
                            val currentUserDb = mUserDatabaseReference!!.child(currentUserCount!!.minus(1).toString())
                            currentUserDb.child("name").setValue("test")
                            currentUserDb.child("userId").setValue(userId)
                            currentUserDb.child("birthday").setValue("dob")
                            currentUserDb.child("bio").setValue("test bio")
                            currentUserDb.child("rating").setValue("5")
                            currentUserDb.child("score").setValue("5")

                            updateUserInfoAndUI()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserInfoAndUI() {
        // Start next activity
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun verifyEmail() {
        val mUser = mAuth!!.currentUser;
        mUser!!.sendEmailVerification()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,
                                "Verification email sent to " + mUser.email,
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.exception)
                        Toast.makeText(this,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }
}
