package com.github.sureshcs_50.musicplayerapplication.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.github.sureshcs_50.musicplayerapplication.Constants;
import com.github.sureshcs_50.musicplayerapplication.Models.Song;
import com.github.sureshcs_50.musicplayerapplication.R;
import com.github.sureshcs_50.musicplayerapplication.State;
import com.github.sureshcs_50.musicplayerapplication.home.HomeActivity;
import com.github.sureshcs_50.musicplayerapplication.home.HomeView;

import java.util.List;

/**
 * Created by adminaccount on 20/12/17.
 * <p>
 * <p>
 * Ref:
 * <p>
 * Media Player state lifecycle : https://developer.android.com/reference/android/media/MediaPlayer.html#StateDiagram
 * <p>
 * state 1 - reset
 * state 8 - prepared
 * state 4 - pause
 */


public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private final IBinder mMusicBinder = new MusicBinder();
    public State mState = State.STOPPED;
    private HomeView mHomeView;
    private MediaPlayer mMediaPlayer;
    private List<Song> mSongs;
    private int mSongIndex;
    private Song mCurrentSong;

    @Override
    public void onCreate() {
        super.onCreate();
        mSongIndex = 0;
        initMediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            // notification actions..
            if (intent.getAction().equals(Constants.PREV_ACTION)) {
                Log.d("onStartCommand", "Clicked Previous");
                playPrevious();
            } else if (intent.getAction().equals(Constants.PLAY_ACTION)) {
                Log.d("onStartCommand", "Clicked Play");
                playPlayer();
            } else if (intent.getAction().equals(Constants.PAUSE_ACTION)) {
                Log.d("onStartCommand", "Clicked Pause");
                pausePlayer();
            } else if (intent.getAction().equals(Constants.NEXT_ACTION)) {
                Log.d("onStartCommand", "Clicked Next");
                playNext();
            } else if (intent.getAction().equals(Constants.STOP_FOREGROUND_ACTION)) {
                Log.d("onStartCommand", "Received Stop Foreground Intent");
                stopForeground(true);
                stopSelf();
            }
        }
        return START_STICKY;
    }

    private void initMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("onUnbind", "onUnbind");
        return false;
    }

    public void loadSongs() {
        mSongs = Song.getSongs();
    }

    public void setHomeView(HomeView view) {
        mHomeView = view;
    }

    public void playSong(int position) {
        try {
            // if user clicks on an item in list when media player is in pause state it will throw error
            // to avoid this we need to stop() the media player and then we are reset() _ing and preparing the media player
            if (mState == State.PAUSED) {
                mMediaPlayer.stop();
            }
            mCurrentSong = mSongs.get(position);
            mHomeView.showMediaPlayer(mCurrentSong, position);
            mSongIndex = position;
            // we should reset before preparing media player else it will throw error on state 1
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mCurrentSong.url);
            mCurrentSong.setLastPlayedTimestamp();
            mCurrentSong.incrementPlayCount();
            mMediaPlayer.prepareAsync();
            mState = State.PREPARING;
            showForegroundNotification();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        playNext();
    }

    public MediaPlayer getPlayer() {
        return mMediaPlayer;
    }

    public void playNext() {
        mSongIndex++;
        if (mSongIndex >= mSongs.size())
            mSongIndex = 0;
        playSong(mSongIndex);
    }

    public void playPrevious() {
        mSongIndex--;
        if (mSongIndex < 0)
            mSongIndex = mSongs.size() - 1;
        playSong(mSongIndex);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        mState = State.STOPPED;
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        // we can pause media player only on start() start or when it is prepared.
        // else it will throw error. pause called on state 8
        if (mState == State.PAUSED || mState == State.PAUSE_ON_PREPARED) {
            mediaPlayer.start();
            mediaPlayer.pause();
            mState = State.PAUSED;
            // this updates UI..
            mHomeView.togglePlayPauseIconInMediaPlayer(android.R.drawable.ic_media_play);
        } else {
            mediaPlayer.start();
            mState = State.PLAYING;
            mHomeView.togglePlayPauseIconInMediaPlayer(android.R.drawable.ic_media_pause);
        }
    }

    private void showForegroundNotification() {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.layout_notification);
        RemoteViews bigViews = new RemoteViews(getPackageName(), R.layout.layout_notification_extended);

        // showing default album image
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        bigViews.setImageViewResource(R.id.status_bar_album_art, R.mipmap.ic_launcher);

        Intent notificationIntent = new Intent(this, HomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent previousIntent = new Intent(this, MusicPlayerService.class);
        previousIntent.setAction(Constants.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, MusicPlayerService.class);
        String playPauseAction = (mState == State.PLAYING || mState == State.PREPARING) ? Constants.PAUSE_ACTION : Constants.PLAY_ACTION;
        playIntent.setAction(playPauseAction);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, MusicPlayerService.class);
        nextIntent.setAction(Constants.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, MusicPlayerService.class);
        closeIntent.setAction(Constants.STOP_FOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        int playResourceId = (mState == State.PLAYING || mState == State.PREPARING) ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play;

        views.setImageViewResource(R.id.status_bar_play, playResourceId);
        bigViews.setImageViewResource(R.id.status_bar_play, playResourceId);

        views.setTextViewText(R.id.status_bar_track_name, mCurrentSong.name);
        bigViews.setTextViewText(R.id.status_bar_track_name, mCurrentSong.name);

        views.setTextViewText(R.id.status_bar_artist_name, mCurrentSong.artists);
        bigViews.setTextViewText(R.id.status_bar_artist_name, mCurrentSong.artists);

        Notification notification = new Notification.Builder(this).build();
        notification.contentView = views;
        notification.bigContentView = bigViews;
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.icon = R.mipmap.ic_launcher;
        notification.contentIntent = pendingIntent;
        startForeground(1001, notification);

    }

    public State getPlayerState() {
        return mState;
    }

    public void setPlayerState(State state) {
        mState = state;
    }

    @Override
    public void onDestroy() {
        Log.d("onDestroy", "onDestroy");
        mMediaPlayer.reset();
        mHomeView.hideMediaPlayer();
        super.onDestroy();
    }

    public void pausePlayer() {
        // if we pause while preparing it will show pause on state 8 error
        // to avoid this we are doing this check
        // we can pause media player only on start() state
        if (mState == State.PLAYING) {
            mMediaPlayer.pause();
        }
        setPlayerState(State.PAUSED);
        mHomeView.togglePlayPauseIconInMediaPlayer(android.R.drawable.ic_media_play);
        showForegroundNotification();
    }

    public void playPlayer() {
        // while preparing when user clicks pause() it will throw start called on state 4
        // to handle that case we are doing this..
        if (mState == State.PREPARING || mState == State.PAUSE_ON_PREPARED) {
            // while preparing we are setting this to paused state cos, when it is prepared. In onPrepareListener it will be paused.
            setPlayerState(State.PAUSE_ON_PREPARED);
        } else {
            mMediaPlayer.start();
            setPlayerState(State.PLAYING);
        }
        mHomeView.togglePlayPauseIconInMediaPlayer(android.R.drawable.ic_media_pause);
        showForegroundNotification();
    }

    public Song getCurrentSong() {
        return mCurrentSong;
    }

    public class MusicBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

}