package com.andrew.siasat.model

data class Transkrip(
    val id: String,
    val mahasiswa: Mahasiswa,
    val nilaiList: List<Nilai>,
    val totalSks: Int,
    val ipk: Double
)
