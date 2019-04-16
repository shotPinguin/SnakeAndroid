package com.structit.snake.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.structit.snake.R;
import com.structit.snake.Score;

import java.util.List;

public class ScoreboardAdapter extends ArrayAdapter<Score> {

    private final Context context;
    private final List<Score> scores;

    public ScoreboardAdapter(Context context, int simple_list_item_1, List<Score> scores) {
        super(context, R.layout.row_scoreboard_view, scores);
        this.context = context;
        this.scores = scores;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_scoreboard_view, parent, false);

        TextView nameUser = (TextView) rowView.findViewById(R.id.name);
        TextView scoreUser = (TextView) rowView.findViewById(R.id.score);


        nameUser.setText(scores.get(position).getName());
        scoreUser.setText(scores.get(position).getScore());

        return rowView;
        //String s = values[position];
    }
}
