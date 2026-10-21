package com.example.corpaliv.data.local.seguridad

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object ClaveSegura {

    private const val ALGORITMO = "PBKDF2WithHmacSHA1"
    private const val ITERACIONES = 65536
    private const val LARGO_HASH_BITS = 256

    fun generarSal(): String {
        val sal = ByteArray(16)
        SecureRandom().nextBytes(sal)
        return Base64.encodeToString(sal, Base64.NO_WRAP)
    }

    fun cifrar(clave: String, sal: String): String {
        val especificacion = PBEKeySpec(
            clave.toCharArray(),
            Base64.decode(sal, Base64.NO_WRAP),
            ITERACIONES,
            LARGO_HASH_BITS
        )
        val hash = SecretKeyFactory.getInstance(ALGORITMO)
            .generateSecret(especificacion)
            .encoded
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun verificar(clave: String, sal: String, hashGuardado: String): Boolean {
        val hashNuevo = cifrar(clave, sal)
        return MessageDigest.isEqual(hashNuevo.toByteArray(), hashGuardado.toByteArray())
    }
}