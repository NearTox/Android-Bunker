package com.bunker.bunker.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TextView;

import com.bunker.bunker.EmailFormater;
import com.bunker.bunker.MyDatabase;
import com.bunker.bunker.MyToast;
import com.bunker.bunker.R;
import com.bunker.bunker.model.CalendarModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddNewActivity extends AppCompatActivity
implements TextWatcher, ValueEventListener {
  public static final String EXTRA_POST_KEY = "EXTRA_POST_KEY";
  private static final String TAG = "AddNewActivity";

  Query mPostRef;
  private String mPostKey = "";
  private DatabaseReference mDatabase;
  private boolean mHasMod = false;
  private boolean mTask = false;

  private View mProgressView;
  private View mLoginFormView;

  private AppCompatSpinner mAseguradoraSpinner;
  private AppCompatSpinner mPlanSpinner;

  private TextInputLayout mPolizaLayout;
  private TextInputLayout mClientLayout;
  private TextInputLayout mDateLayout;
  private TextInputLayout mBeneficiarioLayout;
  private TextInputLayout mMontoLayout;
  private TextInputLayout mEmailLayout;
  private TextInputLayout mPhoneLayout;

  private AppCompatEditText mPoliza;
  private AppCompatEditText mClient;
  private AppCompatTextView mDate;
  private AppCompatEditText mBeneficiario;
  private AppCompatEditText mMonto;
  private AppCompatEditText mEmail;
  private AppCompatEditText mPhone;

  private void hideKeyboard() {
    if(getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
      InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
      inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if(mPostRef != null) {
      mPostRef.removeEventListener(this);
    }
  }

  Calendar myCalendar = Calendar.getInstance();

  DatePickerDialog.OnDateSetListener MyDatePick = new DatePickerDialog.OnDateSetListener() {

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
      myCalendar.set(Calendar.YEAR, year);
      myCalendar.set(Calendar.MONTH, monthOfYear);
      myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
      updateLabel();
    }

  };

  private void updateLabel() {

    String myFormat = "dd/MM/yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    mDate.setText(sdf.format(myCalendar.getTime()));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_new);

    mDatabase = MyDatabase.getInstance().getReference();

    if(getIntent() != null) {
      Intent Cons = getIntent();
      if(Cons.getStringExtra(EXTRA_POST_KEY) != null) {
        mPostKey = Cons.getStringExtra(EXTRA_POST_KEY);
        Log.i(TAG, EXTRA_POST_KEY + ": " + mPostKey);
      }
    }

    setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
    ActionBar acBar = getSupportActionBar();
    if(acBar!=null) {
      acBar.setDisplayHomeAsUpEnabled(true);
    }
    if(mPostKey.isEmpty()) {
      getSupportActionBar().setTitle("Nueva Poliza");
    } else {
      getSupportActionBar().setTitle("Modificar Poliza");
    }

    //
    mLoginFormView = findViewById(R.id.add_form);
    mProgressView = findViewById(R.id.add_progress);

    //

    mAseguradoraSpinner = (AppCompatSpinner)findViewById(R.id.add_aseguradora);
    mAseguradoraSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mHasMod = true;
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });
    mPlanSpinner = (AppCompatSpinner)findViewById(R.id.add_plan);
    mPlanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mHasMod = true;
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });

    //

    mPolizaLayout = (TextInputLayout)findViewById(R.id.add_num_poliza_layout);
    mPoliza = (AppCompatEditText)findViewById(R.id.add_num_poliza);

    if(mPostKey.isEmpty()) {
      mPoliza.addTextChangedListener(this);
    } else {
      mPoliza.setEnabled(false);
      mPoliza.setFocusable(false);
    }

    mClientLayout = (TextInputLayout)findViewById(R.id.add_client_layout);
    mClient = (AppCompatEditText)findViewById(R.id.add_client);
    mClient.addTextChangedListener(this);

    mClient.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_NEXT) {
          hideKeyboard();
          textView.clearFocus();
          mDate.requestFocus();
        }
        return true;
      }
    });

    mDateLayout = (TextInputLayout)findViewById(R.id.add_date_layout);
    mDate = (AppCompatTextView)findViewById(R.id.add_date);
    mDate.addTextChangedListener(this);

    mDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        hideKeyboard();
        new DatePickerDialog(AddNewActivity.this, MyDatePick, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
      }
    });

    mDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View view, boolean b) {
        if(b) {
          view.performClick();
        }
      }
    });

    mBeneficiarioLayout = (TextInputLayout)findViewById(R.id.add_beneficiario_layout);
    mBeneficiario = (AppCompatEditText)findViewById(R.id.add_beneficiario);
    mBeneficiario.addTextChangedListener(this);

    mMontoLayout = (TextInputLayout)findViewById(R.id.add_monto_layout);
    mMonto = (AppCompatEditText)findViewById(R.id.add_monto);
    mMonto.addTextChangedListener(this);
    mMonto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_NEXT) {
          hideKeyboard();
          textView.clearFocus();
          mAseguradoraSpinner.requestFocus();
          mAseguradoraSpinner.performClick();
        }
        return true;
      }
    });

    mEmailLayout = (TextInputLayout)findViewById(R.id.add_email_layout);
    mEmail = (AppCompatEditText)findViewById(R.id.add_email);
    mEmail.addTextChangedListener(this);

    mPhoneLayout = (TextInputLayout)findViewById(R.id.add_phone_layout);
    mPhone = (AppCompatEditText)findViewById(R.id.add_phone);
    mPhone.addTextChangedListener(this);

    if(!mPostKey.isEmpty()) {
      showProgress(true);
      mPostRef = mDatabase.child("contacts").child(getUid()).child(mPostKey);
      // Attach an listener to read the data at our posts reference
      mPostRef.addValueEventListener(this);
    } else {
      updateLabel();
      mHasMod = false;
      showProgress(false);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    /*if(mAdapter != null) {
      mAdapter.cleanup();
    }*/
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_add_new, menu);

    if(!mPostKey.isEmpty()) {
      menu.findItem(R.id.action_remove).setVisible(true);
    }
    return true;
  }

  CalendarModel newCalendar;

  private void UpdatePost() {
    if(newCalendar != null) {
      Map<String, Object> postValues = newCalendar.toMap();

      Map<String, Object> childUpdates = new HashMap<>();
      childUpdates.put("/contacts/" + getUid() + "/" + String.valueOf(newCalendar.NoPoliza), postValues);

      mDatabase.updateChildren(childUpdates);
    }
  }

  private void writeNewPost() {
    if(newCalendar != null) {
      mPostRef = mDatabase.child("contacts").child(getUid()).child(String.valueOf(newCalendar.NoPoliza));
      // Attach an listener to read the data at our posts reference
      mPostRef.addValueEventListener(this);
    }
  }

  @Override
  public void onDataChange(DataSnapshot snapshot) {
    Log.e(TAG, "La información ya existe");
    if((mTask && !mPostKey.isEmpty()) || !snapshot.exists()) {
      mTask = false;
      UpdatePost();
      finish();
    } else {
      if(mTask) {
        mTask = false;
        showProgress(false);
        MyToast.ShowToast("La información ya existe", AddNewActivity.this);
      }
      if(!mPostKey.isEmpty() && !mTask && !mHasMod) {
        showProgress(false);
        CalendarModel data = snapshot.getValue(CalendarModel.class);
        mPoliza.setText(String.valueOf(data.NoPoliza));
        mClient.setText(data.Nombre);
        mBeneficiario.setText(data.Beneficiario);
        mMonto.setText(String.valueOf(data.Monto));
        mEmail.setText(data.Email);
        mPhone.setText(data.Telefono);
        myCalendar.set(Calendar.DAY_OF_MONTH, data.Dia);
        myCalendar.set(Calendar.MONTH, data.Mes);
        myCalendar.set(Calendar.YEAR, data.Year);
        updateLabel();
        mHasMod = false;
      }
    }
  }

  @Override
  public void onCancelled(DatabaseError firebaseError) {
    Log.e(TAG, "La informacion no es axcesible");
  }

  private void doSave() {
    if(mTask) {
      return;
    }

    // Reset errors.
    mPolizaLayout.setError(null);
    mClientLayout.setError(null);
    mDateLayout.setError(null);
    mBeneficiarioLayout.setError(null);
    mMontoLayout.setError(null);
    mEmailLayout.setError(null);
    mPhoneLayout.setError(null);

    int poliza = Integer.parseInt("0" + mPoliza.getText().toString());
    String client = mClient.getText().toString().trim();

    String beneficiario = mBeneficiario.getText().toString().trim();
    double monto = Double.parseDouble("0" + mMonto.getText().toString());
    String email = mEmail.getText().toString().trim();
    EmailFormater email_info = new EmailFormater(email);
    String phone = mPhone.getText().toString().trim();

    boolean cancel = false;
    View focusView = null;

    // Check for a valid email address.
    if(poliza < 1) {
      mPolizaLayout.setError(getString(R.string.error_field_required));
      focusView = mPolizaLayout;
      cancel = true;
    } else if(client.split(" ").length < 2) {
      mClientLayout.setError(getString(R.string.error_invalid_name));
      focusView = mClientLayout;
      cancel = true;
    } else if(beneficiario.split(" ").length < 2) {
      mBeneficiarioLayout.setError(getString(R.string.error_invalid_name));
      focusView = mBeneficiarioLayout;
      cancel = true;
    } else if(monto <= 0) {
      mMontoLayout.setError(getString(R.string.error_field_required));
      focusView = mMontoLayout;
      cancel = true;
    } else if(!email.isEmpty() && !email_info.isValid()) {
      mEmailLayout.setError(getString(R.string.error_invalid_email));
      focusView = mEmailLayout;
      cancel = true;
    }

    if(cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      hideKeyboard();
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      showProgress(true);
      mTask = true;
      if(newCalendar == null) {
        newCalendar = new CalendarModel();
      }
      newCalendar.NoPoliza = poliza;
      newCalendar.Nombre = client;
      newCalendar.Beneficiario = beneficiario;
      newCalendar.Monto = monto;
      newCalendar.Dia = myCalendar.get(Calendar.DAY_OF_MONTH);
      newCalendar.Mes = myCalendar.get(Calendar.MONTH);
      newCalendar.Year = myCalendar.get(Calendar.YEAR);
      //newCalendar.Plan = ;
      if(email_info.isValid()) {
        newCalendar.Email = email_info.GetEmail();
      }
      if(!phone.isEmpty()) {
        newCalendar.Telefono = phone;
      }
      writeNewPost();
    }
  }

  private void doDialogFish() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
    //builder.setTitle("Title");
    builder.setMessage("¿Descartar este contacto?");

    String positiveText = "DESCARTAR";
    builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        // positive button logic
        finish();
      }
    });

    String negativeText = getString(android.R.string.cancel);
    builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        // negative button logic
      }
    });

    AlertDialog dialog = builder.create();
    // display dialog
    dialog.show();
  }

  @Override
  public void onBackPressed() {
    if(mHasMod) {
      doDialogFish();
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if(id == R.id.action_Save) {
      doSave();
    } else if(id == android.R.id.home || id == R.id.menu_add_new_descartar) {
      if(mHasMod) {
        doDialogFish();
      } else {
        finish();
      }
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public String getUid() {
    FirebaseUser aa = FirebaseAuth.getInstance().getCurrentUser();
    if(aa != null){
      return aa.getUid();
    }    else{
      return "";
    }
  }


  @Override
  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
  }

  @Override
  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
  }

  @Override
  public void afterTextChanged(Editable editable) {
    mHasMod = true;
  }

  /**
   * Shows the progress UI and hides the login form.
   */

  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
      mLoginFormView.setAlpha(show ? 1 : 0);
      mLoginFormView.animate().
        setDuration(shortAnimTime)
        .alpha(show ? 0 : 1)
        .setInterpolator(new DecelerateInterpolator())
        .setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
      });
      mProgressView.setAlpha(show ? 0 : 1);
      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mProgressView.animate().setDuration(shortAnimTime)
        .alpha(show ? 1 : 0)
        .setInterpolator(new DecelerateInterpolator())
        .setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
      });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }
}