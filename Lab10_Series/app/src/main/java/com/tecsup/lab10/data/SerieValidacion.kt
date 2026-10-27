package com.tecsup.lab10.data

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Locale

fun validarSerie(name: String, releaseDate: String, rating: String, category: String): String? {
    if (name.isBlank() || name.trim().length > 100) return "Escribe un nombre de hasta 100 caracteres."
    val fecha = releaseDate.trim()
    val position = ParsePosition(0)
    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }
    if (!Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}").matches(fecha) ||
        formato.parse(fecha, position) == null || position.index != fecha.length) {
        return "Escribe una fecha válida con formato AAAA-MM-DD, por ejemplo 2026-10-27."
    }
    val numero = rating.trim().toIntOrNull()
    if (numero == null || numero < 0) return "Rating debe ser un número entero mayor o igual a 0."
    if (category.trim().lowercase(Locale.ROOT) !in setOf("horror", "comedy", "action", "drama")) {
        return "Category debe ser horror, comedy, action o drama."
    }
    return null
}
