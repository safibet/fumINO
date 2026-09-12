package com.fumino.app;

import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/** Scheda "Obiettivi": tutti i traguardi, con data di sblocco prevista o raggiunta. */
public class GoalsScreen extends Screen {

    private static final String[] FILTERS = {"Tutti", "Da sbloccare", "Raggiunti",
            "Tempo", "Sigarette", "Risparmio", "Voglie"};

    private int filter = 0;
    private TextView headline, sub, nextLine;
    private BarView overall;
    private LinearLayout list, chips;

    public GoalsScreen(MainActivity a) {
        super(a);
    }

    @Override
    protected View build() {
        LinearLayout c = Theme.col(act);

        c.addView(Theme.h1(act, "I tuoi obiettivi"), Theme.matchW());
        c.addView(Theme.body(act, "Ogni traguardo mostra quando lo hai raggiunto oppure quando lo "
                        + "raggiungerai, al ritmo attuale."),
                Theme.margins(Theme.matchW(), act, 0, 6, 0, 0));
        c.addView(Theme.space(act, 16));

        LinearLayout head = Theme.cardTinted(act, Theme.GOLD);
        headline = Theme.text(act, "", 30, Theme.TEXT, Theme.bold());
        head.addView(headline, Theme.matchW());
        sub = Theme.text(act, "", 13.5f, Theme.MUTED, Theme.regular());
        head.addView(sub, Theme.margins(Theme.matchW(), act, 0, 4, 0, 0));
        overall = new BarView(act, 8);
        overall.setColors(Theme.GOLD, Theme.GREEN);
        head.addView(overall, Theme.margins(Theme.matchW(), act, 0, 14, 0, 0));
        nextLine = Theme.text(act, "", 12.5f, Theme.GOLD, Theme.medium());
        head.addView(nextLine, Theme.margins(Theme.matchW(), act, 0, 10, 0, 0));
        c.addView(head, Theme.matchW());
        c.addView(Theme.space(act, 16));

        HorizontalScrollView hs = new HorizontalScrollView(act);
        hs.setHorizontalScrollBarEnabled(false);
        chips = Theme.row(act);
        hs.addView(chips);
        c.addView(hs, Theme.matchW());
        c.addView(Theme.space(act, 14));

        list = Theme.col(act);
        c.addView(list, Theme.matchW());

        buildChips();
        refresh();
        return Ui.screen(act, c);
    }

    private void buildChips() {
        chips.removeAllViews();
        for (int i = 0; i < FILTERS.length; i++) {
            final int idx = i;
            boolean on = filter == i;
            TextView t = Theme.text(act, FILTERS[i], 13, on ? 0xFF06281F : Theme.MUTED,
                    on ? Theme.bold() : Theme.medium());
            t.setPadding(Theme.dp(act, 14), Theme.dp(act, 9), Theme.dp(act, 14), Theme.dp(act, 9));
            t.setBackground(Theme.ripple(on
                    ? Theme.roundRect(act, 20, Theme.GREEN)
                    : Theme.roundRect(act, 20, Theme.CARD, Theme.LINE, 1), 0x22FFFFFF));
            t.setClickable(true);
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filter = idx;
                    buildChips();
                    buildList();
                }
            });
            LinearLayout.LayoutParams lp = Theme.wrap();
            lp.rightMargin = Theme.dp(act, 8);
            chips.addView(t, lp);
        }
    }

    private List<Goal> filtered() {
        List<Goal> out = new ArrayList<>();
        for (Goal g : Goal.all()) {
            boolean done = g.reached(p);
            switch (filter) {
                case 1:
                    if (!done) out.add(g);
                    break;
                case 2:
                    if (done) out.add(g);
                    break;
                case 3:
                    if (g.type == Goal.TIME) out.add(g);
                    break;
                case 4:
                    if (g.type == Goal.CIGS) out.add(g);
                    break;
                case 5:
                    if (g.type == Goal.MONEY) out.add(g);
                    break;
                case 6:
                    if (g.type == Goal.CRAVINGS) out.add(g);
                    break;
                default:
                    out.add(g);
            }
        }
        return out;
    }

    private void buildList() {
        list.removeAllViews();
        int lastType = -1;
        for (Goal g : filtered()) {
            if (filter <= 2 && g.type != lastType) {
                lastType = g.type;
                TextView t = Theme.text(act, Goal.CATEGORY_EMOJI[g.type] + "  " + Goal.CATEGORIES[g.type],
                        13, Theme.CATEGORY_COLORS[g.type], Theme.bold());
                LinearLayout.LayoutParams lp = Theme.matchW();
                lp.topMargin = Theme.dp(act, list.getChildCount() == 0 ? 0 : 14);
                lp.bottomMargin = Theme.dp(act, 8);
                list.addView(t, lp);
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = Theme.dp(act, 10);
            list.addView(Cards.goal(act, p, g), lp);
        }
        if (list.getChildCount() == 0) {
            LinearLayout empty = Theme.card(act);
            empty.addView(Theme.body(act, "Nessun obiettivo in questa categoria, per ora."));
            list.addView(empty, Theme.matchW());
        }
    }

    @Override
    public void refresh() {
        if (headline == null) return;
        int done = Goal.reachedCount(p);
        int total = Goal.all().size();
        headline.setText(done + " / " + total);
        sub.setText("obiettivi raggiunti finora");
        overall.setProgress(done / (float) total, true);

        Goal n = Goal.next(p);
        if (n != null) {
            long w = n.when(p);
            nextLine.setText("Prossimo: " + n.title + (w > 0
                    ? " • " + Fmt.dateTime(w) + " (tra " + Fmt.human(w - System.currentTimeMillis()) + ")"
                    : " • " + n.currentLabel(p) + " di " + n.targetLabel()));
        } else {
            nextLine.setText("Li hai sbloccati tutti. Sei nella leggenda.");
        }
        buildList();
    }
}
