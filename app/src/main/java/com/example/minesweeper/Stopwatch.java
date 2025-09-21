package com.example.minesweeper;

import android.os.Handler;
import android.os.Looper;

public class
Stopwatch {
    private int clock = 0;
    private boolean running = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

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
            if (timerRunnable != null) {
                handler.removeCallbacks(timerRunnable);
            }
            runTimer();
        }
    }

    // pause stopwatch
    public void stop() {
        running = false;
        if (timerRunnable != null) {
            handler.removeCallbacks(timerRunnable);
        }
    }

    // reset to 0 / stop
    public void reset() {
        running = false;
        if (timerRunnable != null) {
            handler.removeCallbacks(timerRunnable);
        }
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
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (running) {
                    clock++;
                    if (listener != null) {
                        listener.onTick(clock);
                    }
                    // Only schedule next tick if still running
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(timerRunnable);
    }
}
