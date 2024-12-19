package com.example.proyekakhirpapb

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.proyekakhirpapb.ui.theme.ProyekakhirpapbTheme
import com.google.firebase.auth.FirebaseAuth

class SignUp : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance() // Inisialisasi FirebaseAuth

        setContent {
            ProyekakhirpapbTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyScreen { email, password, confirmPassword ->
                        signUp(email, password, confirmPassword)
                    }
                }
            }
        }
    }

    private fun signUp(email: String, password: String, confirmPassword: String) {
        val context = this
        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password == confirmPassword) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Jika pendaftaran berhasil, buka SignIn Activity
                            Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, AuthViewModel::class.java)
                            startActivity(intent)
                            finish() // Menutup SignUp Activity
                        } else {
                            Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Password is not matching", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun MyScreen(onSignUp: (String, String, String) -> Unit) {
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var showCat by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isFormValid = emailInput.isNotBlank() && passwordInput.isNotBlank() && confirmPasswordInput.isNotBlank()

    val buttonColor by animateColorAsState(
        targetValue = if (isFormValid) Color.Green else Color.Gray
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation() // Menyembunyikan teks yang diketik
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPasswordInput,
            onValueChange = { confirmPasswordInput = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation() // Menyembunyikan teks yang diketik
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isFormValid) {
                    onSignUp(emailInput, passwordInput, confirmPasswordInput)
                    showCat = true
                }
            },
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Sign Up")
        }
    }
}




