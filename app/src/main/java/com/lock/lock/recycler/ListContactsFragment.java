package com.lock.lock.recycler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.lock.lock.R;
import com.lock.lock.model.CalendarModel;

public abstract class ListContactsFragment extends Fragment {
  private static final String TAG = "ListContactsFragment";

  private DatabaseReference mDatabase;

  private FirebaseRecyclerAdapter<CalendarModel, CalendarHolder> mAdapter;
  private RecyclerView mRecycler;
  private LinearLayoutManager mManager;

  public ListContactsFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    //super.onCreateView(inflater, container, savedInstanceState);
    View rootView = inflater.inflate(R.layout.fragment_all_data, container, false);

    // [START create_database_reference]
    mDatabase = FirebaseDatabase.getInstance().getReference();
    // [END create_database_reference]

    mRecycler = (RecyclerView)rootView.findViewById(R.id.all_data_list);
    //mRecycler.setHasFixedSize(true);

    return rootView;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // Set up Layout Manager, reverse layout
    mManager = new LinearLayoutManager(getActivity());
    //mManager.setReverseLayout(true);
    //mManager.setStackFromEnd(true);
    mRecycler.setLayoutManager(mManager);

    // Set up FirebaseRecyclerAdapter with the Query
    Query postsQuery = getQuery(mDatabase);
    mAdapter = new FirebaseRecyclerAdapter<CalendarModel, CalendarHolder>(CalendarModel.class, R.layout.item_contants,
      CalendarHolder.class, postsQuery) {
      @Override
      protected void populateViewHolder(final CalendarHolder viewHolder, final CalendarModel model, final int position) {
        final DatabaseReference postRef = getRef(position);

        // Set click listener for the whole post view
        final String postKey = postRef.getKey();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            // Launch PostDetailActivity
            //Intent intent = new Intent(getActivity(), PostDetailActivity.class);
            //intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
            //startActivity(intent);
          }
        });

        // Determine if the current user has liked this post and set UI accordingly
        /*if (model.stars.containsKey(getUid())) {
          viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
        } else {
          viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
        }*/

        // Bind Post to ViewHolder, setting OnClickListener for the star button
        viewHolder.bindToPost(model, new View.OnClickListener() {
          @Override
          public void onClick(View starView) {
            // Need to write to both places the post is stored
            //DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
            //DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());

            // Run two transactions
            //onStarClicked(globalPostRef);
            //onStarClicked(userPostRef);
          }
        });
      }
    };
    mRecycler.setAdapter(mAdapter);
  }

  // [START post_stars_transaction]
  private void onStarClicked(DatabaseReference postRef) {
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

  @Override
  public void onDestroy() {
    super.onDestroy();
    if(mAdapter != null) {
      mAdapter.cleanup();
    }
  }

  public String getUid() {
    return FirebaseAuth.getInstance().getCurrentUser().getUid();
  }

  public abstract Query getQuery(DatabaseReference databaseReference);


  public static class CalendarHolder extends RecyclerView.ViewHolder {

    public TextView nameView;
    public TextView subnameView;
    public ImageView iconView;

    public void bindToPost(CalendarModel post, View.OnClickListener starClickListener) {
      nameView.setText(post.Name);
      subnameView.setText(String.valueOf(post.NoPoliza));
      iconView.setOnClickListener(starClickListener);
    }

    public CalendarHolder(View itemView) {
      super(itemView);
      nameView = (TextView)itemView.findViewById(R.id.item_name);
      subnameView = (TextView)itemView.findViewById(R.id.item_subname);
      iconView = (ImageView)itemView.findViewById(R.id.item_icon);
    }
  }
}