package com.andrew.siasat.model

data class KartuStudi(
    val id: String,
    val mahasiswa: Mahasiswa,
    val semester: Int,
    val mataKuliahList: List<MataKuliah>
)
