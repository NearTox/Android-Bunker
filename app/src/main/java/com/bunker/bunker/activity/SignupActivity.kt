package com.bunker.bunker.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bunker.bunker.EmailFormater
import com.bunker.bunker.R
import com.bunker.bunker.data.UserViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest

class SignupActivity : AppCompatActivity() {
  private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

  // UI references.
  private lateinit var mEmailView: TextInputEditText
  private lateinit var mPasswordView: TextInputEditText
  private lateinit var mNameView: TextInputEditText

  private lateinit var mProgressView: View
  private lateinit var mLoginFormView: View

  private var mAuthTask = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_signup)

    //Toolbar myToolbar = (Toolbar)findViewById(R.id.toolbar)
    //setSupportActionBar(myToolbar)
    //getSupportActionBar().setDisplayHomeAsUpEnabled(true)
    //getSupportActionBar().setTitle(getString(R.string.action_sign_up))

    // Set up the login form.
    //EditText
    mPasswordView = findViewById(R.id.sign_up_form_password)
    mEmailView = findViewById(R.id.sign_up_form_email)
    mNameView = findViewById(R.id.sign_up_form_name)

    //View
    mLoginFormView = findViewById(R.id.sign_up_form)
    mProgressView = findViewById(R.id.sign_up_progress)

    //Button
    val mEmailSignInButton = findViewById<MaterialButton>(R.id.sign_up_form_button)
    mEmailSignInButton.setOnClickListener { attemptLogin() }

    val viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
    viewModel.userData.observe(this, Observer {
      if(it.isLoged) {
        val name = mNameView.text.toString().trim { it <= ' ' }
        if(!name.isEmpty() && mAuthTask) {
          val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()

          mAuth.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if(task.isSuccessful) {
              Log.d(TAG, "User profile updated.")
            }
          }
        }
        finish()
      }
    })
  }


  private fun hideKeyboard() {
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS)
  }

  private fun attemptLogin() {
    if(mAuthTask) {
      return
    }

    // Reset errors.
    mEmailView.error = null
    mPasswordView.error = null

    // Store values at the time of the login attempt.
    var email = mEmailView.text.toString().trim { it <= ' ' }
    val email_info = EmailFormater(email)
    val password = mPasswordView.text.toString().trim { it <= ' ' }

    var cancel = false
    var focusView: View? = null

    // Check for a valid password, if the user entered one.
    if(!BaseAuth.isPasswordValid(password)) {
      mPasswordView.error = getString(R.string.error_small_password)
      focusView = mPasswordView
      cancel = true
    }

    // Check for a valid email address.
    if(email.isEmpty()) {
      mEmailView.error = getString(R.string.error_field_required)
      focusView = mEmailView
      cancel = true
    } else if(!email_info.isValid) {
      mEmailView.error = getString(R.string.error_invalid_email)
      focusView = mEmailView
      cancel = true
    }

    if(cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView?.requestFocus()
    } else {
      hideKeyboard()
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      showProgress(true)

      // TODO: attempt authentication against a network service.
      mAuthTask = true
      email = email_info.GetEmail()
      mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
        Log.d(TAG, "signInWithEmail: onComplete:" + task.isSuccessful)

        // If sign in fails, display a message to the user. If sign in succeeds
        // the auth state listener will be notified and logic to handle the
        // signed in user can be handled in the listener.
        mAuthTask = false

        if(!task.isSuccessful) {
          showProgress(false)
          /*try {
            throw task.exception
          } catch(e: FirebaseAuthWeakPasswordException) {
            mPasswordView.error = getString(R.string.error_invalid_password)
            mPasswordView.requestFocus()
          } catch(e: FirebaseAuthInvalidUserException) {
            mEmailView.error = getString(R.string.error_invalid_email)
            mEmailView.requestFocus()
          } catch(e: FirebaseAuthInvalidCredentialsException) {
            mPasswordView.error = getString(R.string.error_incorrect_password)
            mPasswordView.requestFocus()
          } catch(e: FirebaseAuthUserCollisionException) {
            mEmailView.error = getString(R.string.error_user_exists)
            mEmailView.requestFocus()
          } catch(e: Exception) {
            Log.e(TAG, "FirebaseAuthException: ", e)
          }*/
          val theException = task.exception
          if(theException != null) {
            if(theException is FirebaseAuthException) {
              val exc = theException as FirebaseAuthException?
              Log.e(TAG, "Exception: " + exc?.errorCode)
            } else {
              Log.e(TAG, "Exception: " + theException.message)
            }

            mPasswordView.error = getString(R.string.error_incorrect_password)
            mPasswordView.requestFocus()

          }
        }

      }
    }
  }

  private fun showProgress(show: Boolean) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    val shortAnimTime = resources.getInteger(android.R.integer.config_mediumAnimTime)

    mLoginFormView.visibility = if(show) View.GONE else View.VISIBLE
    mLoginFormView.animate()
        .setDuration(shortAnimTime.toLong())
        .alpha((if(show) 0 else 1).toFloat())
        .setInterpolator(DecelerateInterpolator())
        .setListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator) {
            mLoginFormView.visibility = if(show) View.GONE else View.VISIBLE
          }
        })

    mProgressView.alpha = (if(show) 0 else 1).toFloat()
    mProgressView.visibility = if(show) View.VISIBLE else View.GONE
    mProgressView.animate()
        .setDuration(shortAnimTime.toLong())
        .alpha((if(show) 1 else 0).toFloat())
        .setInterpolator(DecelerateInterpolator())
        .setListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator) {
            mProgressView.visibility = if(show) View.VISIBLE else View.GONE
          }
        })
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
    private val TAG = SignupActivity::class.java.simpleName
  }
}