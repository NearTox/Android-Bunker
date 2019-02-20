package com.bunker.bunker.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import com.bunker.bunker.EmailFormater
import com.bunker.bunker.MyDatabase
import com.bunker.bunker.MyToast
import com.bunker.bunker.R
import com.bunker.bunker.model.CalendarModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AddNewActivity : AppCompatActivity(), TextWatcher, ValueEventListener {

  private var mPostRef: Query? = null
  private lateinit var mPostKey: String
  private lateinit var mDatabase: DatabaseReference
  private var mHasMod = false
  private var mTask = false

  private lateinit var mProgressView: View
  private lateinit var mLoginFormView: View

  private lateinit var mAseguradoraSpinner: AppCompatSpinner
  private lateinit var mPlanSpinner: AppCompatSpinner

  private lateinit var mPolizaLayout: TextInputLayout
  private lateinit var mClientLayout: TextInputLayout
  private lateinit var mDateLayout: TextInputLayout
  private lateinit var mBeneficiarioLayout: TextInputLayout
  private lateinit var mMontoLayout: TextInputLayout
  private lateinit var mEmailLayout: TextInputLayout
  private lateinit var mPhoneLayout: TextInputLayout

  private lateinit var mPoliza: TextInputEditText
  private lateinit var mClient: TextInputEditText
  private lateinit var mDate: AppCompatTextView
  private lateinit var mBeneficiario: TextInputEditText
  private lateinit var mMonto: TextInputEditText
  private lateinit var mEmail: TextInputEditText
  private lateinit var mPhone: TextInputEditText

  private var myCalendar = Calendar.getInstance()

  private var MyDatePick: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
    myCalendar.set(Calendar.YEAR, year)
    myCalendar.set(Calendar.MONTH, monthOfYear)
    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    updateLabel()
  }

  private var newCalendar: CalendarModel? = null

  val uid: String
    get() {
      val aa = FirebaseAuth.getInstance().currentUser
      return aa?.uid ?: ""
    }

  private fun hideKeyboard() {
    if(currentFocus != null && currentFocus!!.windowToken != null) {
      val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      inputManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
  }

  public override fun onStop() {
    super.onStop()
    mPostRef?.removeEventListener(this)
  }

  private fun updateLabel() {

    val myFormat = "dd/MM/yyyy" //In which you need put here
    val sdf = SimpleDateFormat(myFormat, Locale.US)

    mDate.text = sdf.format(myCalendar.time)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_new)

    mDatabase = MyDatabase.Database.reference

    mPostKey = intent.getStringExtra(EXTRA_POST_KEY) ?: ""

    setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
    val acBar = supportActionBar
    acBar?.setDisplayHomeAsUpEnabled(true)
    if(mPostKey.isEmpty()) {
      supportActionBar?.title = "Nueva Poliza"
    } else {
      supportActionBar?.title = "Modificar Poliza"
    }

    //
    mLoginFormView = findViewById(R.id.add_form)
    mProgressView = findViewById(R.id.add_progress)

    //

    mAseguradoraSpinner = findViewById(R.id.add_aseguradora)
    mAseguradoraSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        mHasMod = true
      }

      override fun onNothingSelected(adapterView: AdapterView<*>) {

      }
    }
    mPlanSpinner = findViewById(R.id.add_plan)
    mPlanSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        mHasMod = true
      }

      override fun onNothingSelected(adapterView: AdapterView<*>) {

      }
    }

    //

    mPolizaLayout = findViewById(R.id.add_num_poliza_layout)
    mPoliza = findViewById(R.id.add_num_poliza)

    if(mPostKey.isEmpty()) {
      mPoliza.addTextChangedListener(this)
    } else {
      mPoliza.isEnabled = false
      mPoliza.isFocusable = false
    }

    mClientLayout = findViewById(R.id.add_client_layout)
    mClient = findViewById(R.id.add_client)
    mClient.addTextChangedListener(this)

    mClient.setOnEditorActionListener { textView, actionId, event ->
      if(actionId == EditorInfo.IME_ACTION_NEXT) {
        hideKeyboard()
        textView.clearFocus()
        mDate.requestFocus()
      }
      true
    }

    mDateLayout = findViewById(R.id.add_date_layout)
    mDate = findViewById(R.id.add_date)
    mDate.addTextChangedListener(this)

    mDate.setOnClickListener {
      hideKeyboard()
      DatePickerDialog(this, MyDatePick, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    mDate.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
      if(b) {
        view.performClick()
      }
    }

    mBeneficiarioLayout = findViewById(R.id.add_beneficiario_layout)
    mBeneficiario = findViewById(R.id.add_beneficiario)
    mBeneficiario.addTextChangedListener(this)

    mMontoLayout = findViewById(R.id.add_monto_layout)
    mMonto = findViewById(R.id.add_monto)
    mMonto.addTextChangedListener(this)
    mMonto.setOnEditorActionListener { textView, actionId, event ->
      if(actionId == EditorInfo.IME_ACTION_NEXT) {
        hideKeyboard()
        textView.clearFocus()
        mAseguradoraSpinner.requestFocus()
        mAseguradoraSpinner.performClick()
      }
      true
    }

    mEmailLayout = findViewById(R.id.add_email_layout)
    mEmail = findViewById(R.id.add_email)
    mEmail.addTextChangedListener(this)

    mPhoneLayout = findViewById(R.id.add_phone_layout)
    mPhone = findViewById(R.id.add_phone)
    mPhone.addTextChangedListener(this)

    if(!mPostKey.isEmpty()) {
      showProgress(true)
      mPostRef = mDatabase.child("contacts").child(uid).child(mPostKey)
      // Attach an listener to read the data at our posts reference
      mPostRef?.addValueEventListener(this)
    } else {
      updateLabel()
      mHasMod = false
      showProgress(false)
    }
  }

  public override fun onDestroy() {
    super.onDestroy()
    // if(mAdapter != null) mAdapter.cleanup()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.menu_add_new, menu)

    if(!mPostKey.isEmpty()) {
      menu.findItem(R.id.action_remove).isVisible = true
    }
    return true
  }

  private fun UpdatePost() {
    if(newCalendar != null) {
      val postValues = newCalendar!!.toMap()

      val childUpdates = HashMap<String, Any>()
      childUpdates["/contacts/" + uid + "/" + newCalendar!!.NoPoliza] = postValues

      mDatabase.updateChildren(childUpdates)
    }
  }

  private fun writeNewPost() {
    if(newCalendar != null) {
      mPostRef = mDatabase.child("contacts").child(uid).child(newCalendar!!.NoPoliza.toString())
      // Attach an listener to read the data at our posts reference
      mPostRef?.addValueEventListener(this)
    }
  }

  override fun onDataChange(snapshot: DataSnapshot) {
    Log.e(TAG, "La información ya existe")
    if(mTask && !mPostKey.isEmpty() || !snapshot.exists()) {
      mTask = false
      UpdatePost()
      finish()
    } else {
      if(mTask) {
        mTask = false
        showProgress(false)
        MyToast.ShowToast("La información ya existe", this)
      }
      if(!mPostKey.isEmpty() && !mTask && !mHasMod) {
        showProgress(false)
        val data = snapshot.getValue(CalendarModel::class.java)
        mPoliza.setText(data!!.NoPoliza.toString())
        mClient.setText(data.Nombre)
        mBeneficiario.setText(data.Beneficiario)
        mMonto.setText(data.Monto.toString())
        mEmail.setText(data.Email)
        mPhone.setText(data.Telefono)
        myCalendar.set(Calendar.DAY_OF_MONTH, data.Dia)
        myCalendar.set(Calendar.MONTH, data.Mes)
        myCalendar.set(Calendar.YEAR, data.Year)
        updateLabel()
        mHasMod = false
      }
    }
  }

  override fun onCancelled(firebaseError: DatabaseError) {
    Log.e(TAG, "La informacion no es axcesible")
  }

  private fun doSave() {
    if(mTask) {
      return
    }

    // Reset errors.
    mPolizaLayout.error = null
    mClientLayout.error = null
    mDateLayout.error = null
    mBeneficiarioLayout.error = null
    mMontoLayout.error = null
    mEmailLayout.error = null
    mPhoneLayout.error = null

    val poliza = Integer.parseInt("0" + mPoliza.text!!.toString())
    val client = mClient.text!!.toString().trim { it <= ' ' }

    val beneficiario = mBeneficiario.text!!.toString().trim { it <= ' ' }
    val monto = java.lang.Double.parseDouble("0" + mMonto.text!!.toString())
    val email = mEmail.text!!.toString().trim { it <= ' ' }
    val email_info = EmailFormater(email)
    val phone = mPhone.text!!.toString().trim { it <= ' ' }

    var cancel = false
    var focusView: View? = null

    // Check for a valid email address.
    if(poliza < 1) {
      mPolizaLayout.error = getString(R.string.error_field_required)
      focusView = mPolizaLayout
      cancel = true
    } else if(client.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size < 2) {
      mClientLayout.error = getString(R.string.error_invalid_name)
      focusView = mClientLayout
      cancel = true
    } else if(beneficiario.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size < 2) {
      mBeneficiarioLayout.error = getString(R.string.error_invalid_name)
      focusView = mBeneficiarioLayout
      cancel = true
    } else if(monto <= 0) {
      mMontoLayout.error = getString(R.string.error_field_required)
      focusView = mMontoLayout
      cancel = true
    } else if(!email.isEmpty() && !email_info.isValid) {
      mEmailLayout.error = getString(R.string.error_invalid_email)
      focusView = mEmailLayout
      cancel = true
    }

    if(cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView!!.requestFocus()
    } else {
      hideKeyboard()
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      showProgress(true)
      mTask = true
      if(newCalendar == null) {
        newCalendar = CalendarModel()
      }
      newCalendar!!.NoPoliza = poliza
      newCalendar!!.Nombre = client
      newCalendar!!.Beneficiario = beneficiario
      newCalendar!!.Monto = monto
      newCalendar!!.Dia = myCalendar.get(Calendar.DAY_OF_MONTH)
      newCalendar!!.Mes = myCalendar.get(Calendar.MONTH)
      newCalendar!!.Year = myCalendar.get(Calendar.YEAR)
      //newCalendar.Plan =
      if(email_info.isValid) {
        newCalendar!!.Email = email_info.GetEmail()
      }
      if(!phone.isEmpty()) {
        newCalendar!!.Telefono = phone
      }
      writeNewPost()
    }
  }

  private fun doDialogFish() {
    val builder = AlertDialog.Builder(this, R.style.AppDialogTheme)
    //builder.setTitle("Title")
    builder.setMessage("¿Descartar este contacto?")

    val positiveText = "DESCARTAR"
    builder.setPositiveButton(positiveText) { dialog, which ->
      // positive button logic
      finish()
    }

    val negativeText = getString(android.R.string.cancel)
    builder.setNegativeButton(negativeText) { dialog, which ->
      // negative button logic
    }

    val dialog = builder.create()
    // display dialog
    dialog.show()
  }

  override fun onBackPressed() {
    if(mHasMod) {
      doDialogFish()
    } else {
      super.onBackPressed()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId
    if(id == R.id.action_Save) {
      doSave()
    } else if(id == android.R.id.home || id == R.id.menu_add_new_descartar) {
      if(mHasMod) {
        doDialogFish()
      } else {
        finish()
      }
      return true
    }
    return super.onOptionsItemSelected(item)
  }


  override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

  override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

  override fun afterTextChanged(editable: Editable) {
    mHasMod = true
  }

  /**
   * Shows the progress UI and hides the login form.
   */
  private fun showProgress(show: Boolean) {
    val shortAnimTime = resources.getInteger(android.R.integer.config_mediumAnimTime)

    mLoginFormView.visibility = if(show) View.GONE else View.VISIBLE
    mLoginFormView.alpha = (if(show) 1 else 0).toFloat()
    mLoginFormView.animate().setDuration(shortAnimTime.toLong())
        .alpha((if(show) 0 else 1).toFloat())
        .setInterpolator(DecelerateInterpolator())
        .setListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator) {
            mLoginFormView.visibility = if(show) View.GONE else View.VISIBLE
          }
        })
    mProgressView.alpha = (if(show) 0 else 1).toFloat()
    mProgressView.visibility = if(show) View.VISIBLE else View.GONE
    mProgressView.animate().setDuration(shortAnimTime.toLong())
        .alpha((if(show) 1 else 0).toFloat())
        .setInterpolator(DecelerateInterpolator())
        .setListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator) {
            mProgressView.visibility = if(show) View.VISIBLE else View.GONE
          }
        })

  }

  companion object {
    private const val TAG = "AddNewActivity"
    const val EXTRA_POST_KEY = "EXTRA_POST_KEY"
  }
}