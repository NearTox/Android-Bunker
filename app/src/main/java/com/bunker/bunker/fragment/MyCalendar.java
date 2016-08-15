package com.bunker.bunker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bunker.bunker.MyDatabase;
import com.bunker.bunker.R;
import com.bunker.bunker.activity.AddNewActivity;
import com.bunker.bunker.model.CalendarModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.Calendar;

public class MyCalendar extends Fragment {
  private String[] mMesesStr;
  private DatabaseReference mDatabase;

  private FirebaseRecyclerAdapter<CalendarModel, CalendarHolder> mAdapter;
  private RecyclerView mRecycler;
  private LinearLayoutManager mManager;
  Calendar myCalendar = Calendar.getInstance();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    //super.onCreateView(inflater, container, savedInstanceState);
    View rootView = inflater.inflate(R.layout.fragment_all_data, container, false);

    // [START create_database_reference]
    mDatabase = MyDatabase.getInstance().getReference();
    // [END create_database_reference]

    mRecycler = (RecyclerView)rootView.findViewById(R.id.all_data_list);
    //mRecycler.setHasFixedSize(true);
    mMesesStr = getResources().getStringArray(R.array.month);
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
    mAdapter = new FirebaseRecyclerAdapter<CalendarModel, CalendarHolder>(CalendarModel.class, R.layout.item_calendar_day, CalendarHolder.class, postsQuery) {
      @Override
      protected void populateViewHolder(final CalendarHolder viewHolder, final CalendarModel model, final int position) {
        final DatabaseReference postRef = getRef(position);

        // Set click listener for the whole post view
        final String postKey = postRef.getKey();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            // Launch PostDetailActivity
            Intent intent = new Intent(getActivity(), AddNewActivity.class);
            intent.putExtra(AddNewActivity.EXTRA_POST_KEY, postKey);
            startActivity(intent);
          }
        });

        // Determine if the current user has liked this post and set UI accordingly
        /*if (model.stars.containsKey(getUid())) {
          viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
        } else {
          viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
        }*/

        // Bind Post to ViewHolder, setting OnClickListener for the star button
        bindToPost(viewHolder, model, new View.OnClickListener() {
          @Override
          public void onClick(View starView) {
          }
        });
      }
    };
    mRecycler.setAdapter(mAdapter);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if(mAdapter != null) {
      mAdapter.cleanup();
    }
  }

  public String getUid() {
    FirebaseUser aa = FirebaseAuth.getInstance().getCurrentUser();
    if(aa != null){
      return aa.getUid();
    }    else{
      return "";
    }
  }

  boolean comparePlan(CalendarModel post) {
    if((post.Plan > 0 && post.Plan < 5) || post.Plan == 6 || post.Plan == 12) {
      int mod = (post.Mes + 1) % post.Plan;
      return mod != ((myCalendar.get(Calendar.MONTH) + 1) % post.Plan);
    } else {
      return !(post.Mes == myCalendar.get(Calendar.MONTH) && post.Year == myCalendar.get(Calendar.YEAR));
    }
  }

  public void bindToPost(CalendarHolder pThis, CalendarModel post, View.OnClickListener starClickListener) {
    pThis.dayView.setText(String.valueOf(post.Dia));
    boolean isVisible = !comparePlan(post);
    if(isVisible) {
      pThis.monthView.setText(mMesesStr[myCalendar.get(Calendar.MONTH)]);
    } else if(post.Mes < mMesesStr.length) {
      pThis.monthView.setText(mMesesStr[post.Mes]);
    } else {
      pThis.monthView.setText(String.valueOf(post.Mes));
    }
    pThis.nameView.setText(post.Nombre);
    pThis.subnameView.setText(String.valueOf(post.NoPoliza));
    pThis.iconView.setOnClickListener(starClickListener);
    pThis.hidePost(comparePlan(post));
  }

  public static class CalendarHolder extends RecyclerView.ViewHolder {
    private static int myMargin = 0;
    View view;
    AppCompatTextView dayView;
    AppCompatTextView monthView;
    AppCompatTextView nameView;
    AppCompatTextView subnameView;
    AppCompatImageView iconView;

    public CalendarHolder(View itemView) {
      super(itemView);
      view = itemView;
      dayView = (AppCompatTextView)itemView.findViewById(R.id.item_day);
      monthView = (AppCompatTextView)itemView.findViewById(R.id.item_month);
      nameView = (AppCompatTextView)itemView.findViewById(R.id.item_name);
      subnameView = (AppCompatTextView)itemView.findViewById(R.id.item_subname);
      iconView = (AppCompatImageView)itemView.findViewById(R.id.item_icon);
      hidePost(true);
    }

    void hidePost(boolean hide) {
      RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)view.getLayoutParams();
      if(param.bottomMargin != 0 && myMargin == 0) {
        myMargin = param.bottomMargin;
      }if(hide) {
        param.height = 0;
        param.bottomMargin = 0;
        param.topMargin = 0;
        view.setAlpha(0);
      } else {
        param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        param.bottomMargin = myMargin;
        param.topMargin = myMargin;
        view.setAlpha(1);
      }
      view.setLayoutParams(param);
    }
  }
  public MyCalendar() {
  }

  public Query getQuery(DatabaseReference databaseReference) {
    // All my posts
    DatabaseReference myData = databaseReference.child("contacts").child(getUid());
    myData.keepSynced(true);
    return myData.orderByChild("Dia");
  }
}