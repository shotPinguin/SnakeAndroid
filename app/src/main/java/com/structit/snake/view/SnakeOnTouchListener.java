package com.structit.snake.view;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.structit.snake.activity.MainActivity;

public class SnakeOnTouchListener implements View.OnTouchListener {
    private final String LOG_TAG = SnakeOnTouchListener.class.getName();

    private MainActivity mainActivity;

    public SnakeOnTouchListener(MainActivity activity) {
        this.mainActivity = activity;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        this.mainActivity.updateDirectionPlayer(motionEvent.getX(),
                motionEvent.getY());

        return false;
    }
}
