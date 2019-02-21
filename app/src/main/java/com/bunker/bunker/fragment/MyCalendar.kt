package com.bunker.bunker.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bunker.bunker.R
import com.bunker.bunker.activity.AddNewActivity
import com.bunker.bunker.data.MyDatabase
import com.bunker.bunker.data.UserViewModel
import com.bunker.bunker.data.UserViewModelFactory
import com.bunker.bunker.data.model.CalendarModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.Query
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR

class MyCalendar : Fragment() {
  private lateinit var viewModel: UserViewModel
  private lateinit var mRecycler: RecyclerView

  private lateinit var mMesesStr: Array<String>
  private val myCalendar = java.util.Calendar.getInstance()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    //super.onCreateView(inflater, container, savedInstanceState)
    mMesesStr = resources.getStringArray(R.array.month)
    return inflater.inflate(R.layout.fragment_all_data, container, false).apply {
      mRecycler = findViewById(R.id.all_data_list)
      //mRecycler.setHasFixedSize(true)
    }
  }

  private fun createAdapter() {
    // Set up FirebaseRecyclerAdapter with the Query
    val postsQuery = getQuery()
    val options = FirebaseRecyclerOptions.Builder<CalendarModel>()
        .setQuery(postsQuery, CalendarModel::class.java)
        .setLifecycleOwner(this)
        .build()

    mRecycler.adapter = object : FirebaseRecyclerAdapter<CalendarModel, CalendarHolder>(options) {
      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarHolder {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)

        return CalendarHolder(view)
      }

      override fun onBindViewHolder(viewHolder: CalendarHolder, position: Int, model: CalendarModel) {
        val postRef = getRef(position)

        // Set click listener for the whole post view
        val postKey = postRef.key
        viewHolder.itemView.setOnClickListener {
          // Launch PostDetailActivity
          val intent = Intent(activity, AddNewActivity::class.java)
          intent.putExtra(AddNewActivity.EXTRA_POST_KEY, postKey)
          startActivity(intent)
        }

        // Determine if the current user has liked this post and set UI accordingly
        /*if (model.stars.containsKey(getUid())) {
          viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24)
        } else {
          viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24)
        }*/

        // Bind Post to ViewHolder, setting OnClickListener for the star button
        bindToPost(viewHolder, model, View.OnClickListener { })
      }
    }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    viewModel = ViewModelProviders.of(this, UserViewModelFactory(activity!!))
        .get(UserViewModel::class.java)

    // Set up Layout Manager, reverse layout
    val mManager = LinearLayoutManager(activity)
    //mManager.setReverseLayout(true)
    //mManager.setStackFromEnd(true)
    mRecycler.layoutManager = mManager

    viewModel.userData.observe(this, Observer {
      if(it.isLogged) createAdapter()
    })
  }

  private fun comparePlan(post: CalendarModel): Boolean {
    if(post.Plan in 1..4 || post.Plan == 6 || post.Plan == 12) {
      val mod = (post.Mes + 1) % post.Plan
      return mod != (myCalendar.get(MONTH) + 1) % post.Plan
    } else {
      return !(post.Mes == myCalendar.get(MONTH) && post.Year == myCalendar.get(YEAR))
    }
  }

  fun bindToPost(pThis: CalendarHolder, post: CalendarModel, starClickListener: View.OnClickListener) {
    pThis.dayView.text = post.Dia.toString()
    val isVisible = !comparePlan(post)
    if(isVisible) {
      pThis.monthView.text = mMesesStr[myCalendar.get(MONTH)]
    } else if(post.Mes < mMesesStr.size) {
      pThis.monthView.text = mMesesStr[post.Mes]
    } else {
      pThis.monthView.text = post.Mes.toString()
    }
    pThis.nameView.text = post.Nombre
    pThis.subNameView.text = post.NoPoliza.toString()
    pThis.iconView.setOnClickListener(starClickListener)
    pThis.hidePost(comparePlan(post))
  }

  class CalendarHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    internal val dayView: AppCompatTextView = view.findViewById(R.id.item_day)
    internal val monthView: AppCompatTextView = view.findViewById(R.id.item_month)
    internal val nameView: AppCompatTextView = view.findViewById(R.id.item_name)
    internal val subNameView: AppCompatTextView = view.findViewById(R.id.item_subname)
    internal val iconView: AppCompatImageView = view.findViewById(R.id.item_icon)

    init {
      hidePost(true)
    }

    internal fun hidePost(hide: Boolean) {
      val param = view.layoutParams as RecyclerView.LayoutParams
      if(param.bottomMargin != 0 && myMargin == 0) {
        myMargin = param.bottomMargin
      }
      if(hide) {
        param.height = 0
        param.bottomMargin = 0
        param.topMargin = 0
        view.alpha = 0f
      } else {
        param.height = LinearLayout.LayoutParams.WRAP_CONTENT
        param.bottomMargin = myMargin
        param.topMargin = myMargin
        view.alpha = 1f
      }
      view.layoutParams = param
    }

    companion object {
      private var myMargin = 0
    }
  }

  private fun getQuery(): Query {
    // All my posts
    val myData = MyDatabase.Database.reference.child("contacts/${viewModel.userData.value?.uid}")
    myData.keepSynced(true)
    return myData.orderByChild("Dia")
  }
}