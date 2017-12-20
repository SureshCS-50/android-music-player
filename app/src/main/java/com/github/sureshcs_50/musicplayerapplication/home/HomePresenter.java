package com.github.sureshcs_50.musicplayerapplication.home;

import com.github.sureshcs_50.musicplayerapplication.Models.Song;
import com.github.sureshcs_50.musicplayerapplication.network.HttpCallback;

import java.util.List;

/**
 * Created by adminaccount on 20/12/17.
 */

public interface HomePresenter {

    void fetchSongs(boolean fetchSongsOnRefresh, HttpCallback callback);

    void populateView(List<Song> songs);

}
