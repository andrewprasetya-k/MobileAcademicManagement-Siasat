package com.andrew.siasat.kaprodi

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andrew.siasat.databinding.ActivityViewMatkulKaprodiBinding
import com.andrew.siasat.utils.FirebaseUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LihatMatkulKaprodiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewMatkulKaprodiBinding

    private val dosenList = mutableListOf<String>()
    private val dosenIdMap = mutableMapOf<String, String>()

    private val matkulList = mutableListOf<String>()
    private val matkulIdMap = mutableMapOf<String, String>()

    private val mahasiswaNilaiList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMatkulKaprodiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadDosenData()

        binding.spinnerDosen.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    matkulList.clear()
                    matkulIdMap.clear()
                    updateMatkulSpinner()
                    mahasiswaNilaiList.clear()
                    updateListView()
                } else {
                    val selectedDosenId = dosenIdMap[dosenList[position]]
                    if (selectedDosenId != null) {
                        loadMatakuliahData(selectedDosenId)
                    }
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        binding.spinnerMatkul.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    mahasiswaNilaiList.clear()
                    updateListView()
                } else {
                    val selectedMatkulId = matkulIdMap[matkulList[position]]
                    if (selectedMatkulId != null) {
                        loadMahasiswaDanNilai(selectedMatkulId)
                    }
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadDosenData() {
        dosenList.clear()
        dosenIdMap.clear()
        dosenList.add("Pilih Dosen")

        FirebaseUtils.database.child("dosens")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { data ->
                        val id = data.key ?: return@forEach
                        val nama = data.child("nama").getValue(String::class.java) ?: "Tanpa Nama"
                        val display = "$id - $nama"
                        dosenList.add(display)
                        dosenIdMap[display] = id
                    }
                    val adapter = ArrayAdapter(this@LihatMatkulKaprodiActivity, android.R.layout.simple_spinner_item, dosenList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerDosen.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadMatakuliahData(dosenId: String) {
        matkulList.clear()
        matkulIdMap.clear()
        matkulList.add("Pilih Matakuliah")

        FirebaseUtils.database.child("matakuliahs")
            .orderByChild("dosenId").equalTo(dosenId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { data ->
                        val id = data.child("id").getValue(String::class.java) ?: return@forEach
                        val nama = data.child("nama").getValue(String::class.java) ?: "Tanpa Nama"
                        val display = "$id - $nama"
                        matkulList.add(display)
                        matkulIdMap[display] = id
                    }
                    updateMatkulSpinner()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun updateMatkulSpinner() {
        val adapter = ArrayAdapter(this@LihatMatkulKaprodiActivity, android.R.layout.simple_spinner_item, matkulList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMatkul.adapter = adapter
    }

    private fun loadMahasiswaDanNilai(matkulId: String) {
        mahasiswaNilaiList.clear()

        FirebaseUtils.database.child("kartustudis")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mahasiswaIds = mutableSetOf<String>()

                    snapshot.children.forEach { krs ->
                        val mkList = krs.child("matakuliahIds").children.mapNotNull { it.getValue(String::class.java) }
                        if (mkList.contains(matkulId)) {
                            val mahasiswaId = krs.child("mahasiswaId").getValue(String::class.java) ?: return@forEach
                            mahasiswaIds.add(mahasiswaId)
                        }
                    }

                    if (mahasiswaIds.isEmpty()) {
                        mahasiswaNilaiList.add("Tidak ada mahasiswa.")
                        updateListView()
                        return
                    }

                    val iterator = mahasiswaIds.iterator()
                    val mahasiswaDataList = mutableListOf<String>()

                    fun nextMahasiswa() {
                        if (!iterator.hasNext()) {
                            mahasiswaNilaiList.clear()
                            mahasiswaNilaiList.addAll(mahasiswaDataList)
                            updateListView()
                            return
                        }

                        val mhsId = iterator.next()
                        FirebaseUtils.database.child("users").child(mhsId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnap: DataSnapshot) {
                                    val nama = userSnap.child("nama").getValue(String::class.java) ?: "Tanpa Nama"
                                    val nim = userSnap.child("id").getValue(String::class.java) ?: mhsId

                                    FirebaseUtils.database.child("nilais")
                                        .orderByChild("mahasiswaId").equalTo(mhsId)
                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(nilaiSnap: DataSnapshot) {
                                                var nilai = "Belum Dinilai"
                                                nilaiSnap.children.forEach { n ->
                                                    val mkId = n.child("matakuliahId").getValue(String::class.java)
                                                    if (mkId == matkulId) {
                                                        val angka = n.child("nilai").getValue(Double::class.java)
                                                        nilai = konversiNilai(angka)
                                                    }
                                                }
                                                mahasiswaDataList.add("$nim - $nama : $nilai")
                                                nextMahasiswa()
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                nextMahasiswa()
                                            }
                                        })
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    nextMahasiswa()
                                }
                            })
                    }

                    nextMahasiswa()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun konversiNilai(nilai: Double?): String {
        return when (nilai) {
            4.0 -> "A"
            3.5 -> "AB"
            3.0 -> "B"
            2.5 -> "BC"
            2.0 -> "C"
            1.5 -> "D"
            0.0 -> "E"
            else -> "Belum Dinilai"
        }
    }

    private fun updateListView() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mahasiswaNilaiList)
        binding.listViewMahasiswa.adapter = adapter
    }
}
