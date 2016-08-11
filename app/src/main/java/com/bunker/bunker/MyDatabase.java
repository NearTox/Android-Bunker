package com.bunker.bunker;

import com.google.firebase.database.FirebaseDatabase;

public class MyDatabase {
  private static boolean mIsInit = false;
  private static FirebaseDatabase mDatabase;

  static public FirebaseDatabase getInstance() {
    if(mDatabase == null) {
      mDatabase = FirebaseDatabase.getInstance();
    }
    if(!mIsInit) {
      mDatabase.setPersistenceEnabled(true);
      mIsInit = true;
    }
    return mDatabase;
  }
}