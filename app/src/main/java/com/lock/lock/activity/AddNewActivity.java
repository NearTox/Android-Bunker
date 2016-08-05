package com.lock.lock.activity;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lock.lock.R;

public class AddNewActivity extends AppCompatActivity {
  private DrawerLayout drawerLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_new);

    setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId()== android.R.id.home){
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}