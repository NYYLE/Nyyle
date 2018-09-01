package com.nyyle

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    // Firebase References
    private var mParamDatabaseReference: DatabaseReference? = null
    private var mUserDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    // Global Variables
    var currentUserCount: Int? = null
    var currentProfileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialise()
    }

    private fun initialise() {
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

        getProfile()

        nameText.text = currentProfileName
    }

    fun profileTapped(view: View) {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun getProfile() {
        val userChosen = 0

        mUserDatabaseReference!!.child(userChosen.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentProfileName = snapshot.child("name").value.toString()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                return
            }
        })
    }
}
