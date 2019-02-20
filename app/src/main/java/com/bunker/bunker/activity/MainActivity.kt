package com.bunker.bunker.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.bunker.bunker.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val currentUser = FirebaseAuth.getInstance().currentUser
    if(currentUser != null) {
      val dashboard = Intent(this, DashBoardActivity::class.java)
      startActivity(dashboard)
      finish()
    }

    // Buttons
    val mIniciar: MaterialButton = findViewById(R.id.main_iniciar)
    val mRegistar: MaterialButton = findViewById(R.id.main_registro)
    mIniciar.setOnClickListener {
      val logo: View = findViewById(R.id.logo_app)
      val intent = Intent(this, LoginActivity::class.java)
      val transitionName = getString(R.string.logo_animation)
      val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, logo, // The view which starts the transition
          transitionName    // The transitionName of the view we’re transitioning to
      )
      ActivityCompat.startActivity(this, intent, options.toBundle())
      // startActivity(Intent(this, LoginActivity::class.java))
    }
    mRegistar.setOnClickListener {
      val logo = findViewById<View>(R.id.logo_app)
      val intent = Intent(this, SignupActivity::class.java)
      val transitionName = getString(R.string.logo_animation)
      val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, logo, // The view which starts the transition
          transitionName    // The transitionName of the view we’re transitioning to
      )
      ActivityCompat.startActivity(this, intent, options.toBundle())
      // startActivity(Intent(this, SignupActivity::class.java))
    }
  }

}