package com.bunker.bunker.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

import java.util.HashMap

@IgnoreExtraProperties
class CalendarModel {
  var NoPoliza: Int = 0
  var Nombre: String = ""
  var Beneficiario: String = ""
  var Monto: Double = 0.0
  // 1-31
  var Dia: Int = 0
  // 1-12
  var Mes: Int = 0
  //1900->
  var Year: Int = 0
  //
  var Telefono: String = ""
  var Email: String = ""
  var HasStar: Boolean = false
  var Compania: String = ""

  // Monthly 1-12()
  var Plan: Int = 0

  @Exclude
  fun toMap(): Map<String, Any> {
    val result = HashMap<String, Any>()
    result["NoPoliza"] = NoPoliza
    result["Nombre"] = Nombre
    result["Beneficiario"] = Beneficiario
    result["Monto"] = Monto
    result["Dia"] = Dia
    result["Mes"] = Mes
    result["Year"] = Year
    result["Telefono"] = Telefono
    result["Email"] = Email
    result["HasStar"] = HasStar
    result["Compania"] = Compania
    result["Plan"] = Plan
    return result
  }
}