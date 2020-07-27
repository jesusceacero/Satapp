package com.groupfive.satapp.ui.users.userprofile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.groupfive.satapp.R;
import com.groupfive.satapp.commons.MyApp;
import com.groupfive.satapp.data.viewModel.UserViewModel;
import com.groupfive.satapp.models.auth.AuthLoginUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import okhttp3.ResponseBody;

public class ProfileAdminActivity extends AppCompatActivity {

    private CircularImageView circularImageView;
    private TextView name,email;
    private UserViewModel userViewModel;
    private String id;
    private AuthLoginUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_admin);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        id = getIntent().getExtras().getString("id");

        circularImageView = findViewById(R.id.imageViewPhotoProfileAdmin);
        name = findViewById(R.id.textViewNamePfrofileAdmin);
        email = findViewById(R.id.textViewEmailProfileAdmin);

        userViewModel.getUserId(id).observe(this, new Observer<AuthLoginUser>() {
            @Override
            public void onChanged(AuthLoginUser authLoginUser) {
                user = authLoginUser;

                name.setText(user.getName());
                email.setText(user.getEmail());

                if (user.picture != null) {
                    userViewModel.getPicture(user.id).observeForever(new Observer<ResponseBody>() {
                        @Override
                        public void onChanged(ResponseBody responseBody) {
                            Bitmap bmp = BitmapFactory.decodeStream(responseBody.byteStream());
                            Glide.with(MyApp.getContext())
                                    .load(bmp)
                                    .thumbnail(Glide.with(ProfileAdminActivity.this).load(R.drawable.loading_gif))
                                    .centerCrop()
                                    .into(circularImageView);
                        }
                    });
                }else {
                    Glide.with(MyApp.getContext())
                            .load(R.drawable.ic_perfil)
                            .thumbnail(Glide.with(ProfileAdminActivity.this).load(R.drawable.loading_gif))
                            .centerCrop()
                            .into(circularImageView);
                }
            }
        });

    }
}
