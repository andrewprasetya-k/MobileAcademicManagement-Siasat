package com.andrew.siasat.mahasiswa

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrew.siasat.databinding.ActivityLihatKstBinding
import com.andrew.siasat.model.Dosen
import com.andrew.siasat.model.KartuStudi
import com.andrew.siasat.model.Matakuliah
import com.andrew.siasat.utils.FirebaseUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LihatKstActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLihatKstBinding
    private lateinit var adapter: KstAdapter
    private val matkulList = mutableListOf<Matakuliah>()

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLihatKstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID") ?: run {
            Toast.makeText(this, "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        loadKartuStudi()
    }

    private fun setupRecyclerView() {
        adapter = KstAdapter(matkulList)
        binding.rvKst.layoutManager = LinearLayoutManager(this)
        binding.rvKst.adapter = adapter
    }

    private fun loadKartuStudi() {
        binding.progressBar.visibility = View.VISIBLE

        FirebaseUtils.database.child(FirebaseUtils.KARTU_STUDI_PATH)
            .orderByChild("mahasiswaId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val matkulIds = mutableListOf<String>()

                    for (data in snapshot.children) {
                        val kst = data.getValue(KartuStudi::class.java)
                        kst?.matakuliahIds?.let { matkulIds.addAll(it) }
                    }

                    loadMatakuliah(matkulIds)
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@LihatKstActivity, "Gagal memuat KST", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private val dosenMap = mutableMapOf<String, String>()  // id -> nama dosen

    private fun loadDosen(onComplete: () -> Unit) {
        FirebaseUtils.database.child(FirebaseUtils.DOSEN_PATH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        val dosen = data.getValue(Dosen::class.java)
                        dosen?.let { dosenMap[it.id] = it.nama }
                    }
                    onComplete()
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete()
                }
            })
    }


    private fun loadMatakuliah(matkulIds: List<String>) {
        FirebaseUtils.database.child(FirebaseUtils.MATAKULIAH_PATH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    matkulList.clear()
                    for (data in snapshot.children) {
                        val matkul = data.getValue(Matakuliah::class.java)
                        if (matkul != null && matkulIds.contains(matkul.id)) {
                            matkulList.add(matkul)
                        }
                    }
                    binding.progressBar.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                }
            })
    }
}

// Adapter KstAdapter & Layout activity_lihat_kst.xml & item_kst.xml perlu dibuat sesuai tampilan yang diinginkan.
