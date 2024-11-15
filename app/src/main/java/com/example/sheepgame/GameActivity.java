package com.example.sheepgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import android.util.Log;

public class GameActivity extends AppCompatActivity {
    private FrameLayout gameBoardLayer1, gameBoardLayer2, gameBoardLayer3, gameBoardLayer4;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private final List<String> historyList = new ArrayList<>();
    private TextView timerTextView;
    private CountDownTimer countDownTimer;
    private static final long START_TIME_IN_MILLIS = 180000; // 3 minutes
    private long timeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean isGameActive = true;

    private static final int TOTAL_CARDS = 14; // 5 + 4 + 3 + 2 cards in total

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameBoardLayer1 = findViewById(R.id.gameBoardLayer1);
        gameBoardLayer2 = findViewById(R.id.gameBoardLayer2);
        gameBoardLayer3 = findViewById(R.id.gameBoardLayer3);
        gameBoardLayer4 = findViewById(R.id.gameBoardLayer4);
        recyclerView = findViewById(R.id.recyclerView);
        timerTextView = findViewById(R.id.timerTextView);

        // 设置 RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        historyAdapter = new HistoryAdapter(historyList);
        recyclerView.setAdapter(historyAdapter);

        // 初始化并生成卡片
        generateTiles(gameBoardLayer1, 5, Color.RED);
        generateTiles(gameBoardLayer2, 4, Color.YELLOW);
        generateTiles(gameBoardLayer3, 3, Color.BLUE);
        generateTiles(gameBoardLayer4, 2, Color.GREEN);

        // 开始倒计时
        startTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isGameActive = false;
                endGame();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.US, "%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }

    private void generateTiles(FrameLayout frameLayout, int numTiles, int color) {
        frameLayout.post(() -> {
            int frameWidth = frameLayout.getWidth();
            int frameHeight = frameLayout.getHeight();
            int tileSize = 100;
            int bottomMargin = 100;

            for (int i = 0; i < numTiles; i++) {
                TextView tile = new TextView(this);
                tile.setText(String.valueOf(i + 1));
                tile.setBackgroundColor(color);

                // 根据背景颜色设置文字颜色
                if (color == Color.YELLOW) {
                    tile.setTextColor(Color.BLACK); // 黄色背景使用黑色文字
                    tile.setShadowLayer(1.5f, 1, 1, Color.GRAY);
                } else {
                    tile.setTextColor(Color.WHITE); // 其他颜色背景使用白色文字
                }

                tile.setGravity(android.view.Gravity.CENTER);
                tile.setPadding(16, 16, 16, 16);
                tile.setTextSize(20); // 设置更大的文字尺寸

                // 设置随机位置，确保不会生成在底部预留区域
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(tileSize, tileSize);
                params.leftMargin = (int) (Math.random() * (frameWidth - tileSize));
                params.topMargin = (int) (Math.random() * (frameHeight - tileSize - bottomMargin));

                tile.setLayoutParams(params);
                tile.setOnClickListener(v -> handleTileClick(tile, frameLayout));
                frameLayout.addView(tile);
            }
        });
    }

    private void handleTileClick(TextView tile, FrameLayout frameLayout) {
        if (!isGameActive) return;

        // 添加点击动画
        animateTileClick(tile);

        frameLayout.removeView(tile);
        historyList.add(0, tile.getText().toString());
        historyAdapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0);

