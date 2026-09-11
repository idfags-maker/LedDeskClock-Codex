package com.example.leddeskclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

public final class MainActivity extends Activity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ClockView clock;
    private SharedPreferences preferences;
    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            clock.refresh();
            handler.postDelayed(this, 1000 - Math.floorMod(System.currentTimeMillis(), 1000));
        }
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        preferences = getSharedPreferences("clock", MODE_PRIVATE);
        clock = new ClockView(this, preferences);
        clock.setOnClickListener(v -> showSettings());
        setContentView(clock);
        applyBrightness();
        immerse();
    }

    private void immerse() {
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            controller.hide(WindowInsets.Type.systemBars());
        }
    }

    private void applyBrightness() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = preferences.getBoolean("dim", false) ? 0.08f : -1f;
        getWindow().setAttributes(params);
    }

    private void showSettings() {
        String[] labels = {"24시간 표시", "초 표시", "야간 모드 (낮은 밝기)"};
        String[] keys = {"24hour", "seconds", "dim"};
        boolean[] checked = {preferences.getBoolean(keys[0], true),
                preferences.getBoolean(keys[1], true), preferences.getBoolean(keys[2], false)};
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("시계 설정")
                .setMultiChoiceItems(labels, checked, (d, index, enabled) -> {
                    preferences.edit().putBoolean(keys[index], enabled).apply();
                    applyBrightness();
                    clock.refresh();
                })
                .setPositiveButton("닫기", null)
                .setNeutralButton("시계 종료", (d, which) -> finish())
                .create();
        dialog.setOnDismissListener(d -> immerse());
        dialog.show();
    }

    @Override protected void onResume() {
        super.onResume();
        handler.removeCallbacks(ticker);
        ticker.run();
        immerse();
    }

    @Override protected void onPause() {
        handler.removeCallbacks(ticker);
        super.onPause();
    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) immerse();
    }
}
