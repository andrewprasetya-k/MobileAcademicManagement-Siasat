package com.andrew.siasat.mahasiswa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.andrew.siasat.auth.LoginActivity
import com.andrew.siasat.databinding.ActivityMahasiswaMainBinding
import com.google.android.material.navigation.NavigationBarView

class MahasiswaMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMahasiswaMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMahasiswaMainBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, com.andrew.siasat.R.color.primaryDark)
        setContentView(binding.root)

        setupUI()
        setupClickListeners()
        setupBottomNav()
    }

    private fun setupUI() {
        binding.tvWelcome.text = "Halo, Andrew 👋"
        binding.tvNim.text = "NIM: 672022007"
        binding.tvProdiSemester.text = "Teknik Informatika - Semester 3 (2024/2025)"
        binding.tvSksMaks.text = "Beban SKS Maks: 20"
        binding.tvIpk.text = "3.75"
        binding.tvSksTotal.text = "110"
        binding.tvStatusAktif.text = "Aktif"
    }

    private fun setupClickListeners() {
        binding.btnLihatKrs.setOnClickListener {
            Toast.makeText(this, "Menu Kartu Studi dibuka", Toast.LENGTH_SHORT).show()
        }

        binding.btnLihatNilai.setOnClickListener {
            Toast.makeText(this, "Menu Lihat Nilai dibuka", Toast.LENGTH_SHORT).show()
        }

        binding.btnLihatTranskrip.setOnClickListener {
            Toast.makeText(this, "Menu Transkrip dibuka", Toast.LENGTH_SHORT).show()
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
