package com.groupfive.satapp.ui.tickets.phototicketdetail;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.groupfive.satapp.R;
import com.groupfive.satapp.listeners.IPhotoTicketDetailListener;
import com.groupfive.satapp.retrofit.service.SatAppService;
import com.groupfive.satapp.retrofit.servicegenerator.SatAppServiceGenerator;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyPhotosTicketRecyclerViewAdapter extends RecyclerView.Adapter<MyPhotosTicketRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private List<String> mValues;
    private final IPhotoTicketDetailListener mListener;
    SatAppService service;

    public MyPhotosTicketRecyclerViewAdapter(Context context, List<String> mValues, IPhotoTicketDetailListener mListener) {
        this.context = context;
        this.mValues = mValues;
        this.mListener = mListener;
        service = SatAppServiceGenerator.createService(SatAppService.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_phoyos_ticket_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(mValues != null){
            holder.mItem = mValues.get(position);
            String string = holder.mItem;
            String[] parts = string.split("/");
            String part1 = parts[3];
            String part2 = parts[4];
            Call<ResponseBody> getFotos = service.getTicketImg(part1, part2);
            getFotos.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                            Glide
                                    .with(context)
                                    .load(bmp)
                                    .error(Glide.with(context).load(R.drawable.image_not_loaded_icon))
                                    .thumbnail(Glide.with(context).load(R.drawable.loading_gif))
                                    .into(holder.ivFoto);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //Toast.makeText(context, "Error loading one photo", Toast.LENGTH_SHORT).show();
                }
            });
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onFotoItemClick(holder.mItem);
                }
            }
        });
    }

    public void setData(List<String> list){
        this.mValues = list;
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
        public final ImageView ivFoto;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ivFoto = view.findViewById(R.id.imageViewFotoTicketDetailList);
        }

    }
}
