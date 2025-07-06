package com.andrew.siasat.model

data class User(
    val id: String = "",
    val nama: String = "",
    val role: String = "", // "kaprodi", "dosen", "mahasiswa"
    val password: String = "" // Dalam produksi, sebaiknya gunakan hash
)