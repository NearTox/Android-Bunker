package com.lock.lock.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.lock.lock.R;

public class SignupActivity extends AppCompatActivity {
  private static final String TAG = SignupActivity.class.getName();
  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;
  /**
   * Keep track of the login task to ensure we can cancel it if requested.
   */

  // UI references.
  private EditText mEmailView;
  private EditText mPasswordView;
  private View mProgressView;
  private View mLoginFormView;
  private Button mEmailSignInButton;
  private boolean mAuthTask = false;
  private int multi = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);
    mAuth = FirebaseAuth.getInstance();
    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
          // User is signed in
          Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
          // User is signed out
          Log.d(TAG, "onAuthStateChanged:signed_out");
        }
        // ...
      }
    };
    // Set up the login form.
    mPasswordView = (EditText) findViewById(R.id.password);
    mEmailView = (EditText) findViewById(R.id.email);
    mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
    mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        attemptLogin();
      }
    });

    mLoginFormView = findViewById(R.id.login_form);
    mProgressView = findViewById(R.id.login_progress);
  }


  @Override
  public void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthListener);
  }

  @Override
  public void onStop() {
    super.onStop();
    if(mAuthListener != null) {
      mAuth.removeAuthStateListener(mAuthListener);
    }
  }

  /**
   * Attempts to sign in or register the account specified by the login form.
   * If there are form errors (invalid email, missing fields, etc.), the
   * errors are presented and no actual login attempt is made.
   */
  private void attemptLogin() {
    if(mAuthTask) {
      return;
    }

    // Reset errors.
    mEmailView.setError(null);
    mPasswordView.setError(null);

    // Store values at the time of the login attempt.
    String email = mEmailView.getText().toString().trim();
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
    if(email.trim().isEmpty()) {
      mEmailView.setError(getString(R.string.error_field_required));
      focusView = mEmailView;
      cancel = true;
    } else if(!isEmailValid(email)) {
      mEmailView.setError(getString(R.string.error_invalid_email));
      focusView = mEmailView;
      cancel = true;
    }

    if(cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      showProgress(true);
      View cfocus = this.getCurrentFocus();
      if(cfocus != null) {
        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(cfocus.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
      }
      SignupActivity.MyLoader task = new SignupActivity.MyLoader(email, password);
      task.execute((Void) null);
    }
  }

  private boolean isEmailValid(String email) {
    //TODO: Replace this with your own logic
    return email.contains("@");
  }

  private boolean isPasswordValid(String password) {
    //TODO: Replace this with your own logic
    return password.length() > 4;
  }

  /**
   * Shows the progress UI and hides the login form.
   */
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

  public class MyLoader extends AsyncTask<Object, Object, Void> {
    private String mEmail;
    private String mPassword;

    public MyLoader(@NonNull String email, @NonNull String password) {
      mEmail = email;
      mPassword = password;
    }

    @Override
    protected Void doInBackground(Object... objects) {
      if(multi < 3){
        multi++;
      }
      try {
        // Simulate network access.
        Thread.sleep(500 * multi);
      } catch(InterruptedException e) {
        return null;
      }

      // TODO: attempt authentication against a network service.
      mAuthTask = true;
      mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
          .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

              // If sign in fails, display a message to the user. If sign in succeeds
              // the auth state listener will be notified and logic to handle the
              // signed in user can be handled in the listener.
              mAuthTask = false;
              showProgress(false);

              if(task.isSuccessful()) {
                finish();
              } else {

                try {
                  throw task.getException();
                } catch(FirebaseAuthWeakPasswordException e) {
                  mPasswordView.setError(getString(R.string.error_invalid_password));
                  mPasswordView.requestFocus();
                }catch(FirebaseAuthInvalidUserException e) {
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
      return null;
    }
  }
}