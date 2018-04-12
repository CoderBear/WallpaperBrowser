package com.udemy.sbsapps.wallpaperbrowser.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.udemy.sbsapps.wallpaperbrowser.Adapters.CollectionAdapter;
import com.udemy.sbsapps.wallpaperbrowser.Models.Collection;
import com.udemy.sbsapps.wallpaperbrowser.R;
import com.udemy.sbsapps.wallpaperbrowser.Utils.Functions;
import com.udemy.sbsapps.wallpaperbrowser.WebServices.ApiInterface;
import com.udemy.sbsapps.wallpaperbrowser.WebServices.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionsFragment extends Fragment {

    private final String TAG = CollectionsFragment.class.getSimpleName();
    @BindView(R.id.fragments_collections_gridview)
    GridView gridView;
    @BindView(R.id.fragments_collections_progressBar)
    ProgressBar  progressBar;

    private CollectionAdapter collectionAdapter;
    private List<Collection> collections = new ArrayList<>();
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collections, container, false);
        unbinder = ButterKnife.bind(this,view);
        collectionAdapter = new CollectionAdapter(getActivity(), collections);
        gridView.setAdapter(collectionAdapter);
        showProgressBar(true);
        getCollections();

        return view;
    }

    @OnItemClick(R.id.fragments_collections_gridview)
    public void setGridView(int position) {
        Collection collection = collections.get(position);
        Log.i(TAG, collection.getId() + "");
        Bundle bundle = new Bundle();
        bundle.putInt("collectionId", collection.getId());
        CollectionFragment collectionFragment = new CollectionFragment();
        collectionFragment.setArguments(bundle);
        Functions.changeMainFragmentWithBack(getActivity(), collectionFragment);
    }

    private void getCollections() {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<List<Collection>> call = apiInterface.getCollections();
        call.enqueue(new Callback<List<Collection>>() {
            @Override
            public void onResponse(@NonNull Call<List<Collection>> call, @NonNull Response<List<Collection>> response) {
                if(response.isSuccessful()){
                    List<Collection> body = response.body();
                    if(body != null) {
                        collections.addAll(body);
                        collectionAdapter.notifyDataSetChanged();
                        Log.d(TAG, "size " + collections.size());
                    }
                }
                else {
                    Log.i(TAG,"response fail " + response.message());
                }
                showProgressBar(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Collection>> call, @NonNull Throwable t) {
                Log.i(TAG,"getCollections fail " + t.getMessage());
                showProgressBar(false);
            }
        });

    }

    private void showProgressBar(boolean isShow) {
        if (isShow) {
            progressBar.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        }
        else {
            progressBar.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
