package com.bunker.bunker.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText

import com.bunker.bunker.EmailFormater
import com.bunker.bunker.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser

class LoginActivity : BaseAuth() {

  // UI references.
  private var mEmailView: AppCompatEditText? = null
  private var mPasswordView: AppCompatEditText? = null

  private var mProgressView: View? = null
  private var mLoginFormView: View? = null

  private var mAuthTask = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    //Toolbar myToolbar = (Toolbar)findViewById(R.id.toolbar);
    //setSupportActionBar(myToolbar);
    //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    //getSupportActionBar().setTitle(getString(R.string.action_sign_in));

    // Set up the login form.
    //EditText
    mPasswordView = findViewById(R.id.login_form_password)
    mEmailView = findViewById(R.id.login_form_email)

    //View
    mLoginFormView = findViewById(R.id.login_form)
    mProgressView = findViewById(R.id.login_progress)

    //Button
    val mEmailSignInButton = findViewById<AppCompatButton>(R.id.login_form_button)
    mEmailSignInButton.setOnClickListener { attemptLogin() }
    val mEmailRecover = findViewById<AppCompatButton>(R.id.login_form_recover)
    mEmailRecover.setOnClickListener {
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

  }

  override fun onLogIn(user: FirebaseUser) {
    finish()
  }

  override fun onLogOut() {
    Log.e(TAG, "Must only occur one time")
  }

  private fun hideKeyboard() {
    if(currentFocus != null && currentFocus!!.windowToken != null) {
      val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      inputManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken,
          InputMethodManager.HIDE_NOT_ALWAYS)
    }
  }

  private fun attemptLogin() {
    if(mAuthTask) {
      return
    }

    // Reset errors.
    mEmailView!!.error = null
    mPasswordView!!.error = null

    // Store values at the time of the login attempt.
    var email = mEmailView!!.text!!.toString().trim { it <= ' ' }
    val email_info = EmailFormater(email)
    val password = mPasswordView!!.text!!.toString().trim { it <= ' ' }

    var cancel = false
    var focusView: View? = null

    // Check for a valid password, if the user entered one.
    if(!isPasswordValid(password)) {
      mPasswordView!!.error = getString(R.string.error_small_password)
      focusView = mPasswordView
      cancel = true
    }

    // Check for a valid email address.
    if(email.isEmpty()) {
      mEmailView!!.error = getString(R.string.error_field_required)
      focusView = mEmailView
      cancel = true
    } else if(!email_info.isValid) {
      mEmailView!!.error = getString(R.string.error_invalid_email)
      focusView = mEmailView
      cancel = true
    }

    if(cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView!!.requestFocus()
    } else {
      hideKeyboard()
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      showProgress(true)

      mAuthTask = true
      email = email_info.GetEmail()
      mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful)

        // If sign in fails, display a message to the user. If sign in succeeds
        // the auth state listener will be notified and logic to handle the
        // signed in user can be handled in the listener.
        mAuthTask = false
        if(!task.isSuccessful) {
          showProgress(false)
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
          val theException = task.exception
          if(theException != null) {
            if(theException is FirebaseAuthException) {
              val exc = theException as FirebaseAuthException?
              Log.e(TAG, "Exception: " + exc!!.errorCode)
            } else {
              Log.e(TAG, "Exception: " + theException.message)
            }

            mPasswordView!!.error = getString(R.string.error_incorrect_password)
            mPasswordView!!.requestFocus()
          }
        }

        // ...
      }
    }
  }

  /**
   * Shows the progress UI and hides the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private fun showProgress(show: Boolean) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      val shortAnimTime = resources.getInteger(android.R.integer.config_mediumAnimTime)

      mLoginFormView!!.visibility = if(show) View.GONE else View.VISIBLE
      mLoginFormView!!.animate()
          .setDuration(shortAnimTime.toLong())
          .alpha((if(show) 0 else 1).toFloat())
          .setInterpolator(DecelerateInterpolator())
          .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
              mLoginFormView!!.visibility = if(show) View.GONE else View.VISIBLE
            }
          })

      mProgressView!!.alpha = (if(show) 0 else 1).toFloat()
      mProgressView!!.visibility = if(show) View.VISIBLE else View.GONE
      mProgressView!!.animate()
          .setDuration(shortAnimTime.toLong())
          .alpha((if(show) 1 else 0).toFloat())
          .setInterpolator(DecelerateInterpolator())
          .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
              mProgressView!!.visibility = if(show) View.VISIBLE else View.GONE
            }
          })
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      mProgressView!!.visibility = if(show) View.VISIBLE else View.GONE
      mLoginFormView!!.visibility = if(show) View.GONE else View.VISIBLE
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId

    if(id == android.R.id.home) {
      finish()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  companion object {
    private val TAG = LoginActivity::class.java.simpleName
  }
}