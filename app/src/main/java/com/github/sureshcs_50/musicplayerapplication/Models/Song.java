package com.github.sureshcs_50.musicplayerapplication.Models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adminaccount on 20/12/17.
 */

public class Song extends SugarRecord implements Serializable {

    // All these variables have to be in public for SugarRecord so, making them public
    @SerializedName("song")
    public String name;
    @SerializedName("url")
    public String url;
    @SerializedName("artists")
    public String artists;
    @SerializedName("cover_image")
    public String coverImage;
    public boolean isFav = false;
    public long lastPlayedTimestamp = 0; // to maintain history -> to pull latest song played (Last In First Out in list)..
    public int playCount = 0; // meta data.. show times this song is played by user..

    public Song() {
    }

    public static List<Song> getSongs() {
        List<Song> songs = Song.listAll(Song.class);
        if (songs == null) {
            return new ArrayList<>();
        }
        Log.d("songs count - ", String.valueOf(songs.size()));
        return songs;
    }

    public static void flushSongs() {
        deleteAll(Song.class);
    }

    public void toggleFav() {
        this.isFav = !this.isFav;
        save();
    }

    public void setLastPlayedTimestamp() {
        lastPlayedTimestamp = System.currentTimeMillis();
        save();
    }

    public void incrementPlayCount() {
        playCount++;
        save();
    }
}
