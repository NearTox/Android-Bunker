package com.bunker.bunker.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.bunker.bunker.data.model.UserData
import com.google.firebase.auth.FirebaseAuth

class UserLiveData : LiveData<UserData>() {
  private val mConnect = FirebaseAuth.AuthStateListener {
    val user = it.currentUser
    value = if(user != null) {
      Log.d(TAG, "onAuthStateChanged: signed_in")
      UserData(
          true,
          user.displayName ?: ""
      )
    } else {
      Log.d(TAG, "onAuthStateChanged: signed_out")
      UserData(false)
    }
  }


  override fun onActive() {
    mAuth.addAuthStateListener(mConnect)
  }

  override fun onInactive() {
    mAuth.removeAuthStateListener(mConnect)
  }

  companion object {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private const val TAG = "UserLiveData"
  }
}