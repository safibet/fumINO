package com.fumino.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

/** Schermata SOS: respirazione guidata, conto alla rovescia e consigli immediati. */
public class SosActivity extends Activity {

    private Prefs p;
    private BreathView breath;
    private TextView phase, phaseHint, countdown, cycles, tipTitle, tipBody, tipEmoji;
    private long startedAt;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private static final long WINDOW = 3 * 60 * 1000L; // la voglia passa in ~3 minuti

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            long left = Math.max(0, WINDOW - (SystemClock.elapsedRealtime() - startedAt));
            long s = left / 1000;
            countdown.setText(String.format(Fmt.IT, "%d:%02d", s / 60, s % 60));
            if (left == 0) {
                countdown.setText("0:00");
                countdown.setTextColor(Theme.GREEN);
            }
            handler.postDelayed(this, 250);
        }
    };

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        p = new Prefs(this);

        LinearLayout c = Theme.col(this);
        c.setBackgroundColor(Theme.BG);

        TextView title = Theme.text(this, "Respira. Passerà.", 24, Theme.TEXT, Theme.bold());
        title.setGravity(Gravity.CENTER);
        c.addView(title, Theme.matchW());

        TextView sub = Theme.text(this,
                "Una voglia dura in media meno di tre minuti. Restiamo qui insieme finché passa.",
                13.5f, Theme.MUTED, Theme.regular());
        sub.setGravity(Gravity.CENTER);
        c.addView(sub, Theme.margins(Theme.matchW(), this, 0, 8, 0, 0));
        c.addView(Theme.space(this, 10));

        // ------------------------------------------------------- respirazione
        FrameLayout wrap = new FrameLayout(this);
        breath = new BreathView(this);
        wrap.addView(breath, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout center = Theme.col(this);
        center.setGravity(Gravity.CENTER);
        phase = Theme.text(this, "Inspira", 26, 0xFF06281F, Theme.bold());
        phase.setGravity(Gravity.CENTER);
        center.addView(phase, Theme.matchW());
        countdown = Theme.text(this, "3:00", 15, 0xCC06281F, Theme.bold());
        countdown.setGravity(Gravity.CENTER);
        center.addView(countdown, Theme.margins(Theme.matchW(), this, 0, 4, 0, 0));
        FrameLayout.LayoutParams clp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clp.gravity = Gravity.CENTER;
        wrap.addView(center, clp);

        LinearLayout.LayoutParams wl = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Theme.dp(this, 230));
        c.addView(wrap, wl);

        phaseHint = Theme.text(this, "dal naso, gonfiando la pancia", 13, Theme.GREEN, Theme.medium());
        phaseHint.setGravity(Gravity.CENTER);
        c.addView(phaseHint, Theme.margins(Theme.matchW(), this, 0, 6, 0, 0));

        cycles = Theme.text(this, "Respirazione 4-7-8 · ciclo 1", 12, Theme.DIM, Theme.regular());
        cycles.setGravity(Gravity.CENTER);
        c.addView(cycles, Theme.margins(Theme.matchW(), this, 0, 4, 0, 0));
        c.addView(Theme.space(this, 18));

        // ------------------------------------------------------------ consiglio
        LinearLayout tip = Theme.cardTinted(this, Theme.BLUE);
        tip.setClickable(true);
        tipEmoji = Theme.text(this, "", 22, Theme.TEXT, Theme.regular());
        tip.addView(tipEmoji);
        tip.addView(Theme.space(this, 6));
        tipTitle = Theme.text(this, "", 16, Theme.TEXT, Theme.bold());
        tip.addView(tipTitle, Theme.matchW());
        tipBody = Theme.text(this, "", 13.5f, Theme.MUTED, Theme.regular());
        tip.addView(tipBody, Theme.margins(Theme.matchW(), this, 0, 6, 0, 0));
        tip.addView(Theme.space(this, 12));
        TextView another = Theme.buttonOutline(this, "Un altro consiglio", Theme.BLUE);
        another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTip();
            }
        });
        tip.addView(another, Theme.matchW());
        c.addView(tip, Theme.matchW());
        c.addView(Theme.space(this, 14));
        showTip();

        // --------------------------------------------------------- i miei perché
        List<String> reasons = p.reasons();
        if (!reasons.isEmpty()) {
            LinearLayout rc = Theme.card(this);
            rc.addView(Theme.label(this, "Ricordati perché"));
            rc.addView(Theme.space(this, 8));
            int n = 0;
            for (String r : reasons) {
                if (n++ >= 3) break;
                rc.addView(Theme.text(this, "•  " + r, 14.5f, Theme.TEXT, Theme.medium()),
                        Theme.margins(Theme.matchW(), this, 0, 0, 0, 6));
            }
            c.addView(rc, Theme.matchW());
            c.addView(Theme.space(this, 14));
        }

        // -------------------------------------------------------- stato attuale
        LinearLayout status = Theme.cardTinted(this, Theme.GOLD);
        status.addView(Theme.label(this, "Quello che perderesti"));
        status.addView(Theme.space(this, 8));
        status.addView(Theme.text(this, Fmt.human(p.elapsed()) + " senza fumare", 18,
                Theme.TEXT, Theme.bold()), Theme.matchW());
        status.addView(Theme.text(this, Goal.reachedCount(p) + " obiettivi raggiunti · "
                        + Fmt.money(p.moneySaved()) + " risparmiati · "
                        + Health.completed(p.elapsed()) + " tappe di salute superate",
                13, Theme.MUTED, Theme.regular()), Theme.margins(Theme.matchW(), this, 0, 6, 0, 0));
        Goal next = Goal.next(p);
        if (next != null) {
            long w = next.when(p);
            status.addView(Theme.text(this, "Prossimo obiettivo: " + next.title
                            + (w > 0 ? " tra " + Fmt.human(w - System.currentTimeMillis()) : ""),
                    13, Theme.GOLD, Theme.medium()), Theme.margins(Theme.matchW(), this, 0, 8, 0, 0));
        }
        c.addView(status, Theme.matchW());
        c.addView(Theme.space(this, 20));

        // ------------------------------------------------------------- azioni
        TextView win = Theme.buttonGradient(this, "💪  Ce l'ho fatta, è passata",
                Theme.GREEN, Theme.BLUE);
        win.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resisted();
            }
        });
        c.addView(win, Theme.matchW());
        c.addView(Theme.space(this, 10));

        TextView back = Theme.button(this, "Torna indietro", 0x00000000, Theme.MUTED);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        c.addView(back, Theme.matchW());

        ScrollView sv = Ui.screen(this, c);
        sv.setBackgroundColor(Theme.BG);
        setContentView(sv);

        breath.setPhaseListener(new BreathView.PhaseListener() {
            @Override
            public void onPhase(String name, String hint, int seconds, int cyc) {
                phase.setText(name + " " + seconds);
                phaseHint.setText(hint);
                cycles.setText("Respirazione 4-7-8 · ciclo " + (cyc + 1));
            }
        });
    }

    private void showTip() {
        String[] t = Motivation.randomTip();
        tipEmoji.setText(t[0]);
        tipTitle.setText(t[1]);
        tipBody.setText(t[2]);
        tipBody.setAlpha(0f);
        tipBody.animate().alpha(1f).setDuration(280).start();
    }

    private void resisted() {
        p.addCravingResisted();
        Ui.vibrate(this, 40);
        Ui.dialog(this, "🎉", "Grande!",
                "Hai superato la voglia numero " + p.cravingsResisted() + ".\n\n"
                        + "Ogni volta che resisti, la voglia successiva è più debole. "
                        + "Il tuo cervello sta imparando che si può stare bene senza.",
                "Continua così", new Ui.Action() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startedAt = SystemClock.elapsedRealtime();
        breath.start();
        handler.post(tick);
    }

    @Override
    protected void onPause() {
        super.onPause();
        breath.stop();
        handler.removeCallbacks(tick);
    }
}
