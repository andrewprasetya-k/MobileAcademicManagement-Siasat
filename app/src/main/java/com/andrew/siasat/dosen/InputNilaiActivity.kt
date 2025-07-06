package com.andrew.siasat.dosen
//
//import android.os.Bundle
//import android.widget.ArrayAdapter
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.andrew.siasat.databinding.ActivityInputNilaiBinding
//import com.andrew.siasat.model.Nilai
//import com.andrew.siasat.utils.FirebaseUtils
//
//class InputNilaiActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityInputNilaiBinding
//    private lateinit var mahasiswaList: MutableList<String>
//    private lateinit var matakuliahList: MutableList<String>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityInputNilaiBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        mahasiswaList = mutableListOf()
//        matakuliahList = mutableListOf()
//
//        loadMahasiswa()
//        loadMatakuliah()
//
//        binding.btnSimpan.setOnClickListener {
//            val mahasiswa = binding.spinnerMahasiswa.selectedItem.toString()
//            val matakuliah = binding.spinnerMatakuliah.selectedItem.toString()
//            val nilai = binding.spinnerNilai.selectedItem.toString()
//            val semester = binding.etSemester.text.toString()
//            val tahunAjaran = binding.etTahunAjaran.text.toString()
//
//            if (mahasiswa == "Pilih Mahasiswa" || matakuliah == "Pilih Matakuliah" || nilai == "Pilih Nilai" || semester.isEmpty() || tahunAjaran.isEmpty()) {
//                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
//            } else {
//                val nilaiObj = Nilai(
//                    mahasiswaId = mahasiswa.split(" - ")[0],
//                    matakuliahId = matakuliah.split(" - ")[0],
//                    nilai = nilai,
//                    semester = semester.toInt(),
//                    tahunAjaran = tahunAjaran
//                )
//                saveNilai(nilaiObj)
//            }
//        }
//    }
//
//    private fun loadMahasiswa() {
//        FirebaseUtils.database.child("users")
//            .orderByChild("role").equalTo("mahasiswa")
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    mahasiswaList.clear()
//                    mahasiswaList.add("Pilih Mahasiswa")
//                    snapshot.children.forEach { data ->
//                        val id = data.key ?: ""
//                        val nama = data.child("nama").value.toString()
//                        mahasiswaList.add("$id - $nama")
//                    }
//                    setupMahasiswaSpinner()
//                }
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@InputNilaiActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }
//
//    private fun loadMatakuliah() {
//        FirebaseUtils.database.child("matakuliah")
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    matakuliahList.clear()
//                    matakuliahList.add("Pilih Matakuliah")
//                    snapshot.children.forEach { data ->
//                        val kode = data.key ?: ""
//                        val nama = data.child("nama").value.toString()
//                        matakuliahList.add("$kode - $nama")
//                    }
//                    setupMatakuliahSpinner()
//                }
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@InputNilaiActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }
//
//    private fun setupMahasiswaSpinner() {
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mahasiswaList)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerMahasiswa.adapter = adapter
//    }
//
//    private fun setupMatakuliahSpinner() {
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, matakuliahList)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerMatakuliah.adapter = adapter
//    }
//
//    private fun saveNilai(nilai: Nilai) {
//        val key = FirebaseUtils.database.child("nilai").push().key
//        key?.let {
//            nilai.id = it
//            FirebaseUtils.database.child("nilai").child(it).setValue(nilai)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "Nilai berhasil disimpan", Toast.LENGTH_SHORT).show()
//                    finish()
//                }
//                .addOnFailureListener {
//                    Toast.makeText(this, "Gagal menyimpan: ${it.message}", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
//}