package com.example.leddeskclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Vector seven-segment digits: no network, fonts, or bitmap assets required. */
public final class ClockView extends View {
    private static final int[] MASKS = {0x3f, 0x06, 0x5b, 0x4f, 0x66, 0x6d, 0x7d, 0x07, 0x7f, 0x6f};
    private static final float[][] SEGMENTS = {
            {10, 3, 46, 9}, {47, 10, 53, 46}, {47, 54, 53, 90},
            {10, 91, 46, 97}, {3, 54, 9, 90}, {3, 10, 9, 46}, {10, 47, 46, 53}
    };
    private final SharedPreferences preferences;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd  EEEE", Locale.KOREAN);
    private ZonedDateTime now = ZonedDateTime.now();

    public ClockView(Context context, SharedPreferences preferences) {
        super(context);
        this.preferences = preferences;
        setBackgroundColor(Color.BLACK);
        setFocusable(true);
        setClickable(true);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        refresh();
    }

    public void refresh() {
        now = ZonedDateTime.now(); // Re-read device time zone and clock every tick.
        String pattern = preferences.getBoolean("24hour", true) ? "HH:mm" : "a hh:mm";
        if (preferences.getBoolean("seconds", true)) pattern += ":ss";
        setContentDescription(now.format(DateTimeFormatter.ofPattern(pattern, Locale.KOREAN))
                + ", " + now.format(dateFormat) + ". 탭하여 시계 설정 열기");
        invalidate();
    }

    @Override public boolean performClick() { return super.performClick(); }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean seconds = preferences.getBoolean("seconds", true);
        boolean twentyFour = preferences.getBoolean("24hour", true);
        boolean dim = preferences.getBoolean("dim", false);
        int hour = now.getHour();
        if (!twentyFour) hour = hour % 12 == 0 ? 12 : hour % 12;
        String digits = String.format(Locale.ROOT, seconds ? "%02d:%02d:%02d" : "%02d:%02d",
                hour, now.getMinute(), now.getSecond());
        float designWidth = seconds ? 408 : 264;
        float scale = Math.min(getWidth() * 0.9f / designWidth, getHeight() * 0.52f / 100f);
        float left = (getWidth() - designWidth * scale) / 2f;
        float top = (getHeight() - 100 * scale) / 2f;
        int led = Color.rgb(dim ? 155 : 255, dim ? 35 : 70, dim ? 20 : 42);
        canvas.save();
        canvas.translate(left, top);
        canvas.scale(scale, scale);
        float x = 0;
        for (int i = 0; i < digits.length(); i++) {
            char digit = digits.charAt(i);
            if (digit == ':') {
                paint.setColor(led);
                canvas.drawCircle(x + 8, 33, 3.5f, paint);
                canvas.drawCircle(x + 8, 67, 3.5f, paint);
                x += 24;
            } else {
                int mask = MASKS[digit - '0'];
                for (int s = 0; s < SEGMENTS.length; s++) {
                    paint.setColor((mask & (1 << s)) != 0 ? led : Color.rgb(24, 8, 5));
                    float[] r = SEGMENTS[s];
                    canvas.drawRoundRect(x + r[0], r[1], x + r[2], r[3], 1.8f, 1.8f, paint);
                }
                x += 60;
            }
        }
        canvas.restore();
        float density = getResources().getDisplayMetrics().density;
        float textSize = Math.min(18 * density, getHeight() * 0.045f);
        paint.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSize);
        paint.setColor(dim ? 0xff806962 : 0xffbfaaa2);
        String date = now.format(dateFormat);
        if (!twentyFour) date = (now.getHour() < 12 ? "오전   " : "오후   ") + date;
        if (paint.measureText(date) > getWidth() * 0.9f)
            paint.setTextSize(textSize * getWidth() * 0.9f / paint.measureText(date));
        canvas.drawText(date, getWidth() / 2f, top + 100 * scale + textSize * 2.1f, paint);
        paint.setTextSize(Math.min(12 * density, getHeight() * 0.033f));
        paint.setColor(dim ? 0xff554944 : 0xff80736d);
        canvas.drawText("화면을 탭하면 설정이 열립니다", getWidth() / 2f,
                getHeight() - Math.max(16 * density, getHeight() * 0.055f), paint);
    }
}
