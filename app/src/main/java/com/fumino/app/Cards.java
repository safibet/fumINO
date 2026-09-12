package com.fumino.app;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Componenti riutilizzabili: riquadri statistica, righe obiettivo, tappe di salute. */
public final class Cards {

    private Cards() {
    }

    // ------------------------------------------------------- riquadro dati

    public static LinearLayout stat(Context c, String emoji, String label, String value,
                                    String sub, int accent) {
        LinearLayout box = Theme.col(c);
        box.setBackground(Theme.roundRect(c, 20, Theme.CARD, Theme.LINE, 1));
        int p = Theme.dp(c, 14);
        box.setPadding(p, p, p, p);

        TextView e = Theme.text(c, emoji, 20, accent, Theme.regular());
        box.addView(e);
        box.addView(Theme.space(c, 8));

        TextView v = Theme.text(c, value, 21, Theme.TEXT, Theme.bold());
        v.setTag("value");
        box.addView(v);

        TextView l = Theme.text(c, label, 12.5f, Theme.MUTED, Theme.regular());
        box.addView(l, Theme.margins(Theme.matchW(), c, 0, 2, 0, 0));

        if (sub != null) {
            TextView s = Theme.text(c, sub, 11.5f, accent, Theme.medium());
            s.setTag("sub");
            box.addView(s, Theme.margins(Theme.matchW(), c, 0, 6, 0, 0));
        }
        return box;
    }

    public static void updateStat(LinearLayout box, String value, String sub) {
        TextView v = (TextView) box.findViewWithTag("value");
        if (v != null) v.setText(value);
        TextView s = (TextView) box.findViewWithTag("sub");
        if (s != null && sub != null) s.setText(sub);
    }

    /** Riga con due riquadri affiancati. */
    public static LinearLayout pair(Context c, View a, View b) {
        LinearLayout r = Theme.row(c);
        r.setGravity(Gravity.TOP);
        LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        pa.rightMargin = Theme.dp(c, 6);
        LinearLayout.LayoutParams pb = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        pb.leftMargin = Theme.dp(c, 6);
        r.addView(a, pa);
        r.addView(b, pb);
        return r;
    }

    public static TextView sectionTitle(Context c, String s) {
        TextView t = Theme.text(c, s, 17, Theme.TEXT, Theme.bold());
        return t;
    }

    /** Pastiglia rotonda con emoji. */
    public static TextView emojiBadge(Context c, String emoji, int color, float sizeDp, boolean faded) {
        TextView t = Theme.text(c, emoji, sizeDp * 0.42f, Theme.TEXT, Theme.regular());
        t.setGravity(Gravity.CENTER);
        t.setBackground(Theme.circle(Theme.withAlpha(color, faded ? 0x14 : 0x2E)));
        int s = Theme.dp(c, sizeDp);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(s, s);
        t.setLayoutParams(lp);
        if (faded) t.setAlpha(0.55f);
        return t;
    }

    // ------------------------------------------------------------ obiettivi

