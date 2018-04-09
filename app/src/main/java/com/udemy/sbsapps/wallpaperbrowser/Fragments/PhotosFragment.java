package com.udemy.sbsapps.wallpaperbrowser.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.udemy.sbsapps.wallpaperbrowser.Adapters.PhotoAdapter;
import com.udemy.sbsapps.wallpaperbrowser.Models.Photo;
import com.udemy.sbsapps.wallpaperbrowser.R;
import com.udemy.sbsapps.wallpaperbrowser.WebServices.ApiInterface;
import com.udemy.sbsapps.wallpaperbrowser.WebServices.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotosFragment extends Fragment {

    private final String TAG = PhotosFragment.class.getSimpleName();
    @BindView(R.id.fragment_photos_progressbar)
    ProgressBar progressBar;
    @BindView(R.id.fragment_photos_recyclerview)
    RecyclerView recyclerView;

    private PhotoAdapter photoAdapter;
    private List<Photo> photos = new ArrayList<>();

    private Unbinder unbinder;

    public PhotosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        unbinder = ButterKnife.bind(this, view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        photoAdapter = new PhotoAdapter(getActivity(), photos);
        recyclerView.setAdapter(photoAdapter);

        showProgressBar(true);
        getPhotos();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getPhotos() {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<List<Photo>> call = apiInterface.getPhotos();
        call.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Photo>> call, @NonNull Response<List<Photo>> response) {
                if(response.isSuccessful()) {
                    List<Photo> body = response.body();
                    if(body != null) {
                        photos.addAll(body);
                        photoAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.i(TAG, "fail response: " + response.message());
                }
                showProgressBar(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Photo>> call, @NonNull Throwable t) {
                Log.i(TAG,"onFailure() " + t.getMessage());
                showProgressBar(false);
            }
        });
    }

    private void showProgressBar(boolean isShow) {
        if (isShow) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
