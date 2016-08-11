package com.bunker.bunker.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.bunker.bunker.MyDatabase;
import com.bunker.bunker.R;
import com.bunker.bunker.model.CalendarModel;

import java.util.HashMap;
import java.util.Map;

public class AddNewActivity extends AppCompatActivity {
  public static final String EXTRA_POST_KEY = "EXTRA_POST_KEY";
  private static final String TAG = "AddNewActivity";
  private String mPostKey = "";
  private DatabaseReference mDatabase;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_new);

    mDatabase = MyDatabase.getInstance().getReference();

    if(savedInstanceState != null) {
      if(savedInstanceState.getString(EXTRA_POST_KEY) != null) {
        mPostKey = savedInstanceState.getString(EXTRA_POST_KEY);
        Log.i(TAG, EXTRA_POST_KEY + ": " + mPostKey);
      }
    }

    setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Nueva Poliza");

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_add_new, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if(id == android.R.id.home) {
      writeNewPost(1234, "567", "89");
      finish();
      return true;
    } else if(id == R.id.menu_add_new_descartar) {

      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void writeNewPost(int NoPoliza, String name, String email) {
    // Create new post at /user-posts/$userid/$postid and at
    // /posts/$postid simultaneously
    String key = mDatabase.child("contacts").child(getUid()).push().getKey();
    CalendarModel contact = new CalendarModel();
    contact.NoPoliza = NoPoliza;
    contact.Nombre = name;
    contact.Email = email;
    Map<String, Object> postValues = contact.toMap();

    Map<String, Object> childUpdates = new HashMap<>();
    childUpdates.put("/contacts/" + getUid() + "/" + key, postValues);

    mDatabase.updateChildren(childUpdates);
  }

  public String getUid() {
    return FirebaseAuth.getInstance().getCurrentUser().getUid();
  }
}