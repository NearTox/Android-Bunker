package com.lock.lock.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Empresa {
  public String Nombre;

  @SuppressWarnings("unused")
  public Empresa() {
    // Default constructor required for calls to DataSnapshot.getValue(Empresa.class)
  }

  public Empresa(String nombre) {
    Nombre = nombre;
  }

  @Exclude
  public Map<String, Object> toMap() {
    HashMap<String, Object> result = new HashMap<>();
    result.put("Nombre", Nombre);

    return result;
  }

  @Override
  public String toString() {
    return "Empresa{" +
        "Nombre='" + Nombre + '\'' +
        '}';
  }
}
