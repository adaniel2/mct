package com.example.releasesapp;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.releasesapp.Models.ReleaseModel;
import com.example.releasesapp.Models.ReleaseModelResponse;
import com.example.releasesapp.Retrofit.ApiClient;
import com.example.releasesapp.Retrofit.ApiInterface;
import com.example.releasesapp.Session.SessionManagement;
import com.example.releasesapp.databinding.ActivityCatalogueBinding;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogueActivity extends AppCompatActivity {

    ApiInterface apiInterface;

    private ActivityCatalogueBinding binding;
    private RecyclerView listCatalogue;
    private SearchView searchBarCatalogue;

    private SessionManagement sessionManagement;
    private String userId;

    private ArrayList<Track> loadedTracks;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCatalogueBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sessionManagement = new SessionManagement(CatalogueActivity.this);
        userId = sessionManagement.getSessionUid();

        listCatalogue = binding.listCatalogue;
        listCatalogue.setLayoutManager(new LinearLayoutManager(this));
        listCatalogue.setItemAnimator(new DefaultItemAnimator());

        searchBarCatalogue = binding.searchBarCatalogue;
        TextView srcText = searchBarCatalogue.findViewById(androidx.appcompat.R.id.search_src_text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            srcText.setTextCursorDrawable(R.drawable.cursor_primary);
        }

        // init apiInterface before calling load
        apiInterface = ApiClient.getApiClient(CatalogueActivity.this).create(ApiInterface.class);

        // load latest 10 releases
        load(view, true, new OnTracksLoadedListener() {
            @Override
            public void onTracksLoaded(ArrayList<Track> tracks) {
                loadedTracks = tracks;

                TrackAdapter adapter = new TrackAdapter(CatalogueActivity.this, loadedTracks);
                listCatalogue.setAdapter(adapter);
            }

        });

        compositeDisposable.add(Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        searchBarCatalogue.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                emitter.onNext(query);
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                emitter.onNext(newText);

                                ArrayList<Track> filteredTracks = new ArrayList<>();

                                if (loadedTracks != null) {
                                    for (Track track : loadedTracks) {
                                        if (track.getTrackName()
                                                 .toLowerCase()
                                                 .contains(newText)) {
                                            filteredTracks.add(track);
                                        }

                                    }

                                }

                                TrackAdapter adapter = new TrackAdapter(CatalogueActivity.this, filteredTracks);
                                listCatalogue.setAdapter(adapter);

                                if (newText.isEmpty()) {
                                    adapter = new TrackAdapter(CatalogueActivity.this, filteredTracks);
                                    listCatalogue.setAdapter(adapter);
                                }

                                return false;
                            }

                        });

                    }

                }).map(s -> s.toLowerCase().trim())
                .debounce(400, TimeUnit.MILLISECONDS)
                .distinct()
                .filter(s -> !s.isBlank())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        load(view, false, new OnTracksLoadedListener() {
                            @Override
                            public void onTracksLoaded(ArrayList<Track> tracks) {
                                loadedTracks = tracks;

                                loadedTracks.forEach(t -> Log.d("debug", t.getTrackName()));
                                Log.d("debug", "-------------------------");

                                ArrayList<Track> filteredTracks = new ArrayList<>();

                                for (Track track : loadedTracks) {
                                    if (track.getTrackName()
                                             .toLowerCase()
                                             .contains(s)) {
                                        filteredTracks.add(track);
                                    }

                                }

                                TrackAdapter adapter = new TrackAdapter(CatalogueActivity.this, filteredTracks);
                                listCatalogue.setAdapter(adapter);

                            }

                        });

                    }

                }));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //compositeDisposable.clear();
    }

    public void load(View v, boolean initState, OnTracksLoadedListener listener) {
        ArrayList<Track> trackArray = new ArrayList<>();

        Call<ReleaseModelResponse> releaseModelResponseCall = apiInterface.getReleases(initState);

        releaseModelResponseCall.enqueue(new Callback<ReleaseModelResponse>() {
            @Override
            public void onResponse(Call<ReleaseModelResponse> call, Response<ReleaseModelResponse> response) {
                if (response.body() != null) {
                    ReleaseModelResponse releaseModelResponse = response.body();
                    ArrayList<ReleaseModel> releaseModels = releaseModelResponse.getReleases();

                    if (releaseModelResponse.getStatus() == 200) {
                        for (ReleaseModel releaseModel : releaseModels) {
                            float progress = releaseModel.getReleaseProgress();
                            String isrc = releaseModel.getIsrc() == null ? "null" : releaseModel.getIsrc();
                            String trackName = releaseModel.getTrackName() == null ? "null" : releaseModel.getTrackName();

                            trackArray.add(new Track(progress, R.drawable.track_progress_circle, isrc, trackName));
                        }

                    }

                }

                listener.onTracksLoaded(trackArray);
            }

            @Override
            public void onFailure(Call<ReleaseModelResponse> call, Throwable t) {
                Toast.makeText(CatalogueActivity.this, "Error, no response.",
                        Toast.LENGTH_LONG).show();
            }

        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();

            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

}