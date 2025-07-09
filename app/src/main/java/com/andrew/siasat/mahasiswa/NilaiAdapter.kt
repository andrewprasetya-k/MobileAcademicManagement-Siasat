package com.andrew.siasat.mahasiswa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrew.siasat.databinding.ItemNilaiBinding
import com.andrew.siasat.model.Nilai

class NilaiAdapter(
    private val nilaiList: List<Nilai>,
    private val matkulMap: Map<String, String>
) : RecyclerView.Adapter<NilaiAdapter.NilaiViewHolder>() {

    inner class NilaiViewHolder(val binding: ItemNilaiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NilaiViewHolder {
        val binding = ItemNilaiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NilaiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NilaiViewHolder, position: Int) {
        val nilai = nilaiList[position]
        val matkulNama = matkulMap[nilai.matakuliahId] ?: "Matkul Tidak Dikenal"

        val nilaiHuruf = when (nilai.nilai) {
            4.0 -> "A"
            3.5 -> "AB"
            3.0 -> "B"
            2.5 -> "BC"
            2.0 -> "C"
            1.5 -> "D"
            0.0 -> "E"
            else -> "Belum dinilai"
        }

        holder.binding.tvMatakuliah.text = matkulNama
        holder.binding.tvNilaiAngka.text = nilaiHuruf
    }

    override fun getItemCount(): Int = nilaiList.size
}

