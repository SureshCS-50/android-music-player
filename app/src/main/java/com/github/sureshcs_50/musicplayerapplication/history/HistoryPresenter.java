package com.github.sureshcs_50.musicplayerapplication.history;

import com.github.sureshcs_50.musicplayerapplication.Models.Song;

import java.util.List;

/**
 * Created by adminaccount on 20/12/17.
 */

public interface HistoryPresenter {
    List<Song> getSortedList();
}
