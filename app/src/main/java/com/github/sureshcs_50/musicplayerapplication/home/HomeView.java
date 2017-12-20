package com.github.sureshcs_50.musicplayerapplication.home;

import com.github.sureshcs_50.musicplayerapplication.Models.Song;

import java.util.List;

/**
 * Created by adminaccount on 20/12/17.
 */

public interface HomeView {

    void showProgress();

    void hideProgress();

    void showToast(String message);

    void populateViewInListView(List<Song> songs);

    void showMediaPlayer(Song song, int position);

    void hideMediaPlayer();

    // while media player is preparing when we press pause. OnErrorListener will be called.
    // to update the Ui we are doing this.
    void togglePlayPauseIconInMediaPlayer(int resourceId);

}
