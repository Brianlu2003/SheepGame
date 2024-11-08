package com.example.sheepgame;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class GameActivity extends AppCompatActivity {
    private GridLayout gameBoardLayer1, gameBoardLayer2, gameBoardLayer3, gameBoardLayer4;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private List<String> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameBoardLayer1 = findViewById(R.id.gameBoardLayer1);
        gameBoardLayer2 = findViewById(R.id.gameBoardLayer2);
        gameBoardLayer3 = findViewById(R.id.gameBoardLayer3);
        gameBoardLayer4 = findViewById(R.id.gameBoardLayer4);
        recyclerView = findViewById(R.id.recyclerView);


        historyAdapter = new HistoryAdapter(historyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(historyAdapter);


        generateTiles(gameBoardLayer1, 5, 5);
        generateTiles(gameBoardLayer2, 4, 4);
        generateTiles(gameBoardLayer3, 3, 3);
        generateTiles(gameBoardLayer4, 2, 2);
    }

    private void generateTiles(GridLayout gridLayout, int rows, int columns) {
        gridLayout.setRowCount(rows);
        gridLayout.setColumnCount(columns);
        for (int i = 0; i < rows * columns; i++) {
            TextView tile = new TextView(this);
            tile.setText(String.valueOf(i + 1));
            tile.setBackgroundColor(Color.LTGRAY);
            tile.setPadding(16, 16, 16, 16);
            tile.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tile.setOnClickListener(v -> handleTileClick(tile, gridLayout));
            gridLayout.addView(tile);
        }
    }

    private void handleTileClick(TextView tile, GridLayout gridLayout) {
        Log.d("GameActivity", "Tile clicked: " + tile.getText());
        gridLayout.removeView(tile);
        historyList.add(tile.getText().toString());
        historyAdapter.notifyDataSetChanged();
        checkAndRemoveMatchingCards();
    }

    private void checkAndRemoveMatchingCards() {
        if (historyList.size() >= 3) {
            historyList.clear();
            historyAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Matching cards removed!", Toast.LENGTH_SHORT).show();
        }
    }
}








