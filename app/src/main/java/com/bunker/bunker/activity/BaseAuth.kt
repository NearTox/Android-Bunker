package com.bunker.bunker.activity

object BaseAuth {
  fun isPasswordValid(password: String): Boolean {
    return password.length > 3
  }
}