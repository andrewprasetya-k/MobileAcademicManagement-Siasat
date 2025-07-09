package com.andrew.siasat.dosen

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andrew.siasat.R
import com.andrew.siasat.utils.FirebaseUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

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

        loadMatkulMahasiswaNilai()
    }

    private fun loadMatkulMahasiswaNilai() {
        FirebaseUtils.database.child("jadwals")
            .orderByChild("dosenId").equalTo(dosenId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(this@LihatMatkulActivity, "Tidak ada matakuliah", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val matkulIds = mutableSetOf<String>()
                    snapshot.children.forEach { jadwal ->
                        val matkulId = jadwal.child("matakuliahId").getValue(String::class.java)
                        matkulId?.let { matkulIds.add(it) }
                    }

                    if (matkulIds.isEmpty()) {
                        Toast.makeText(this@LihatMatkulActivity, "Tidak ada matakuliah", Toast.LENGTH_SHORT).show()
                        return
                    }

                    matkulIds.forEach { matkulId ->
                        FirebaseUtils.database.child("kartustudis")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(krsSnapshot: DataSnapshot) {
                                    val mahasiswaList = mutableListOf<String>()

                                    krsSnapshot.children.forEach { krs ->
                                        val mkList = krs.child("matakuliahIds").children.mapNotNull { it.getValue(String::class.java) }
                                        if (mkList.contains(matkulId)) {
                                            val mahasiswaId = krs.child("mahasiswaId").getValue(String::class.java) ?: ""
                                            mahasiswaList.add(mahasiswaId)
                                        }
                                    }

                                    if (mahasiswaList.isEmpty()) {
                                        matkulData.add("$matkulId:\n  Tidak ada mahasiswa terdaftar.")
                                        updateListView()
                                        return
                                    }

                                    mahasiswaList.forEach { mhsId ->
                                        FirebaseUtils.database.child("users").child(mhsId)
                                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                                    val nama = userSnapshot.child("nama").getValue(String::class.java) ?: "Tanpa Nama"

                                                    FirebaseUtils.database.child("nilais")
                                                        .orderByChild("mahasiswaId").equalTo(mhsId)
                                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                                            override fun onDataChange(nilaiSnapshot: DataSnapshot) {
                                                                var nilaiHuruf = "Belum dinilai"
                                                                nilaiSnapshot.children.forEach { n ->
                                                                    val mkId = n.child("matakuliahId").getValue(String::class.java)
                                                                    if (mkId == matkulId) {
                                                                        val nilai = n.child("nilai").getValue(Double::class.java)
                                                                        nilaiHuruf = konversiNilai(nilai)
                                                                    }
                                                                }
                                                                matkulData.add("$matkulId:\n  $mhsId - $nama : $nilaiHuruf")
                                                                updateListView()
                                                            }

                                                            override fun onCancelled(error: DatabaseError) {}
                                                        })
                                                }

                                                override fun onCancelled(error: DatabaseError) {}
                                            })
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }
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
            else -> "Belum dinilai"
        }
    }

    private fun updateListView() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, matkulData)
        listView.adapter = adapter
    }
}
