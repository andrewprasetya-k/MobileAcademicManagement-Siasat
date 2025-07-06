package com.andrew.siasat.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.andrew.siasat.databinding.ActivityKaprodiLoginBinding
import com.andrew.siasat.kaprodi.KaprodiMainActivity
import com.andrew.siasat.model.User
import com.andrew.siasat.utils.FirebaseUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class KaprodiLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKaprodiLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKaprodiLoginBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, com.andrew.siasat.R.color.primaryDark)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                authenticateKaprodi(username, password)
            }
        }
    }

    private fun authenticateKaprodi(username: String, password: String) {
        FirebaseUtils.database.child(FirebaseUtils.USERS_PATH).child(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        if (user?.role == "kaprodi" && user.password == password) {
                            startActivity(
                                Intent(this@KaprodiLoginActivity, KaprodiMainActivity::class.java)
                                    .putExtra("USER_ID", user.id)
                            )
                            finish()
                        } else {
                            Toast.makeText(this@KaprodiLoginActivity, "Akses ditolak", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@KaprodiLoginActivity, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@KaprodiLoginActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}