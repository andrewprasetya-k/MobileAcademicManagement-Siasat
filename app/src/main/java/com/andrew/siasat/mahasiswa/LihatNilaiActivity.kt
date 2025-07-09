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
        FirebaseUtils.database.child(FirebaseUtils.NILAI_PATH)
            .orderByChild("mahasiswaId").equalTo(mahasiswaId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    nilaiList.clear()
                    for (data in snapshot.children) {
                        val nilai = data.getValue(Nilai::class.java)
                        nilai?.let { nilaiList.add(it) }
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
}