package com.github.sureshcs_50.musicplayerapplication.portfolio;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.sureshcs_50.musicplayerapplication.Models.Portfolio;
import com.github.sureshcs_50.musicplayerapplication.R;

import java.util.List;

/**
 * Created by adminaccount on 20/12/17.
 */

public class PortfolioAdapter extends BaseAdapter {

    private List<Portfolio> mDetails;
    private LayoutInflater mInflater;
    private Spannable mSpannableText;

    public PortfolioAdapter(Context context) {
        this.mDetails = Portfolio.getDetails();
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDetails.size();
    }

    @Override
    public Portfolio getItem(int position) {
        return mDetails.get(position);
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
            view = mInflater.inflate(R.layout.layout_portfolio_items, null);
            viewHolder.txtKey = (TextView) view.findViewById(R.id.txtKey);
            viewHolder.txtValue = (TextView) view.findViewById(R.id.txtValue);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Portfolio detail = mDetails.get(position);
        viewHolder.txtKey.setText(detail.getKey());
        if (detail.getLink().isEmpty()) {
            viewHolder.txtValue.setText(detail.getValue());
        } else {
            viewHolder.txtValue.setMovementMethod(LinkMovementMethod.getInstance());
            Linkify.addLinks(viewHolder.txtValue, Linkify.WEB_URLS);
            viewHolder.txtValue.setText(Html.fromHtml(detail.getLink()));
        }

        return view;
    }

    private static class ViewHolder {
        TextView txtKey, txtValue;
    }

}