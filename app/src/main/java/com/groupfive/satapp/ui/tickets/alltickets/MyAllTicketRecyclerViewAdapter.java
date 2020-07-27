package com.groupfive.satapp.ui.tickets.alltickets;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.groupfive.satapp.R;
import com.groupfive.satapp.listeners.IAllTicketsListener;
import com.groupfive.satapp.models.tickets.TicketModel;
import com.groupfive.satapp.transformations.DateTransformation;

import java.util.ArrayList;
import java.util.List;


public class MyAllTicketRecyclerViewAdapter extends RecyclerView.Adapter<MyAllTicketRecyclerViewAdapter.ViewHolder> {

    private final Context ctx;
    private List<TicketModel> mValues;
    private final IAllTicketsListener mListener;
    DateTransformation dateTransformer = new DateTransformation();

    public MyAllTicketRecyclerViewAdapter(Context ctx, List<TicketModel> mValues, IAllTicketsListener mListener) {
        this.ctx = ctx;
        this.mValues = mValues;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_fragment_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(mValues != null) {
            holder.mItem = mValues.get(position);
            holder.txtTitle.setText(holder.mItem.getTitulo());
            holder.txtNameCreatedBy.setText(ctx.getResources().getString(R.string.name_created_by) + " " + holder.mItem.getCreadoPor().getName());
            holder.txtEmailCreatedBy.setText(ctx.getResources().getString(R.string.contact_created_by) + " " + holder.mItem.getCreadoPor().getEmail());
            String string = holder.mItem.getCreatedAt();
            String[] parts = string.split("T");
            String date = parts[0];
            String dateToShow = dateTransformer.dateTransformation(date);
            holder.txtDate.setText(dateToShow);
            holder.mItem.getFechaCreacion();

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onAllTicketsItemClick(holder.mItem);
                    }
                }
            });
        }
    }

    public void setData(List<TicketModel> list){
        if(this.mValues != null) {
            this.mValues.clear();
        } else {
            this.mValues =  new ArrayList<>();
        }
        this.mValues.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if(mValues != null){
            return mValues.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView txtTitle;
        public final TextView txtNameCreatedBy;
        public final TextView txtEmailCreatedBy;
        public final TextView txtDate;
        public TicketModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtTitle = view.findViewById(R.id.textViewTitleAllTickets);
            txtNameCreatedBy = view.findViewById(R.id.textViewCreatedByAllTickets);
            txtEmailCreatedBy = view.findViewById(R.id.textViewEmailAllTickets);
            txtDate = view.findViewById(R.id.textViewDateAllTickets);
        }
    }
}
