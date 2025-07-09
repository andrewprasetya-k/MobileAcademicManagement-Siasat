package com.andrew.siasat.mahasiswa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrew.siasat.databinding.ItemTranskripBinding
import com.andrew.siasat.model.Matakuliah
import com.andrew.siasat.model.Nilai

class TranskripAdapter(
    private val nilaiList: List<Nilai>,
    private val matkulMap: Map<String, Matakuliah>
) : RecyclerView.Adapter<TranskripAdapter.TranskripViewHolder>() {

    inner class TranskripViewHolder(val binding: ItemTranskripBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TranskripViewHolder {
        val binding = ItemTranskripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TranskripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TranskripViewHolder, position: Int) {
        val nilai = nilaiList[position]
        val matkul = matkulMap[nilai.matakuliahId]

        holder.binding.tvMatkulNama.text = matkul?.nama ?: "-"
        holder.binding.tvMatkulKode.text = "Kode: ${matkul?.kode ?: "-"}"
        holder.binding.tvMatkulSks.text = "SKS: ${matkul?.sks ?: "-"}"

        val nilaiAngka = nilai.nilai
        val hurufMutu = when {
            nilaiAngka >= 4.0 -> "A"
            nilaiAngka >= 3.5 -> "AB"
            nilaiAngka >= 3.0 -> "B"
            nilaiAngka >= 2.5 -> "BC"
            nilaiAngka >= 2.0 -> "C"
            nilaiAngka >= 1.0 -> "D"
            else -> "E"
        }

        holder.binding.tvNilaiAngka.text = "Nilai: $hurufMutu"
    }

    override fun getItemCount(): Int = nilaiList.size
}
