package com.andrew.siasat.model

data class KartuStudi(
    val id: String = "",
    val mahasiswaId: String = "",
    val semester: Int = 1,
    val matakuliahIds: List<String> = listOf()
)