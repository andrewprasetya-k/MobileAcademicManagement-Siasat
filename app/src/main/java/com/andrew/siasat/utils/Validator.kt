package com.andrew.siasat.utils

object Validator {
    fun isDosenIdValid(id: String): Boolean {
        return id.length == 5 && id.startsWith("67")
    }

    fun isMahasiswaIdValid(id: String): Boolean {
        return id.length == 9
    }
}