package com.andrew.siasat.kaprodi
//
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.andrew.siasat.databinding.ActivityAddMataKuliahBinding
//import com.andrew.siasat.model.MataKuliah
//import com.andrew.siasat.utils.FirebaseUtils
//import com.google.firebase.database.DatabaseReference
//
//class AddMataKuliahActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityAddMataKuliahBinding
//    private lateinit var database: DatabaseReference
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityAddMataKuliahBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        database = FirebaseUtils.database
//
//        binding.btnSimpan.setOnClickListener {
//            val kode = binding.etKode.text.toString().trim()
//            val nama = binding.etNama.text.toString().trim()
//            val sks = binding.etSks.text.toString().trim()
//            val semester = binding.etSemester.text.toString().trim()
//            val dosenPengampu = binding.etDosenPengampu.text.toString().trim()
//
//            if (validateInput(kode, nama, sks, semester, dosenPengampu)) {
//                val mataKuliah = MataKuliah(
//                    kode = kode,
//                    nama = nama,
//                    sks = sks.toInt(),
//                    semester = semester.toInt(),
//                    dosenPengampu = dosenPengampu
//                )
//
//                saveMataKuliah(mataKuliah)
//            }
//        }
//    }
//
//    private fun validateInput(
//        kode: String,
//        nama: String,
//        sks: String,
//        semester: String,
//        dosen: String
//    ): Boolean {
//        if (kode.isEmpty()) {
//            binding.etKode.error = "Kode tidak boleh kosong"
//            return false
//        }
//        if (nama.isEmpty()) {
//            binding.etNama.error = "Nama tidak boleh kosong"
//            return false
//        }
//        if (sks.isEmpty() || !sks.matches(Regex("\\d+"))) {
//            binding.etSks.error = "SKS harus angka"
//            return false
//        }
//        if (semester.isEmpty() || !semester.matches(Regex("\\d+"))) {
//            binding.etSemester.error = "Semester harus angka"
//            return false
//        }
//        if (dosen.isEmpty() || !dosen.matches(Regex("67\\d{3}"))) {
//            binding.etDosenPengampu.error = "Kode dosen tidak valid"
//            return false
//        }
//        return true
//    }
//
//    private fun saveMataKuliah(mataKuliah: MataKuliah) {
//        database.child(FirebaseUtils.MATA_KULIAH_PATH).child(mataKuliah.kode)
//            .setValue(mataKuliah)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Mata kuliah berhasil disimpan", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Gagal menyimpan: ${it.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//}