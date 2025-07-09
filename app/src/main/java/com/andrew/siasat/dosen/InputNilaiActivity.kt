package com.andrew.siasat.dosen

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andrew.siasat.databinding.ActivityInputNilaiBinding
import com.andrew.siasat.model.Nilai
import com.andrew.siasat.utils.FirebaseUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class InputNilaiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputNilaiBinding
    private val matakuliahList = mutableListOf<String>()
    private val mahasiswaList = mutableListOf<String>()
    private val dosenId: String by lazy { intent.getStringExtra("USER_ID") ?: "" }

    private var selectedMatakuliahId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputNilaiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (dosenId.isEmpty()) {
            Toast.makeText(this, "ID Dosen tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadMatakuliahDosen(dosenId)
        setupNilaiSpinner()

        binding.spinnerMatakuliah.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    mahasiswaList.clear()
                    updateMahasiswaSpinner()
                    selectedMatakuliahId = null
                } else {
                    selectedMatakuliahId = matakuliahList[position].split(" - ")[0]
                    loadMahasiswaByMatakuliah(selectedMatakuliahId!!)
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        binding.btnSimpan.setOnClickListener {
            val mahasiswa = binding.spinnerMahasiswa.selectedItem.toString()
            val matakuliah = binding.spinnerMatakuliah.selectedItem.toString()
            val nilaiHuruf = binding.spinnerNilai.selectedItem.toString()

            if (mahasiswa == "Pilih Mahasiswa" || matakuliah == "Pilih Matakuliah" || nilaiHuruf == "Pilih Nilai") {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val mahasiswaId = mahasiswa.split(" - ")[0]
            val matakuliahId = matakuliah.split(" - ")[0]
            val nilaiObj = Nilai(
                mahasiswaId = mahasiswaId,
                matakuliahId = matakuliahId,
                nilai = hurufKeAngka(nilaiHuruf),
            )

            saveNilai(nilaiObj)
        }
    }

    private fun loadMatakuliahDosen(dosenId: String) {
        FirebaseUtils.database.child("jadwals")
            .orderByChild("dosenId").equalTo(dosenId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    matakuliahList.clear()
                    matakuliahList.add("Pilih Matakuliah")
                    snapshot.children.forEach { data ->
                        val matakuliahId = data.child("matakuliahId").getValue(String::class.java) ?: return@forEach
                        val namaMatakuliah = data.child("namaMatakuliah").getValue(String::class.java) ?: "Tanpa Nama"
                        matakuliahList.add("$matakuliahId - $namaMatakuliah")
                    }
                    val adapter = ArrayAdapter(this@InputNilaiActivity, android.R.layout.simple_spinner_item, matakuliahList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerMatakuliah.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadMahasiswaByMatakuliah(matakuliahId: String) {
        mahasiswaList.clear()
        mahasiswaList.add("Pilih Mahasiswa")

        FirebaseUtils.database.child("kartustudis")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mahasiswaIds = mutableSetOf<String>()

                    snapshot.children.forEach { krsSnapshot ->
                        val matkulList = krsSnapshot.child("matakuliahIds").children.mapNotNull { it.getValue(String::class.java) }
                        if (matkulList.contains(matakuliahId)) {
                            val mahasiswaId = krsSnapshot.child("mahasiswaId").getValue(String::class.java) ?: return@forEach
                            mahasiswaIds.add(mahasiswaId)
                        }
                    }

                    if (mahasiswaIds.isEmpty()) {
                        Toast.makeText(this@InputNilaiActivity, "Tidak ada mahasiswa untuk matakuliah ini", Toast.LENGTH_SHORT).show()
                        updateMahasiswaSpinner()
                        return
                    }

                    mahasiswaIds.forEach { mhsId ->
                        FirebaseUtils.database.child("users").child(mhsId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                    val nama = userSnapshot.child("nama").getValue(String::class.java) ?: "Tanpa Nama"
                                    mahasiswaList.add("$mhsId - $nama")
                                    updateMahasiswaSpinner()
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun updateMahasiswaSpinner() {
        val adapter = ArrayAdapter(this@InputNilaiActivity, android.R.layout.simple_spinner_item, mahasiswaList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMahasiswa.adapter = adapter
    }

    private fun setupNilaiSpinner() {
        val nilaiOptions = listOf("Pilih Nilai", "A", "AB", "B", "BC", "C", "D", "E")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nilaiOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerNilai.adapter = adapter
    }

    private fun hurufKeAngka(nilai: String): Double {
        return when (nilai) {
            "A" -> 4.0
            "AB" -> 3.5
            "B" -> 3.0
            "BC" -> 2.5
            "C" -> 2.0
            "D" -> 1.5
            "E" -> 0.0
            else -> 0.0
        }
    }

    private fun saveNilai(nilai: Nilai) {
        FirebaseUtils.database.child("nilais")
            .orderByChild("mahasiswaId").equalTo(nilai.mahasiswaId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var existingKey: String? = null

                    snapshot.children.forEach { data ->
                        val existingMatkulId = data.child("matakuliahId").getValue(String::class.java)
                        if (existingMatkulId == nilai.matakuliahId) {
                            existingKey = data.key
                        }
                    }

                    val key = existingKey ?: FirebaseUtils.database.child("nilais").push().key

                    key?.let {
                        val nilaiToSave = nilai.copy(id = it)
                        FirebaseUtils.database.child("nilais").child(it).setValue(nilaiToSave)
                            .addOnSuccessListener {
                                Toast.makeText(this@InputNilaiActivity, "Nilai berhasil disimpan", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@InputNilaiActivity, "Gagal menyimpan nilai", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@InputNilaiActivity, "Gagal menyimpan nilai", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
