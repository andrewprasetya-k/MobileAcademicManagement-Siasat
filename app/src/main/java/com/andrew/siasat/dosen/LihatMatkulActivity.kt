package com.andrew.siasat.dosen

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andrew.siasat.R
import com.andrew.siasat.utils.FirebaseUtils
import com.google.firebase.database.*

class LihatMatkulActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val matkulData = mutableListOf<String>()
    private val dosenId: String by lazy { intent.getStringExtra("USER_ID") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lihat_matkul)

        listView = findViewById(R.id.listViewMatkul)

        if (dosenId.isEmpty()) {
            Toast.makeText(this, "ID Dosen tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadMatakuliahDanMahasiswa()
    }

    private fun loadMatakuliahDanMahasiswa() {
        FirebaseUtils.database.child("matakuliahs")
            .orderByChild("dosenId").equalTo(dosenId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(matkulSnapshot: DataSnapshot) {
                    matkulData.clear()

                    if (!matkulSnapshot.exists()) {
                        Toast.makeText(this@LihatMatkulActivity, "Tidak ada matakuliah", Toast.LENGTH_SHORT).show()
                        updateListView()
                        return
                    }

                    val matkulList = matkulSnapshot.children.toList()
                    processNextMatkul(matkulList.iterator())
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LihatMatkulActivity, "Gagal: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun processNextMatkul(iterator: Iterator<DataSnapshot>) {
        if (!iterator.hasNext()) {
            updateListView()
            return
        }

        val matkulSnapshot = iterator.next()
        val matkulId = matkulSnapshot.child("id").getValue(String::class.java) ?: ""
        val kode = matkulSnapshot.child("kode").getValue(String::class.java) ?: "-"
        val nama = matkulSnapshot.child("nama").getValue(String::class.java) ?: "-"
        val sks = matkulSnapshot.child("sks").getValue(Int::class.java) ?: 0
        val semester = matkulSnapshot.child("semester").getValue(Int::class.java) ?: 0

        val matkulHeader = "$kode - $nama\nSKS: $sks | Semester: $semester"
        val mahasiswaList = mutableListOf<String>()

        // Ambil mahasiswa yang ambil matkul ini dari KRS
        FirebaseUtils.database.child("kartustudis")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(krsSnapshot: DataSnapshot) {
                    val mahasiswaIds = mutableSetOf<String>()
                    krsSnapshot.children.forEach { krs ->
                        val mkIds = krs.child("matakuliahIds").children.mapNotNull { it.getValue(String::class.java) }
                        if (mkIds.contains(matkulId)) {
                            krs.child("mahasiswaId").getValue(String::class.java)?.let { mahasiswaIds.add(it) }
                        }
                    }

                    if (mahasiswaIds.isEmpty()) {
                        matkulData.add("$matkulHeader\n  Tidak ada mahasiswa terdaftar.")
                        processNextMatkul(iterator)
                        return
                    }

                    processNextMahasiswa(matkulId, mahasiswaIds.iterator(), mahasiswaList) {
                        val combinedData = "$matkulHeader\n" + mahasiswaList.joinToString("\n") { "  $it" }
                        matkulData.add(combinedData)
                        processNextMatkul(iterator)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    processNextMatkul(iterator)
                }
            })
    }

    private fun processNextMahasiswa(matkulId: String, iterator: Iterator<String>, resultList: MutableList<String>, onComplete: () -> Unit) {
        if (!iterator.hasNext()) {
            onComplete()
            return
        }

        val mahasiswaId = iterator.next()

        FirebaseUtils.database.child("users").child(mahasiswaId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(userSnapshot: DataSnapshot) {
                    val nama = userSnapshot.child("nama").getValue(String::class.java) ?: "Tanpa Nama"
                    val nim = userSnapshot.child("id").getValue(String::class.java) ?: mahasiswaId

                    // Ambil nilai mahasiswa untuk matkul ini
                    FirebaseUtils.database.child("nilais")
                        .orderByChild("mahasiswaId").equalTo(mahasiswaId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(nilaiSnapshot: DataSnapshot) {
                                var nilaiHuruf = "Belum dinilai"
                                nilaiSnapshot.children.forEach { nilai ->
                                    val mkId = nilai.child("matakuliahId").getValue(String::class.java)
                                    if (mkId == matkulId) {
                                        val nilaiAngka = nilai.child("nilai").getValue(Double::class.java)
                                        nilaiHuruf = konversiNilai(nilaiAngka)
                                    }
                                }
                                resultList.add("$nim - $nama : $nilaiHuruf")
                                processNextMahasiswa(matkulId, iterator, resultList, onComplete)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                processNextMahasiswa(matkulId, iterator, resultList, onComplete)
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    processNextMahasiswa(matkulId, iterator, resultList, onComplete)
                }
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
            else -> "Belum dinilai"
        }
    }

    private fun updateListView() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, matkulData)
        listView.adapter = adapter
    }
}
