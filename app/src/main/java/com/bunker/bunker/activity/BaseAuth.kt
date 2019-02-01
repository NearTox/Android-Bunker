package com.bunker.bunker.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

abstract class BaseAuth : AppCompatActivity(), FirebaseAuth.AuthStateListener {

  protected var mAuth: FirebaseAuth? = null
  private var mIsUserLogging = false
  private var mUniqueLogOut = true

  override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
    val user = firebaseAuth.currentUser
    if(user != null) {
      // User is signed in
      Log.d(TAG, "onAuthStateChanged: signed_in: " + user.uid)
      if(!mIsUserLogging) {
        mIsUserLogging = true
        onLogIn(user)
      }
      mUniqueLogOut = true
    } else {
      mIsUserLogging = false
      if(mUniqueLogOut) {
        onLogOut()
        mUniqueLogOut = false
      }
      // User is signed out
      Log.d(TAG, "onAuthStateChanged: signed_out")
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // initialize_auth
    mAuth = FirebaseAuth.getInstance()
  }

  public override fun onStart() {
    super.onStart()
    mAuth?.addAuthStateListener(this)
  }

  public override fun onStop() {
    super.onStop()
    mAuth?.removeAuthStateListener(this)
  }

  protected fun isPasswordValid(password: String): Boolean {
    return password.length > 3
  }

  protected abstract fun onLogIn(user: FirebaseUser)

  protected abstract fun onLogOut()

  companion object {
    private val TAG = MainActivity::class.java.name
  }
}