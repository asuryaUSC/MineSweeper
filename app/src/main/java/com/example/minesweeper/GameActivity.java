package com.example.minesweeper;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import androidx.gridlayout.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private TextView minesRemaining;
    private TextView timerText;
    private ImageView modeToggle;

    // game + timer
    private final GameEngine engine = new GameEngine();
    private final Stopwatch stopwatch = new Stopwatch();

    // grid handling
    private final ArrayList<TextView> cellViews = new ArrayList<>();
    private static final int ROWS = 10;
    private static final int COLS = 10;

    // state flags
    private boolean isFlagMode = false;     // start in dig mode
    private boolean awaitingEndTap = false;

    // intents to send to results
    private static final String EXTRA_DID_WIN = "com.example.minesweeper.DID_WIN";
    private static final String EXTRA_SECONDS = "com.example.minesweeper.SECONDS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // set up views
        gridLayout = findViewById(R.id.gridLayout);
        minesRemaining = findViewById(R.id.minesRemaining);
        timerText = findViewById(R.id.timerText);
        modeToggle = findViewById(R.id.modeToggle);

        // new game + stopwatch
        engine.initNewGame();
        updateMinesRemaining();
        setupStopwatch();
        stopwatch.reset();

        // build 10x10 grid
        buildGrid();

        // toggle shovel / flag
        modeToggle.setOnClickListener(v -> {
            if (awaitingEndTap) {
                return;
            }
            isFlagMode = !isFlagMode;
            modeToggle.setImageResource(isFlagMode ? R.drawable.flag : R.drawable.shovel);
        });
    }


    // helpers

    // build grid
    private void buildGrid() {
        gridLayout.removeAllViews();
        cellViews.clear();

        gridLayout.setColumnCount(COLS);
        gridLayout.setRowCount(ROWS);

        int cellMinSizeDp = 28;
        float density = getResources().getDisplayMetrics().density;
        int cellMinPx = Math.round(cellMinSizeDp * density);
        int marginPx = Math.round(2 * density);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                TextView tv = new TextView(this);
                tv.setGravity(Gravity.CENTER);
                tv.setMinWidth(cellMinPx);
                tv.setMinHeight(cellMinPx);
                tv.setTextSize(16);
                tv.setBackgroundResource(R.drawable.cell_covered);
                tv.setText("");

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.rowSpec = GridLayout.spec(r);
                lp.columnSpec = GridLayout.spec(c);
                lp.setMargins(marginPx, marginPx, marginPx, marginPx);
                tv.setLayoutParams(lp);

                int index = r * COLS + c;
                tv.setTag(index);
                tv.setOnClickListener(this::onCellClick);

                gridLayout.addView(tv);
                cellViews.add(tv);
            }
        }
    }

    // cell click
    private void onCellClick(View v) {
        if (!(v instanceof TextView)) return;

        if (awaitingEndTap) {
            // End-of-game: this tap navigates to results
            goToResults();
            return;
        }

        int index = (int) v.getTag();
        int r = index / COLS;
        int c = index % COLS;

        if (isFlagMode) {
            handleFlagTap(r, c);
        } else {
            handleDigTap(r, c);
        }
    }

    // handle flags
    private void handleFlagTap(int r, int c) {
        if (engine.isGameOver()) return;

        boolean changed = engine.toggleFlag(r, c);
        if (changed) {
            // update the cell and flag counter
            updateOneCellView(r, c);
            updateMinesRemaining();
        }
    }

    // handle digs
    private void handleDigTap(int r, int c) {
        if (engine.isGameOver()) return;

        List<int[]> changed = engine.dig(r, c);

        if (!changed.isEmpty()) {
            if (!stopwatchRunningOrStarted()) {
                stopwatch.start();
            }
            for (int[] pos : changed) {
                updateOneCellView(pos[0], pos[1]);
            }
        }

        if (engine.isGameOver()) {
            stopwatch.stop();
            awaitingEndTap = true;
        }
    }

    // stopwatch running or start
    private boolean stopwatchRunningOrStarted() {
        return stopwatch.getSeconds() > 0 || false;
    }

    // update mines remaining
    private void updateMinesRemaining() {
        int remaining = engine.getTotalMines() - engine.getFlagsPlaced();
        minesRemaining.setText(String.valueOf(remaining));
    }

    // update one cell
    private void updateOneCellView(int r, int c) {
        int index = r * COLS + c;
        TextView tv = cellViews.get(index);
        Cell cell = engine.getBoard()[r][c];

        if (!cell.isRevealed()) {
            // covered or flagged
            if (cell.isFlagged()) {
                tv.setBackgroundResource(R.drawable.cell_flagged);
                tv.setText("");
            } else {
                tv.setBackgroundResource(R.drawable.cell_covered);
                tv.setText("");
            }
            return;
        }

        // revealed
        if (cell.isMine()) {
            tv.setBackgroundResource(R.drawable.cell_mine);
        } else {
            tv.setBackgroundResource(R.drawable.cell_revealed);
            int adj = cell.getAdjacent();
            tv.setText(adj > 0 ? String.valueOf(adj) : "");
        }
    }

    // stopwatch
    private void setupStopwatch() {
        stopwatch.setOnTickListener(seconds ->
                timerText.setText(formatTime(seconds))
        );
        timerText.setText(formatTime(0));
    }

    private String formatTime(int totalSeconds) {
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    // navigation
    private void goToResults() {
        awaitingEndTap = false;

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(EXTRA_DID_WIN, engine.didWin());
        intent.putExtra(EXTRA_SECONDS, stopwatch.getSeconds());
        startActivity(intent);
    }

    // reset new game
    private void resetForNewGame() {
        engine.initNewGame();
        stopwatch.reset();
        updateMinesRemaining();

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                updateOneCellView(r, c);
            }
        }
        isFlagMode = false;
        modeToggle.setImageResource(R.drawable.shovel);
        awaitingEndTap = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopwatch.stop();
    }


}