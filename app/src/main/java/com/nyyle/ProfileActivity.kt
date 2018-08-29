package com.nyyle

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    // Firebase References
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    // Global Variables
    // -------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initialise()
    }

    override fun onStart() {
        super.onStart()
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

        //emailText!!.text = mUser.email   <<< readonly
        //email verified lark   !!.text = mUser.isEmailVerified.toString()

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nameText.text = Editable.Factory.getInstance().newEditable(snapshot.child("name").value as String)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()
    }

    fun submitChanges(view: View) {
        if (!nameText.text.isNullOrEmpty()) {
            val userId = mAuth!!.currentUser!!.uid

            mDatabase = FirebaseDatabase.getInstance()
            mDatabaseReference = mDatabase!!.reference!!.child("Users")

            val currentUserDb = mDatabaseReference!!.child(userId)
            currentUserDb.child("name").setValue(nameText.text.toString())

            Toast.makeText(this, "Changes applied.",
                    Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please fill in all the fields.",
                    Toast.LENGTH_SHORT).show()
        }
    }
}
