package com.bunker.bunker.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat

import com.bunker.bunker.R
import com.google.firebase.auth.FirebaseUser

class MainActivity : BaseAuth(), View.OnClickListener {

  override fun onLogIn(user: FirebaseUser) {
    val dash_board = Intent(this, DashBoardActivity::class.java)

    startActivity(dash_board)
    finish()
  }

  override fun onLogOut() {

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Buttons
    val mIniciar = findViewById<AppCompatButton>(R.id.main_iniciar)
    val mRegistar = findViewById<AppCompatButton>(R.id.main_registro)
    mIniciar.setOnClickListener(this)
    mRegistar.setOnClickListener(this)

  }

  override fun onClick(view: View) {
    if(view.id == R.id.main_iniciar) {
      val logo = findViewById<View>(R.id.logo_app)
      val intent = Intent(this, LoginActivity::class.java)
      val transitionName = getString(R.string.logo_animation)
      val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, logo, // The view which starts the transition
          transitionName    // The transitionName of the view we’re transitioning to
      )
      ActivityCompat.startActivity(this, intent, options.toBundle())
      //startActivity(new Intent(this, LoginActivity.class));
    } else if(view.id == R.id.main_registro) {
      val logo = findViewById<View>(R.id.logo_app)
      val intent = Intent(this, SignupActivity::class.java)
      val transitionName = getString(R.string.logo_animation)
      val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, logo, // The view which starts the transition
          transitionName    // The transitionName of the view we’re transitioning to
      )
      ActivityCompat.startActivity(this, intent, options.toBundle())
      //startActivity(new Intent(this, SignupActivity.class));
    } else {
      Log.d(TAG, "OnClick: $view")
    }
  }

  companion object {
    private val TAG = MainActivity::class.java.name
  }
}