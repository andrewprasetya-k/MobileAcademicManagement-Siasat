package com.andrew.siasat.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils {
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val database: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }

    // Reference paths
    const val USERS_PATH = "users"
    const val MATA_KULIAH_PATH = "mata_kuliah"
    const val NILAI_PATH = "nilai"
    const val TRANSKRIP_PATH = "transkrip"
}