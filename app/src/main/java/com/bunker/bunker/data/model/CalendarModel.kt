package com.bunker.bunker.data.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CalendarModel(
    var NoPoliza: Int = 0,
    var Nombre: String = "",
    var Beneficiario: String = "",
    var Monto: Double = 0.0,
    // 1-31
    var Dia: Int = 0,
    // 1-12
    var Mes: Int = 0,
    //1900->
    var Year: Int = 0,
    //
    var Telefono: String = "",
    var Email: String = "",
    var HasStar: Boolean = false,
    var Compania: String = "",

    // Monthly 1-12()
    var Plan: Int = 0
) {
  @Exclude
  fun toMap(): Map<String, Any?> {
    return mapOf(
        "NoPoliza" to NoPoliza,
        "Nombre" to Nombre,
        "Beneficiario" to Beneficiario,
        "Monto" to Monto,
        "Dia" to Dia,
        "Mes" to Mes,
        "Year" to Year,
        "Telefono" to Telefono,
        "Email" to Email,
        "HasStar" to HasStar,
        "Compania" to Compania,
        "Plan" to Plan
    )
  }
}