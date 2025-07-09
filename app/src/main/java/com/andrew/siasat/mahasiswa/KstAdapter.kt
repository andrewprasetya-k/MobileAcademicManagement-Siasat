package com.andrew.siasat.mahasiswa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andrew.siasat.R
import com.andrew.siasat.model.Matakuliah

class KstAdapter(private val matkulList: List<Matakuliah>) : RecyclerView.Adapter<KstAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMatkulNama: TextView = itemView.findViewById(R.id.tvMatkulNama)
        val tvMatkulKode: TextView = itemView.findViewById(R.id.tvMatkulKode)
        val tvMatkulSks: TextView = itemView.findViewById(R.id.tvMatkulSks)
        val tvMatkulSemester: TextView = itemView.findViewById(R.id.tvMatkulSemester)
        val tvMatkulJadwal: TextView = itemView.findViewById(R.id.tvMatkulJadwal)
        val tvMatkulDosen: TextView = itemView.findViewById(R.id.tvMatkulDosen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kst, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val matkul = matkulList[position]
        holder.tvMatkulNama.text = matkul.nama
        holder.tvMatkulKode.text = "Kode: ${matkul.kode}"
        holder.tvMatkulSks.text = "SKS: ${matkul.sks}"
        holder.tvMatkulSemester.text = "Semester: ${matkul.semester}"
        holder.tvMatkulJadwal.text = "Jadwal: ${matkul.jadwal}"
        holder.tvMatkulDosen.text = "Dosen: ${matkul.dosenId}"
    }

    override fun getItemCount(): Int = matkulList.size
}
