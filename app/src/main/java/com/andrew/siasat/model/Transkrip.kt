package com.andrew.siasat.model

data class Transkrip(
    val id: String = "",
    val mahasiswaId: String = "",
    val totalSks: Int = 0,
    val ipk: Double = 0.0,
    val nilaiList: List<String> = emptyList() // List of Nilai IDs
)