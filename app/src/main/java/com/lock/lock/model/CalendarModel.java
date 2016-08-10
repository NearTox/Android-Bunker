package com.lock.lock.model;

import com.google.firebase.database.IgnoreExtraProperties;

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

}
