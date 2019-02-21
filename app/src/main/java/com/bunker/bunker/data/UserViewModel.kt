package com.bunker.bunker.data

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bunker.bunker.R
import com.bunker.bunker.data.model.UserData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class UserViewModel(private val currentActivity: FragmentActivity) : ViewModel() {
  val userData: LiveData<UserData> = UserLiveData()
  val isConnected: LiveData<Boolean> = ConnectionLiveData()
  var autoLogin = true

  private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(currentActivity.getString(R.string.default_web_client_id))
      .requestEmail()
      .build()
  private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(currentActivity, gso)

  fun signIn() {
    val signInIntent = googleSignInClient.signInIntent
    currentActivity.startActivityForResult(
        signInIntent,
        RC_SIGN_IN
    )
  }

  fun signOut() {
    autoLogin = false
    // Firebase sign out
    mAuth.signOut()

    // Google sign out
    googleSignInClient.signOut().addOnCompleteListener(currentActivity) {
      currentActivity.finish()
    }
  }

  fun revokeAccess() {
    autoLogin = false
    // Firebase sign out
    mAuth.signOut()

    // Google revoke access
    googleSignInClient.revokeAccess().addOnCompleteListener(currentActivity) {
      currentActivity.finish()
    }
  }

  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if(requestCode != RC_SIGN_IN) return
    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
    try {
      // Google Sign In was successful, authenticate with Firebase
      val account = task.getResult(ApiException::class.java)
      firebaseAuthWithGoogle(account!!)
    } catch(e: ApiException) {
      // Google Sign In failed, update UI appropriately
      Log.w(TAG, "Google sign in failed", e)
      autoLogin = false
      currentActivity.finish()
    }

  }

  private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
    val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
    mAuth.signInWithCredential(credential).addOnCompleteListener(currentActivity) {
      if(it.isSuccessful) {
        // Sign in success, update UI with the signed-in user's information
        Log.d(TAG, "signInWithCredential:success")
      } else {
        // If sign in fails, display a message to the user.
        Log.w(TAG, "signInWithCredential:failure", it.exception)
        Toast.makeText(currentActivity, "Authentication Failed.", Toast.LENGTH_SHORT).show()
        autoLogin = false
        currentActivity.finish()
      }
    }
  }

  companion object {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private const val TAG = "UserViewModel"

    const val RC_SIGN_IN = 9501
  }
}
