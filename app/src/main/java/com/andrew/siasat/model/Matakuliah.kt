package com.andrew.siasat.model

data class Matakuliah(
    val id: String = "",
    val kode: String = "",
    val nama: String = "",
    val sks: Int = 0,
    val semester: Int = 1,
    val dosenId: String = "",
    val jadwal: String = ""    // tambahkan ini
)