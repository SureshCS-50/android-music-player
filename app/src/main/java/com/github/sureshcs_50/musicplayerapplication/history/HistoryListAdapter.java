package com.github.sureshcs_50.musicplayerapplication.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.sureshcs_50.musicplayerapplication.Models.Song;
import com.github.sureshcs_50.musicplayerapplication.MusicPlayerApplication;
import com.github.sureshcs_50.musicplayerapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adminaccount on 20/12/17.
 */

public class HistoryListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Song> mSongs;
    private LayoutInflater mInflater;
    private Picasso mPicasso;

    public HistoryListAdapter(Context context) {
        this.mContext = context;
        this.mSongs = new ArrayList<>();
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mPicasso = MusicPlayerApplication.getPicassoInstance(context);
    }

    @Override
    public int getCount() {
        return mSongs.size();
    }

    @Override
    public Song getItem(int position) {
        return mSongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.layout_history_item, null);
            viewHolder.txtSongTitle = (TextView) view.findViewById(R.id.txtSongTitle);
            viewHolder.txtArtist = (TextView) view.findViewById(R.id.txtArtists);
            viewHolder.imgCover = (ImageView) view.findViewById(R.id.imgCover);
            viewHolder.txtPlayCount = (TextView) view.findViewById(R.id.txtPlayCount);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Song song = mSongs.get(position);
        viewHolder.txtSongTitle.setText(song.name);
        viewHolder.txtArtist.setText(song.artists);
        viewHolder.txtPlayCount.setText("Play Count : " + song.playCount);

        mPicasso.load(song.coverImage)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into(viewHolder.imgCover);

        return view;
    }

    public void loadSongs(List<Song> songs) {
        if (songs != null) {
            this.mSongs = songs;
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder {
        TextView txtSongTitle, txtArtist, txtPlayCount;
        ImageView imgCover;
    }

}