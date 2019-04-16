package com.structit.snake.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.structit.snake.R;
import com.structit.snake.Score;
import com.structit.snake.adapter.ScoreboardAdapter;
import com.structit.snake.service.ApiData;

public class ScoreboardActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView mListView;
    ArrayAdapter<Score> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoreboard_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("Scoreboard");
        }

        mListView = (ListView) findViewById(R.id.scoreboardView);

        mListView.setOnItemClickListener(this);

        mListView.setAdapter(null);
        adapter = new ScoreboardAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, ApiData.getInstance().getScoresList());
        mListView.setAdapter(adapter);

    }

    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}