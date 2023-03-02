package com.example.myloginapp

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.myloginapp.ui.theme.MyLoginAppTheme
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private lateinit var analytics: FirebaseAnalytics
private lateinit var auth: FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = Firebase.analytics
        // Initialize Firebase Auth
        auth = Firebase.auth

        setContent {
            MyLoginAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    LoginInterface("Enter Username", "Enter Password")
                }
            }
        }
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
           // reload()
        }
    }
    @Composable
    fun LoginInterface(
        text1: String,
        text2: String,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            var email = remember { mutableStateOf(TextFieldValue()) }
            var password = remember { mutableStateOf(TextFieldValue()) }
            TextField(
                value = email.value,
                onValueChange = {email.value = it},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = text1) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password.value,
                onValueChange = {password.value = it},
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                placeholder = { Text(text = text2) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ){
                Button(onClick = {
                    loginUser(email.value.text,password.value.text) }) {
                    Text(text = "Login")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { createAccount(email.value.text,password.value.text) }) {
                    Text(text = "SignUp")
                }

            }
        }
    }
    fun createAccount(email: String, password: String) {
        if (!checkTextFields(email,password)) {
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser

                } else {
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Log.d(ContentValues.TAG, "createAccount: $email --- $password")
                    Toast.makeText(this, "Signup failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    fun loginUser(email: String, password: String){
        if (!checkTextFields(email,password)) {
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

                if (!task.isSuccessful) {
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkTextFields(email: String, password: String): Boolean {
        var valid = true
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Plz enter a valid email", Toast.LENGTH_SHORT).show()
            valid = false
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Plz enter a valid password", Toast.LENGTH_SHORT).show()
            valid = false
        }
        return valid
    }
}


