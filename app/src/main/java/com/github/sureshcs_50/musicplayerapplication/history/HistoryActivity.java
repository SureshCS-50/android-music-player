package com.github.sureshcs_50.musicplayerapplication.history;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.github.sureshcs_50.musicplayerapplication.Models.Song;
import com.github.sureshcs_50.musicplayerapplication.R;

import java.util.List;

public class HistoryActivity extends AppCompatActivity implements HistoryView {

    private ListView mLvHistory;
    private HistoryListAdapter mHistoryListAdapter;
    private List<Song> mSongs;

    private HistoryPresenterImpl mHistoryPresenterImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mHistoryPresenterImpl = new HistoryPresenterImpl(this);
        mSongs = mHistoryPresenterImpl.getSortedList();

        mLvHistory = (ListView) findViewById(R.id.lvHistory);
        mHistoryListAdapter = new HistoryListAdapter(this);
        mHistoryListAdapter.loadSongs(mSongs);
        mLvHistory.setAdapter(mHistoryListAdapter);

    }
}
