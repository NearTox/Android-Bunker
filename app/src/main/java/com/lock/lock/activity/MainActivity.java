package com.lock.lock.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lock.lock.R;

public class MainActivity extends AppCompatActivity
implements View.OnClickListener {
  private static final String TAG = MainActivity.class.getName();

  private void RedirectUser() {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if(user != null) {
      Intent dash_board = new Intent(this, DashBoardActivity.class);
      startActivity(dash_board);
      finish();
    }

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button mIniciar = (Button) findViewById(R.id.main_iniciar);
    Button mRegistar = (Button) findViewById(R.id.main_registro);
    mIniciar.setOnClickListener(this);
    mRegistar.setOnClickListener(this);
    RedirectUser();
  }

  @Override
  protected void onResume() {
    super.onResume();
    RedirectUser();
  }

  @Override
  public void onClick(View view) {
    if(view.getId() == R.id.main_iniciar) {
      startActivity(new Intent(this, LoginActivity.class));
    } else if(view.getId() == R.id.main_registro) {
      startActivity(new Intent(this, SignupActivity.class));
    } else {
      Log.d(TAG, "OnClick: " + view.toString());
    }
  }
}