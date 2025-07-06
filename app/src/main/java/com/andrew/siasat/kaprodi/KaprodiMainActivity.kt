package com.andrew.siasat.kaprodi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andrew.siasat.databinding.ActivityKaprodiMainBinding
import com.andrew.siasat.model.User
import com.andrew.siasat.utils.FirebaseUtils

import com.andrew.siasat.auth.LoginActivity

class KaprodiMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKaprodiMainBinding
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKaprodiMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil USER_ID dari Intent
        userId = intent.getStringExtra("USER_ID") ?: run {
            Toast.makeText(this, "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadUserData()
        setupButtons()
    }

    private fun loadUserData() {
        FirebaseUtils.database.child("users").child(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    binding.tvWelcome.text = "Selamat datang, ${user.username} (Kaprodi)"
                } else {
                    Toast.makeText(this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memuat data user", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupButtons() {
        binding.apply {
            btnAddMatakuliah.setOnClickListener {
                Toast.makeText(this@KaprodiMainActivity, "Menu Tambah Mata Kuliah dibuka", Toast.LENGTH_SHORT).show()
                // startActivity(Intent(this@KaprodiMainActivity, AddMatakuliahActivity::class.java))
            }

            btnAddTranskrip.setOnClickListener {
                Toast.makeText(this@KaprodiMainActivity, "Menu Tambah Transkrip dibuka", Toast.LENGTH_SHORT).show()
                // startActivity(Intent(this@KaprodiMainActivity, AddTranskripActivity::class.java))
            }

            btnLogout.setOnClickListener {
                Toast.makeText(this@KaprodiMainActivity, "Logout berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@KaprodiMainActivity, LoginActivity::class.java))
                finishAffinity()
            }
        }
    }
}
