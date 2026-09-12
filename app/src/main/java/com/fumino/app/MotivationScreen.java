package com.fumino.app;

import android.app.Dialog;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/** Scheda "Carica": frasi, motivi personali, consigli anti-voglia e curiosità. */
public class MotivationScreen extends Screen {

    private TextView quote;
    private LinearLayout reasonsBox;

    public MotivationScreen(MainActivity a) {
        super(a);
    }

    @Override
    protected View build() {
        LinearLayout c = Theme.col(act);

        c.addView(Theme.h1(act, "Carica motivazionale"), Theme.matchW());
        c.addView(Theme.body(act, "Quando la testa vacilla, torna qui."),
                Theme.margins(Theme.matchW(), act, 0, 6, 0, 0));
        c.addView(Theme.space(act, 16));

        // ------------------------------------------------------------ frase
        LinearLayout qc = Theme.cardTinted(act, Theme.PURPLE);
        qc.addView(Theme.label(act, "Frase del giorno"));
        qc.addView(Theme.space(act, 10));
        quote = Theme.text(act, Motivation.quoteOfTheDay(), 19, Theme.TEXT, Theme.bold());
        qc.addView(quote, Theme.matchW());
        qc.addView(Theme.space(act, 16));

        LinearLayout qb = Theme.row(act);
        TextView another = Theme.buttonOutline(act, "🔀  Un'altra", Theme.PURPLE);
        another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quote.setText(Motivation.randomQuote());
                quote.setAlpha(0f);
                quote.animate().alpha(1f).setDuration(320).start();
            }
        });
        LinearLayout.LayoutParams w1 = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        w1.rightMargin = Theme.dp(act, 8);
        qb.addView(another, w1);

        TextView shareQ = Theme.buttonOutline(act, "↑  Condividi", Theme.MUTED);
        shareQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ui.share(act, "“" + quote.getText() + "”\n\n" + Ui.progressText(p));
            }
        });
        qb.addView(shareQ, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        qc.addView(qb, Theme.matchW());
        c.addView(qc, Theme.matchW());
        c.addView(Theme.space(act, 16));

        // -------------------------------------------------------------- SOS
        TextView sos = Theme.buttonGradient(act, "🆘  Sto per cedere, aiutami",
                0xFFF87171, 0xFFFB923C);
        sos.setTextColor(0xFF2A0A0A);
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.openSos();
            }
        });
        c.addView(sos, Theme.matchW());
        c.addView(Theme.space(act, 22));

        // -------------------------------------------------------- i miei perché
        LinearLayout head = Theme.row(act);
        head.addView(Cards.sectionTitle(act, "I miei perché"),
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView add = Theme.text(act, "+ Aggiungi", 13, Theme.GREEN, Theme.bold());
        add.setPadding(Theme.dp(act, 8), Theme.dp(act, 6), Theme.dp(act, 8), Theme.dp(act, 6));
        add.setClickable(true);
        add.setBackground(Theme.ripple(Theme.roundRect(act, 12, 0x00000000), 0x22FFFFFF));
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReasonDialog();
            }
        });
        head.addView(add);
        c.addView(head, Theme.matchW());
        c.addView(Theme.space(act, 10));

        reasonsBox = Theme.col(act);
        c.addView(reasonsBox, Theme.matchW());
        buildReasons();
        c.addView(Theme.space(act, 22));

        // --------------------------------------------------------- consigli
        c.addView(Cards.sectionTitle(act, "Cosa fare quando arriva la voglia"), Theme.matchW());
        c.addView(Theme.space(act, 10));
        for (String[] tip : Motivation.TIPS) {
            c.addView(tipCard(tip), Theme.margins(Theme.matchW(), act, 0, 0, 0, 10));
        }
        c.addView(Theme.space(act, 12));

        // -------------------------------------------------------- curiosità
        c.addView(Cards.sectionTitle(act, "Lo sapevi?"), Theme.matchW());
        c.addView(Theme.space(act, 10));
        for (String[] f : Motivation.FACTS) {
            LinearLayout card = Theme.card(act);
            LinearLayout r = Theme.row(act);
            r.addView(Theme.text(act, f[0], 18, Theme.TEXT, Theme.regular()));
            LinearLayout col = Theme.col(act);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            lp.leftMargin = Theme.dp(act, 12);
            col.addView(Theme.text(act, f[1], 14.5f, Theme.TEXT, Theme.bold()));
            col.addView(Theme.text(act, f[2], 13, Theme.MUTED, Theme.regular()),
                    Theme.margins(Theme.matchW(), act, 0, 4, 0, 0));
            r.addView(col, lp);
            card.addView(r, Theme.matchW());
            c.addView(card, Theme.margins(Theme.matchW(), act, 0, 0, 0, 10));
        }

        return Ui.screen(act, c);
    }

    private View tipCard(final String[] tip) {
        LinearLayout card = Theme.col(act);
        card.setBackground(Theme.ripple(Theme.roundRect(act, 18, Theme.CARD, Theme.LINE, 1), 0x22FFFFFF));
        int pad = Theme.dp(act, 14);
        card.setPadding(pad, pad, pad, pad);
        card.setClickable(true);

        LinearLayout r = Theme.row(act);
        r.addView(Theme.text(act, tip[0], 18, Theme.TEXT, Theme.regular()));
        LinearLayout col = Theme.col(act);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp.leftMargin = Theme.dp(act, 12);
        col.addView(Theme.text(act, tip[1], 14.5f, Theme.TEXT, Theme.medium()));
        r.addView(col, lp);
        r.addView(Theme.text(act, "›", 18, Theme.DIM, Theme.regular()));
        card.addView(r, Theme.matchW());

        final TextView detail = Theme.text(act, tip[2], 13.5f, Theme.MUTED, Theme.regular());
        detail.setVisibility(View.GONE);
        card.addView(detail, Theme.margins(Theme.matchW(), act, 0, 10, 0, 0));
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail.setVisibility(detail.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        return card;
    }

    private void buildReasons() {
        reasonsBox.removeAllViews();
        List<String> list = p.reasons();
        if (list.isEmpty()) {
            LinearLayout empty = Theme.card(act);
            empty.addView(Theme.body(act, "Scrivi i motivi per cui hai deciso di smettere. "
                    + "Nei momenti difficili rileggerli è l'arma più efficace che hai."));
            empty.addView(Theme.space(act, 12));
            TextView b = Theme.buttonOutline(act, "Scrivi il primo motivo", Theme.GREEN);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addReasonDialog();
                }
            });
            empty.addView(b, Theme.matchW());
            reasonsBox.addView(empty, Theme.matchW());
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            final int idx = i;
            LinearLayout card = Theme.row(act);
            card.setBackground(Theme.roundRect(act, 16,
                    Theme.blend(Theme.GREEN, Theme.CARD, 0.06f), Theme.LINE, 1));
            int pad = Theme.dp(act, 14);
            card.setPadding(pad, pad, pad, pad);
            card.addView(Theme.text(act, "💬", 16, Theme.GREEN, Theme.regular()));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            lp.leftMargin = Theme.dp(act, 12);
            card.addView(Theme.text(act, list.get(i), 14.5f, Theme.TEXT, Theme.medium()), lp);

            TextView del = Theme.text(act, "✕", 14, Theme.DIM, Theme.regular());
            del.setPadding(Theme.dp(act, 8), Theme.dp(act, 4), Theme.dp(act, 4), Theme.dp(act, 4));
            del.setClickable(true);
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    p.removeReason(idx);
                    buildReasons();
                }
            });
            card.addView(del);
            reasonsBox.addView(card, Theme.margins(Theme.matchW(), act, 0, 0, 0, 10));
        }
    }

    private void addReasonDialog() {
        LinearLayout card = Theme.col(act);
        card.addView(Theme.text(act, "Perché smetti?", 20, Theme.TEXT, Theme.bold()), Theme.matchW());
        card.addView(Theme.space(act, 6));
        card.addView(Theme.body(act, "Una frase tua, scritta con parole tue."), Theme.matchW());
        card.addView(Theme.space(act, 14));
        final EditText e = Theme.input(act, "Es. Voglio correre con mio figlio", "",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        card.addView(e, Theme.matchW());
        card.addView(Theme.space(act, 8));

        LinearLayout sugg = Theme.col(act);
        for (final String s : p.defaultReasons()) {
            TextView t = Theme.text(act, "+ " + s, 12.5f, Theme.MUTED, Theme.regular());
            t.setPadding(0, Theme.dp(act, 7), 0, Theme.dp(act, 7));
            t.setClickable(true);
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    e.setText(s);
                    e.setSelection(s.length());
                }
            });
            sugg.addView(t, Theme.matchW());
        }
        card.addView(sugg, Theme.matchW());
        card.addView(Theme.space(act, 12));

        final Dialog d = Ui.custom(act, card);
        TextView ok = Theme.buttonGradient(act, "Salva", Theme.GREEN, Theme.BLUE);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = e.getText().toString().trim();
                if (!s.isEmpty()) {
                    p.addReason(s);
                    buildReasons();
                }
                d.dismiss();
            }
        });
        card.addView(ok, Theme.matchW());
        card.addView(Theme.space(act, 8));
        TextView cancel = Theme.button(act, "Annulla", 0x00000000, Theme.MUTED);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        card.addView(cancel, Theme.matchW());
    }

    @Override
    public void refresh() {
        if (reasonsBox != null) buildReasons();
    }
}
