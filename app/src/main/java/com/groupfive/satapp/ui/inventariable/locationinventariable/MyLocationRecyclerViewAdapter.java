package com.groupfive.satapp.ui.inventariable.locationinventariable;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.SharedPreferencesManager;
import com.groupfive.satapp.listeners.ILocationListener;

import java.util.List;

public class MyLocationRecyclerViewAdapter extends RecyclerView.Adapter<MyLocationRecyclerViewAdapter.ViewHolder> {
    private final Context ctx;
    private List<String> mValues;
    private final ILocationListener mListener;

    public MyLocationRecyclerViewAdapter(Context context, List<String> list, ILocationListener listener) {
        this.ctx = context;
        this.mValues = list;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvLocation.setText(holder.mItem);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesManager.setStringValue("location", holder.mItem);
                Intent i = new Intent(ctx, InvLocationListActivity.class);
                ctx.startActivity(i);
            }
        });
    }

    public void setData(List<String> result) {
        this.mValues = result;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mValues != null) {
            return mValues.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvLocation;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvLocation = (TextView) view.findViewById(R.id.textViewLocation);

        }

    }
}
