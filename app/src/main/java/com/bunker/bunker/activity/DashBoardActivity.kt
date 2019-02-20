package com.bunker.bunker.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bunker.bunker.MyDatabase
import com.bunker.bunker.R
import com.bunker.bunker.fragment.MyCalendar
import com.bunker.bunker.fragment.MyContacts
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.security.NoSuchAlgorithmException

class DashBoardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
  private lateinit var drawerLayout: DrawerLayout
  private lateinit var mPagerAdapter: FragmentPagerAdapter
  private lateinit var mViewPager: ViewPager
  private lateinit var mDatabase: FirebaseDatabase
  private lateinit var connectedRef: DatabaseReference

  private val MyConection = object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
      val connected = snapshot.getValue(Boolean::class.java)!!
      if(connected) {
        Log.d(TAG, "connected")
        /*val snackbar = Snackbar.make(drawerLayout, "Sincronizando...", Snackbar.LENGTH_LONG)
        snackbar.show()*/
      } else {
        /*val snackbar = Snackbar.make(drawerLayout, "Usando Datos Locales", Snackbar.LENGTH_LONG)
        snackbar.show()*/
        Log.d(TAG, "not connected")
      }
    }

    override fun onCancelled(error: DatabaseError) {
      Log.d(TAG, "Listener was cancelled")
    }
  }

  public override fun onStop() {
    super.onStop()
    connectedRef.removeEventListener(MyConection)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Fresco.initialize(this)
    setContentView(R.layout.activity_dash_board)

    val user = FirebaseAuth.getInstance().currentUser

    mDatabase = MyDatabase.Database
    connectedRef = mDatabase.getReference(".info/connected")
    connectedRef.addValueEventListener(MyConection)

    drawerLayout = findViewById(R.id.drawer)

    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)

    val fab = findViewById<FloatingActionButton>(R.id.fab_add_client)
    fab.setOnClickListener { view ->
      //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()

      val context = view.context
      val intent = Intent(context, AddNewActivity::class.java)
      context.startActivity(intent)
    }

    supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_wrapped)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    val navigationView = findViewById<NavigationView>(R.id.nav_view)
    navigationView.setNavigationItemSelectedListener(this)
    val headerLayout = navigationView.inflateHeaderView(R.layout.navheader)
    if(user!!.email != null) {
      val email = headerLayout.findViewById<AppCompatTextView>(R.id.nav_email_view)
      email.text = user.email
    }
    if(user.displayName != null) {
      val name = headerLayout.findViewById<AppCompatTextView>(R.id.nav_name_view)
      name.text = user.displayName
    }
    val draweeView = headerLayout.findViewById<SimpleDraweeView>(R.id.nav_imageView)
    val roundingParams = RoundingParams.fromCornersRadius(5f)
    roundingParams.setBorder(ContextCompat.getColor(this, R.color.colorPrimaryDark), 1.0f)
    roundingParams.roundAsCircle = true
    draweeView.hierarchy.roundingParams = roundingParams
    if(user.photoUrl != null) {
      draweeView.setImageURI(user.photoUrl.toString())
    } else if(user.email != null) {
      val md5_email = md5(user.email!!)
      if(!md5_email.isEmpty()) {
        draweeView.setImageURI("https://secure.gravatar.com/avatar/$md5_email")
      }
    }


    // Create the adapter that will return a fragment for each section
    mPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
      private val mFragments = arrayOf(MyCalendar(), MyContacts())
      private val mFragmentNames = arrayOf("Calendario", "Contatctos")

      override fun getItem(position: Int): Fragment {
        return mFragments[position]
      }

      override fun getCount(): Int {
        return mFragments.size
      }

      override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentNames[position]
      }
    }
    // Set up the ViewPager with the sections adapter.
    mViewPager = findViewById(R.id.container)
    mViewPager.adapter = mPagerAdapter
    val tabLayout = findViewById<TabLayout>(R.id.tabs)
    tabLayout.setupWithViewPager(mViewPager)

    //
    //ImageView iv = (ImageView) findViewById(R.id.img_anim)
    //Animation rotation = AnimationUtils.loadAnimation(this, R.anim.popup_in)
    //rotation.setRepeatCount(1)
    //iv.startAnimation(rotation)
    //menu.findItem(R.id.my_menu_item_id).setActionView(iv)
  }

  override fun onBackPressed() {
    if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if(item.itemId == android.R.id.home) {
      drawerLayout.openDrawer(GravityCompat.START)
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    // Handle navigation view item clicks here.
    val id = item.itemId
    if(id == R.id.nav_logout) {
      FirebaseAuth.getInstance().signOut()
      val main = Intent(this, MainActivity::class.java)
      startActivity(main)
      finish()
    }
    drawerLayout.closeDrawer(GravityCompat.START)
    return true
  }

  companion object {

    fun md5(s: String): String {
      val MD5 = "MD5"
      try {
        // Create MD5 Hash
        val digest = java.security.MessageDigest.getInstance(MD5)
        digest.update(s.toByteArray())
        val messageDigest = digest.digest()

        // Create Hex String
        val hexString = StringBuilder()
        for(aMessageDigest in messageDigest) {
          var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
          while(h.length < 2)
            h = "0$h"
          hexString.append(h)
        }
        return hexString.toString()

      } catch(e: NoSuchAlgorithmException) {
        e.printStackTrace()
      }

      return ""
    }

    private const val TAG = "DashBoardActivity"
  }
}