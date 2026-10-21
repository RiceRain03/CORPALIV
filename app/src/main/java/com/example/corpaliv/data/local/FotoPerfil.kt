package com.example.corpaliv.data.local

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object FotoPerfil {

    fun crearArchivoFoto(context: Context, usuarioId: Int): File {
        val carpeta = File(context.filesDir, "fotos")
        if (!carpeta.exists()) carpeta.mkdirs()
        return File(carpeta, "perfil_${usuarioId}_${System.currentTimeMillis()}.jpg")
    }

    fun uriSegura(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    fun copiarDesdeUri(context: Context, origen: Uri, usuarioId: Int): File? {
        return try {
            val entrada = context.contentResolver.openInputStream(origen) ?: return null
            val destino = crearArchivoFoto(context, usuarioId)
            entrada.use { flujoEntrada ->
                destino.outputStream().use { flujoSalida -> flujoEntrada.copyTo(flujoSalida) }
            }
            destino
        } catch (e: Exception) {
            null
        }
    }

    fun borrar(ruta: String?) {
        if (ruta != null) File(ruta).delete()
    }
}