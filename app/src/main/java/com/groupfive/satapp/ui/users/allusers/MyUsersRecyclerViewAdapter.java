package com.groupfive.satapp.ui.users.allusers;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.Constants;
import com.groupfive.satapp.commons.MyApp;
import com.groupfive.satapp.data.repositories.UserSatAppRepository;
import com.groupfive.satapp.data.viewModel.UserViewModel;
import com.groupfive.satapp.models.auth.AuthLoginUser;
import com.groupfive.satapp.ui.users.userprofile.ProfileAdminActivity;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class MyUsersRecyclerViewAdapter extends RecyclerView.Adapter<MyUsersRecyclerViewAdapter.ViewHolder> {

    private List<AuthLoginUser> mValues;
    private Context ctx;
    private UserViewModel userViewModel;
    private UserSatAppRepository userSatAppRepository;
    private Boolean validated;

    public MyUsersRecyclerViewAdapter(Context ctx, List<AuthLoginUser> items, UserViewModel userViewModel,Boolean validated) {
        this.ctx = ctx;
        this.mValues = items;
        this.userViewModel = userViewModel;
        this.userSatAppRepository = new UserSatAppRepository();
        this.validated = validated;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.name.setText(holder.mItem.name);
        holder.email.setText(holder.mItem.email);
        holder.rol.setText(holder.mItem.role);
        holder.check.setVisibility(View.GONE);
        holder.tecnico.setVisibility(View.GONE);

        if(holder.mItem.role.equals(Constants.ROLE_ADMIN)){
            holder.cancel.setVisibility(View.GONE);
        }

        if (holder.mItem.picture != null) {
            userViewModel.getPicture(holder.mItem.id).observeForever(new Observer<ResponseBody>() {
                @Override
                public void onChanged(ResponseBody responseBody) {
                    Bitmap bmp = BitmapFactory.decodeStream(responseBody.byteStream());
                    Glide.with(ctx)
                            .load(bmp)
                            .circleCrop()
                            .into(holder.foto);
                }
            });
        }else {
            Glide.with(ctx)
                    .load(R.drawable.ic_perfil)
                    .circleCrop()
                    .into(holder.foto);
        }

        if (!holder.mItem.validated){
            holder.check.setVisibility(View.VISIBLE);
        }else {
            if (holder.mItem.role.equals(Constants.ROLE_USER)){
                holder.tecnico.setVisibility(View.VISIBLE);
            }
        }

        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ctx);
                alertDialogBuilder.setTitle(R.string.validacion);
                alertDialogBuilder
                        .setMessage(R.string.mensageValidacion)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                userViewModel.putValidated(holder.mItem.id);
                                if (validated){
                                    mValues.remove(holder.mItem);
                                    notifyDataSetChanged();
                                }else {
                                    holder.check.setVisibility(View.GONE);
                                }

                            }
                        })
                        .setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ctx);
                alertDialogBuilder.setTitle(R.string.borrado);
                alertDialogBuilder
                        .setMessage(R.string.mensageBorrado)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                mValues.remove(holder.mItem);
                                notifyDataSetChanged();
                                userViewModel.deleteUser(holder.mItem.id);
                            }
                        })
                        .setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        holder.tecnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ctx);
                alertDialogBuilder.setTitle(R.string.promocion);
                alertDialogBuilder
                        .setMessage(R.string.mensagepromocion)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                holder.tecnico.setVisibility(View.GONE);
                                userViewModel.putTecnico(holder.mItem.id);
                                holder.mItem.setRole(Constants.ROLE_TECNICO);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        holder.foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyApp.getContext(), ProfileAdminActivity.class);
                i.putExtra("id",holder.mItem.getId());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApp.getContext().startActivity(i);
            }
        });
    }

    public void setData(List<AuthLoginUser> list){
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
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView foto;
        public final TextView name;
        public final TextView email;
        public final TextView rol;
        public final ImageButton check;
        public final ImageButton cancel;
        public final ImageButton tecnico;
        public AuthLoginUser mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = view.findViewById(R.id.textViewNameList);
            email = view.findViewById(R.id.textViewEmailList);
            rol = view.findViewById(R.id.textViewRolList);
            foto = view.findViewById(R.id.imageViewFotoList);
            check = view.findViewById(R.id.imageButtonValidated);
            cancel = view.findViewById(R.id.imageButtonCancel);
            tecnico = view.findViewById(R.id.imageButtonTecnico);
        }
    }
}
