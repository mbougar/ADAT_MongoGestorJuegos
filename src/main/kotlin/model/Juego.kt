package model

import java.util.Date

data class Juego(
    val titulo: String,
    val genero: String,
    val precio: Double,
    val fechaLanzamiento: Date
)