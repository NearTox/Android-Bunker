package com.bunker.bunker

import com.google.firebase.database.FirebaseDatabase

object MyDatabase {
  val Database: FirebaseDatabase = FirebaseDatabase.getInstance()

  init {
    Database.setPersistenceEnabled(true)
  }
}