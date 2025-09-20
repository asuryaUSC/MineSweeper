package com.example.minesweeper;

import android.os.Handler;

public class Stopwatch {
    private int clock = 0;
    private boolean running = false;
    private Handler handler = new Handler();

    // listener to update UI
    public interface OnTickListener {
        void onTick(int seconds);
    }
    private OnTickListener listener;
    public void setOnTickListener(OnTickListener l) {
        this.listener = l;
    }

    // start stopwatch
    public void start() {
        if (!running) {
            running = true;
            runTimer();
        }
    }

    // pause stopwatch
    public void stop() {
        running = false;
    }

    // reset to 0 / stop
    public void reset() {
        running = false;
        clock = 0;
        if (listener != null) {
            listener.onTick(clock);
        }
    }

    // get seconds
    public int getSeconds() {
        return clock;
    }

    // tick once per second
    private void runTimer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    clock++;
                    if (listener != null) {
                        listener.onTick(clock);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}
