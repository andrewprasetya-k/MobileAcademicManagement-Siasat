package com.andrew.siasat.model

data class Nilai(
    val id: String = "",
    val mahasiswaId: String = "",
    val mataKuliahId: String = "",
    val nilai: String = "", // A, B, C, D, E
    val semester: Int = 0,
    val tahunAjaran: String = ""
)