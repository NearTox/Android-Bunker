package com.lock.lock.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lock.lock.R;
import com.lock.lock.model.CalendarModel;

import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {
  public List<CalendarModel> allDays;
  private Context context;
  public CalendarAdapter(Context context) {
    this.allDays = new ArrayList<>();
    this.context = context;
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =  LayoutInflater.from(context)
      .inflate(R.layout.item_calendar_day,parent,false);
    return new MyViewHolder(view);
  }

  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {

  }

  @Override
  public int getItemCount() {
    return allDays.size();
  }

  protected class MyViewHolder extends RecyclerView.ViewHolder {

    public MyViewHolder(View itemView) {
      super(itemView);
    }
  }
}
