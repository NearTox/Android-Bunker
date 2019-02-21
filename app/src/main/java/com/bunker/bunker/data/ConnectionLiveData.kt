package com.bunker.bunker.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ConnectionLiveData : LiveData<Boolean>() {
  private val connectedRef = MyDatabase.Database.getReference(".info/connected")
  private val mConnection = object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
      value = snapshot.getValue(Boolean::class.java)!!
    }

    override fun onCancelled(error: DatabaseError) {
      Log.d(TAG, "Listener was cancelled")
    }
  }

  override fun onInactive() {
    connectedRef.removeEventListener(mConnection)
  }

  override fun onActive() {
    connectedRef.addValueEventListener(mConnection)
  }

  companion object {
    private const val TAG = "ConnectionLiveData"
  }
}