package com.fumino.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/** Scheda "Profilo": dati, promemoria, statistiche personali e ripartenze. */
public class ProfileScreen extends Screen {

    public ProfileScreen(MainActivity a) {
        super(a);
    }

    @Override
    protected View build() {
        LinearLayout c = Theme.col(act);

        c.addView(Theme.h1(act, "Profilo"), Theme.matchW());
        c.addView(Theme.body(act, "I tuoi dati restano solo su questo telefono."),
                Theme.margins(Theme.matchW(), act, 0, 6, 0, 0));
        c.addView(Theme.space(act, 16));

        // ------------------------------------------------------------ dati
        c.addView(Cards.sectionTitle(act, "I tuoi dati"), Theme.matchW());
        c.addView(Theme.space(act, 10));

        c.addView(Theme.settingRow(act, "👤", "Nome",
                p.displayName().isEmpty() ? "Non impostato" : p.displayName(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editName();
                    }
                }), Theme.margins(Theme.matchW(), act, 0, 0, 0, 10));

        c.addView(Theme.settingRow(act, "🗓️", "Ultima sigaretta",
                Fmt.dateTime(p.quitAt()), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickQuitDate();
                    }
                }), Theme.margins(Theme.matchW(), act, 0, 0, 0, 10));

        c.addView(Theme.settingRow(act, "🚬", "Sigarette al giorno",
                p.cigsPerDay() + " sigarette", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editNumber("Sigarette al giorno",
                                "Quante ne fumavi in media ogni giorno?",
                                String.valueOf(p.cigsPerDay()), false, new ValueCallback() {
                                    @Override
                                    public void onValue(String s) {
                                        try {
                                            p.setCigsPerDay(Integer.parseInt(s.trim()));
                                        } catch (Exception ignored) {
                                        }
                                    }
                                });
                    }
                }), Theme.margins(Theme.matchW(), act, 0, 0, 0, 10));

        c.addView(Theme.settingRow(act, "📦", "Sigarette per pacchetto",
                p.cigsPerPack() + " sigarette", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editNumber("Sigarette per pacchetto", "Di solito 20.",
                                String.valueOf(p.cigsPerPack()), false, new ValueCallback() {
                                    @Override
                                    public void onValue(String s) {
                                        try {
                                            p.setCigsPerPack(Integer.parseInt(s.trim()));
                                        } catch (Exception ignored) {
                                        }
                                    }
                                });
                    }
                }), Theme.margins(Theme.matchW(), act, 0, 0, 0, 10));

        c.addView(Theme.settingRow(act, "💶", "Prezzo del pacchetto",
                Fmt.moneyExact(p.pricePerPack()), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editNumber("Prezzo del pacchetto", "In euro, anche con i centesimi.",
                                String.valueOf(p.pricePerPack()), true, new ValueCallback() {
                                    @Override
                                    public void onValue(String s) {
                                        try {
                                            p.setPricePerPack(Float.parseFloat(
                                                    s.trim().replace(",", ".").replace("€", "")));
                                        } catch (Exception ignored) {
                                        }
                                    }
                                });
                    }
                }), Theme.margins(Theme.matchW(), act, 0, 0, 0, 10));

        c.addView(Theme.space(act, 12));

        // ------------------------------------------------------- notifiche
        c.addView(Cards.sectionTitle(act, "Notifiche"), Theme.matchW());
        c.addView(Theme.space(act, 10));

        if (!Notifications.allowed(act)) {
            LinearLayout warn = Theme.cardTinted(act, Theme.RED);
            warn.addView(Theme.text(act, "\u26A0\uFE0F  Notifiche disattivate", 15, Theme.TEXT, Theme.bold()));
            warn.addView(Theme.text(act, "Android sta bloccando le notifiche di fumINO: senza permesso "
                            + "non riceverai né la carica quotidiana né gli avvisi dei traguardi.",
                    13, Theme.MUTED, Theme.regular()), Theme.margins(Theme.matchW(), act, 0, 6, 0, 0));
            warn.addView(Theme.space(act, 12));
            TextView fix = Theme.buttonOutline(act, "Attiva le notifiche", Theme.RED);
            fix.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    act.enableNotifications();
                }
            });
            warn.addView(fix, Theme.matchW());
            c.addView(warn, Theme.margins(Theme.matchW(), act, 0, 0, 0, 12));
        }

        LinearLayout rem = Theme.card(act);

        // carica quotidiana
        LinearLayout switchRow = Theme.row(act);
        LinearLayout col = Theme.col(act);
        col.addView(Theme.text(act, "Carica quotidiana", 14.5f, Theme.TEXT, Theme.medium()));
        col.addView(Theme.text(act, "Una frase e i tuoi progressi, ogni giorno", 12.5f,
                Theme.MUTED, Theme.regular()), Theme.margins(Theme.matchW(), act, 0, 2, 0, 0));
        switchRow.addView(col, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        Switch sw = new Switch(act);
        sw.setChecked(p.reminderOn());
        sw.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.CompoundButton b, boolean on) {
                p.setReminderOn(on);
                Notifications.scheduleAll(act);
                Ui.toast(act, on ? "Carica quotidiana attiva" : "Carica quotidiana disattivata");
            }
        });
        switchRow.addView(sw);
        rem.addView(switchRow, Theme.matchW());

        final TextView timeRow = Theme.text(act,
                "\u23F0  Ogni giorno alle " + String.format(Fmt.IT, "%02d:%02d",
                        p.reminderHour(), p.reminderMinute()), 14.5f, Theme.GREEN, Theme.medium());
        timeRow.setPadding(0, Theme.dp(act, 10), 0, Theme.dp(act, 4));
        timeRow.setClickable(true);
        timeRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(act, android.R.style.Theme_Material_Dialog_Alert,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int h, int m) {
                                p.setReminderTime(h, m);
                                Notifications.scheduleAll(act);
                                timeRow.setText("\u23F0  Ogni giorno alle "
                                        + String.format(Fmt.IT, "%02d:%02d", h, m));
                            }
                        }, p.reminderHour(), p.reminderMinute(), true).show();
            }
        });
        rem.addView(timeRow, Theme.matchW());
        rem.addView(Theme.divider(act));

        // avvisi dei traguardi
        LinearLayout goalRow = Theme.row(act);
        LinearLayout gcol = Theme.col(act);
        gcol.addView(Theme.text(act, "Avvisi dei traguardi", 14.5f, Theme.TEXT, Theme.medium()));
        gcol.addView(Theme.text(act, "Ti avviso quando sblocchi un obiettivo o quando il corpo "
                        + "raggiunge una tappa di recupero", 12.5f, Theme.MUTED, Theme.regular()),
                Theme.margins(Theme.matchW(), act, 0, 2, 0, 0));
        goalRow.addView(gcol, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        final TextView nextNotif = Theme.text(act, "", 12.5f, Theme.GREEN, Theme.medium());
        Switch swGoals = new Switch(act);
        swGoals.setChecked(p.milestoneNotifOn());
        swGoals.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.CompoundButton b, boolean on) {
                p.setMilestoneNotifOn(on);
                Notifications.scheduleAll(act);
                nextNotif.setText(nextNotifText());
                Ui.toast(act, on ? "Avvisi dei traguardi attivi" : "Avvisi dei traguardi disattivati");
            }
        });
        goalRow.addView(swGoals);
        rem.addView(goalRow, Theme.matchW());

        nextNotif.setText(nextNotifText());
        nextNotif.setPadding(0, Theme.dp(act, 10), 0, Theme.dp(act, 4));
        rem.addView(nextNotif, Theme.matchW());
        rem.addView(Theme.divider(act));

        TextView test = Theme.text(act, "\uD83D\uDD14  Invia una notifica di prova", 14.5f,
                Theme.BLUE, Theme.medium());
        test.setPadding(0, Theme.dp(act, 6), 0, Theme.dp(act, 6));
        test.setClickable(true);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Notifications.allowed(act)) {
                    act.enableNotifications();
                    return;
                }
                Notifications.showTest(act);
                Ui.toast(act, "Notifica inviata");
            }
        });
        rem.addView(test, Theme.matchW());

        c.addView(rem, Theme.matchW());
        c.addView(Theme.space(act, 22));

        // ------------------------------------------------------ statistiche
        c.addView(Cards.sectionTitle(act, "Statistiche personali"), Theme.matchW());
        c.addView(Theme.space(act, 10));
        LinearLayout stats = Theme.card(act);
        addStat(stats, "🏅", "Record personale", Fmt.human(p.bestStreak()));
        addStat(stats, "💪", "Voglie superate", String.valueOf(p.cravingsResisted()));
        addStat(stats, "🔁", "Ripartenze", p.relapses() == 0
                ? "Nessuna, sei partito e non ti sei fermato" : String.valueOf(p.relapses()));
        addStat(stats, "📦", "Pacchetti non comprati", Fmt.num(p.packsAvoided(), 1));
        addStat(stats, "🧪", "Nicotina evitata", Fmt.num(p.nicotineGrams(), 2) + " g");
        addStat(stats, "🌍", "CO₂ non emessa", Fmt.num(p.co2Kg(), 1) + " kg");
        addStat(stats, "📅", "Usi fumINO da", Fmt.human(
                System.currentTimeMillis() - p.firstRun()));
        c.addView(stats, Theme.matchW());
        c.addView(Theme.space(act, 22));

        // ----------------------------------------------------------- azioni
        TextView share = Theme.buttonGradient(act, "↑  Condividi i miei progressi",
                Theme.GREEN, Theme.BLUE);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.shareProgress();
            }
        });
        c.addView(share, Theme.matchW());
        c.addView(Theme.space(act, 10));

        TextView relapse = Theme.buttonOutline(act, "🔁  Ho fumato, voglio ripartire",
                Theme.GOLD);
        relapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relapseDialog();
            }
        });
        c.addView(relapse, Theme.matchW());
        c.addView(Theme.space(act, 10));

        TextView reset = Theme.buttonOutline(act, "🗑️  Azzera tutti i dati", Theme.RED);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ui.dialog(act, "⚠️", "Azzerare tutto?",
                        "Perderai contatore, obiettivi, voglie superate e motivi. "
                                + "L'operazione non si può annullare.",
                        "Sì, azzera tutto", new Ui.Action() {
                            @Override
                            public void run() {
                                p.resetAll();
                                Intent i = new Intent(act, SetupActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                act.startActivity(i);
                                act.finish();
                            }
                        }, "Annulla", null);
            }
        });
        c.addView(reset, Theme.matchW());
        c.addView(Theme.space(act, 24));

        TextView about = Theme.text(act, "fumINO 1.3 • fatto per chi vuole smettere davvero\n"
                + "Nessun account, nessuna pubblicità, nessun dato che esce dal telefono.\n"
                + "Le informazioni sulla salute seguono le linee guida OMS, NHS e American Cancer Society "
                + "e non sostituiscono il parere del medico.", 11.5f, Theme.DIM, Theme.regular());
        about.setGravity(android.view.Gravity.CENTER);
        c.addView(about, Theme.matchW());

        return Ui.screen(act, c);
    }

    /** Descrive il prossimo avviso in programma. */
    private String nextNotifText() {
        if (!p.milestoneNotifOn()) return "Nessun avviso in programma";
        long now = System.currentTimeMillis();
        String title = null;
        long when = Long.MAX_VALUE;
        Goal g = Goal.next(p);
        if (g != null && g.when(p) > now && g.when(p) < when) {
            when = g.when(p);
            title = "\uD83C\uDFC6 " + g.title;
        }
        Health h = Health.next(p.elapsed());
        if (h != null) {
            long w = h.date(p.quitAt());
            if (w > now && w < when) {
                when = w;
                title = h.emoji + " " + h.title;
            }
        }
        if (title == null) return "Hai raggiunto tutti i traguardi previsti";
        return "Prossimo avviso: " + title + " \u00B7 " + Fmt.dateTime(when);
    }

    private void addStat(LinearLayout box, String emoji, String label, String value) {
        if (box.getChildCount() > 0) box.addView(Theme.divider(act));
        LinearLayout r = Theme.row(act);
        r.addView(Theme.text(act, emoji, 16, Theme.TEXT, Theme.regular()));
        LinearLayout col = Theme.col(act);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp.leftMargin = Theme.dp(act, 12);
        col.addView(Theme.text(act, label, 13, Theme.MUTED, Theme.regular()));
        col.addView(Theme.text(act, value, 14.5f, Theme.TEXT, Theme.bold()),
                Theme.margins(Theme.matchW(), act, 0, 2, 0, 0));
        r.addView(col, lp);
        box.addView(r, Theme.matchW());
    }

    // ---------------------------------------------------------- modifiche

    private interface ValueCallback {
        void onValue(String s);
    }

    private void editName() {
        LinearLayout card = Theme.col(act);
        card.addView(Theme.text(act, "Come ti chiami?", 20, Theme.TEXT, Theme.bold()), Theme.matchW());
        card.addView(Theme.space(act, 12));
        final EditText e = Theme.input(act, "Il tuo nome", p.displayName(),
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        card.addView(e, Theme.matchW());
        card.addView(Theme.space(act, 14));
        final Dialog d = Ui.custom(act, card);
        TextView ok = Theme.buttonGradient(act, "Salva", Theme.GREEN, Theme.BLUE);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p.setName(e.getText().toString());
                d.dismiss();
                act.refreshAll();
            }
        });
        card.addView(ok, Theme.matchW());
    }

    private void editNumber(String title, String hint, String value, final boolean decimal,
                            final ValueCallback cb) {
        LinearLayout card = Theme.col(act);
        card.addView(Theme.text(act, title, 20, Theme.TEXT, Theme.bold()), Theme.matchW());
        card.addView(Theme.space(act, 6));
        card.addView(Theme.body(act, hint), Theme.matchW());
        card.addView(Theme.space(act, 12));
        final EditText e = Theme.input(act, title, value, decimal
                ? (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                : InputType.TYPE_CLASS_NUMBER);
        card.addView(e, Theme.matchW());
        card.addView(Theme.space(act, 14));
        final Dialog d = Ui.custom(act, card);
        TextView ok = Theme.buttonGradient(act, "Salva", Theme.GREEN, Theme.BLUE);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb.onValue(e.getText().toString());
                d.dismiss();
                act.refreshAll();
            }
        });
        card.addView(ok, Theme.matchW());
    }

    private void pickQuitDate() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(p.quitAt());
        DatePickerDialog dp = new DatePickerDialog(act, android.R.style.Theme_Material_Dialog_Alert,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int y, int m, int d) {
                        cal.set(Calendar.YEAR, y);
                        cal.set(Calendar.MONTH, m);
                        cal.set(Calendar.DAY_OF_MONTH, d);
                        new TimePickerDialog(act, android.R.style.Theme_Material_Dialog_Alert,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker v, int h, int min) {
                                        cal.set(Calendar.HOUR_OF_DAY, h);
                                        cal.set(Calendar.MINUTE, min);
                                        cal.set(Calendar.SECOND, 0);
                                        long t = Math.min(cal.getTimeInMillis(),
                                                System.currentTimeMillis());
                                        p.setQuitAt(t);
                                        act.checkNewGoals(false);
                                        act.refreshAll();
                                    }
                                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dp.getDatePicker().setMaxDate(System.currentTimeMillis());
        dp.show();
    }

    private void relapseDialog() {
        Ui.dialog(act, "🫂", "Nessun giudizio",
                "Una ricaduta non cancella quello che hai imparato: in media servono più tentativi, "
                        + "e ogni tentativo aumenta le probabilità di farcela.\n\nIl tuo record di "
                        + Fmt.human(p.bestStreak()) + " resta salvato. Vuoi far ripartire il contatore da adesso?",
                "Sì, riparto adesso", new Ui.Action() {
                    @Override
                    public void run() {
                        p.relapse(System.currentTimeMillis());
                        act.refreshAll();
                        Ui.dialog(act, "🌱", "Si riparte",
                                "Giorno uno, di nuovo. Questa volta hai in più tutto quello che "
                                        + "hai imparato l'ultima volta.", "Andiamo", null, null, null);
                    }
                },
                "No, ho resistito", null);
    }

    @Override
    public void refresh() {
    }
}
