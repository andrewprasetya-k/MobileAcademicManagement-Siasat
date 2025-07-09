package com.andrew.siasat.mahasiswa

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrew.siasat.databinding.ActivityLihatTranskripBinding
import com.andrew.siasat.model.Matakuliah
import com.andrew.siasat.model.Nilai
import com.andrew.siasat.utils.FirebaseUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LihatTranskripActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLihatTranskripBinding
    private lateinit var adapter: TranskripAdapter
    private val nilaiList = mutableListOf<Nilai>()
    private val matkulMap = mutableMapOf<String, Matakuliah>()

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLihatTranskripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID") ?: run {
            Toast.makeText(this, "User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        loadMatakuliah {
            loadNilai(userId)
        }
    }

    private fun setupRecyclerView() {
        adapter = TranskripAdapter(nilaiList, matkulMap)
        binding.rvTranskrip.layoutManager = LinearLayoutManager(this)
        binding.rvTranskrip.adapter = adapter
    }

    private fun loadMatakuliah(onComplete: () -> Unit) {
        binding.progressBar.visibility = View.VISIBLE

        FirebaseUtils.database.child(FirebaseUtils.MATAKULIAH_PATH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    matkulMap.clear()
                    for (data in snapshot.children) {
                        val matkul = data.getValue(Matakuliah::class.java)
                        matkul?.let { matkulMap[it.id] = it }
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
                    var totalBobot = 0.0
                    var totalSks = 0

                    for (data in snapshot.children) {
                        val nilai = data.getValue(Nilai::class.java)
                        if (nilai != null) {
                            nilaiList.add(nilai)

                            val matkul = matkulMap[nilai.matakuliahId]
                            val sks = matkul?.sks ?: 0
                            val bobot = getBobot(nilai.nilai)

                            totalSks += sks
                            totalBobot += bobot * sks
                        }
                    }

                    adapter.notifyDataSetChanged()
                    binding.progressBar.visibility = View.GONE

                    val ipk = if (totalSks > 0) totalBobot / totalSks else 0.0
                    binding.tvSummaryIpk.text = "IPK Keseluruhan: %.2f".format(ipk)
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@LihatTranskripActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getBobot(nilaiAngka: Double): Double {
        return when {
            nilaiAngka >= 80 -> 4.0      // A
            nilaiAngka >= 75 -> 3.5      // AB
            nilaiAngka >= 70 -> 3.0      // B
            nilaiAngka >= 65 -> 2.5      // BC
            nilaiAngka >= 60 -> 2.0      // C
            nilaiAngka >= 50 -> 1.5      // D
            else -> 0.0                  // E
        }
    }
}
