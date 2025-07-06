package com.andrew.siasat.mahasiswa
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.andrew.siasat.databinding.ActivityLihatTranskripBinding
//import com.andrew.siasat.model.Transkrip
//import com.andrew.siasat.utils.FirebaseUtils
//
//class LihatTranskripActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityLihatTranskripBinding
//    private lateinit var transkrip: Transkrip
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLihatTranskripBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val mahasiswaId = intent.getStringExtra("USER_ID") ?: return
//
//        loadTranskrip(mahasiswaId)
//    }
//
//    private fun loadTranskrip(mahasiswaId: String) {
//        FirebaseUtils.database.child("transkrip")
//            .orderByChild("mahasiswaId").equalTo(mahasiswaId)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        transkrip = snapshot.children.first().getValue(Transkrip::class.java)!!
//                        displayTranskripData()
//                    } else {
//                        Toast.makeText(this@LihatTranskripActivity, "Transkrip tidak ditemukan", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@LihatTranskripActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }
//
//    private fun displayTranskripData() {
//        binding.tvTotalSks.text = "Total SKS: ${transkrip.totalSks}"
//        binding.tvIpk.text = "IPK: ${transkrip.ipk}"
//
//        // Setup RecyclerView for nilai list
//        binding.rvNilai.layoutManager = LinearLayoutManager(this)
//        binding.rvNilai.adapter = NilaiAdapter(emptyList()) // Will be updated after loading nilai
//
//        loadNilaiDetails()
//    }
//
//    private fun loadNilaiDetails() {
//        val nilaiList = mutableListOf<Nilai>()
//        transkrip.nilaiList.forEach { nilaiId ->
//            FirebaseUtils.database.child("nilai").child(nilaiId)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        val nilai = snapshot.getValue(Nilai::class.java)
//                        nilai?.let {
//                            nilaiList.add(it)
//                            // Update adapter when all nilai loaded
//                            if (nilaiList.size == transkrip.nilaiList.size) {
//                                binding.rvNilai.adapter = NilaiAdapter(nilaiList)
//                            }
//                        }
//                    }
//                    override fun onCancelled(error: DatabaseError) {
//                        Toast.makeText(this@LihatTranskripActivity, "Error loading nilai", Toast.LENGTH_SHORT).show()
//                    }
//                })
//        }
//    }
//}