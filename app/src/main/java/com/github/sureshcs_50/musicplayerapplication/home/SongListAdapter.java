package com.github.sureshcs_50.musicplayerapplication.home;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
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

public class SongListAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private List<Song> mSongs;
    private List<Song> mFilteredSongs;
    private LayoutInflater mInflater;
    private Picasso mPicasso;

    public SongListAdapter(Context context) {
        this.mContext = context;
        this.mSongs = new ArrayList<>();
        this.mFilteredSongs = new ArrayList<>();
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mPicasso = MusicPlayerApplication.getPicassoInstance(context);
    }

    public static Drawable getTintedDrawable(@NonNull Drawable inputDrawable, @ColorInt int color) {
        Drawable wrapDrawable = DrawableCompat.wrap(inputDrawable);
        DrawableCompat.setTint(wrapDrawable, color);
        DrawableCompat.setTintMode(wrapDrawable, PorterDuff.Mode.SRC_IN);
        return wrapDrawable;
    }

    @Override
    public int getCount() {
        return mFilteredSongs.size();
    }

    @Override
    public Song getItem(int position) {
        return mFilteredSongs.get(position);
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
            view = mInflater.inflate(R.layout.layout_list_item, null);
            viewHolder.txtSongTitle = (TextView) view.findViewById(R.id.txtSongTitle);
            viewHolder.txtArtist = (TextView) view.findViewById(R.id.txtArtists);
            viewHolder.imgCover = (ImageView) view.findViewById(R.id.imgCover);
            viewHolder.imgBtnFav = (ImageView) view.findViewById(R.id.imgFav);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Song song = mFilteredSongs.get(position);
        viewHolder.txtSongTitle.setText(song.name);
        viewHolder.txtArtist.setText(song.artists);

        mPicasso.load(song.coverImage)
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into(viewHolder.imgCover);

        viewHolder.imgBtnFav.setOnClickListener(new OnFavBtnClickListener(song));

        if (song.isFav) {
            viewHolder.imgBtnFav.setColorFilter(ContextCompat.getColor(mContext, R.color.yellow), PorterDuff.Mode.MULTIPLY);
        } else {
            viewHolder.imgBtnFav.setColorFilter(ContextCompat.getColor(mContext, R.color.colorLightGrayBG), PorterDuff.Mode.MULTIPLY);
        }

        return view;
    }

    public void loadSongs(List<Song> songs) {
        if (songs != null) {
            this.mFilteredSongs = songs;
            this.mSongs = songs;
            notifyDataSetChanged();
        }
    }

    @Override
    public Filter getFilter() {
        try {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    if ((constraint != null) && (constraint.length() != 0)) {
                        List<Song> searchResult = new ArrayList<>();
                        searchResult.clear();
                        for (Song s : mSongs) {
                            if (s.name.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                                searchResult.add(s);
                            }
                        }
                        results.values = searchResult;
                        results.count = searchResult.size();
                    } else {
                        results.values = mSongs;
                        results.count = mSongs.size();
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    mFilteredSongs = (ArrayList<Song>) results.values;
                    if (mFilteredSongs == null) {
                        mFilteredSongs = new ArrayList<>();
                    }
                    notifyDataSetChanged();
                }
            };
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static class ViewHolder {
        TextView txtSongTitle, txtArtist;
        ImageView imgCover, imgBtnFav;
    }

    private class OnFavBtnClickListener implements View.OnClickListener {

        private Song mSong;

        public OnFavBtnClickListener(Song song) {
            this.mSong = song;
        }

        @Override
        public void onClick(View view) {
            mSong.toggleFav();
            notifyDataSetChanged();
        }

    }
}
