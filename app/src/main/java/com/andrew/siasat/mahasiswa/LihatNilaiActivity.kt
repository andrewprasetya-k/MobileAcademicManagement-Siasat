package com.andrew.siasat.mahasiswa
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.andrew.siasat.databinding.ActivityLihatNilaiBinding
//import com.andrew.siasat.model.Nilai
//import com.andrew.siasat.utils.FirebaseUtils
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.ValueEventListener
//
//class LihatNilaiActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityLihatNilaiBinding
//    private lateinit var adapter: NilaiAdapter
//    private val nilaiList = mutableListOf<Nilai>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLihatNilaiBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val mahasiswaId = intent.getStringExtra("USER_ID") ?: return
//
//        setupRecyclerView()
//        loadNilai(mahasiswaId)
//    }
//
//    private fun setupRecyclerView() {
//        adapter = NilaiAdapter(nilaiList)
//        binding.rvNilai.layoutManager = LinearLayoutManager(this)
//        binding.rvNilai.adapter = adapter
//    }
//
//    private fun loadNilai(mahasiswaId: String) {
//        FirebaseUtils.database.child(FirebaseUtils.NILAI_PATH)
//            .orderByChild("mahasiswaId").equalTo(mahasiswaId)
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    nilaiList.clear()
//                    for (data in snapshot.children) {
//                        val nilai = data.getValue(Nilai::class.java)
//                        nilai?.let { nilaiList.add(it) }
//                    }
//                    adapter.notifyDataSetChanged()
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    // Handle error
//                }
//            })
//    }
//}