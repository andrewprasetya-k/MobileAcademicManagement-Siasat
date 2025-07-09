package com.andrew.siasat.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.andrew.siasat.R
import com.andrew.siasat.databinding.ActivityLoginBinding
import com.andrew.siasat.dosen.DosenMainActivity
import com.andrew.siasat.kaprodi.KaprodiMainActivity
import com.andrew.siasat.mahasiswa.MahasiswaMainActivity
import com.andrew.siasat.model.User
import com.andrew.siasat.utils.FirebaseUtils
import com.andrew.siasat.utils.Validator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryDark)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnLogin.setOnClickListener { attemptLogin() }
            btnKaprodiLogin.setOnClickListener {
                startActivity(Intent(this@LoginActivity, KaprodiLoginActivity::class.java))
            }
        }
    }

    private fun attemptLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (!validateInput(username, password)) return

        if (Validator.isMahasiswaIdValid(username) || Validator.isDosenIdValid(username)) {
            authenticateUser(username, password)
        } else {
            showError("Format username tidak valid")
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        var isValid = true

        if (username.isEmpty()) {
            binding.etUsername.error = "Username tidak boleh kosong"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password tidak boleh kosong"
            isValid = false
        }

        return isValid
    }

    private fun authenticateUser(userId: String, password: String) {
        val userRef = FirebaseUtils.database.child(FirebaseUtils.USERS_PATH).child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                if (user == null) {
                    showError("User tidak ditemukan")
                    return
                }

                if (user.password != password) {
                    showError("Password salah")
                    return
                }

                redirectToRoleSpecificScreen(user)
            }

            override fun onCancelled(error: DatabaseError) {
                showError("Gagal memuat data: ${error.message}")
            }
        })
    }

    private fun redirectToRoleSpecificScreen(user: User) {
        val intent = when (user.role) {
            "mahasiswa" -> Intent(this, MahasiswaMainActivity::class.java).apply {
                putExtra("USER_ID", user.id)
                putExtra("USER_NAMA", user.nama)
                startActivity(this)
                finish()
            }
            "dosen" -> Intent(this, DosenMainActivity::class.java)
            "kaprodi" -> Intent(this, DosenMainActivity::class.java)
            else -> {
                showError("Role tidak dikenali")
                return
            }
        }

        intent.putExtra("USER_ID", user.id)
        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
