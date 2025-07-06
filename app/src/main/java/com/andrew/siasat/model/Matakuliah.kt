package com.andrew.siasat.model

data class MataKuliah(
    val kode: String = "",
    val nama: String = "",
    val sks: Int = 0,
    val semester: Int = 0,
    val dosenPengampu: String = "" // ID Dosen
)