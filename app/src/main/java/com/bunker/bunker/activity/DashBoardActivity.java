package com.bunker.bunker.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bunker.bunker.MyDatabase;
import com.bunker.bunker.R;
import com.bunker.bunker.fragment.MyCalendar;
import com.bunker.bunker.fragment.MyContacts;
import com.bunker.bunker.model.Empresa;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
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
  DatabaseReference mEmpresas;


  private FirebaseUser user;

  public static final String md5(final String s) {
    final String MD5 = "MD5";
    try {
      // Create MD5 Hash
      MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
      digest.update(s.getBytes());
      byte messageDigest[] = digest.digest();

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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Fresco.initialize(this);
    setContentView(R.layout.activity_dash_board);

    user = FirebaseAuth.getInstance().getCurrentUser();

    mDatabase = MyDatabase.getInstance();
    mEmpresas = mDatabase.getReference("empresas");
    DatabaseReference connectedRef = mDatabase.getReference(".info/connected");
    connectedRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot snapshot) {
        boolean connected = snapshot.getValue(Boolean.class);
        if(connected) {
          Log.d(TAG, "connected");
        } else {
          Log.d(TAG, "not connected");
        }
      }

      @Override
      public void onCancelled(DatabaseError error) {
        Log.d(TAG, "Listener was cancelled");
      }
    });

    mEmpresas.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(TAG, "There are " + dataSnapshot.getChildrenCount() + " blog posts");
        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
          String name = (String)postSnapshot.child("Nombre").getValue();
          Log.d(TAG, name);
          Empresa company = postSnapshot.getValue(Empresa.class);
          Log.d(TAG, company.toString());
        }
      }

      @Override
      public void onCancelled(DatabaseError error) {
        // Failed to read value
        Log.w(TAG, "Failed to read value.", error.toException());
      }
    });
    /*ChildEventListener childEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

        // A new comment has been added, add it to the displayed list
        Comment comment = dataSnapshot.getValue(Comment.class);

        // ...
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

        // A comment has changed, use the key to determine if we are displaying this
        // comment and if so displayed the changed comment.
        Comment newComment = dataSnapshot.getValue(Comment.class);
        String commentKey = dataSnapshot.getKey();

        // ...
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

        // A comment has changed, use the key to determine if we are displaying this
        // comment and if so remove it.
        String commentKey = dataSnapshot.getKey();

        // ...
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

        // A comment has changed position, use the key to determine if we are
        // displaying this comment and if so move it.
        Comment movedComment = dataSnapshot.getValue(Comment.class);
        String commentKey = dataSnapshot.getKey();

        // ...
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "postComments:onCancelled", databaseError.toException());
        Toast.makeText(DashBoardActivity.this, "Failed to load comments.",
            Toast.LENGTH_SHORT).show();
      }
    };
    mDatabase.addChildEventListener(childEventListener);*/


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

    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    View headerLayout = navigationView.inflateHeaderView(R.layout.navheader);
    if(user.getEmail() != null) {
      AppCompatTextView email = (AppCompatTextView)headerLayout.findViewById(R.id.nav_email_view);
      email.setText(user.getEmail());
    }
    if(user.getDisplayName() != null) {
      AppCompatTextView name = (AppCompatTextView)headerLayout.findViewById(R.id.nav_name_view);
      name.setText(user.getDisplayName());
    }
    SimpleDraweeView draweeView = (SimpleDraweeView)headerLayout.findViewById(R.id.nav_imageView);
    RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
    roundingParams.setBorder(R.color.colorPrimaryDark, 1.0f);
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
    mViewPager = (ViewPager)findViewById(R.id.container);
    mViewPager.setAdapter(mPagerAdapter);
    TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
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