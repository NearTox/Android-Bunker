package com.bunker.bunker.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.bunker.bunker.R
import com.bunker.bunker.data.UserViewModel
import com.bunker.bunker.data.UserViewModelFactory
import com.bunker.bunker.fragment.MyCalendar
import com.bunker.bunker.fragment.MyContacts
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout

class DashBoardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
  private lateinit var viewModel: UserViewModel
  private lateinit var drawerLayout: DrawerLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Fresco.initialize(this)
    setContentView(R.layout.activity_dash_board)

    drawerLayout = findViewById(R.id.drawer)

    val toolbar: Toolbar = findViewById(R.id.toolbar)
    setSupportActionBar(toolbar)

    val fab: FloatingActionButton = findViewById(R.id.fab_add_client)
    fab.setOnClickListener {
      val intent = Intent(this, AddNewActivity::class.java)
      startActivity(intent)
    }

    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_wrapped)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    // Create the adapter that will return a fragment for each section
    val mPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
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
    val mViewPager: ViewPager = findViewById(R.id.container)
    mViewPager.adapter = mPagerAdapter
    val tabLayout = findViewById<TabLayout>(R.id.tabs)
    tabLayout.setupWithViewPager(mViewPager)

    //
    //ImageView iv = (ImageView) findViewById(R.id.img_anim)
    //Animation rotation = AnimationUtils.loadAnimation(this, R.anim.popup_in)
    //rotation.setRepeatCount(1)
    //iv.startAnimation(rotation)
    //menu.findItem(R.id.my_menu_item_id).setActionView(iv)

    viewModel = ViewModelProviders.of(this, UserViewModelFactory(this))
        .get(UserViewModel::class.java)

    val navigationView: NavigationView = findViewById(R.id.nav_view)
    navigationView.setNavigationItemSelectedListener(this)
    val headerLayout = navigationView.inflateHeaderView(R.layout.navheader)

    val email: AppCompatTextView = headerLayout.findViewById(R.id.nav_email_view)
    val name: AppCompatTextView = headerLayout.findViewById(R.id.nav_name_view)
    val draweeView: SimpleDraweeView = headerLayout.findViewById(R.id.nav_imageView)

    viewModel.userData.observe(this, Observer {
      if(it.isLogged) {
        email.text = it.email
        name.text = it.name
        draweeView.setImageURI(it.photoUrl)
      } else if(viewModel.autoLogin) {
        viewModel.signIn()
      }
    })
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    // Result returned from launching the Intent from GoogleSignInApi
    viewModel.onActivityResult(requestCode, resultCode, data)
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
      viewModel.signOut()
    }
    drawerLayout.closeDrawer(GravityCompat.START)
    return true
  }
}