package com.groupfive.satapp.ui.annotations.allticketannotation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.groupfive.satapp.R;
import com.groupfive.satapp.listeners.ITicketAnnotationListener;
import com.groupfive.satapp.listeners.OnUpdateAnnotationDialogListener;
import com.groupfive.satapp.models.tickets.TicketAnotaciones;
import com.groupfive.satapp.retrofit.service.SatAppService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;
import com.groupfive.satapp.ui.annotations.editannotation.EditAnnotationDialogFragment;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAllTicketAnnotationRecyclerViewAdapter extends RecyclerView.Adapter<MyAllTicketAnnotationRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private List<TicketAnotaciones> mValues;
    private final ITicketAnnotationListener mListener;
    SatAppService service;
    Activity activity;
    private final OnUpdateAnnotationDialogListener onUpdateAnnotationDialogListener;

    public MyAllTicketAnnotationRecyclerViewAdapter(Context context, List<TicketAnotaciones> mValues, ITicketAnnotationListener mListener, OnUpdateAnnotationDialogListener onUpdateAnnotationDialogListener) {
        this.context = context;
        this.mValues = mValues;
        this.mListener = mListener;
        this.onUpdateAnnotationDialogListener = onUpdateAnnotationDialogListener;
        service = SatAppServiceGenerator.createService(SatAppService.class);
        activity = (Activity)context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_all_ticket_annotation_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(mValues != null) {
            holder.mItem = mValues.get(position);
            holder.txtCuerpo.setText(holder.mItem.getCuerpo());
            Glide
                    .with(context)
                    .load(context.getResources().getDrawable(R.drawable.icon_annotation))
                    .into(holder.ivAnnotation);
            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditAnnotationDialogFragment dialog = new EditAnnotationDialogFragment(activity, holder.mItem.getId(), onUpdateAnnotationDialogListener);
                    dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "EditAnnotationDialogFragment");
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                    alert.setTitle(context.getResources().getString(R.string.delete_annotation_title));
                    alert.setMessage(context.getResources().getString(R.string.delete_annotation_message));
                    alert.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Call<ResponseBody> call = service.deleteAnnotationById(holder.mItem.getId());
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.code() == 204){
                                        Toast.makeText(context, context.getResources().getString(R.string.delete_annotation_succed), Toast.LENGTH_SHORT).show();
                                        mValues.remove(position);
                                        notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(context, context.getResources().getString(R.string.delete_annotation_error), Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialog.dismiss();
                        }
                    });

                    alert.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            });

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onFotoItemClick(holder.mItem);
                    }
                }
            });
        }
    }

    public void setData(List<TicketAnotaciones> list){
        this.mValues = list;
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
        public final TextView txtCuerpo;
        public final ImageView ivAnnotation;
        public final ImageButton btnEdit;
        public final ImageButton btnDelete;
        public TicketAnotaciones mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            txtCuerpo = view.findViewById(R.id.textViewAnnotationTicketDetail);
            ivAnnotation = view.findViewById(R.id.imageViewAnnotationTicketetail);
            btnEdit = view.findViewById(R.id.imageButtonEditAnnotationTicketDetail);
            btnDelete = view.findViewById(R.id.imageButtonDeleteAnnotationTicketDetail);
        }

    }
}
