package com.example.dayorganizer
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dayorganizer.databinding.FirebaseRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class FirebaseReg : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FirebaseRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FirebaseRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.confirmRegisterButton.setOnClickListener {
            val email = binding.emailRegister.text.toString()
            val password = binding.passwordRegister.text.toString()

            if (email.isNotEmpty() && password.length >= 6) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Успешная регистрация", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Ошибка: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Email или пароль некорректны", Toast.LENGTH_SHORT).show()
            }
        }
    }
}