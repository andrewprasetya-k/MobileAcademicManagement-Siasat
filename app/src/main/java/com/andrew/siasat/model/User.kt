package com.andrew.siasat.model

data class User(
    val id: String,
    val username: String,
    val password: String,
    val role: String // "mahasiswa", "dosen", "kaprodi"
)
