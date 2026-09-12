package com.fumino.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Schermata principale con le cinque schede. */
public class MainActivity extends Activity {

    private static final String[] TAB_EMOJI = {"🏠", "❤️", "🏆", "✨", "⚙️"};
    private static final String[] TAB_NAME = {"Casa", "Salute", "Obiettivi", "Carica", "Profilo"};

    private Prefs p;
    private FrameLayout content;
    private LinearLayout nav;
    private final List<Screen> screens = new ArrayList<>();
    private int current = 0;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            Screen s = screens.get(current);
            if (s.needsTick()) s.refresh();
            handler.postDelayed(this, 1000);
        }
    };

    public Prefs prefs() {
        return p;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        p = new Prefs(this);
        if (!p.isSetupDone()) {
            startActivity(new Intent(this, SetupActivity.class));
            finish();
            return;
        }

        LinearLayout root = Theme.col(this);
        root.setBackgroundColor(Theme.BG);

        content = new FrameLayout(this);
        root.addView(content, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        nav = buildNav();
        root.addView(nav, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(root);

        screens.add(new HomeScreen(this));
        screens.add(new HealthScreen(this));
        screens.add(new GoalsScreen(this));
        screens.add(new MotivationScreen(this));
        screens.add(new ProfileScreen(this));

        select(getIntent() != null ? getIntent().getIntExtra("tab", 0) : 0);
        askNotificationPermission();
        Notifications.schedule(this);
    }

    // --------------------------------------------------------- navigazione

    private LinearLayout buildNav() {
        LinearLayout bar = Theme.row(this);
        bar.setBackground(Theme.roundRect(this, 0, 0xFF0A1120, Theme.LINE, 0.7f));
        bar.setPadding(Theme.dp(this, 6), Theme.dp(this, 8), Theme.dp(this, 6), Theme.dp(this, 10));
        for (int i = 0; i < TAB_EMOJI.length; i++) {
            final int idx = i;
            LinearLayout item = Theme.col(this);
            item.setGravity(Gravity.CENTER);
            item.setPadding(0, Theme.dp(this, 7), 0, Theme.dp(this, 7));
            item.setClickable(true);
            item.setBackground(Theme.ripple(Theme.roundRect(this, 16, 0x00000000), 0x22FFFFFF));

            TextView e = Theme.text(this, TAB_EMOJI[i], 17, Theme.TEXT, Theme.regular());
            e.setGravity(Gravity.CENTER);
            item.addView(e);
            TextView l = Theme.text(this, TAB_NAME[i], 10.5f, Theme.DIM, Theme.medium());
            l.setGravity(Gravity.CENTER);
            item.addView(l, Theme.margins(Theme.matchW(), this, 0, 3, 0, 0));

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(idx);
                }
            });
            bar.addView(item, new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        }
        return bar;
    }

    public void select(int index) {
        if (index < 0 || index >= screens.size()) index = 0;
        current = index;
        Screen s = screens.get(index);
        s.refresh();
        content.removeAllViews();
        content.addView(s.root(), new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        for (int i = 0; i < nav.getChildCount(); i++) {
            LinearLayout item = (LinearLayout) nav.getChildAt(i);
            boolean on = i == index;
            item.setBackground(Theme.ripple(Theme.roundRect(this, 16,
                    on ? Theme.withAlpha(Theme.GREEN, 0x1F) : 0x00000000), 0x22FFFFFF));
            ((TextView) item.getChildAt(0)).setAlpha(on ? 1f : 0.55f);
            TextView label = (TextView) item.getChildAt(1);
            label.setTextColor(on ? Theme.GREEN : Theme.DIM);
            label.setTypeface(on ? Theme.bold() : Theme.medium());
        }
    }

    /** Ricostruisce tutte le schede (dopo una modifica dei dati). */
    public void refreshAll() {
        for (Screen s : screens) s.rebuild();
        select(current);
    }

    // -------------------------------------------------------- ciclo di vita

    @Override
    protected void onResume() {
        super.onResume();
        if (p == null || !p.isSetupDone()) return;
        handler.removeCallbacks(ticker);
        handler.post(ticker);
        checkNewGoals(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(ticker);
    }

    @Override
    public void onBackPressed() {
        if (current != 0) {
            select(0);
        } else {
            super.onBackPressed();
        }
    }

    // ------------------------------------------------------- celebrazioni

    /** Mostra la festa per gli obiettivi appena sbloccati. */
    public void checkNewGoals(boolean celebrate) {
        Set<String> seen = p.seenGoals();
        List<Goal> fresh = new ArrayList<>();
        Set<String> now = new HashSet<>();
        for (Goal g : Goal.all()) {
            if (g.reached(p)) {
                now.add(g.id);
                if (!seen.contains(g.id)) fresh.add(g);
            }
        }
        p.markGoalsSeen(now);
        if (!celebrate || fresh.isEmpty()) return;
        // alla primissima apertura non mostriamo decine di dialoghi
        if (seen.isEmpty() && fresh.size() > 3) return;
        celebrate(fresh);
    }

    public void celebrate(final List<Goal> goals) {
        final Goal g = goals.get(0);
        String extra = goals.size() > 1
                ? "\n\nE con questo hai sbloccato anche altri " + (goals.size() - 1) + " obiettivi!"
                : "";
        Ui.vibrate(this, 60);
        Ui.dialog(this, g.emoji, "Obiettivo raggiunto!",
                g.title + "\n\n" + g.desc + extra,
                "Condividi", new Ui.Action() {
                    @Override
                    public void run() {
                        Ui.share(MainActivity.this, "🏆 " + g.title + "\n" + g.desc
                                + "\n\n" + Ui.progressText(p));
                    }
                },
                "Continua così", null);
    }

    // ------------------------------------------------------------- azioni

    public void openSos() {
        startActivity(new Intent(this, SosActivity.class));
    }

    public void shareProgress() {
        Ui.share(this, Ui.progressText(p));
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission("android.permission.POST_NOTIFICATIONS")
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 42);
            }
        }
    }
}
