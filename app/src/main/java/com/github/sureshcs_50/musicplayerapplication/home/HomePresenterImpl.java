package com.github.sureshcs_50.musicplayerapplication.home;

import android.util.Log;

import com.github.sureshcs_50.musicplayerapplication.Models.Song;
import com.github.sureshcs_50.musicplayerapplication.network.APIClient;
import com.github.sureshcs_50.musicplayerapplication.network.HttpCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by adminaccount on 20/12/17.
 */

public class HomePresenterImpl implements HomePresenter {

    private HomeView mHomeView;

    public HomePresenterImpl(HomeView homeView) {
        this.mHomeView = homeView;
    }

    @Override
    public void fetchSongs(boolean fetchSongsOnRefresh, HttpCallback callback) {
        final List<Song> songs = Song.getSongs();
        if (fetchSongsOnRefresh || songs.size() == 0) {
            callGetSongsAPI(callback);
        } else {
            mHomeView.hideProgress();
            populateView(songs);
        }
    }

    private void callGetSongsAPI(final HttpCallback callback) {
        mHomeView.showProgress();
        Call<List<Song>> callSongs = APIClient.getAPIInterface().getSongs();
        callSongs.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful()) {
                    Song.flushSongs();
                    List<Song> songs = response.body();
                    for (Song song : songs) {
                        song.lastPlayedTimestamp = 0;
                        song.playCount = 0;
                        song.save();
                    }
                    populateView(songs);
                } else {
                    Log.d("Error - Fetch songs", String.valueOf(response.code()));
                    mHomeView.showToast("Error occurred!");
                }
                mHomeView.hideProgress();
                callback.onResponse();
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                mHomeView.hideProgress();
                mHomeView.showToast("Error occurred!");
                call.cancel();
            }
        });
    }

    @Override
    public void populateView(List<Song> songs) {
        mHomeView.populateViewInListView(songs);
    }

}