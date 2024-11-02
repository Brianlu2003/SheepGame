package com.example.sheepgame;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private GridLayout gameBoard;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private List<String> historyList = new ArrayList<>();
    private int currentGridSize = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameBoard = findViewById(R.id.gameBoard);
        Button switchGridButton = findViewById(R.id.btnSwitchGrid);
        recyclerView = findViewById(R.id.recyclerView);

        // Setup RecyclerView
        historyAdapter = new HistoryAdapter(historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(historyAdapter);

        // Initialize the game board with default grid size
        generateTiles(currentGridSize, currentGridSize);

        // Switch Grid Size Button
        switchGridButton.setOnClickListener(v -> switchGridSize());
    }

    /**
     * Generates a grid of TextViews within the GridLayout
     */
    private void generateTiles(int rows, int columns) {
        gameBoard.removeAllViews();
        gameBoard.setRowCount(rows);
        gameBoard.setColumnCount(columns);

        for (int i = 0; i < rows * columns; i++) {
            TextView tile = new TextView(this);
            tile.setText(String.valueOf(i + 1));
            tile.setBackgroundColor(Color.LTGRAY);
            tile.setGravity(Gravity.CENTER);
            tile.setPadding(16, 16, 16, 16);

            // Set click listener for each tile
            final int tileNumber = i + 1;
            tile.setOnClickListener(v -> {
                tile.setBackgroundColor(Color.GREEN);
                String message = "Clicked tile: " + tileNumber;
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                addToHistory(message);
            });

            // Add tile to game board
            gameBoard.addView(tile);
        }
    }

    /**
     * Switches the grid size in a rotating order: 5x5 -> 4x4 -> 3x3 -> 2x2
     */
    private void switchGridSize() {
        if (currentGridSize == 5) {
            currentGridSize = 4;
        } else if (currentGridSize == 4) {
            currentGridSize = 3;
        } else if (currentGridSize == 3) {
            currentGridSize = 2;
        } else {
            currentGridSize = 5;
        }
        generateTiles(currentGridSize, currentGridSize);
    }

    /**
     * Adds an entry to the click history and updates the RecyclerView
     */
    private void addToHistory(String entry) {
        historyList.add(entry);
        historyAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(historyList.size() - 1);
    }
}



