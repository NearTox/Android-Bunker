package com.bunker.bunker.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class CalendarModel {
  public int NoPoliza;
  public String Nombre;
  public String Beneficiario;
  public double Monto;
  // 1-31
  public int Dia;
  // 1-12
  public int Mes;
  //
  public String Telefono;
  public String Email;
  public boolean HasStar;
  public String Compania;

  // Monthly 1-12()
  public int Plan;

  public CalendarModel() {
  }

  @Exclude
  public Map<String, Object> toMap() {
    HashMap<String, Object> result = new HashMap<>();
    result.put("NoPoliza", NoPoliza);
    result.put("Nombre", Nombre);
    result.put("Beneficiario", Beneficiario);
    result.put("Monto", Monto);
    result.put("Dia", Dia);
    result.put("Mes", Mes);
    result.put("Telefono", Telefono);
    result.put("Email", Email);
    result.put("HasStar", HasStar);
    result.put("Compania", Compania);
    result.put("Plan", Plan);
    return result;
  }
}