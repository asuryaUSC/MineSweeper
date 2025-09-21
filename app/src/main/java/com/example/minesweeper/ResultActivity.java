package com.example.minesweeper;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    private static final String EXTRA_DID_WIN = "com.example.minesweeper.DID_WIN";
    private static final String EXTRA_SECONDS = "com.example.minesweeper.SECONDS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView timeMessage   = findViewById(R.id.timeMessage);
        TextView resultMessage = findViewById(R.id.resultMessage);
        TextView remarkMessage = findViewById(R.id.remarkMessage);
        Button playAgain       = findViewById(R.id.playAgainButton);

        boolean didWin = getIntent().getBooleanExtra(EXTRA_DID_WIN, false);
        int seconds    = getIntent().getIntExtra(EXTRA_SECONDS, 0);

        timeMessage.setText("Used " + seconds + " seconds.");
        if (didWin) {
            resultMessage.setText("You won.");
            remarkMessage.setText("Good job!");
        } else {
            resultMessage.setText("You lost.");
            remarkMessage.setText("Nice try!");
        }

        playAgain.setOnClickListener(v -> finish()); // go back to GameActivity
    }
}