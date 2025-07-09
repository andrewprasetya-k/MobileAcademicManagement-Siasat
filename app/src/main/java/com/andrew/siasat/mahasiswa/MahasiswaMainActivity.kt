package com.andrew.siasat.mahasiswa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.andrew.siasat.auth.LoginActivity
import com.andrew.siasat.databinding.ActivityMahasiswaMainBinding
import com.andrew.siasat.utils.FirebaseUtils
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MahasiswaMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMahasiswaMainBinding
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMahasiswaMainBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, com.andrew.siasat.R.color.primaryDark)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID") ?: run {
            Toast.makeText(this, "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        setupClickListeners()
        setupBottomNav()
    }

    private fun setupUI() {
        val mahasiswaRef = FirebaseUtils.database.child("mahasiswas").child(userId)

        mahasiswaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val nama = intent.getStringExtra("USER_NAMA") ?: "Mahasiswa"
                    val nim = snapshot.child("nim").getValue(String::class.java) ?: "-"
                    val prodi = snapshot.child("prodi").getValue(String::class.java) ?: "-"
                    val semester = snapshot.child("semester").getValue(Int::class.java) ?: 1
                    val ipk = snapshot.child("ipk").getValue(Double::class.java) ?: 0.0
                    val sksTotal = snapshot.child("sksTotal").getValue(Int::class.java) ?: 0
                    val status = snapshot.child("status").getValue(String::class.java) ?: "-"
                    val sksMaks = snapshot.child("sksMaks").getValue(Int::class.java) ?: 0

                    binding.tvWelcome.text = "Halo, $nama \uD83D\uDC4B"
                    binding.tvNim.text = "NIM: $nim"
                    binding.tvProdiSemester.text = "$prodi - Semester $semester (2024/2025)"
                    binding.tvSksMaks.text = "Beban SKS Maks: $sksMaks"
                    binding.tvIpk.text = String.format("%.2f", ipk)
                    binding.tvSksTotal.text = sksTotal.toString()
                    binding.tvStatusAktif.text = status
                } else {
                    Toast.makeText(this@MahasiswaMainActivity, "Data mahasiswa tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MahasiswaMainActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openLihatKst() {
        val intent = Intent(this, LihatKstActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openLihatNilai() {
        val intent = Intent(this, LihatNilaiActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun openLihatTranskrip() {
        val intent = Intent(this, LihatTranskripActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    private fun setupClickListeners() {
        binding.btnLihatKrs.setOnClickListener {
            openLihatKst()
        }

        binding.btnLihatNilai.setOnClickListener {
            openLihatNilai()
        }

        binding.btnLihatTranskrip.setOnClickListener {
            openLihatTranskrip()
        }

        binding.btnJadwalKuliah.setOnClickListener {
            Toast.makeText(this, "Menu Jadwal Kuliah dibuka", Toast.LENGTH_SHORT).show()
        }

        binding.btnTagihan.setOnClickListener {
            Toast.makeText(this, "Menu Tagihan dibuka", Toast.LENGTH_SHORT).show()
        }

        binding.btnKeaktifanMahasiswa.setOnClickListener {
            Toast.makeText(this, "Menu Poin KKM dibuka", Toast.LENGTH_SHORT).show()
        }

        binding.btnHasilStudi.setOnClickListener {
            Toast.makeText(this, "Menu Hasil Studi dibuka", Toast.LENGTH_SHORT).show()
        }

        binding.btnPerwalian.setOnClickListener {
            Toast.makeText(this, "Menu Perwalian dibuka", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNav() {
        binding.bottomNavigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                com.andrew.siasat.R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    true
                }
                com.andrew.siasat.R.id.nav_akademik -> {
                    Toast.makeText(this, "Akademik", Toast.LENGTH_SHORT).show()
                    true
                }
                com.andrew.siasat.R.id.nav_presensi -> {
                    Toast.makeText(this, "Presensi", Toast.LENGTH_SHORT).show()
                    true
                }
                com.andrew.siasat.R.id.nav_keuangan -> {
                    Toast.makeText(this, "Keuangan", Toast.LENGTH_SHORT).show()
                    true
                }
                com.andrew.siasat.R.id.nav_profil -> {
                    Toast.makeText(this, "Profil", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        })
    }

    override fun onBackPressed() {
        finishAffinity()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
