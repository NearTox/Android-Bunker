package com.lock.lock.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lock.lock.R;
import com.lock.lock.recycler.CalendarAdapter;

import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DashBoardActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  private RecyclerView mCalendarList;
  private CalendarAdapter mCalendarAdapter;
  private DrawerLayout drawerLayout;

  private FirebaseUser user;
  public static final String md5(final String s) {
    final String MD5 = "MD5";
    try {
      // Create MD5 Hash
      MessageDigest digest = java.security.MessageDigest
          .getInstance(MD5);
      digest.update(s.getBytes());
      byte messageDigest[] = digest.digest();

      // Create Hex String
      StringBuilder hexString = new StringBuilder();
      for (byte aMessageDigest : messageDigest) {
        String h = Integer.toHexString(0xFF & aMessageDigest);
        while (h.length() < 2)
          h = "0" + h;
        hexString.append(h);
      }
      return hexString.toString();

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return "";
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Fresco.initialize(this);
    setContentView(R.layout.activity_dash_board);

    user = FirebaseAuth.getInstance().getCurrentUser();
    drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

    Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_add_client);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        Context context = view.getContext();
        Intent intent = new Intent(context, AddNewActivity.class);
        context.startActivity(intent);
      }
    });
    final ActionBar ab = getSupportActionBar();
    ab.setHomeAsUpIndicator(R.drawable.ic_menu);
    ab.setDisplayHomeAsUpEnabled(true);

    NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    View headerLayout = navigationView.inflateHeaderView(R.layout.navheader);
    if(user.getEmail() != null) {
      TextView email = (TextView)headerLayout.findViewById(R.id.nav_email_view);
      email.setText(user.getEmail());
    }
    if(user.getDisplayName() != null) {
      TextView name = (TextView)headerLayout.findViewById(R.id.nav_name_view);
      name.setText(user.getDisplayName());
    }
    SimpleDraweeView draweeView = (SimpleDraweeView)headerLayout.findViewById(R.id.nav_imageView);
    RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
    roundingParams.setBorder(R.color.colorPrimaryDark, 1.0f);
    roundingParams.setRoundAsCircle(true);
    draweeView.getHierarchy().setRoundingParams(roundingParams);
    if(user.getPhotoUrl() != null) {
      draweeView.setImageURI(user.getPhotoUrl());
    }else if(user.getEmail() != null) {
      String md5_email = md5(user.getEmail());
      if(!md5_email.isEmpty()) {
        draweeView.setImageURI("https://secure.gravatar.com/avatar/" + md5_email);
      }
    }

    mCalendarList = (RecyclerView)findViewById(R.id.my_calendar_list);
    mCalendarList.setLayoutManager(new LinearLayoutManager(this));
    mCalendarAdapter = new CalendarAdapter(this);
    mCalendarList.setAdapter(mCalendarAdapter);
  }

  @Override
  public void onBackPressed() {
    if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == android.R.id.home) {
      drawerLayout.openDrawer(GravityCompat.START);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
    if(id == R.id.nav_logout) {
      FirebaseAuth.getInstance().signOut();
      Intent main = new Intent(this, MainActivity.class);
      startActivity(main);
      finish();
    }
    drawerLayout.closeDrawer(GravityCompat.START);
    return true;
  }
}