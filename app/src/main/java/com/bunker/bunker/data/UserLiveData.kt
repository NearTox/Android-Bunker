package com.bunker.bunker.data

import androidx.lifecycle.LiveData
import com.bunker.bunker.data.model.UserData
import com.google.firebase.auth.FirebaseAuth

class UserLiveData : LiveData<UserData>() {
  private val mConnect = FirebaseAuth.AuthStateListener { itt ->
    val temp = itt.currentUser?.let {
      UserData(true,
          it.displayName ?: "",
          it.email ?: "",
          it.photoUrl?.toString() ?: "",
          it.uid
      )
    } ?: UserData(false)
    // TODO: temp != value
    if(temp != value) value = temp
  }

  override fun onActive() {
    mAuth.addAuthStateListener(mConnect)
  }

  override fun onInactive() {
    mAuth.removeAuthStateListener(mConnect)
  }

  companion object {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
  }
}