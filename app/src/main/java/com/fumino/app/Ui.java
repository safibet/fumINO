package com.fumino.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/** Dialoghi, condivisione e piccoli aiuti comuni a tutte le schermate. */
public final class Ui {

    private Ui() {
    }

    public interface Action {
        void run();
    }

    /** Contenitore scorrevole con il padding standard delle schermate. */
    public static ScrollView screen(Context c, LinearLayout content) {
        ScrollView sv = new ScrollView(c);
        sv.setVerticalScrollBarEnabled(false);
        sv.setClipToPadding(false);
        content.setPadding(Theme.dp(c, 18), Theme.dp(c, 8), Theme.dp(c, 18), Theme.dp(c, 28));
        sv.addView(content, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return sv;
    }

    /** Dialogo scuro con card arrotondata: titolo, testo e fino a due azioni. */
    public static Dialog dialog(Activity a, String emoji, String title, CharSequence message,
                                String positive, final Action onPositive,
                                String negative, final Action onNegative) {
        LinearLayout card = Theme.col(a);
        card.setBackground(Theme.roundRect(a, 26, Theme.CARD, Theme.LINE, 1));
        int p = Theme.dp(a, 22);
        card.setPadding(p, p, p, Theme.dp(a, 16));

        if (emoji != null && !emoji.isEmpty()) {
            TextView e = Theme.text(a, emoji, 34, Theme.TEXT, Theme.regular());
            e.setGravity(Gravity.CENTER);
            card.addView(e, Theme.matchW());
            card.addView(Theme.space(a, 6));
        }
        TextView t = Theme.text(a, title, 20, Theme.TEXT, Theme.bold());
        t.setGravity(Gravity.CENTER);
        card.addView(t, Theme.matchW());
        if (message != null && message.length() > 0) {
            card.addView(Theme.space(a, 10));
            TextView m = Theme.text(a, message, 14.5f, Theme.MUTED, Theme.regular());
            m.setGravity(Gravity.CENTER);
            card.addView(m, Theme.matchW());
        }
        card.addView(Theme.space(a, 20));

        final AlertDialog d = new AlertDialog.Builder(a).setView(wrapDialog(a, card)).create();
        Window w = d.getWindow();
        if (w != null) {
            w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (positive != null) {
            TextView b = Theme.buttonGradient(a, positive, Theme.GREEN, Theme.BLUE);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                    if (onPositive != null) onPositive.run();
                }
            });
            card.addView(b, Theme.matchW());
        }
        if (negative != null) {
            card.addView(Theme.space(a, 8));
            TextView b = Theme.button(a, negative, 0x00000000, Theme.MUTED);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                    if (onNegative != null) onNegative.run();
                }
            });
            card.addView(b, Theme.matchW());
        }
        d.show();
        return d;
    }

    /** Dialogo con contenuto libero. */
    public static Dialog custom(Activity a, LinearLayout card) {
        card.setBackground(Theme.roundRect(a, 26, Theme.CARD, Theme.LINE, 1));
        int p = Theme.dp(a, 22);
        card.setPadding(p, p, p, Theme.dp(a, 16));
        AlertDialog d = new AlertDialog.Builder(a).setView(wrapDialog(a, card)).create();
        Window w = d.getWindow();
        if (w != null) w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.show();
        return d;
    }

    private static View wrapDialog(Activity a, LinearLayout card) {
        ScrollView sv = new ScrollView(a);
        sv.setVerticalScrollBarEnabled(false);
        int m = Theme.dp(a, 10);
        sv.setPadding(m, m, m, m);
        sv.addView(card, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return sv;
    }

    public static void toast(Context c, String s) {
        Toast.makeText(c, s, Toast.LENGTH_SHORT).show();
    }

    public static void share(Context c, String text) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, text);
        c.startActivity(Intent.createChooser(i, "Condividi i tuoi progressi"));
    }

    public static void vibrate(Context c, long ms) {
        try {
            Vibrator v = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null && v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= 26) {
                    v.vibrate(android.os.VibrationEffect.createOneShot(ms,
                            android.os.VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(ms);
                }
            }
        } catch (Exception ignored) {
        }
    }

    /** Testo di condivisione con il riepilogo dei progressi. */
    public static String progressText(Prefs p) {
        StringBuilder sb = new StringBuilder();
        sb.append("Non fumo da ").append(Fmt.human(p.elapsed())).append(" 💪\n");
        sb.append("• ").append(Fmt.intNum(p.cigsAvoided())).append(" sigarette non fumate\n");
        sb.append("• ").append(Fmt.money(p.moneySaved())).append(" risparmiati\n");
        sb.append("• ").append(Fmt.life(p.lifeMinutes())).append(" di vita guadagnati\n");
        sb.append("• ").append(Goal.reachedCount(p)).append(" obiettivi su ")
                .append(Goal.all().size()).append(" raggiunti\n");
        sb.append("\nContato con fumINO");
        return sb.toString();
    }
}
