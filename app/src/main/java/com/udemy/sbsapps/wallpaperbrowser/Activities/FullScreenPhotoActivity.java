package com.udemy.sbsapps.wallpaperbrowser.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.udemy.sbsapps.wallpaperbrowser.Adapters.GlideApp;
import com.udemy.sbsapps.wallpaperbrowser.Models.Photo;
import com.udemy.sbsapps.wallpaperbrowser.R;
import com.udemy.sbsapps.wallpaperbrowser.Utils.Functions;
import com.udemy.sbsapps.wallpaperbrowser.Utils.RealmController;
import com.udemy.sbsapps.wallpaperbrowser.WebServices.ApiInterface;
import com.udemy.sbsapps.wallpaperbrowser.WebServices.ServiceGenerator;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullScreenPhotoActivity extends AppCompatActivity {
    @BindView(R.id.activity_fullscreen_photo_photo)
    ImageView fullscreenPhoto;
    @BindView(R.id.activity_fullscreen_photo_user_avatar)
    CircleImageView userAvatar;
    @BindView(R.id.activity_fullscreen_photo_fab_menu)
    FloatingActionMenu fabMenu;
    @BindView(R.id.activity_fullscreen_photo_fab_favorite)
    FloatingActionButton fabFavorite;
    @BindView(R.id.activity_fullscreen_photo_fab_wallpaper)
    FloatingActionButton fabWallpaper;
    @BindView(R.id.activity_fullscreen_photo_username)
    TextView username;

    @BindDrawable(R.drawable.ic_check_favorite)
    Drawable icFavorite;
    @BindDrawable(R.drawable.ic_check_favorited)
    Drawable icFavorited;

    private Bitmap photoBitmap;
    private Unbinder unbinder;
    private Photo photo;

    private RealmController realmController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        unbinder = ButterKnife.bind(this);
        Intent intent = getIntent();
        String photoId = intent.getStringExtra("photoId");
        getPhoto(photoId);

        realmController = new RealmController();
        if(realmController.isPhotoExist(photoId)) {
            fabFavorite.setImageDrawable(icFavorited);
        }
    }

    private void getPhoto(String id) {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<Photo> call = apiInterface.getPhoto(id);
        call.enqueue(new Callback<Photo>() {
            @Override
            public void onResponse(@NonNull Call<Photo> call, @NonNull Response<Photo> response) {
                if(response.isSuccessful()){
                    photo = response.body();
                    updateUI(photo);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Photo> call, @NonNull Throwable t) {
            }
        });
    }

    private void updateUI(Photo photo) {
        try {
            username.setText(photo.getUser().getUsername());
            GlideApp.with(FullScreenPhotoActivity.this)
                    .load(photo.getUser().getProfileImage().getSmall())
                    .into(userAvatar);

            GlideApp.with(FullScreenPhotoActivity.this)
                    .asBitmap()
                    .load(photo.getUrl().getRegular())
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            fullscreenPhoto.setImageBitmap(resource);
                            photoBitmap = resource;
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.activity_fullscreen_photo_fab_favorite)
    public void setFabFavorite() {
        if(realmController.isPhotoExist(photo.getId())){
            realmController.deletePhoto(photo);
            fabFavorite.setImageDrawable(icFavorite);
            Toast.makeText(FullScreenPhotoActivity.this, "Remove Favorite", Toast.LENGTH_SHORT).show();
        } else {
            realmController.savePhoto(photo);
            fabFavorite.setImageDrawable(icFavorited);
            Toast.makeText(FullScreenPhotoActivity.this, "Added Favorite", Toast.LENGTH_SHORT).show();
        }

        fabMenu.close(true);
    }

    @OnClick(R.id.activity_fullscreen_photo_fab_wallpaper)
    public void setFabWallpaper() {
        if(photoBitmap != null) {
            if(Functions.setWallpaper(FullScreenPhotoActivity.this, photoBitmap)) {
                Toast.makeText(FullScreenPhotoActivity.this, "Set Wallpaper successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FullScreenPhotoActivity.this, "Set Wallpaper failed", Toast.LENGTH_SHORT).show();
            }
        }
        fabMenu.close(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