        // 检查当前层是否所有卡片都已清空
        if (frameLayout.getChildCount() == 0) {
            frameLayout.setVisibility(View.GONE);
            updateNextLayerColor();
        }
        Log.d("GameActivity", "Checking for matching cards");
        checkAndRemoveMatchingCards();
        checkGameEnd();
    }

    private void animateTileClick(View view) {
        Log.d("Animation", "Tile click animation started");
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.7f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.7f, 1.1f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(500); // 增加持续时间
        animatorSet.start();
    }

    private void updateNextLayerColor() {
        if (gameBoardLayer2.getVisibility() == View.VISIBLE) {
            gameBoardLayer2.setBackgroundColor(Color.YELLOW);
        } else if (gameBoardLayer3.getVisibility() == View.VISIBLE) {
            gameBoardLayer3.setBackgroundColor(Color.BLUE);
        } else if (gameBoardLayer4.getVisibility() == View.VISIBLE) {
            gameBoardLayer4.setBackgroundColor(Color.GREEN);
        }
    }

    private void checkAndRemoveMatchingCards() {
        Log.d("GameActivity", "Checking matching cards. History size: " + historyList.size());
        if (historyList.size() >= 3) {
            String lastCard = historyList.get(0);
            Log.d("GameActivity", "Last three cards: " + lastCard + ", " + historyList.get(1) + ", " + historyList.get(2));
            if (historyList.get(1).equals(lastCard) && historyList.get(2).equals(lastCard)) {
                Log.d("GameActivity", "Found matching cards: " + lastCard);

                // Run on main thread to ensure UI updates are handled properly
                runOnUiThread(() -> {
                    // 添加消除动画
                    animateCardRemoval();
                    // 添加特效
                    addSpecialEffect();

                    historyList.subList(0, 3).clear();
                    historyAdapter.notifyItemRangeRemoved(0, 3);
                    Toast.makeText(this, "Matching cards removed!", Toast.LENGTH_SHORT).show();
                });
            } else {
                Log.d("GameActivity", "No matching cards found");
            }
        }
    }

    private void animateCardRemoval() {
        Log.d("Animation", "Card removal animation started");
        for (int i = 0; i < 3; i++) {
            View view = Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(i);
            if (view != null) {
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
                fadeOut.setDuration(1000); // 增加持续时间
                fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.start();
            }
        }
    }

    private void addSpecialEffect() {
        Log.d("GameActivity", "Starting special effect animation");

        // Get the root view
        View decorView = getWindow().getDecorView();
        FrameLayout rootView = decorView.findViewById(android.R.id.content);

        if (rootView == null) {
            Log.e("GameActivity", "Root view not found!");
            return;
        }

        // Create effect view with explicit size
        View effectView = new View(this);
        effectView.setBackgroundColor(Color.argb(100, 255, 255, 0)); // Semi-transparent yellow
        effectView.setAlpha(0f);

        // Use match parent with specific layout params
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        // Add view to root
        rootView.addView(effectView, params);
        Log.d("GameActivity", "Effect view added to root");

        // Create animation set
        AnimatorSet animatorSet = new AnimatorSet();

        // Create fade in animation
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(effectView, View.ALPHA, 0f, 0.7f);
        fadeIn.setDuration(300);

        // Create fade out animation
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(effectView, View.ALPHA, 0.7f, 0f);
        fadeOut.setDuration(300);

        // Configure animation set
        animatorSet.playSequentially(fadeIn, fadeOut);
        animatorSet.setInterpolator(new DecelerateInterpolator());

        // Add listeners for debugging
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d("GameActivity", "Special effect animation started");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("GameActivity", "Special effect animation ended");
                rootView.post(() -> {
                    rootView.removeView(effectView);
                    Log.d("GameActivity", "Effect view removed");
                });
            }
        });

        // Start animation
        animatorSet.start();
    }



    private void checkGameEnd() {
        if (gameBoardLayer1.getVisibility() == View.GONE &&
                gameBoardLayer2.getVisibility() == View.GONE &&
                gameBoardLayer3.getVisibility() == View.GONE &&
                gameBoardLayer4.getVisibility() == View.GONE) {
            endGame();
        }
    }

    private void endGame() {
        // 停止计时器
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isGameActive = false;

        // 统计剩余卡片数
        int remainingCards = 0;
        remainingCards += gameBoardLayer1.getChildCount();
        remainingCards += gameBoardLayer2.getChildCount();
        remainingCards += gameBoardLayer3.getChildCount();
        remainingCards += gameBoardLayer4.getChildCount();

        // 计算得分 (移除的卡片数 * 10)
        int score = (TOTAL_CARDS - remainingCards) * 10;

        // 禁用所有游戏层的交互
        gameBoardLayer1.setEnabled(false);
        gameBoardLayer2.setEnabled(false);
        gameBoardLayer3.setEnabled(false);
        gameBoardLayer4.setEnabled(false);

        // 使用 Locale.US 避免本地化问题
        String message = String.format(Locale.US,
                "时间到！\n" +
                        "最终得分: %d\n" +
                        "移除卡片: %d/%d\n" +
                        "剩余时间: %d秒",
                score,
                (TOTAL_CARDS - remainingCards),
                TOTAL_CARDS,
                timeLeftInMillis / 1000
        );

        // 显示游戏结束对话框
        new AlertDialog.Builder(this)
                .setTitle("游戏结束")
                .setMessage(message)
                .setPositiveButton("重新开始", (dialog, which) -> restartGame())
                .setNegativeButton("退出", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void restartGame() {
        // 重置游戏状态
        isGameActive = true;
        timeLeftInMillis = START_TIME_IN_MILLIS;

        // 清空历史记录并使用更高效的通知方法
        int oldSize = historyList.size();
        historyList.clear();
        if (oldSize > 0) {
            historyAdapter.notifyItemRangeRemoved(0, oldSize);
        }

        // 重新显示所有层
        gameBoardLayer1.setVisibility(View.VISIBLE);
        gameBoardLayer2.setVisibility(View.VISIBLE);
        gameBoardLayer3.setVisibility(View.VISIBLE);
        gameBoardLayer4.setVisibility(View.VISIBLE);

        // 重新启用所有层的交互
        gameBoardLayer1.setEnabled(true);
        gameBoardLayer2.setEnabled(true);
        gameBoardLayer3.setEnabled(true);
        gameBoardLayer4.setEnabled(true);

        // 清除现有卡片
        gameBoardLayer1.removeAllViews();
        gameBoardLayer2.removeAllViews();
        gameBoardLayer3.removeAllViews();
        gameBoardLayer4.removeAllViews();

        // 重新生成卡片
        generateTiles(gameBoardLayer1, 5, Color.RED);
        generateTiles(gameBoardLayer2, 4, Color.YELLOW);
        generateTiles(gameBoardLayer3, 3, Color.BLUE);
        generateTiles(gameBoardLayer4, 2, Color.GREEN);

        // 重新开始计时器
        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}










