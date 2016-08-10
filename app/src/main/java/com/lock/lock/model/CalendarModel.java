package com.lock.lock.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class CalendarModel {
  public String Day;
  public String Month;

  public String NoPoliza;
  public String Name;
  public String Phone;
  public String Email;
  public boolean IsPreferenctial;
  public String Company;
  public int Plan;

  public CalendarModel() {  }

  @Exclude
  public Map<String, Object> toMap() {
    HashMap<String, Object> result = new HashMap<>();
    result.put("NoPoliza", NoPoliza);
    result.put("Name", Name);
    result.put("Email", Email);
    result.put("IsPreferenctial", IsPreferenctial);
    result.put("Phone", Phone);

    return result;
  }

}
