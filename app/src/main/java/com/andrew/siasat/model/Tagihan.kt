package com.andrew.siasat.model

data class Tagihan(
    val id: String,
    val mahasiswa: Mahasiswa,
    val deskripsi: String,
    val jumlah: Double,
    val status: String // Lunas / Belum Lunas
)
