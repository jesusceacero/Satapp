package com.groupfive.satapp.ui.inventariable.inventariabletickets;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.listeners.IHistoryListener;
import com.groupfive.satapp.models.tickets.TicketModel;
import com.groupfive.satapp.retrofit.service.SatAppService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyticketsInvRecyclerViewAdapter extends RecyclerView.Adapter<MyticketsInvRecyclerViewAdapter.ViewHolder> {
    private Context ctx;
    private List<TicketModel> mValues;
    private IHistoryListener mListener;
    private LocalDate changer;
    private SatAppService service;

    public MyticketsInvRecyclerViewAdapter(Context context, List<TicketModel> items, IHistoryListener listener) {
        this.ctx = context;
        this.mValues = items;
        this.mListener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tickets_inv, parent, false);

        service = SatAppServiceGenerator.createService(SatAppService.class);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        changer = LocalDate.parse(holder.mItem.getCreatedAt().split("T")[0], DateTimeFormat.forPattern("yyyy-mm-dd"));


        holder.tvName.setText(holder.mItem.getTitulo());
        holder.tvDate.setText(Constants.FORMATTER.print(changer));
        holder.tvStatus.setText(holder.mItem.getEstado());

        String string = holder.mItem.getFotos().get(0);
        String[] parts = string.split("/");
        String part1 = parts[3];
        String part2 = parts[4];

        Call<ResponseBody> call = service.getTicketImg(part1, part2);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                        Glide
                                .with(ctx)
                                .load(bmp)
                                .error(Glide.with(ctx).load(R.drawable.image_not_loaded_icon))
                                .thumbnail(Glide.with(ctx).load(R.drawable.loading_gif))
                                .into(holder.ivPhoto);
                    }
                } else {
                    Glide
                            .with(ctx)
                            .load(R.drawable.image_not_loaded_icon)
                            .error(Glide.with(ctx).load(R.drawable.image_not_loaded_icon))
                            .thumbnail(Glide.with(ctx).load(R.drawable.loading_gif))
                            .into(holder.ivPhoto);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ctx, ctx.getResources().getString(R.string.error_loading_ticket), Toast.LENGTH_SHORT).show();
            }
        });

        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResponseBody> call2 = service.deleteTicketById(holder.mItem.getId());
                call2.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Snackbar.make(holder.mView, ctx.getResources().getString(R.string.delete_succes), Snackbar.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Snackbar.make(holder.mView, ctx.getResources().getString(R.string.error_in_the_connection), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public void setData(List<TicketModel> result) {
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
        public final TextView tvName;
        public final TextView tvDate;
        public final TextView tvStatus;
        public final ImageView ivPhoto;
        public final ImageButton ibDelete;
        public TicketModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvName = view.findViewById(R.id.textViewName);
            tvDate = view.findViewById(R.id.textViewDate);
            tvStatus = view.findViewById(R.id.textViewStatus);
            ivPhoto = view.findViewById(R.id.imageViewPhoto);
            ibDelete = view.findViewById(R.id.imageButtonDelete);
        }
    }
}
