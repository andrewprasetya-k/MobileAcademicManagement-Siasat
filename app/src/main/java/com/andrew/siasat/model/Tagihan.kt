package com.andrew.siasat.model

data class Tagihan(
    val id: String = "",
    val mahasiswaId: String = "",
    val periode: String = "",
    val jumlah: Double = 0.0,
    val status: String = "" // "Lunas", "Belum Lunas"
)
