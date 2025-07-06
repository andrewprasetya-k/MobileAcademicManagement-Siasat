package com.andrew.siasat.model

// Mahasiswa.kt
data class Mahasiswa(
    val id: String = "",
    val nim: String = "",
    val nama: String = "",
    val prodi: String = "",
    val semester: Int = 1,
    val ipk: Double = 0.0,
    val sksTotal: Int = 0,
    val sksMaks: Int = 0,
    val status: String = ""  // "Aktif", "Non-Aktif"
)