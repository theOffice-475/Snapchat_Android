package com.example.snapchatandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    val mAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        emailEditText = findViewById(R.id.idEditText)
        passwordEditText = findViewById(R.id.passwordTextView)

        if(mAuth.currentUser != null){
            logIn()
        }
    }

    fun goClicked(view : View){
        //sign in existing user
        mAuth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    logIn()
                } else {
                    //sign up the user

                    mAuth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseDatabase.getInstance().getReference().child("users").child(
                                    task.result!!.user!!.uid).child("email").setValue(emailEditText?.text.toString())
                                logIn()
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }


    }

    fun logIn(){
        //move to another activity
        val intent = Intent(this,SnapsActivity::class.java)
        startActivity(intent)
    }
}
