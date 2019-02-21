package com.bunker.bunker.data

import com.google.firebase.database.FirebaseDatabase

object MyDatabase {
  val Database: FirebaseDatabase = FirebaseDatabase.getInstance()

  init {
    Database.setPersistenceEnabled(true)
  }
}