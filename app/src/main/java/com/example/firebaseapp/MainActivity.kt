package com.example.firebaseapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firebaseapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)
        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.loginBtn.setOnClickListener {
            auth.createUserWithEmailAndPassword(
                binding.emailEtxt.text.toString(), binding.passwordEtxt.text.toString()
            ).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, task.result.toString(), Toast.LENGTH_LONG).show()
                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(
                        this,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
            }
        }

        binding.googleBtn.setOnClickListener {
            val signInClient = googleSignInClient.signInIntent
            launcher.launch(signInClient)
        }
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)

            if (task.isSuccessful){
                val account:GoogleSignInAccount?=task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(this,"Done",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,SignOutActivity::class.java))
                    }else{
                        Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
        }
    }
}