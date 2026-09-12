package com.fumino.app;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Scheda "Salute": la cronologia del recupero del corpo, con date precise. */
public class HealthScreen extends Screen {

    private TextView headline, sub, nextLine;
    private BarView overall;
    private LinearLayout list;

    public HealthScreen(MainActivity a) {
        super(a);
    }

    @Override
    public boolean needsTick() {
        return false;
    }

    @Override
    protected View build() {
        LinearLayout c = Theme.col(act);

        c.addView(Theme.h1(act, "Il tuo corpo si ripara"), Theme.matchW());
        c.addView(Theme.body(act, "Ogni tappa ha una data precisa: quella in cui il tuo organismo "
                + "recupera una funzione. Tocca una tappa per i dettagli."),
                Theme.margins(Theme.matchW(), act, 0, 6, 0, 0));
        c.addView(Theme.space(act, 16));

        LinearLayout head = Theme.cardTinted(act, Theme.GREEN);
        headline = Theme.text(act, "", 30, Theme.TEXT, Theme.bold());
        head.addView(headline, Theme.matchW());
        sub = Theme.text(act, "", 13.5f, Theme.MUTED, Theme.regular());
        head.addView(sub, Theme.margins(Theme.matchW(), act, 0, 4, 0, 0));
        overall = new BarView(act, 8);
        overall.setColors(Theme.GREEN, Theme.BLUE);
        head.addView(overall, Theme.margins(Theme.matchW(), act, 0, 14, 0, 0));
        nextLine = Theme.text(act, "", 12.5f, Theme.GREEN, Theme.medium());
        head.addView(nextLine, Theme.margins(Theme.matchW(), act, 0, 10, 0, 0));
        c.addView(head, Theme.matchW());
        c.addView(Theme.space(act, 18));

        list = Theme.col(act);
        c.addView(list, Theme.matchW());

        buildList();
        refresh();
        return Ui.screen(act, c);
    }

    private void buildList() {
        list.removeAllViews();
        for (Health h : Health.all()) {
            View v = Cards.health(act, p, h);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = Theme.dp(act, 10);
            list.addView(v, lp);
        }
    }

    @Override
    public void refresh() {
        if (headline == null) return;
        long el = p.elapsed();
        int done = Health.completed(el);
        int total = Health.all().size();
        headline.setText(done + " / " + total);
        sub.setText("tappe di recupero già superate");
        overall.setProgress(done / (float) total, true);
        Health n = Health.next(el);
        if (n != null) {
            long d = n.date(p.quitAt());
            nextLine.setText("Prossima: " + n.title.toLowerCase(Fmt.IT) + " • "
                    + Fmt.dateTime(d) + " (tra " + Fmt.human(d - System.currentTimeMillis()) + ")");
        } else {
            nextLine.setText("Hai completato l'intera cronologia del recupero. Straordinario.");
        }
        buildList();
    }
}
