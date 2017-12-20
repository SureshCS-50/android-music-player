package com.github.sureshcs_50.musicplayerapplication.portfolio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.github.sureshcs_50.musicplayerapplication.Models.Portfolio;
import com.github.sureshcs_50.musicplayerapplication.R;

import java.util.List;

public class PortfolioActivity extends AppCompatActivity {

    private ListView mLvDetails;
    private PortfolioAdapter mPortfolioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        mLvDetails = (ListView) findViewById(R.id.lvDetails);
        mPortfolioAdapter = new PortfolioAdapter(this);
        mLvDetails.setAdapter(mPortfolioAdapter);

    }
}
