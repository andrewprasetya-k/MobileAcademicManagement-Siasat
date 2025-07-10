package com.andrew.siasat.kaprodi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andrew.siasat.auth.LoginActivity
import com.andrew.siasat.databinding.ActivityKaprodiMainBinding
import com.andrew.siasat.model.Dosen
import com.andrew.siasat.utils.FirebaseUtils

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
        FirebaseUtils.database.child("dosens").child(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(Dosen::class.java)
                if (user != null) {
                    binding.tvWelcome.text = "Halo, ${user.nama} "
                    binding.tvKaprodiName.text = "Nama: ${user.nama}"
                    binding.tvKaprodiNip.text = "NIP: ${user.id}"
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
                val intent = Intent(this@KaprodiMainActivity, TambahMatkulActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            }

            btnViewMatakuliah.setOnClickListener {
                val intent = Intent(this@KaprodiMainActivity, LihatMatkulKaprodiActivity::class.java)
                intent.putExtra("USER_ID", userId)  // jika diperlukan di activity tujuan
                startActivity(intent)
            }

            btnLogout.setOnClickListener {
                startActivity(Intent(this@KaprodiMainActivity, LoginActivity::class.java))
                finishAffinity()
            }
        }
    }
}
