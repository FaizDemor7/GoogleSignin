package com.example.googlesignin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.example.googlesignin.databinding.ActivityProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityProfileBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth= FirebaseAuth.getInstance()
        checkUser()
        
        //handle logout click
        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()

        }

    }

    private fun checkUser() {
        //get currentUser
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser==null){
            //user not logged in
                   startActivity(Intent(this,MainActivity::class.java))
                    finish()


        }
        else{
            //user logged in
            //get user info
            val email=firebaseUser.email
            //set text
            binding.emailTv.text=email
        }
       googleSignInClient.signOut().addOnCompleteListener {
        startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }
}