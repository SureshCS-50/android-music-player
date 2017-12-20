package com.github.sureshcs_50.musicplayerapplication.network;

import com.github.sureshcs_50.musicplayerapplication.Models.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by adminaccount on 20/12/17.
 */

public interface APIInterface {
    @GET("/studio")
    Call<List<Song>> getSongs();
}
