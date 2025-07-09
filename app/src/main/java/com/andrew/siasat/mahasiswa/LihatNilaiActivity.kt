package com.andrew.siasat.mahasiswa

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrew.siasat.databinding.ActivityLihatNilaiBinding
import com.andrew.siasat.databinding.ItemNilaiBinding
import com.andrew.siasat.model.Matakuliah
import com.andrew.siasat.model.Nilai
import com.andrew.siasat.utils.FirebaseUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LihatNilaiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLihatNilaiBinding
    private lateinit var adapter: NilaiAdapter
    private val nilaiList = mutableListOf<Nilai>()
    private val matkulMap = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLihatNilaiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mahasiswaId = intent.getStringExtra("USER_ID") ?: return

        setupRecyclerView()
        loadMatakuliah {
            loadNilai(mahasiswaId)
        }
    }

    private fun setupRecyclerView() {
        adapter = NilaiAdapter(nilaiList, matkulMap)
        binding.rvNilai.layoutManager = LinearLayoutManager(this)
        binding.rvNilai.adapter = adapter
    }

    private fun loadMatakuliah(onComplete: () -> Unit) {
        binding.progressBar.visibility = View.VISIBLE

        FirebaseUtils.database.child(FirebaseUtils.MATAKULIAH_PATH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    matkulMap.clear()
                    for (data in snapshot.children) {
                        val matkul = data.getValue(Matakuliah::class.java)
                        matkul?.let { matkulMap[it.id] = it.nama }
                    }
                    onComplete()
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete()
                }
            })
    }

    private fun loadNilai(mahasiswaId: String) {
        binding.progressBar.visibility = View.VISIBLE

        // Ambil Kartu Studi Mahasiswa
        FirebaseUtils.database.child("kartustudis")
            .orderByChild("mahasiswaId").equalTo(mahasiswaId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(krsSnapshot: DataSnapshot) {
                    nilaiList.clear()

                    if (!krsSnapshot.exists()) {
                        binding.progressBar.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                        return
                    }

                    val matkulIds = krsSnapshot.children.flatMap { krs ->
                        krs.child("matakuliahIds").children.mapNotNull { it.getValue(String::class.java) }
                    }.toSet()

                    if (matkulIds.isEmpty()) {
                        binding.progressBar.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                        return
                    }

                    // Ambil Semua Nilai Mahasiswa
                    FirebaseUtils.database.child(FirebaseUtils.NILAI_PATH)
                        .orderByChild("mahasiswaId").equalTo(mahasiswaId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(nilaiSnapshot: DataSnapshot) {
                                val nilaiMap = mutableMapOf<String, Double?>()

                                nilaiSnapshot.children.forEach { n ->
                                    val mkId = n.child("matakuliahId").getValue(String::class.java)
                                    val nilai = n.child("nilai").getValue(Double::class.java)
                                    if (mkId != null) {
                                        nilaiMap[mkId] = nilai
                                    }
                                }

                                matkulIds.forEach { mkId ->
                                    val nilaiAngka = nilaiMap[mkId]
                                    val nilaiObj = Nilai(
                                        mahasiswaId = mahasiswaId,
                                        matakuliahId = mkId,
                                        nilai = nilaiAngka ?: -1.0  // Pakai -1.0 sbg penanda “Belum dinilai”
                                    )
                                    nilaiList.add(nilaiObj)
                                }

                                binding.progressBar.visibility = View.GONE
                                adapter.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(this@LihatNilaiActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@LihatNilaiActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            })
    }

}