    public static View goal(final Activity a, final Prefs p, final Goal g) {
        final boolean done = g.reached(p);
        int color = Theme.CATEGORY_COLORS[g.type];

        LinearLayout card = Theme.col(a);
        card.setBackground(Theme.ripple(
                done ? Theme.roundRect(a, 20, Theme.blend(color, Theme.CARD, 0.10f),
                        Theme.withAlpha(color, 0x55), 1)
                        : Theme.roundRect(a, 20, Theme.CARD, Theme.LINE, 1),
                0x22FFFFFF));
        int pad = Theme.dp(a, 14);
        card.setPadding(pad, pad, pad, pad);
        card.setClickable(true);

        LinearLayout top = Theme.row(a);
        top.addView(emojiBadge(a, g.emoji, color, 46, !done));

        LinearLayout mid = Theme.col(a);
        LinearLayout.LayoutParams midLp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        midLp.leftMargin = Theme.dp(a, 12);
        mid.addView(Theme.text(a, g.title, 15.5f, done ? Theme.TEXT : 0xFFCBD5E1, Theme.bold()));

        String status;
        long when = g.when(p);
        if (done) {
            status = when > 0
                    ? "✓ Raggiunto il " + Fmt.dateTime(when)
                    : "✓ Raggiunto";
        } else if (when > 0) {
            status = "Previsto il " + Fmt.dateTime(when) + " · tra "
                    + Fmt.compact(when - System.currentTimeMillis());
        } else {
            status = g.currentLabel(p) + " di " + g.targetLabel();
        }
        mid.addView(Theme.text(a, status, 12, done ? Theme.withAlpha(color, 0xDD) : Theme.DIM,
                Theme.regular()), Theme.margins(Theme.matchW(), a, 0, 3, 0, 0));
        top.addView(mid, midLp);

        TextView mark = Theme.text(a, done ? "🏅" : "🔒", 16,
                done ? Theme.GOLD : Theme.DIM, Theme.regular());
        if (!done) mark.setAlpha(0.5f);
        top.addView(mark);
        card.addView(top, Theme.matchW());

        if (!done) {
            BarView bar = new BarView(a, 6);
            bar.setColors(color, Theme.blend(color, Theme.BLUE, 0.4f));
            bar.setProgress(g.progress(p), false);
            LinearLayout.LayoutParams bl = Theme.matchW();
            bl.topMargin = Theme.dp(a, 12);
            card.addView(bar, bl);

            LinearLayout foot = Theme.row(a);
            foot.addView(Theme.text(a, g.currentLabel(p) + " / " + g.targetLabel(), 11.5f,
                    Theme.MUTED, Theme.medium()));
            foot.addView(Theme.flexSpace(a));
            foot.addView(Theme.text(a, Math.round(g.progress(p) * 100) + "%", 11.5f, color,
                    Theme.bold()));
            card.addView(foot, Theme.margins(Theme.matchW(), a, 0, 7, 0, 0));
        }

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalDialog(a, p, g);
            }
        });
        return card;
    }

    public static void goalDialog(final Activity a, final Prefs p, final Goal g) {
        boolean done = g.reached(p);
        long when = g.when(p);
        int color = Theme.CATEGORY_COLORS[g.type];

        LinearLayout card = Theme.col(a);
        TextView e = Theme.text(a, g.emoji, 40, Theme.TEXT, Theme.regular());
        e.setGravity(Gravity.CENTER);
        card.addView(e, Theme.matchW());
        card.addView(Theme.space(a, 6));

        TextView t = Theme.text(a, g.title, 21, Theme.TEXT, Theme.bold());
        t.setGravity(Gravity.CENTER);
        card.addView(t, Theme.matchW());

        TextView cat = Theme.pill(a, Goal.CATEGORY_EMOJI[g.type] + "  " + g.categoryName(), color);
        LinearLayout wrap = Theme.row(a);
        wrap.setGravity(Gravity.CENTER);
        wrap.addView(cat);
        card.addView(wrap, Theme.margins(Theme.matchW(), a, 0, 10, 0, 0));

        card.addView(Theme.space(a, 14));
        TextView d = Theme.text(a, g.desc, 14.5f, Theme.MUTED, Theme.regular());
        d.setGravity(Gravity.CENTER);
        card.addView(d, Theme.matchW());
        card.addView(Theme.space(a, 18));

        LinearLayout info = Theme.col(a);
        info.setBackground(Theme.roundRect(a, 16, Theme.BG_SOFT));
        int ip = Theme.dp(a, 14);
        info.setPadding(ip, ip, ip, ip);

        if (done) {
            info.addView(Theme.label(a, "Obiettivo raggiunto"));
            info.addView(Theme.space(a, 6));
            info.addView(Theme.text(a, when > 0 ? Fmt.capitalize(Fmt.dateLong(when)) + "\nalle "
                    + Fmt.time(when) : "Raggiunto", 15, color, Theme.bold()));
            if (when > 0) {
                info.addView(Theme.space(a, 6));
                info.addView(Theme.text(a, "cioè " + Fmt.human(System.currentTimeMillis() - when)
                        + " fa", 12.5f, Theme.DIM, Theme.regular()));
            }
        } else {
            info.addView(Theme.label(a, "Quando lo raggiungerai"));
            info.addView(Theme.space(a, 6));
            if (when > 0) {
                info.addView(Theme.text(a, Fmt.capitalize(Fmt.dateLong(when)) + "\nalle "
                        + Fmt.time(when), 15, color, Theme.bold()));
                info.addView(Theme.space(a, 6));
                info.addView(Theme.text(a, "mancano " + Fmt.human(when - System.currentTimeMillis()),
                        12.5f, Theme.MUTED, Theme.regular()));
            } else {
                info.addView(Theme.text(a, "Dipende da te: ogni voglia superata ti avvicina",
                        14, color, Theme.medium()));
            }
            info.addView(Theme.space(a, 12));
            BarView bar = new BarView(a, 7);
            bar.setColors(color, Theme.blend(color, Theme.BLUE, 0.4f));
            bar.setProgress(g.progress(p), false);
            info.addView(bar);
            info.addView(Theme.space(a, 8));
            info.addView(Theme.text(a, g.currentLabel(p) + " di " + g.targetLabel()
                    + "  (" + Math.round(g.progress(p) * 100) + "%)", 12, Theme.MUTED, Theme.medium()));
        }
        card.addView(info, Theme.matchW());
        card.addView(Theme.space(a, 16));

        final android.app.Dialog dlg = Ui.custom(a, card);
        if (done) {
            TextView share = Theme.buttonGradient(a, "Condividi questo traguardo", color,
                    Theme.blend(color, Theme.BLUE, 0.5f));
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.dismiss();
                    Ui.share(a, "🏆 Obiettivo raggiunto: " + g.title + "!\n" + g.desc
                            + "\n\n" + Ui.progressText(p));
                }
            });
            card.addView(share, Theme.matchW());
            card.addView(Theme.space(a, 8));
        }
        TextView close = Theme.button(a, "Chiudi", 0x00000000, Theme.MUTED);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        card.addView(close, Theme.matchW());
    }

    // ------------------------------------------------------ tappe di salute

    public static View health(final Activity a, final Prefs p, final Health h) {
        final long elapsed = p.elapsed();
        final boolean done = h.done(elapsed);
        final long date = h.date(p.quitAt());
        int color = done ? Theme.GREEN : Theme.BLUE;

        LinearLayout card = Theme.col(a);
        card.setBackground(Theme.ripple(done
                        ? Theme.roundRect(a, 20, Theme.blend(Theme.GREEN, Theme.CARD, 0.08f),
                        Theme.withAlpha(Theme.GREEN, 0x44), 1)
                        : Theme.roundRect(a, 20, Theme.CARD, Theme.LINE, 1),
                0x22FFFFFF));
        int pad = Theme.dp(a, 14);
        card.setPadding(pad, pad, pad, pad);
        card.setClickable(true);

        LinearLayout top = Theme.row(a);
        top.addView(emojiBadge(a, h.emoji, color, 44, !done));

        LinearLayout mid = Theme.col(a);
        LinearLayout.LayoutParams midLp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        midLp.leftMargin = Theme.dp(a, 12);

        LinearLayout titleRow = Theme.row(a);
        titleRow.addView(Theme.pill(a, h.when, color));
        titleRow.addView(Theme.flexSpace(a));
        titleRow.addView(Theme.text(a, done ? "✓" : Math.round(h.progress(elapsed) * 100) + "%",
                12, color, Theme.bold()));
        mid.addView(titleRow, Theme.matchW());

        mid.addView(Theme.text(a, h.title, 15, done ? Theme.TEXT : 0xFFCBD5E1, Theme.bold()),
                Theme.margins(Theme.matchW(), a, 0, 6, 0, 0));

        String status = done
                ? "Raggiunto il " + Fmt.dateTime(date)
                : "Previsto il " + Fmt.dateTime(date) + " · tra "
                + Fmt.human(date - System.currentTimeMillis());
        mid.addView(Theme.text(a, status, 12, done ? Theme.withAlpha(Theme.GREEN, 0xDD) : Theme.DIM,
                Theme.regular()), Theme.margins(Theme.matchW(), a, 0, 4, 0, 0));
        top.addView(mid, midLp);
        card.addView(top, Theme.matchW());

        if (!done) {
            BarView bar = new BarView(a, 6);
            bar.setColors(Theme.BLUE, Theme.GREEN);
            bar.setProgress(h.progress(elapsed), false);
            LinearLayout.LayoutParams bl = Theme.matchW();
            bl.topMargin = Theme.dp(a, 12);
            card.addView(bar, bl);
        }

        final LinearLayout detail = Theme.col(a);
        detail.setVisibility(View.GONE);
        detail.addView(Theme.text(a, h.detail, 13.5f, Theme.MUTED, Theme.regular()), Theme.matchW());
        detail.addView(Theme.space(a, 10));
        LinearLayout src = Theme.col(a);
        src.setBackground(Theme.roundRect(a, 12, Theme.BG_SOFT));
        int sp = Theme.dp(a, 10);
        src.setPadding(sp, sp, sp, sp);
        src.addView(Theme.label(a, "Fonte"), Theme.matchW());
        src.addView(Theme.text(a, h.source, 11.5f, Theme.MUTED, Theme.regular()),
                Theme.margins(Theme.matchW(), a, 0, 4, 0, 0));
        detail.addView(src, Theme.matchW());
        card.addView(detail, Theme.margins(Theme.matchW(), a, 0, 12, 0, 0));

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail.setVisibility(detail.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        return card;
    }
}
