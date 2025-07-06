package com.andrew.siasat.model

data class Dosen(
    val id: String,
    val nama: String,
    val nidn: String,
    val email: String,
    val mataKuliahDiampu: List<MataKuliah>
)
