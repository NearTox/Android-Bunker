package com.lock.lock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseUser;
import com.lock.lock.R;

public class MainActivity extends BaseAuth
implements View.OnClickListener {
  private static final String TAG = MainActivity.class.getName();

  @Override
  protected void onLogIn(@NonNull FirebaseUser user) {
    Intent dash_board = new Intent(this, DashBoardActivity.class);

    startActivity(dash_board);
    finish();
  }

  @Override
  protected void onLogOut() {

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Buttons
    Button mIniciar = (Button) findViewById(R.id.main_iniciar);
    Button mRegistar = (Button) findViewById(R.id.main_registro);
    mIniciar.setOnClickListener(this);
    mRegistar.setOnClickListener(this);

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