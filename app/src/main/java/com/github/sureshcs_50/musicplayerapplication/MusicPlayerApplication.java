package com.github.sureshcs_50.musicplayerapplication;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.orm.SugarApp;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by adminaccount on 20/12/17.
 */

public class MusicPlayerApplication extends SugarApp {

    private static Picasso picassoInstance;

    public static Picasso getPicassoInstance(Context context) {
        if (picassoInstance == null) {
            File httpCacheDirectory = new File(context.getCacheDir(), "music-player-cache");
            Cache cache = new Cache(httpCacheDirectory, 15 * 1024 * 1024);
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().cache(cache);

            picassoInstance = new Picasso.Builder(context)
                    .downloader(new OkHttp3Downloader(okHttpClientBuilder.build()))
                    .build();
        }
        return picassoInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getPicassoInstance(this);
    }
}
