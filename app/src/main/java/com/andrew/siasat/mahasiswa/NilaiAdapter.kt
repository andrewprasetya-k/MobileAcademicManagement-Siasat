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
        holder.binding.tvMatakuliah.text = matkulMap[nilai.matakuliahId] ?: "-"
        holder.binding.tvNilaiAngka.text = nilai.nilai.toString()
    }

    override fun getItemCount(): Int = nilaiList.size
}

