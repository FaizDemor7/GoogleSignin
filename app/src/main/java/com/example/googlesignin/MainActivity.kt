package com.example.googlesignin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.googlesignin.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class MainActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityMainBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth:FirebaseAuth

    //constants
    private companion object{
        private const val RC_SIGN_IN =100
        private const val TAG="GOOGLE_SIGN_IN_TAG"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //CONFIGURE GOOGLE signin
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient=GoogleSignIn.getClient(this,googleSignInOptions)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkuser()

        //google signin button
        binding.googleSignInBtn.setOnClickListener{

            //begin signin
            Log.d(TAG,"onCreate google signin begin")
            val intent= googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)

    }

        }

    private fun checkuser() {
        // check if user is log in or not
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            //user is logged in
            // startActivity
            startActivity(Intent(this@MainActivity,ProfileActivity::class.java))
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Result returned from lauching the intent for google signin
        if (requestCode== RC_SIGN_IN)
        {
            Log.d(TAG,"onActivityResult: Google SignIn result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                // SignIn Successful
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
                }
            catch (e:Exception){

                //SignIn Failed
                Log.d(TAG,"onActivity : ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount) {

        Log.d(TAG, "firebaseAuthWithGoogleAccount: Begin the firebase for google signin")

        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                //login success
                Log.d(TAG, "firebaseAuthWithGoogleAccount: LoggedIn")
                //get login user id
                val firebaseUser = firebaseAuth.currentUser
                //get user details
                val uid= firebaseUser!!.uid
                val email= firebaseUser.email

                Log.d(TAG, "firebaseAuthWithGoogleAccount: Uid: $uid")
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Email: $email")

                //check if user is exists or not

                if(authResult.additionalUserInfo!!.isNewUser){
                    //user is new ~ Acc Created
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Account Created...\n$email")
                    Toast.makeText(this@MainActivity,"Account Created..\n$email",Toast.LENGTH_SHORT).show()
                                    }
            else{
                //exisiting User
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Existing User...\n$email")
                    Toast.makeText(this@MainActivity,"Existing User Created..\n$email",Toast.LENGTH_SHORT).show()
                }

                // startActivity
                startActivity(Intent(this@MainActivity,ProfileActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                //login failed
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Login failed due to ${e.message}")
                Toast.makeText(this@MainActivity,"Login Failed due to ${e.message}",Toast.LENGTH_SHORT).show()


            }


    }


}





