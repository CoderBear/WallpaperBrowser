package com.udemy.sbsapps.wallpaperbrowser.Fragments;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udemy.sbsapps.wallpaperbrowser.Adapters.GlideApp;
import com.udemy.sbsapps.wallpaperbrowser.Adapters.PhotoAdapter;
import com.udemy.sbsapps.wallpaperbrowser.Models.Collection;
import com.udemy.sbsapps.wallpaperbrowser.Models.Photo;
import com.udemy.sbsapps.wallpaperbrowser.R;
import com.udemy.sbsapps.wallpaperbrowser.WebServices.ApiInterface;
import com.udemy.sbsapps.wallpaperbrowser.WebServices.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionFragment extends Fragment {

    private final String TAG = CollectionFragment.class.getSimpleName();

    @BindView(R.id.fragment_collection_username)
    TextView username;
    @BindView(R.id.fragment_collection_description)
    TextView description;
    @BindView(R.id.fragment_collection_user_avatar)
    CircleImageView userAvatar;
    @BindView(R.id.fragment_collection_title)
    TextView title;

    @BindView(R.id.fragment_collection_progressBar)
    ProgressBar progressBar;
    @BindView(R.id.fragment_collection_recyclerview)
    RecyclerView recyclerView;

    private List<Photo> photos = new ArrayList<>();
    private PhotoAdapter photoAdapter;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        unbinder = ButterKnife.bind(this, view);

        // RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        photoAdapter = new PhotoAdapter(getActivity(), photos);
        recyclerView.setAdapter(photoAdapter);

        Bundle bundle = getArguments();
        int collectionId = bundle.getInt("collectionId");

        getInformationOfCollectionId(collectionId);
        getPhotosOfCollection(collectionId);

        return view;
    }

    private void getInformationOfCollectionId(final int collectionId) {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<Collection> call = apiInterface.getInformationOfCollection(collectionId);
        call.enqueue(new Callback<Collection>() {
            @Override
            public void onResponse(Call<Collection> call, Response<Collection> response) {
                if(response.isSuccessful()){
                    Collection collection = response.body();
                    title.setText(collection.getTitle());
                    description.setText(collection.getDescription());
                    username.setText(collection.getUser().getUsername());
                    GlideApp.with(getActivity())
                            .load(collection.getUser().getProfileImage().getSmall())
                            .into(userAvatar);
                } else {
                    Log.i(TAG, "fail " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Collection> call, Throwable t) {
                Log.i(TAG, "fail " + t.getMessage());
            }
        });
    }
    private void getPhotosOfCollection(int collectionId) {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<List<Photo>> call = apiInterface.getPhotosOfCollection(collectionId);
        call.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                if(response.isSuccessful()){
                    photos.addAll(response.body());
                    photoAdapter.notifyDataSetChanged();
                } else {
                    Log.i(TAG, "fail " + response.message());
                }
                showProgressBar(false);
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                Log.i(TAG, "fail " + t.getMessage());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
