package com.andrew.siasat.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils {
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val database: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }

    // Reference paths
    const val USERS_PATH = "users"
    const val MATAKULIAH_PATH = "matakuliahs"
    const val KARTU_STUDI_PATH = "kartustudis"
    const val DOSEN_PATH = "dosens"
    const val NILAI_PATH = "nilais"
    const val TRANSKRIP_PATH = "transkrips"
}