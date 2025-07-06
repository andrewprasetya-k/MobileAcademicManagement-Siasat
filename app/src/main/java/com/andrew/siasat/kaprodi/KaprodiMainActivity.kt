package com.andrew.siasat.kaprodi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andrew.siasat.databinding.ActivityKaprodiMainBinding
import com.andrew.siasat.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.andrew.siasat.utils.FirebaseUtils

class KaprodiMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKaprodiMainBinding
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKaprodiMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID") ?: run {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadUserData()
        setupButtons()
    }

    private fun loadUserData() {
        FirebaseUtils.database.child("users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        binding.tvWelcome.text = "Welcome, ${it.nama} (Kaprodi)"
                    } ?: run {
                        Toast.makeText(this@KaprodiMainActivity, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@KaprodiMainActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupButtons() {
        binding.apply {
            btnAddMatakuliah.setOnClickListener {
//                startActivity(Intent(this@KaprodiMainActivity, AddMatakuliahActivity::class.java))
            }

            btnAddTranskrip.setOnClickListener {
//                startActivity(Intent(this@KaprodiMainActivity, AddTranskripActivity::class.java))
            }

//            btnManageDosen.setOnClickListener {
//                // Implement dosen management functionality
//                Toast.makeText(this@KaprodiMainActivity, "Dosen Management", Toast.LENGTH_SHORT).show()
//            }

            btnLogout.setOnClickListener {
                finish()
            }
        }
    }
}