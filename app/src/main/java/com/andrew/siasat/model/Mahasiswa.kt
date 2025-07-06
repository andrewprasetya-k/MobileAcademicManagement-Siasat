package com.andrew.siasat.model

data class Mahasiswa(
    val id: String,
    val nama: String,
    val nim: String,
    val prodi: String,
    val semester: Int,
    val ipk: Double,
    val totalSks: Int,
    val status: String, // Aktif / Cuti / Nonaktif
    val poinKkm: Int
)
