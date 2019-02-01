package com.bunker.bunker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bunker.bunker.MyDatabase;
import com.bunker.bunker.R;
import com.bunker.bunker.fragment.MyCalendar;
import com.bunker.bunker.fragment.MyContacts;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DashBoardActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
  private final String TAG = DashBoardActivity.class.getSimpleName();
  private DrawerLayout drawerLayout;
  private FragmentPagerAdapter mPagerAdapter;
  private ViewPager mViewPager;
  FirebaseDatabase mDatabase;
  DatabaseReference connectedRef;
  private FirebaseUser user;

  public static final String md5(final String s) {
    final String MD5 = "MD5";
    try {
      // Create MD5 Hash
      MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
      digest.update(s.getBytes());
      byte[] messageDigest = digest.digest();

      // Create Hex String
      StringBuilder hexString = new StringBuilder();
      for(byte aMessageDigest : messageDigest) {
        String h = Integer.toHexString(0xFF & aMessageDigest);
        while(h.length() < 2)
          h = "0" + h;
        hexString.append(h);
      }
      return hexString.toString();

    } catch(NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return "";
  }

  private ValueEventListener MyConection = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot snapshot) {
      boolean connected = snapshot.getValue(Boolean.class);
      if(connected) {
        Log.d(TAG, "connected");
        //Snackbar snackbar = Snackbar.make(drawerLayout, "Sincronizando...", Snackbar.LENGTH_LONG);
        //snackbar.show();
      } else {
        //Snackbar snackbar = Snackbar.make(drawerLayout, "Usando Datos Locales", Snackbar.LENGTH_LONG);
        //snackbar.show();
        Log.d(TAG, "not connected");
      }
    }

    @Override
    public void onCancelled(DatabaseError error) {
      Log.d(TAG, "Listener was cancelled");
    }
  };

  @Override
  public void onStop() {
    super.onStop();
    if(connectedRef != null) {
      connectedRef.removeEventListener(MyConection);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Fresco.initialize(this);
    setContentView(R.layout.activity_dash_board);

    user = FirebaseAuth.getInstance().getCurrentUser();

    mDatabase = MyDatabase.getInstance();
    connectedRef = mDatabase.getReference(".info/connected");
    connectedRef.addValueEventListener(MyConection);

    drawerLayout = findViewById(R.id.drawer);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = findViewById(R.id.fab_add_client);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        Context context = view.getContext();
        Intent intent = new Intent(context, AddNewActivity.class);
        context.startActivity(intent);
      }
    });

    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_wrapped);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    View headerLayout = navigationView.inflateHeaderView(R.layout.navheader);
    if(user.getEmail() != null) {
      AppCompatTextView email = headerLayout.findViewById(R.id.nav_email_view);
      email.setText(user.getEmail());
    }
    if(user.getDisplayName() != null) {
      AppCompatTextView name = headerLayout.findViewById(R.id.nav_name_view);
      name.setText(user.getDisplayName());
    }
    SimpleDraweeView draweeView = headerLayout.findViewById(R.id.nav_imageView);
    RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
    roundingParams.setBorder(ContextCompat.getColor(this, R.color.colorPrimaryDark), 1.0f);
    roundingParams.setRoundAsCircle(true);
    draweeView.getHierarchy().setRoundingParams(roundingParams);
    if(user.getPhotoUrl() != null) {
      draweeView.setImageURI(user.getPhotoUrl());
    } else if(user.getEmail() != null) {
      String md5_email = md5(user.getEmail());
      if(!md5_email.isEmpty()) {
        draweeView.setImageURI("https://secure.gravatar.com/avatar/" + md5_email);
      }
    }


    // Create the adapter that will return a fragment for each section
    mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
      private final Fragment[] mFragments = new Fragment[]{new MyCalendar(), new MyContacts(),};
      private final String[] mFragmentNames = new String[]{"Calendario", "Contatctos",};

      @Override
      public Fragment getItem(int position) {
        return mFragments[position];
      }

      @Override
      public int getCount() {
        return mFragments.length;
      }

      @Override
      public CharSequence getPageTitle(int position) {
        return mFragmentNames[position];
      }
    };
    // Set up the ViewPager with the sections adapter.
    mViewPager = findViewById(R.id.container);
    mViewPager.setAdapter(mPagerAdapter);
    TabLayout tabLayout = findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(mViewPager);

    //
    //ImageView iv = (ImageView) findViewById(R.id.img_anim);
    //Animation rotation = AnimationUtils.loadAnimation(this, R.anim.popup_in);
    //rotation.setRepeatCount(1);
    //iv.startAnimation(rotation);
    //menu.findItem(R.id.my_menu_item_id).setActionView(iv);
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