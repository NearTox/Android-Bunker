package com.bunker.bunker

import com.google.firebase.database.FirebaseDatabase

object MyDatabase {
  var Database: FirebaseDatabase = FirebaseDatabase.getInstance()

  init {
    Database.setPersistenceEnabled(true)
  }
}