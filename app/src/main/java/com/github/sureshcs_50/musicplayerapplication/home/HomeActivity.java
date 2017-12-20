package com.github.sureshcs_50.musicplayerapplication.home;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sureshcs_50.musicplayerapplication.Models.Song;
import com.github.sureshcs_50.musicplayerapplication.MusicPlayerApplication;
import com.github.sureshcs_50.musicplayerapplication.R;
import com.github.sureshcs_50.musicplayerapplication.State;
import com.github.sureshcs_50.musicplayerapplication.history.HistoryActivity;
import com.github.sureshcs_50.musicplayerapplication.network.HttpCallback;
import com.github.sureshcs_50.musicplayerapplication.network.NetworkManager;
import com.github.sureshcs_50.musicplayerapplication.portfolio.PortfolioActivity;
import com.github.sureshcs_50.musicplayerapplication.service.MusicPlayerService;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by adminaccount on 20/12/17.
 */

public class HomeActivity extends AppCompatActivity implements HomeView, TextWatcher, AdapterView.OnItemClickListener {

    // Home view..
    private HomePresenterImpl mHomePresenterImpl;
    private EditText mEtSearch;
    private SwipeRefreshLayout mSwipeDownRefresh;
    private ListView mSongListView;
    private SongListAdapter mSongsListAdapter;
    private Toast mToast;
    private Picasso mPicasso;
    private Button mBtnHistory;
    private int mCurrentSongIndex = 0;
    private Song mCurrentSong = null;

    // Media player view..
    private ConstraintLayout mLytMediaPlayer;
    private ImageView mSongImage;
    private TextView mSongTitle;
    private TextView mSongArtist;
    private ImageView mIbPlayPauseButton;

    // Music service..
    private MusicPlayerService mMusicService;
    private Intent mServiceIntent;
    private boolean isBound = false;

    // service connection, to bound activity and service..
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicPlayerService.MusicBinder binder = (MusicPlayerService.MusicBinder) iBinder;
            mMusicService = binder.getService();
            mMusicService.loadSongs();
            mMusicService.setHomeView(HomeActivity.this);
            Song mCurrentSong = mMusicService.getCurrentSong();
            if (mCurrentSong != null) {
                showMediaPlayer(mCurrentSong, mCurrentSongIndex);
                int resId = (mMusicService.mState == State.PREPARING || mMusicService.mState == State.PLAYING)
                        ? android.R.drawable.ic_media_pause
                        : android.R.drawable.ic_media_play;
                togglePlayPauseIconInMediaPlayer(resId);
            }
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mEtSearch = (EditText) findViewById(R.id.etSearch);
        mBtnHistory = (Button) findViewById(R.id.btnHistory);
        mSwipeDownRefresh = (SwipeRefreshLayout) findViewById(R.id.lytSwipeDown);
        mSongListView = (ListView) findViewById(R.id.lvSongs);
        mLytMediaPlayer = (ConstraintLayout) findViewById(R.id.lytMediaPlayer);
        mIbPlayPauseButton = (ImageView) findViewById(R.id.imgPlayBtn);
        mSongImage = (ImageView) findViewById(R.id.imgCover);
        mSongTitle = (TextView) findViewById(R.id.txtSongTitle);
        mSongArtist = (TextView) findViewById(R.id.txtArtists);
        mSongsListAdapter = new SongListAdapter(this);
        mSongListView.setAdapter(mSongsListAdapter);

        mHomePresenterImpl = new HomePresenterImpl(this);
        fetchSongs(false);

        mEtSearch.addTextChangedListener(this);

        mSongListView.setOnItemClickListener(this);

        mPicasso = MusicPlayerApplication.getPicassoInstance(this);

        // swipe down refresh will delete all records in db and pulls songs from endpoint
        mSwipeDownRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchSongs(true);
            }
        });

        mIbPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePlayPauseButton();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.portfolio:
                Intent iPortfolio = new Intent(getBaseContext(), PortfolioActivity.class);
                startActivity(iPortfolio);
                break;
        }
        return true;
    }

    public void showHistory(View v) {
        Intent iShowHistory = new Intent(this, HistoryActivity.class);
        startActivity(iShowHistory);
    }

    private void togglePlayPauseButton() {
        int resourceId;
        if (mMusicService.getPlayerState() == State.PLAYING) {
            resourceId = android.R.drawable.ic_media_play;
            mMusicService.pausePlayer();
        } else {
            resourceId = android.R.drawable.ic_media_pause;
            mMusicService.playPlayer();
        }
        mIbPlayPauseButton.setImageResource(resourceId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            // binds activity and service..
            if (mServiceIntent == null) {
                mServiceIntent = new Intent(this, MusicPlayerService.class);
                bindService(mServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                startService(mServiceIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fetchSongs(boolean fetchSongsOnRefresh) {
        if (NetworkManager.hasConnection(this)) {
            mHomePresenterImpl.fetchSongs(fetchSongsOnRefresh, new HttpCallback() {
                @Override
                public void onResponse() {
                    mMusicService.loadSongs();
                }
            });
        } else {
            hideProgress();
            showToast("Connection Failed");
        }
    }

    @Override
    public void showProgress() {
        mSwipeDownRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        mSwipeDownRefresh.setRefreshing(false);
    }

    @Override
    public void showToast(String message) {
        if (mToast != null) {
            mToast = null;
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public void populateViewInListView(List<Song> songs) {
        mSongsListAdapter.loadSongs(songs);
    }

    @Override
    public void showMediaPlayer(Song song, int position) {
        // start service
        mLytMediaPlayer.setVisibility(View.VISIBLE);
        mPicasso.load(song.coverImage)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into(mSongImage);
        mSongTitle.setText(song.name);
        mSongArtist.setText(song.artists);
    }

    @Override
    public void hideMediaPlayer() {
        mLytMediaPlayer.setVisibility(View.GONE);
    }

    @Override
    public void togglePlayPauseIconInMediaPlayer(int res) {
        mIbPlayPauseButton.setImageResource(res);
    }

    @Override
    protected void onDestroy() {
        if (isBound) {
            unbindService(serviceConnection);
        }
        super.onDestroy();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mSongsListAdapter.getFilter().filter(charSequence);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mMusicService.playSong(position);
    }
}
