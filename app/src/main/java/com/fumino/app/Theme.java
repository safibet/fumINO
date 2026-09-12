package com.fumino.app;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Palette, tipografia e piccoli mattoni per costruire l'interfaccia via codice. */
public final class Theme {

    public static final int BG = 0xFF070C16;
    public static final int BG_SOFT = 0xFF0C1526;
    public static final int CARD = 0xFF121C31;
    public static final int CARD_HI = 0xFF1A2740;
    public static final int LINE = 0x1AFFFFFF;
    public static final int TEXT = 0xFFF1F5F9;
    public static final int MUTED = 0xFF9FB0C7;
    public static final int DIM = 0xFF6B7C93;

    public static final int GREEN = 0xFF34D399;
    public static final int GREEN_DEEP = 0xFF059669;
    public static final int BLUE = 0xFF38BDF8;
    public static final int GOLD = 0xFFFBBF24;
    public static final int PINK = 0xFFF472B6;
    public static final int RED = 0xFFF87171;
    public static final int PURPLE = 0xFFA78BFA;

    public static final int[] CATEGORY_COLORS = {GREEN, BLUE, GOLD, PURPLE};

    private Theme() {
    }

    // ------------------------------------------------------------- misure

    public static int dp(Context c, float v) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v,
                c.getResources().getDisplayMetrics()));
    }

    public static Typeface bold() {
        return Typeface.create("sans-serif-medium", Typeface.BOLD);
    }

    public static Typeface medium() {
        return Typeface.create("sans-serif-medium", Typeface.NORMAL);
    }

    public static Typeface regular() {
        return Typeface.create("sans-serif", Typeface.NORMAL);
    }

    // ------------------------------------------------------------- testi

    public static TextView text(Context c, CharSequence s, float sizeSp, int color, Typeface tf) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        t.setTextColor(color);
        t.setTypeface(tf);
        t.setLineSpacing(dp(c, 2), 1.05f);
        return t;
    }

    public static TextView h1(Context c, CharSequence s) {
        return text(c, s, 26, TEXT, bold());
    }

    public static TextView h2(Context c, CharSequence s) {
        return text(c, s, 19, TEXT, bold());
    }

    public static TextView h3(Context c, CharSequence s) {
        return text(c, s, 15, TEXT, medium());
    }

    public static TextView body(Context c, CharSequence s) {
        return text(c, s, 14, MUTED, regular());
    }

    public static TextView caption(Context c, CharSequence s) {
        return text(c, s, 12, DIM, regular());
    }

    public static TextView label(Context c, CharSequence s) {
        TextView t = text(c, s, 11, DIM, medium());
        t.setLetterSpacing(0.14f);
        t.setAllCaps(true);
        return t;
    }

    // --------------------------------------------------------- contenitori

    public static LinearLayout col(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    public static LinearLayout row(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    public static View space(Context c, float h) {
        View v = new View(c);
        v.setLayoutParams(new LinearLayout.LayoutParams(1, dp(c, h)));
        return v;
    }

    public static View flexSpace(Context c) {
        View v = new View(c);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, 1, 1f);
        v.setLayoutParams(p);
        return v;
    }

    public static View divider(Context c) {
        View v = new View(c);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, Math.max(1, dp(c, 0.7f)));
        p.topMargin = dp(c, 12);
        p.bottomMargin = dp(c, 12);
        v.setLayoutParams(p);
        v.setBackgroundColor(LINE);
        return v;
    }

    // ------------------------------------------------------------- sfondi

    public static GradientDrawable roundRect(Context c, float radiusDp, int fill) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(dp(c, radiusDp));
        d.setColor(fill);
        return d;
    }

    public static GradientDrawable roundRect(Context c, float radiusDp, int fill, int stroke, float strokeDp) {
        GradientDrawable d = roundRect(c, radiusDp, fill);
        d.setStroke(Math.max(1, dp(c, strokeDp)), stroke);
        return d;
    }

    public static GradientDrawable gradientRect(Context c, float radiusDp, int from, int to) {
        GradientDrawable d = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR, new int[]{from, to});
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(dp(c, radiusDp));
        return d;
    }

    public static GradientDrawable circle(int fill) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(fill);
        return d;
    }

    public static Drawable ripple(Drawable content, int rippleColor) {
        return new RippleDrawable(ColorStateList.valueOf(rippleColor), content, null);
    }

    /** Card standard, con eventuale bordo tenue. */
    public static LinearLayout card(Context c) {
        LinearLayout l = col(c);
        l.setBackground(roundRect(c, 20, CARD, LINE, 1));
        int p = dp(c, 16);
        l.setPadding(p, p, p, p);
        return l;
    }

    public static LinearLayout cardTinted(Context c, int accent) {
        LinearLayout l = col(c);
        l.setBackground(roundRect(c, 20, blend(accent, CARD, 0.10f), withAlpha(accent, 0x44), 1));
        int p = dp(c, 16);
        l.setPadding(p, p, p, p);
        return l;
    }

    // ------------------------------------------------------------ pulsanti

    public static TextView button(Context c, CharSequence s, int bg, int fg) {
        TextView t = text(c, s, 15, fg, bold());
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(c, 20), dp(c, 15), dp(c, 20), dp(c, 15));
        t.setBackground(ripple(roundRect(c, 16, bg), 0x33FFFFFF));
        t.setClickable(true);
        t.setFocusable(true);
        return t;
    }

    public static TextView buttonGradient(Context c, CharSequence s, int from, int to) {
        TextView t = text(c, s, 16, 0xFF06281F, bold());
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(c, 20), dp(c, 16), dp(c, 20), dp(c, 16));
        t.setBackground(ripple(gradientRect(c, 18, from, to), 0x44FFFFFF));
        t.setClickable(true);
        t.setFocusable(true);
        return t;
    }

    public static TextView buttonOutline(Context c, CharSequence s, int color) {
        TextView t = text(c, s, 14, color, medium());
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(c, 18), dp(c, 13), dp(c, 18), dp(c, 13));
        t.setBackground(ripple(roundRect(c, 14, 0x00000000, withAlpha(color, 0x66), 1), 0x22FFFFFF));
        t.setClickable(true);
        t.setFocusable(true);
        return t;
    }

    /** Piccola pillola colorata (badge). */
    public static TextView pill(Context c, CharSequence s, int color) {
        TextView t = text(c, s, 11, color, medium());
        t.setPadding(dp(c, 10), dp(c, 5), dp(c, 10), dp(c, 5));
        t.setBackground(roundRect(c, 20, withAlpha(color, 0x22)));
        return t;
    }

    // -------------------------------------------------------------- colori

    public static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    public static int blend(int a, int b, float ratio) {
        float r = Math.max(0f, Math.min(1f, ratio));
        int ar = Color.red(a), ag = Color.green(a), ab = Color.blue(a);
        int br = Color.red(b), bg = Color.green(b), bb = Color.blue(b);
        return Color.rgb(
                Math.round(ar * r + br * (1 - r)),
                Math.round(ag * r + bg * (1 - r)),
                Math.round(ab * r + bb * (1 - r)));
    }

    public static LinearLayout.LayoutParams wrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public static LinearLayout.LayoutParams matchW() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public static LinearLayout.LayoutParams weight(float w) {
        return new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, w);
    }

    public static LinearLayout.LayoutParams margins(LinearLayout.LayoutParams p,
                                                    Context c, float l, float t, float r, float b) {
        p.setMargins(dp(c, l), dp(c, t), dp(c, r), dp(c, b));
        return p;
    }

    // ------------------------------------------------------------ input

    public static android.widget.EditText input(Context c, String hint, String value, int inputType) {
        android.widget.EditText e = new android.widget.EditText(c);
        e.setHint(hint);
        e.setHintTextColor(DIM);
        e.setTextColor(TEXT);
        e.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16);
        e.setTypeface(medium());
        e.setInputType(inputType);
        e.setBackground(roundRect(c, 14, BG_SOFT, LINE, 1));
        e.setPadding(dp(c, 14), dp(c, 14), dp(c, 14), dp(c, 14));
        if (value != null) {
            e.setText(value);
            e.setSelection(value.length());
        }
        e.setSingleLine(true);
        return e;
    }

    /** Riga cliccabile con etichetta a sinistra e valore a destra. */
    public static LinearLayout settingRow(Context c, String emoji, String label, String value,
                                          View.OnClickListener click) {
        LinearLayout row = row(c);
        row.setPadding(dp(c, 14), dp(c, 14), dp(c, 14), dp(c, 14));
        row.setBackground(ripple(roundRect(c, 16, CARD, LINE, 1), 0x22FFFFFF));
        row.setClickable(click != null);
        if (click != null) row.setOnClickListener(click);

        TextView e = text(c, emoji, 17, TEXT, regular());
        row.addView(e);

        LinearLayout col = col(c);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        lp.leftMargin = dp(c, 12);
        col.addView(text(c, label, 14.5f, TEXT, medium()));
        TextView v = text(c, value, 12.5f, MUTED, regular());
        v.setTag("rowValue");
        col.addView(v, margins(matchW(), c, 0, 2, 0, 0));
        row.addView(col, lp);

        if (click != null) row.addView(text(c, "\u203A", 20, DIM, regular()));
        return row;
    }
}
