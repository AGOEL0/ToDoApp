package com.example.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.todo.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this ,R.layout.activity_main)
        binding.createAcctBTN.setOnClickListener{
            val intent=Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        auth=Firebase.auth
        binding.emailSignInButton.setOnClickListener{
            loginUser()
        }


    }

    private fun loginUser() {
        val email=binding.email.text.toString()
        val password=binding.password.text.toString()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAGY", "signInWithEmail:success")
                    val user = auth.currentUser
                    goTo()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAGY", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
            }


    }
    private fun goTo() {
        val intent=Intent(this,AddActivity::class.java)
        startActivity(intent)
    }


}
