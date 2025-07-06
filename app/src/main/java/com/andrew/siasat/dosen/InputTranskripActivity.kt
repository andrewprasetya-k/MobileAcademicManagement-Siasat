package com.andrew.siasat.dosen
//
//import android.os.Bundle
//import android.widget.ArrayAdapter
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.andrew.siasat.databinding.ActivityInputTranskripBinding
//import com.andrew.siasat.model.Transkrip
//import com.andrew.siasat.utils.FirebaseUtils
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.ValueEventListener
//
//class InputTranskripActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityInputTranskripBinding
//    private lateinit var mahasiswaList: MutableList<String>
//    private lateinit var nilaiList: MutableList<String>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityInputTranskripBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        mahasiswaList = mutableListOf()
//        nilaiList = mutableListOf()
//
//        loadMahasiswa()
//        setupButtonListeners()
//    }
//
//    private fun loadMahasiswa() {
//        FirebaseUtils.database.child(FirebaseUtils.USERS_PATH)
//            .orderByChild("role").equalTo("mahasiswa")
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    mahasiswaList.clear()
//                    for (data in snapshot.children) {
//                        val id = data.key ?: ""
//                        val nama = data.child("nama").value.toString()
//                        mahasiswaList.add("$id - $nama")
//                    }
//                    setupMahasiswaSpinner()
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@InputTranskripActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }
//
//    private fun loadNilai(mahasiswaId: String) {
//        FirebaseUtils.database.child(FirebaseUtils.NILAI_PATH)
//            .orderByChild("mahasiswaId").equalTo(mahasiswaId)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    nilaiList.clear()
//                    for (data in snapshot.children) {
//                        val nilaiId = data.key ?: ""
//                        val mataKuliahId = data.child("mataKuliahId").value.toString()
//                        nilaiList.add("$nilaiId - $mataKuliahId")
//                    }
//                    setupNilaiSpinner()
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@InputTranskripActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }
//
//    private fun setupMahasiswaSpinner() {
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mahasiswaList)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerMahasiswa.adapter = adapter
//
//        binding.spinnerMahasiswa.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
//                if (position > 0) {
//                    val selected = mahasiswaList[position]
//                    val mahasiswaId = selected.split(" - ")[0]
//                    loadNilai(mahasiswaId)
//                }
//            }
//
//            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
//        }
//    }
//
//    private fun setupNilaiSpinner() {
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nilaiList)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerNilai.adapter = adapter
//    }
//
//    private fun setupButtonListeners() {
//        binding.btnSimpan.setOnClickListener {
//            val mahasiswa = binding.spinnerMahasiswa.selectedItem.toString()
//            val totalSks = binding.etTotalSks.text.toString()
//            val ipk = binding.etIpk.text.toString()
//
//            if (validateInput(mahasiswa, totalSks, ipk)) {
//                val mahasiswaId = mahasiswa.split(" - ")[0]
//                val transkrip = Transkrip(
//                    mahasiswaId = mahasiswaId,
//                    totalSks = totalSks.toInt(),
//                    ipk = ipk.toDouble(),
//                    nilaiList = getSelectedNilaiIds()
//                )
//
//                saveTranskrip(transkrip)
//            }
//        }
//    }
//
//    private fun getSelectedNilaiIds(): List<String> {
//        val selectedItems = mutableListOf<String>()
//        for (i in 0 until binding.spinnerNilai.count) {
//            if (binding.spinnerNilai.isItemChecked(i)) {
//                val nilai = binding.spinnerNilai.getItemAtPosition(i).toString()
//                selectedItems.add(nilai.split(" - ")[0])
//            }
//        }
//        return selectedItems
//    }
//
//    private fun validateInput(mahasiswa: String, totalSks: String, ipk: String): Boolean {
//        if (mahasiswa == "Pilih Mahasiswa") {
//            Toast.makeText(this, "Pilih mahasiswa", Toast.LENGTH_SHORT).show()
//            return false
//        }
//        if (totalSks.isEmpty() || !totalSks.matches(Regex("\\d+"))) {
//            binding.etTotalSks.error = "Total SKS harus angka"
//            return false
//        }
//        if (ipk.isEmpty() || !ipk.matches(Regex("\\d+\\.\\d{1,2}"))) {
//            binding.etIpk.error = "IPK harus angka (contoh: 3.75)"
//            return false
//        }
//        return true
//    }
//
//    private fun saveTranskrip(transkrip: Transkrip) {
//        val key = FirebaseUtils.database.child(FirebaseUtils.TRANSKRIP_PATH).push().key
//        key?.let {
//            transkrip.id = it
//            FirebaseUtils.database.child(FirebaseUtils.TRANSKRIP_PATH).child(it)
//                .setValue(transkrip)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "Transkrip berhasil disimpan", Toast.LENGTH_SHORT).show()
//                    finish()
//                }
//                .addOnFailureListener {
//                    Toast.makeText(this, "Gagal menyimpan: ${it.message}", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
//}