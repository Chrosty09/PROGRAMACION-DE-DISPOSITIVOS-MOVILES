package com.example.livequery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class MensajeEvento(
    val tipo: String,
    val texto: String,
    val timestamp: Long = System.currentTimeMillis(),
    val objectId: String = ""
)

class MensajeViewModel : ViewModel() {
    private val _eventos = MutableStateFlow<List<MensajeEvento>>(emptyList())
    val eventos: StateFlow<List<MensajeEvento>> = _eventos.asStateFlow()

    private val _ultimaActualizacion = MutableStateFlow("")
    val ultimaActualizacion: StateFlow<String> = _ultimaActualizacion.asStateFlow()

    private val mensajesVistos = mutableMapOf<String, String>() // objectId -> updatedAt
    private var isPolling = false

    init {
        iniciarPolling()
    }

    private fun iniciarPolling() {
        if (isPolling) return
        isPolling = true

        viewModelScope.launch {
            agregarEvento(MensajeEvento(
                tipo = "INFO",
                texto = "Iniciando polling cada 3 segundos..."
            ))

            while (isPolling) {
                try {
                    obtenerMensajes()
                    delay(3000)
                } catch (e: Exception) {
                    Log.e("MensajeViewModel", "Error en polling", e)
                    agregarEvento(MensajeEvento(
                        tipo = "ERROR",
                        texto = "Error: ${e.message}"
                    ))
                    delay(5000)
                }
            }
        }
    }

    private suspend fun obtenerMensajes() {
        try {
            val response = Back4AppApiClient.api.getMensajes()
            val ahora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            _ultimaActualizacion.value = "Última actualización: $ahora"

            Log.d("MensajeViewModel", "Obtenidos ${response.results.size} mensajes")

            response.results.forEach { mensaje ->
                val updatedAtAnterior = mensajesVistos[mensaje.objectId]

                if (updatedAtAnterior == null) {
                    Log.d("MensajeViewModel", "Nuevo mensaje: ${mensaje.texto}")
                    agregarEvento(MensajeEvento(
                        tipo = "CREATE",
                        texto = mensaje.texto,
                        objectId = mensaje.objectId
                    ))
                    mensajesVistos[mensaje.objectId] = mensaje.updatedAt
                } else if (updatedAtAnterior != mensaje.updatedAt) {
                    Log.d("MensajeViewModel", "Mensaje actualizado: ${mensaje.texto}")
                    agregarEvento(MensajeEvento(
                        tipo = "UPDATE",
                        texto = mensaje.texto,
                        objectId = mensaje.objectId
                    ))
                    mensajesVistos[mensaje.objectId] = mensaje.updatedAt
                }
            }

        } catch (e: Exception) {
            Log.e("MensajeViewModel", "Error al obtener mensajes", e)
            throw e
        }
    }

    private fun agregarEvento(evento: MensajeEvento) {
        _eventos.value = listOf(evento) + _eventos.value
    }

    fun detenerPolling() {
        isPolling = false
    }

    override fun onCleared() {
        super.onCleared()
        detenerPolling()
    }
}

