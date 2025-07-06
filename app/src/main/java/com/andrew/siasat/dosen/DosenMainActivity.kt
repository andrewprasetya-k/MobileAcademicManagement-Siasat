package com.andrew.siasat.dosen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.andrew.siasat.databinding.ActivityDosenMainBinding
import com.andrew.siasat.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.andrew.siasat.utils.FirebaseUtils

class DosenMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDosenMainBinding
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDosenMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID") ?: return

        loadDosenData()
//        setupButtonListeners()
    }

    private fun loadDosenData() {
        FirebaseUtils.database.child(FirebaseUtils.USERS_PATH).child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dosen = snapshot.getValue(User::class.java)
                    binding.tvWelcome.text = "Selamat datang, ${dosen?.nama ?: "Dosen"}"
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.tvWelcome.text = "Selamat datang, Dosen"
                }
            })
    }

//    private fun setupButtonListeners() {
//        binding.btnInputNilai.setOnClickListener {
//            startActivity(Intent(this, InputNilaiActivity::class.java).putExtra("USER_ID", userId))
//        }
//
//        binding.btnInputTranskrip.setOnClickListener {
//            startActivity(Intent(this, InputTranskripActivity::class.java).putExtra("USER_ID", userId))
//        }
//
//        binding.btnLihatMataKuliah.setOnClickListener {
//            startActivity(Intent(this, LihatMataKuliahActivity::class.java).putExtra("USER_ID", userId))
//        }
//
//        binding.btnLogout.setOnClickListener {
//            finish()
//        }
//    }
}