package com.example.dayorganizer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dayorganizer.databinding.FirebaseLoginBinding
import com.google.firebase.auth.FirebaseAuth

class FirebaseLogin : AppCompatActivity() {

    private lateinit var binding: FirebaseLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FirebaseLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            goToMain()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            goToMain()
                        } else {
                            Toast.makeText(
                                this,
                                "Ошибка входа: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, FirebaseReg::class.java))
        }
    }

    private fun goToMain() {
        val user = FirebaseAuth.getInstance().currentUser
        val name = user?.displayName ?: user?.email ?: "Пользователь"

        getSharedPreferences("prefs", MODE_PRIVATE).edit()
            .putString("username", name)
            .apply()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

