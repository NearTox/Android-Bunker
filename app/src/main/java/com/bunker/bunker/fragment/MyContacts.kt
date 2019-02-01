package com.bunker.bunker.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.bunker.bunker.MyDatabase
import com.bunker.bunker.R
import com.bunker.bunker.activity.AddNewActivity
import com.bunker.bunker.model.CalendarModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

class MyContacts : Fragment() {
  private val SelectedItems: SparseBooleanArray? = null
  private var mDatabase: DatabaseReference? = null

  private var mAdapter: FirebaseRecyclerAdapter<CalendarModel, ContactsHolder>? = null
  private var mRecycler: RecyclerView? = null
  private var mManager: LinearLayoutManager? = null

  val uid: String
    get() {
      val aa = FirebaseAuth.getInstance().currentUser
      return aa?.uid ?: ""
    }

  /*public void toggleSelection(int pos){
    if(SelectedItems.get(pos,false)){
      SelectedItems.delete(pos);
    }else{
      SelectedItems.put(pos,true);
    }
    mAdapter.notifyDataSetChanged();
  }

  public void clearSelections(){
    SelectedItems.clear();
    mAdapter.notifyDataSetChanged();
  }
  public List<Integer> getSelectedItemCount() {
    List<Integer> items = new ArrayList<Integer>(SelectedItems.size());
    for(int i = 0; i < SelectedItems.size(); i++) {
      items.add(SelectedItems.keyAt(i));
    }
    return items;
  }
  public void onLongPress(MotionEvent e) {
   View view =
      recyclerView.findChildViewUnder(e.getX(), e.getY());
   if (actionMode != null) {
      return;
   }
   actionMode =
      startActionMode(RecyclerViewDemoActivity.this);
   int idx = recyclerView.getChildPosition(view);
   myToggleSelection(idx);
   super.onLongPress(e);
}

private void myToggleSelection(int idx) {
   adapter.toggleSelection(idx);
   String title = getString(
         R.string.selected_count,
         adapter.getSelectedItemCount());
   actionMode.setTitle(title);
}*/
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    //super.onCreateView(inflater, container, savedInstanceState);
    val rootView = inflater.inflate(R.layout.fragment_all_data, container, false)

    // [START create_database_reference]
    mDatabase = MyDatabase.Database.getReference()
    // [END create_database_reference]

    mRecycler = rootView.findViewById(R.id.all_data_list)
    mRecycler!!.setHasFixedSize(true)

    return rootView
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    // Set up Layout Manager, reverse layout
    mManager = LinearLayoutManager(activity)
    //mManager.setReverseLayout(true);
    //mManager.setStackFromEnd(true);
    mRecycler!!.layoutManager = mManager

    // Set up FirebaseRecyclerAdapter with the Query
    val postsQuery = getQuery(mDatabase)

    val options = FirebaseRecyclerOptions.Builder<CalendarModel>()
        .setQuery(postsQuery, CalendarModel::class.java)
        .build()
    mAdapter = object : FirebaseRecyclerAdapter<CalendarModel, ContactsHolder>(options) {
      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contants, parent, false)

        return ContactsHolder(view)
      }


      override fun onBindViewHolder(viewHolder: ContactsHolder, position: Int, model: CalendarModel) {
        val postRef = getRef(position)

        // Set click listener for the whole post view
        val postKey = postRef.key
        viewHolder.itemView.setOnClickListener {
          // Launch PostDetailActivity
          val intent = Intent(activity, AddNewActivity::class.java)
          intent.putExtra(AddNewActivity.EXTRA_POST_KEY, postKey)
          startActivity(intent)
        }

        // Determine if the
        viewHolder.itemView.setOnLongClickListener { view ->
          //toggleSelection(mRecycler.getChildAdapterPosition(view));
          Log.i(TAG, "onLongClick: " + mRecycler!!.getChildAdapterPosition(view))
          false
        }
        /*if (model.stars.containsKey(getUid())) {
          viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
        } else {
          viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
        }*/

        // Bind Post to ViewHolder, setting OnClickListener for the star button
        viewHolder.bindToPost(model, View.OnClickListener {
          // Need to write to both places the post is stored
          //DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
          //DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());

          // Run two transactions
          //onStarClicked(globalPostRef);
          //onStarClicked(userPostRef);
        })
      }
    }
    mRecycler!!.adapter = mAdapter
  }

  // [START post_stars_transaction]
  private fun onStarClicked(postRef: DatabaseReference) {
    /*postRef.runTransaction(new Transaction.Handler() {
      @Override
      public Transaction.Result doTransaction(MutableData mutableData) {
        CalendarModel p = mutableData.getValue(CalendarModel.class);
        if (p == null) {
          return Transaction.success(mutableData);
        }

        if (p.stars.containsKey(getUid())) {
          // Unstar the post and remove self from stars
          p.starCount = p.starCount - 1;
          p.stars.remove(getUid());
        } else {
          // Star the post and add self to stars
          p.starCount = p.starCount + 1;
          p.stars.put(getUid(), true);
        }

        // Set value and report transaction success
        mutableData.setValue(p);
        return Transaction.success(mutableData);
      }

      @Override
      public void onComplete(DatabaseError databaseError, boolean b,
                             DataSnapshot dataSnapshot) {
        // Transaction completed
        Log.d(TAG, "postTransaction:onComplete:" + databaseError);
      }
    });*/
  }
  // [END post_stars_transaction]

  override fun onStart() {
    super.onStart()
    mAdapter!!.startListening()
  }

  override fun onStop() {
    super.onStop()
    mAdapter!!.stopListening()
  }

  class ContactsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var nameView: AppCompatTextView
    var subnameView: AppCompatTextView
    var iconView: AppCompatImageView

    fun bindToPost(post: CalendarModel, starClickListener: View.OnClickListener) {
      nameView.text = post.Nombre
      subnameView.text = post.NoPoliza.toString()
      iconView.setOnClickListener(starClickListener)
    }

    init {
      nameView = itemView.findViewById(R.id.item_name)
      subnameView = itemView.findViewById(R.id.item_subname)
      iconView = itemView.findViewById(R.id.item_icon)
    }
  }

  fun getQuery(databaseReference: DatabaseReference?): Query {
    // All my posts
    val myData = databaseReference!!.child("contacts").child(uid)
    myData.keepSynced(true)
    return myData.orderByChild("Nombre")
  }

  companion object {
    private val TAG = "MyContacts"
  }
}