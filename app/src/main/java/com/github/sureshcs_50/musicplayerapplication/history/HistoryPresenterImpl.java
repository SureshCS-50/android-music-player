package com.github.sureshcs_50.musicplayerapplication.history;

import com.github.sureshcs_50.musicplayerapplication.Models.Song;

import java.util.Collections;
import java.util.List;

/**
 * Created by adminaccount on 20/12/17.
 */

public class HistoryPresenterImpl implements HistoryPresenter {

    private HistoryView mHistoryView;
    private SongLastPlayedComparator mSongLastPlayedComparator;

    public HistoryPresenterImpl(HistoryView view) {
        mHistoryView = view;
        mSongLastPlayedComparator = new SongLastPlayedComparator();
    }

    @Override
    public List<Song> getSortedList() {
        List<Song> songs = Song.getSongs();
        Collections.sort(songs, mSongLastPlayedComparator);
        return songs;
    }
}
