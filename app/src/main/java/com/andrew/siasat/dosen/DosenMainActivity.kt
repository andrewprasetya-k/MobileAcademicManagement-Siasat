package com.andrew.siasat.dosen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andrew.siasat.auth.LoginActivity
import com.andrew.siasat.databinding.ActivityDosenMainBinding
import com.andrew.siasat.model.Dosen
import com.andrew.siasat.model.User
import com.andrew.siasat.utils.FirebaseUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DosenMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDosenMainBinding
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDosenMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID") ?: run {
            Toast.makeText(this, "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadDosenData()
        setupButtonListeners()
    }

    private fun loadDosenData() {
        FirebaseUtils.database.child(FirebaseUtils.DOSEN_PATH).child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dosen = snapshot.getValue(Dosen::class.java)
                    val nama = dosen?.nama ?: "Dosen"
                    val nidn = dosen?.id ?: "-"
                    binding.tvWelcome.text = "Halo, $nama"
                    binding.tvDosenName.text = "Nama: $nama"
                    binding.tvNidn.text = "NIDN: $nidn"
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DosenMainActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupButtonListeners() {
        binding.btnInputNilai.setOnClickListener {
            startActivity(Intent(this, InputNilaiActivity::class.java).putExtra("USER_ID", userId))
        }

        binding.btnLihatMatkul.setOnClickListener {
            startActivity(Intent(this, LihatMatkulActivity::class.java).putExtra("USER_ID", userId))
        }

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
