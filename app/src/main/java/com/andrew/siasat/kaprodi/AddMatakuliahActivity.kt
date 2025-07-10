package com.andrew.siasat.kaprodi

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andrew.siasat.databinding.ActivityAddMatakuliahBinding
import com.andrew.siasat.model.Matakuliah
import com.andrew.siasat.utils.FirebaseUtils
import com.google.firebase.database.DatabaseReference

class TambahMatkulActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMatakuliahBinding
    private lateinit var database: DatabaseReference
    private val dosenList = mutableListOf<String>()
    private val dosenIdMap = mutableMapOf<String, String>() // Untuk mapping nama ke ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMatakuliahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseUtils.database

        loadDosenToSpinner()

        binding.btnSimpanMatkul.setOnClickListener {
            simpanMatakuliah()
        }
    }

    private fun loadDosenToSpinner() {
        database.child("dosens")
            .get()
            .addOnSuccessListener { snapshot ->
                dosenList.clear()
                dosenIdMap.clear()

                dosenList.add("Pilih Dosen")
                snapshot.children.forEach { userSnapshot ->
                    val id = userSnapshot.key ?: return@forEach
                    val nama = userSnapshot.child("nama").getValue(String::class.java) ?: "Tanpa Nama"
                    dosenList.add("$id - $nama")
                    dosenIdMap["$id - $nama"] = id
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dosenList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerDosen.adapter = adapter
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Gagal memuat data dosen: ${error.message}", Toast.LENGTH_LONG).show()
            }

    }

    private fun simpanMatakuliah() {
        val kode = binding.etKodeMatkul.text.toString().trim()
        val nama = binding.etNamaMatkul.text.toString().trim()
        val sksStr = binding.etSksMatkul.text.toString().trim()
        val semesterStr = binding.etSemesterMatkul.text.toString().trim()
        val dosenSelected = binding.spinnerDosen.selectedItem.toString()

        if (kode.isEmpty() || nama.isEmpty() || sksStr.isEmpty() || semesterStr.isEmpty() || dosenSelected == "Pilih Dosen") {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val sks = sksStr.toIntOrNull()
        val semester = semesterStr.toIntOrNull()
        val dosenId = dosenIdMap[dosenSelected]

        if (sks == null || semester == null || dosenId == null) {
            Toast.makeText(this, "Input tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val matkulId = kode
        val matakuliah = Matakuliah(
            id = matkulId,
            kode = kode,
            nama = nama,
            sks = sks,
            semester = semester,
            dosenId = dosenId
        )

        database.child("matakuliahs").child(matkulId).setValue(matakuliah)
            .addOnSuccessListener {
                // Tambahkan ke jadwals secara otomatis
                val jadwalId = "jadwal_$matkulId"
                val jadwalData = mapOf(
                    "id" to jadwalId,
                    "matakuliahId" to matkulId,
                    "namaMatakuliah" to nama,
                    "dosenId" to dosenId,
                    "hari" to "-",          // Default kosong, bisa diedit nanti
                    "jam" to "-",
                    "ruangan" to "-"
                )

                database.child("jadwals").child(jadwalId).setValue(jadwalData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Mata kuliah & jadwal berhasil disimpan", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Mata kuliah disimpan, tapi gagal membuat jadwal", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan data matakuliah", Toast.LENGTH_SHORT).show()
            }
    }

}
