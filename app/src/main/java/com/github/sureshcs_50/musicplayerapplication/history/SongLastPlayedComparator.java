package com.github.sureshcs_50.musicplayerapplication.history;

import com.github.sureshcs_50.musicplayerapplication.Models.Song;

import java.util.Comparator;

/**
 * Created by adminaccount on 20/12/17.
 * <p>
 * sort items lastPlayedTimestamp order.. (to maintain history)
 */
public class SongLastPlayedComparator implements Comparator<Song> {

    public int compare(Song obj1, Song obj2) {
        return (int) (obj2.lastPlayedTimestamp - obj1.lastPlayedTimestamp);
    }

}
