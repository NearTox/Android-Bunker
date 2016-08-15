package com.bunker.bunker.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;

import com.bunker.bunker.EmailFormater;
import com.bunker.bunker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends BaseAuth {
  private static final String TAG = LoginActivity.class.getSimpleName();

  // UI references.
  private AppCompatEditText mEmailView;
  private AppCompatEditText mPasswordView;

  private View mProgressView;
  private View mLoginFormView;

  private boolean mAuthTask = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    //Toolbar myToolbar = (Toolbar)findViewById(R.id.toolbar);
    //setSupportActionBar(myToolbar);
    //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    //getSupportActionBar().setTitle(getString(R.string.action_sign_in));

    // Set up the login form.
    //EditText
    mPasswordView = (AppCompatEditText)findViewById(R.id.login_form_password);
    mEmailView = (AppCompatEditText)findViewById(R.id.login_form_email);

    //View
    mLoginFormView = findViewById(R.id.login_form);
    mProgressView = findViewById(R.id.login_progress);

    //Button
    AppCompatButton mEmailSignInButton = (AppCompatButton)findViewById(R.id.login_form_button);
    mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        attemptLogin();
      }
    });
    AppCompatButton mEmailRecover = (AppCompatButton)findViewById(R.id.login_form_recover);
    mEmailRecover.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        /*
        FirebaseAuth auth = FirebaseAuth.getInstance();
String emailAddress = "user@example.com";

auth.sendPasswordResetEmail(emailAddress)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                }
            }
        });
         */
      }
    });

  }

  @Override
  protected void onLogIn(@NonNull FirebaseUser user) {
    finish();
  }

  @Override
  protected void onLogOut() {
    Log.e(TAG, "Must only occur one time");
  }

  private void hideKeyboard() {
    if(getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
      InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
      inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
        InputMethodManager.HIDE_NOT_ALWAYS);
    }
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
      mPasswordView.setError(getString(R.string.error_small_password));
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
      hideKeyboard();
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      showProgress(true);

      mAuthTask = true;
      email = email_info.GetEmail();
      mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

          // If sign in fails, display a message to the user. If sign in succeeds
          // the auth state listener will be notified and logic to handle the
          // signed in user can be handled in the listener.
          mAuthTask = false;
          if(!task.isSuccessful()) {
            showProgress(false);
              /*try {
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
              }*/
            Exception theException = task.getException();
            if(theException != null) {
              if(theException instanceof FirebaseAuthException) {
                FirebaseAuthException exc = (FirebaseAuthException)theException;
                Log.e(TAG, "Exception: " + exc.getErrorCode());
              } else {
                Log.e(TAG, "Exception: " + theException.getMessage());
              }

              mPasswordView.setError(getString(R.string.error_incorrect_password));
              mPasswordView.requestFocus();
            }
          }

          // ...
        }
      });
    }
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
      int shortAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
      mLoginFormView.animate()
        .setDuration(shortAnimTime)
        .alpha(show ? 0 : 1)
        .setInterpolator(new DecelerateInterpolator())
        .setListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
          }
        });

      mProgressView.setAlpha(show ? 0 : 1);
      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mProgressView.animate()
        .setDuration(shortAnimTime)
        .alpha(show ? 1 : 0)
        .setInterpolator(new DecelerateInterpolator())
        .setListener(new AnimatorListenerAdapter() {
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

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if(id == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}