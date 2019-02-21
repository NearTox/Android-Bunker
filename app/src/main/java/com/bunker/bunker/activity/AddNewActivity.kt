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
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bunker.bunker.R
import com.bunker.bunker.data.EmailFormatter
import com.bunker.bunker.data.MyDatabase
import com.bunker.bunker.data.UserViewModel
import com.bunker.bunker.data.UserViewModelFactory
import com.bunker.bunker.data.model.CalendarModel
import com.bunker.bunker.data.model.UserData
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

class AddNewActivity : AppCompatActivity(), TextWatcher {
  private lateinit var viewModel: UserViewModel
  private lateinit var userDateObserver: Observer<UserData>
  private val mDatabase = MyDatabase.Database.reference

  private lateinit var mPostKey: String
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

  private val myCalendar = Calendar.getInstance()

  private fun hideKeyboard() {
    val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
  }

  private fun updateLabel() {
    val current = ConfigurationCompat.getLocales(resources.configuration).get(0)
    val format = SimpleDateFormat("dd/MM/yyyy", current)

    mDate.text = format.format(myCalendar.time)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_new)

    mPostKey = intent.getStringExtra(EXTRA_POST_KEY) ?: ""

    setSupportActionBar(findViewById(R.id.toolbar))

    viewModel = ViewModelProviders.of(this, UserViewModelFactory(this))
        .get(UserViewModel::class.java)

    supportActionBar?.run {
      setDisplayHomeAsUpEnabled(true)
      title = if(mPostKey.isEmpty()) {
        "Nueva Poliza"
      } else {
        "Modificar Poliza"
      }
    }

    //
    mLoginFormView = findViewById(R.id.add_form)
    mProgressView = findViewById(R.id.add_progress)

    //
    val onItemChange = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        mHasMod = true
      }

      override fun onNothingSelected(adapterView: AdapterView<*>) {
      }
    }
    mAseguradoraSpinner = findViewById(R.id.add_aseguradora)
    mAseguradoraSpinner.onItemSelectedListener = onItemChange
    mPlanSpinner = findViewById(R.id.add_plan)
    mPlanSpinner.onItemSelectedListener = onItemChange

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
      return@setOnEditorActionListener true
    }

    mDateLayout = findViewById(R.id.add_date_layout)
    mDate = findViewById(R.id.add_date)
    mDate.addTextChangedListener(this)


    mDate.setOnClickListener {
      hideKeyboard()
      val mDatePick: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        myCalendar.set(YEAR, year)
        myCalendar.set(MONTH, monthOfYear)
        myCalendar.set(DAY_OF_MONTH, dayOfMonth)
        updateLabel()
      }
      DatePickerDialog(this, mDatePick, myCalendar.get(YEAR), myCalendar.get(MONTH), myCalendar.get(DAY_OF_MONTH))
          .show()
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

    val once = object : ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        if(!snapshot.exists()) return

        showProgress(false)
        val data = snapshot.getValue(CalendarModel::class.java)!!
        mPoliza.setText(data.NoPoliza.toString())
        mClient.setText(data.Nombre)
        mBeneficiario.setText(data.Beneficiario)
        mMonto.setText(data.Monto.toString())
        mEmail.setText(data.Email)
        mPhone.setText(data.Telefono)

        myCalendar.set(DAY_OF_MONTH, data.Dia)
        myCalendar.set(MONTH, data.Mes)
        myCalendar.set(YEAR, data.Year)

        updateLabel()
        mHasMod = false
      }

      override fun onCancelled(firebaseError: DatabaseError) {
        Log.e(TAG, "La informacion no es axcesible")
      }
    }

    userDateObserver = androidx.lifecycle.Observer {
      if(!mPostKey.isEmpty()) {
        showProgress(true)
        val mPostRef = mDatabase.child("contacts/${it.uid}/$mPostKey")
        // Attach an listener to read the data at our posts reference
        mPostRef.addListenerForSingleValueEvent(once)
      } else {
        updateLabel()
        mHasMod = false
        showProgress(false)
      }
      viewModel.userData.removeObserver(userDateObserver)
    }

    viewModel.userData.observe(this, userDateObserver)

  }

  override fun onDestroy() {
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

  private fun updatePost(calendar: CalendarModel) {
    val postValues = calendar.toMap()

    val childUpdates = mapOf(
        "/contacts/${viewModel.userData.value?.uid}/${calendar.NoPoliza}" to postValues
    )
    mDatabase.updateChildren(childUpdates).addOnSuccessListener {
      finish()
    }.addOnFailureListener {
      Snackbar.make(findViewById(android.R.id.content), "Error al actualizar la poliza", Snackbar.LENGTH_LONG).show()
    }
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

    val poliza = Integer.parseInt("0" + mPoliza.text)
    val client = mClient.text?.trim { it <= ' ' }.toString()

    val beneficiario = mBeneficiario.text?.trim { it <= ' ' }.toString()
    val monto = java.lang.Double.parseDouble("0" + mMonto.text?.toString())
    val email = mEmail.text?.trim { it <= ' ' }.toString()
    val email_info = EmailFormatter(email)
    val phone = mPhone.text?.trim { it <= ' ' }.toString()

    var cancel = false
    var focusView: View? = null

    // Check for a valid email address.
    if(poliza < 1) {
      mPolizaLayout.error = getString(R.string.error_field_required)
      focusView = mPolizaLayout
      cancel = true
    } else if(client.split(" ").dropLastWhile { it.isEmpty() }.toTypedArray().size < 2) {
      mClientLayout.error = getString(R.string.error_invalid_name)
      focusView = mClientLayout
      cancel = true
    } else if(beneficiario.split(" ").dropLastWhile { it.isEmpty() }.toTypedArray().size < 2) {
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
      focusView?.requestFocus()
    } else {
      hideKeyboard()
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      showProgress(true)
      mTask = true

      val newCalendar = CalendarModel()
      newCalendar.NoPoliza = poliza
      newCalendar.Nombre = client
      newCalendar.Beneficiario = beneficiario
      newCalendar.Monto = monto
      newCalendar.Dia = myCalendar.get(Calendar.DAY_OF_MONTH)
      newCalendar.Mes = myCalendar.get(Calendar.MONTH)
      newCalendar.Year = myCalendar.get(Calendar.YEAR)
      //newCalendar.Plan =
      if(email_info.isValid) {
        newCalendar.Email = email_info.GetEmail()
      }
      if(!phone.isEmpty()) {
        newCalendar.Telefono = phone
      }
      updatePost(newCalendar)
    }
  }

  private fun doDialogFish() {
    val dialog = AlertDialog.Builder(this, R.style.AppDialogTheme)
        //.setTitle("Title")
        .setMessage("¿Descartar este contacto?").setPositiveButton("DESCARTAR") { _, _ ->
          // positive button logic
          finish()
        }.setNegativeButton(getString(android.R.string.cancel)) { _, _ ->
          // negative button logic
        }.create()
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