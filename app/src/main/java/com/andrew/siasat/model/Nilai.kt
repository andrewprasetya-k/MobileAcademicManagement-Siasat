package com.andrew.siasat.model

data class Nilai(
    val id: String,
    val mahasiswa: Mahasiswa,
    val mataKuliah: MataKuliah,
    val nilai: Double // Misal 85.0
)
