package utilities

import java.text.SimpleDateFormat
import java.util.*

fun isNumeric(string: String): Boolean {
    return string.toDoubleOrNull() != null
}

fun isDate(string: String): Boolean {
    return try {
        Date(string)
        true
    } catch (e: Exception) {
        false
    }
}

fun formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    return dateFormat.format(date)
}