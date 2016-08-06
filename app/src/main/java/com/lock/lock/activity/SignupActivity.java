package com.lock.lock.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.lock.lock.EmailFormater;
import com.lock.lock.R;

public class SignupActivity extends BaseAuth {
  private static final String TAG = SignupActivity.class.getSimpleName();

  // UI references.
  private EditText mEmailView;
  private EditText mPasswordView;
  private EditText mNameView;
  private View mProgressView;
  private View mLoginFormView;
  private Button mEmailSignInButton;
  private boolean mAuthTask = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);

    // Set up the login form.
    mPasswordView = (EditText) findViewById(R.id.sign_up_form_password);
    mEmailView = (EditText) findViewById(R.id.sign_up_form_email);
    mNameView = (EditText) findViewById(R.id.sign_up_form_name);
    mEmailSignInButton = (Button) findViewById(R.id.sign_up_form_button);
    mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        attemptLogin();
      }
    });

    mLoginFormView = findViewById(R.id.sign_up_form);
    mProgressView = findViewById(R.id.sign_up_progress);
  }


  @Override
  protected void onLogIn(@NonNull FirebaseUser user) {
    String name = mNameView.getText().toString().trim();
    if(!name.isEmpty() && mAuthTask) {
      UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
        .setDisplayName(name)
        .build();

      user.updateProfile(profileUpdates)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()) {
              Log.d(TAG, "User profile updated.");
            }
          }
        });
    }
    finish();
  }

  @Override
  protected void onLogOut() {
    Log.e(TAG,"Must only occur one time");
  }

  private void attemptLogin() {
    if(mAuthTask) {
      return;
    }

    // Reset errors.
    mEmailView.setError(null);
    mPasswordView.setError(null);

    // Store values at the time of the login attempt.
    String email = mEmailView.getText().toString().trim();
    EmailFormater email_info = new EmailFormater(email);
    String password = mPasswordView.getText().toString().trim();

    boolean cancel = false;
    View focusView = null;

    // Check for a valid password, if the user entered one.
    if(!isPasswordValid(password)) {
      mPasswordView.setError(getString(R.string.error_invalid_password));
      focusView = mPasswordView;
      cancel = true;
    }

    // Check for a valid email address.
    if(email.isEmpty()) {
      mEmailView.setError(getString(R.string.error_field_required));
      focusView = mEmailView;
      cancel = true;
    } else if(!email_info.isValid()) {
      mEmailView.setError(getString(R.string.error_invalid_email));
      focusView = mEmailView;
      cancel = true;
    }

    if(cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      /*View cfocus = this.getCurrentFocus();
      if(cfocus != null) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(cfocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
      }*/
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      showProgress(true);


      // TODO: attempt authentication against a network service.
      mAuthTask = true;
      email = email_info.GetEmail();
      mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            Log.d(TAG, "signInWithEmail: onComplete:" + task.isSuccessful());

            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
            mAuthTask = false;
            showProgress(false);

            if(!task.isSuccessful()) {
              try {
                throw task.getException();
              } catch(FirebaseAuthWeakPasswordException e) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                mPasswordView.requestFocus();
              } catch(FirebaseAuthInvalidUserException e) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                mEmailView.requestFocus();
              } catch(FirebaseAuthInvalidCredentialsException e) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
              } catch(FirebaseAuthUserCollisionException e) {
                mEmailView.setError(getString(R.string.error_user_exists));
                mEmailView.requestFocus();
              } catch(Exception e) {
                Log.e(TAG, "FirebaseAuthException: " + e.getMessage());
              }
            }

            // ...
          }
        });
    }
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
      mLoginFormView.animate().setDuration(shortAnimTime).alpha(
          show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
      });

      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mProgressView.animate().setDuration(shortAnimTime).alpha(
          show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
      });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }
}