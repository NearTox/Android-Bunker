package com.lock.lock.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class CalendarModel {
  // 1-31
  public int Day;
  // 1-12
  public int Month;
  //
  public int NoPoliza;
  public String Name;
  public String Phone;
  public String Email;
  public boolean IsPreferenctial;
  public String Company;

  // Monthly
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
