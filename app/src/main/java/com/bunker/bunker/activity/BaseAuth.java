package com.bunker.bunker.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class BaseAuth extends AppCompatActivity
    implements FirebaseAuth.AuthStateListener {
  private static final String TAG = MainActivity.class.getName();

  protected FirebaseAuth mAuth;
  private boolean mIsUserLogging = false;
  private boolean mUniqueLogOut = true;

  @Override
  public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
    FirebaseUser user = firebaseAuth.getCurrentUser();
    if(user != null) {
      // User is signed in
      Log.d(TAG, "onAuthStateChanged: signed_in: " + user.getUid());
      if(!mIsUserLogging) {
        mIsUserLogging = true;
        onLogIn(user);
      }
      mUniqueLogOut = true;
    } else {
      mIsUserLogging = false;
      if(mUniqueLogOut) {
        onLogOut();
        mUniqueLogOut = false;
      }
      // User is signed out
      Log.d(TAG, "onAuthStateChanged: signed_out");
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // initialize_auth
    mAuth = FirebaseAuth.getInstance();
  }

  @Override
  public void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(this);
  }

  @Override
  public void onStop() {
    super.onStop();
    mAuth.removeAuthStateListener(this);
  }

  protected boolean isPasswordValid(String password) {
    return password.length() > 3;
  }

  abstract protected void onLogIn(@NonNull FirebaseUser user);

  abstract protected void onLogOut();
}