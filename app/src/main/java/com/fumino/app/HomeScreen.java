package com.fumino.app;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/** Scheda principale: contatore vivo, statistiche e prossimi traguardi. */
public class HomeScreen extends Screen {

    private RingView ring;
    private TextView counter, since, ringCaption;
    private LinearLayout sCigs, sMoney, sLife, sCrav;
    private TextView goalTitle, goalWhen, goalPct;
    private BarView goalBar;
    private TextView healthTitle, healthWhen, healthPct;
    private BarView healthBar;
    private TextView quote;
    private TextView summary;

    public HomeScreen(MainActivity a) {
        super(a);
    }

    @Override
    public boolean needsTick() {
        return true;
    }

    @Override
    protected View build() {
        LinearLayout c = Theme.col(act);

        // ---------------------------------------------------------- saluto
        LinearLayout head = Theme.row(act);
        LinearLayout hi = Theme.col(act);
        String name = p.displayName();
        hi.addView(Theme.text(act, name.isEmpty() ? "Ciao 👋" : "Ciao " + name + " 👋",
                20, Theme.TEXT, Theme.bold()));
        hi.addView(Theme.text(act, "Oggi è un altro giorno da non fumatore", 13, Theme.MUTED,
                Theme.regular()), Theme.margins(Theme.matchW(), act, 0, 2, 0, 0));
        head.addView(hi, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView shareBtn = Theme.text(act, "↑", 20, Theme.GREEN, Theme.bold());
        shareBtn.setGravity(Gravity.CENTER);
        int sz = Theme.dp(act, 40);
        shareBtn.setLayoutParams(new LinearLayout.LayoutParams(sz, sz));
        shareBtn.setBackground(Theme.ripple(Theme.circle(Theme.withAlpha(Theme.GREEN, 0x1F)), 0x33FFFFFF));
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.shareProgress();
            }
        });
        head.addView(shareBtn);
        c.addView(head, Theme.matchW());
        c.addView(Theme.space(act, 6));

        // ----------------------------------------------------------- anello
        FrameLayout ringWrap = new FrameLayout(act);
        ring = new RingView(act);
        ringWrap.addView(ring, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout center = Theme.col(act);
        center.setGravity(Gravity.CENTER);
        center.addView(Theme.label(act, "Sei libero da"));
        counter = Theme.text(act, "", 26, Theme.TEXT, Theme.bold());
        counter.setGravity(Gravity.CENTER);
        counter.setMaxLines(1);
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            counter.setAutoSizeTextTypeUniformWithConfiguration(15, 28, 1,
                    android.util.TypedValue.COMPLEX_UNIT_SP);
        }
        center.addView(counter, Theme.margins(Theme.matchW(), act, 0, 6, 0, 0));
        since = Theme.text(act, "", 12, Theme.MUTED, Theme.regular());
        since.setGravity(Gravity.CENTER);
        center.addView(since, Theme.margins(Theme.matchW(), act, 0, 6, 0, 0));
        FrameLayout.LayoutParams cp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cp.gravity = Gravity.CENTER;
        int side = Theme.dp(act, 40);
        cp.leftMargin = side;
        cp.rightMargin = side;
        ringWrap.addView(center, cp);

        LinearLayout.LayoutParams rw = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Theme.dp(act, 252));
        rw.topMargin = Theme.dp(act, 8);
        c.addView(ringWrap, rw);

        ringCaption = Theme.text(act, "", 12.5f, Theme.BLUE, Theme.medium());
        ringCaption.setGravity(Gravity.CENTER);
        c.addView(ringCaption, Theme.margins(Theme.matchW(), act, 0, 2, 0, 0));
        c.addView(Theme.space(act, 18));

        // ------------------------------------------------------ statistiche
        sCigs = Cards.stat(act, "🚭", "sigarette non fumate", "0", "", Theme.GREEN);
        sMoney = Cards.stat(act, "💰", "risparmiati finora", "0 €", "", Theme.GOLD);
        c.addView(Cards.pair(act, sCigs, sMoney), Theme.matchW());
        c.addView(Theme.space(act, 12));

        sLife = Cards.stat(act, "❤️", "di vita guadagnata", "0", "", Theme.PINK);
        sCrav = Cards.stat(act, "💪", "voglie superate", "0", "", Theme.PURPLE);
        c.addView(Cards.pair(act, sLife, sCrav), Theme.matchW());
        c.addView(Theme.space(act, 16));

        // -------------------------------------------------------- SOS
        TextView sos = Theme.buttonGradient(act, "🆘  Ho voglia di fumare", 0xFFF87171, 0xFFFB923C);
        sos.setTextColor(0xFF2A0A0A);
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.openSos();
            }
        });
        c.addView(sos, Theme.matchW());
        c.addView(Theme.space(act, 20));

        // --------------------------------------------- prossimo obiettivo
        c.addView(Cards.sectionTitle(act, "Prossimo obiettivo"), Theme.matchW());
        c.addView(Theme.space(act, 10));
        LinearLayout gc = Theme.card(act);
        gc.setClickable(true);
        gc.setBackground(Theme.ripple(Theme.roundRect(act, 20, Theme.CARD, Theme.LINE, 1), 0x22FFFFFF));
        LinearLayout gr = Theme.row(act);
        goalTitle = Theme.text(act, "", 15.5f, Theme.TEXT, Theme.bold());
        gr.addView(goalTitle, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        goalPct = Theme.text(act, "", 13, Theme.GOLD, Theme.bold());
        gr.addView(goalPct);
        gc.addView(gr, Theme.matchW());
        goalBar = new BarView(act, 7);
        goalBar.setColors(Theme.GOLD, Theme.GREEN);
        gc.addView(goalBar, Theme.margins(Theme.matchW(), act, 0, 12, 0, 0));
        goalWhen = Theme.text(act, "", 12, Theme.MUTED, Theme.regular());
        gc.addView(goalWhen, Theme.margins(Theme.matchW(), act, 0, 9, 0, 0));
        gc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.select(2);
            }
        });
        c.addView(gc, Theme.matchW());
        c.addView(Theme.space(act, 20));

        // ------------------------------------------- prossimo recupero
        c.addView(Cards.sectionTitle(act, "Prossimo recupero del corpo"), Theme.matchW());
        c.addView(Theme.space(act, 10));
        LinearLayout hc = Theme.card(act);
        hc.setClickable(true);
        hc.setBackground(Theme.ripple(Theme.roundRect(act, 20, Theme.CARD, Theme.LINE, 1), 0x22FFFFFF));
        LinearLayout hr = Theme.row(act);
        healthTitle = Theme.text(act, "", 15.5f, Theme.TEXT, Theme.bold());
        hr.addView(healthTitle, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        healthPct = Theme.text(act, "", 13, Theme.BLUE, Theme.bold());
        hr.addView(healthPct);
        hc.addView(hr, Theme.matchW());
        healthBar = new BarView(act, 7);
        healthBar.setColors(Theme.BLUE, Theme.GREEN);
        hc.addView(healthBar, Theme.margins(Theme.matchW(), act, 0, 12, 0, 0));
        healthWhen = Theme.text(act, "", 12, Theme.MUTED, Theme.regular());
        hc.addView(healthWhen, Theme.margins(Theme.matchW(), act, 0, 9, 0, 0));
        hc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.select(1);
            }
        });
        c.addView(hc, Theme.matchW());
        c.addView(Theme.space(act, 20));

        // ------------------------------------------------- frase del giorno
        LinearLayout qc = Theme.cardTinted(act, Theme.PURPLE);
        qc.addView(Theme.label(act, "Frase del giorno"));
        qc.addView(Theme.space(act, 8));
        quote = Theme.text(act, Motivation.quoteOfTheDay(), 16, Theme.TEXT, Theme.medium());
        qc.addView(quote, Theme.matchW());
        c.addView(qc, Theme.matchW());
        c.addView(Theme.space(act, 12));

        // ------------------------------------------------------- riepilogo
        LinearLayout sum = Theme.card(act);
        sum.setClickable(true);
        sum.setBackground(Theme.ripple(Theme.roundRect(act, 20, Theme.CARD, Theme.LINE, 1), 0x22FFFFFF));
        summary = Theme.text(act, "", 13.5f, Theme.MUTED, Theme.regular());
        sum.addView(summary, Theme.matchW());
        sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.select(2);
            }
        });
        c.addView(sum, Theme.matchW());

        refresh();
        ring.setProgress(ringProgress(), true);
        return Ui.screen(act, c);
    }

    private float ringProgress() {
        Goal g = Goal.next(p);
        if (g == null) return 1f;
        return g.progress(p);
    }

    @Override
    public void refresh() {
        if (counter == null) return;
        long el = p.elapsed();
        counter.setText(Fmt.counter(el));
        since.setText("dal " + Fmt.dateTime(p.quitAt()));

        Cards.updateStat(sCigs, Fmt.intNum(p.cigsAvoided()),
                "+" + p.cigsPerDay() + " al giorno");
        Cards.updateStat(sMoney, Fmt.money(p.moneySaved()),
                "+" + Fmt.money(p.moneyPerDay()) + " al giorno");
        Cards.updateStat(sLife, Fmt.compact((long) (p.lifeMinutes() * 60000L)),
                Fmt.num(p.tarGrams(), 1) + " g di catrame evitati");
        Cards.updateStat(sCrav, String.valueOf(p.cravingsResisted()),
                p.cravingsToday() + " nelle ultime 24h");

        Goal g = Goal.next(p);
        if (g != null) {
            goalTitle.setText(g.emoji + "  " + g.title);
            goalPct.setText(Math.round(g.progress(p) * 100) + "%");
            goalBar.setProgress(g.progress(p), false);
            long w = g.when(p);
            goalWhen.setText(w > 0
                    ? "Previsto il " + Fmt.dateTime(w) + " · tra " + Fmt.human(w - System.currentTimeMillis())
                    : g.currentLabel(p) + " di " + g.targetLabel() + " · dipende da te");
        } else {
            goalTitle.setText("🏆  Tutti gli obiettivi raggiunti!");
            goalPct.setText("100%");
            goalBar.setProgress(1f, false);
            goalWhen.setText("Hai completato ogni traguardo dell'app. Leggendario.");
        }

        Health h = Health.next(el);
        if (h != null) {
            healthTitle.setText(h.emoji + "  " + h.title);
            healthPct.setText(Math.round(h.progress(el) * 100) + "%");
            healthBar.setProgress(h.progress(el), false);
            long d = h.date(p.quitAt());
            healthWhen.setText("Previsto il " + Fmt.dateTime(d) + " · tra "
                    + Fmt.human(d - System.currentTimeMillis()));
        } else {
            healthTitle.setText("💚  Recupero completo");
            healthPct.setText("100%");
            healthBar.setProgress(1f, false);
            healthWhen.setText("Il tuo corpo ha recuperato tutto quello che poteva recuperare.");
        }

        List<Goal> all = Goal.all();
        summary.setText("🏅 " + Goal.reachedCount(p) + " obiettivi su " + all.size()
                + " raggiunti\n❤️ " + Health.completed(el) + " tappe di salute su "
                + Health.all().size() + " superate\n📦 "
                + Fmt.num(p.packsAvoided(), 1) + " pacchetti mai comprati · 🌱 "
                + Fmt.num(p.co2Kg(), 1) + " kg di CO₂ in meno");

        if (ring != null) ring.setProgress(ringProgress(), false);
    }
}
