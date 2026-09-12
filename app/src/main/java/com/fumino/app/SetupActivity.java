package com.fumino.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/** Prima configurazione: pochi dati, tutti modificabili in seguito. */
public class SetupActivity extends Activity {

    private Prefs p;
    private long quitAt = System.currentTimeMillis();
    private TextView quitLabel;
    private EditText name, perDay, perPack, price;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        p = new Prefs(this);
        p.firstRun();

        LinearLayout c = Theme.col(this);
        c.setBackgroundColor(Theme.BG);

        TextView logo = Theme.text(this, "🚭", 44, Theme.TEXT, Theme.regular());
        logo.setGravity(Gravity.CENTER);
        c.addView(logo, Theme.margins(Theme.matchW(), this, 0, 12, 0, 0));

        TextView title = Theme.text(this, "fumINO", 32, Theme.TEXT, Theme.bold());
        title.setGravity(Gravity.CENTER);
        title.setLetterSpacing(0.02f);
        c.addView(title, Theme.margins(Theme.matchW(), this, 0, 6, 0, 0));

        TextView sub = Theme.text(this,
                "Il tuo viaggio verso la libertà inizia adesso.\nRispondi a quattro domande e partiamo.",
                14, Theme.MUTED, Theme.regular());
        sub.setGravity(Gravity.CENTER);
        c.addView(sub, Theme.margins(Theme.matchW(), this, 0, 10, 0, 0));
        c.addView(Theme.space(this, 26));

        // ------------------------------------------------------------ nome
        c.addView(Theme.label(this, "Come ti chiami (facoltativo)"), Theme.matchW());
        c.addView(Theme.space(this, 8));
        name = Theme.input(this, "Il tuo nome", "",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        c.addView(name, Theme.matchW());
        c.addView(Theme.space(this, 20));

        // ------------------------------------------- ultima sigaretta
        c.addView(Theme.label(this, "Quando hai fumato l'ultima sigaretta?"), Theme.matchW());
        c.addView(Theme.space(this, 8));
        quitLabel = Theme.text(this, "Adesso · " + Fmt.dateTime(quitAt), 15, Theme.GREEN, Theme.bold());
        quitLabel.setPadding(Theme.dp(this, 14), Theme.dp(this, 14), Theme.dp(this, 14), Theme.dp(this, 14));
        quitLabel.setBackground(Theme.roundRect(this, 14, Theme.BG_SOFT, Theme.LINE, 1));
        c.addView(quitLabel, Theme.matchW());
        c.addView(Theme.space(this, 8));

        LinearLayout btns = Theme.row(this);
        TextView nowBtn = Theme.buttonOutline(this, "Proprio adesso", Theme.GREEN);
        nowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitAt = System.currentTimeMillis();
                quitLabel.setText("Adesso · " + Fmt.dateTime(quitAt));
            }
        });
        LinearLayout.LayoutParams w1 = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        w1.rightMargin = Theme.dp(this, 8);
        btns.addView(nowBtn, w1);

        TextView pickBtn = Theme.buttonOutline(this, "Scegli data e ora", Theme.BLUE);
        pickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });
        btns.addView(pickBtn, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        c.addView(btns, Theme.matchW());
        c.addView(Theme.space(this, 20));

        // ------------------------------------------------ sigarette al giorno
        c.addView(Theme.label(this, "Quante sigarette fumavi al giorno?"), Theme.matchW());
        c.addView(Theme.space(this, 8));
        perDay = Theme.input(this, "20", "20", InputType.TYPE_CLASS_NUMBER);
        c.addView(perDay, Theme.matchW());
        c.addView(Theme.space(this, 8));
        LinearLayout quick = Theme.row(this);
        int[] presets = {5, 10, 15, 20, 30, 40};
        for (int n : presets) {
            final int val = n;
            TextView chip = Theme.text(this, String.valueOf(n), 13, Theme.MUTED, Theme.medium());
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(0, Theme.dp(this, 9), 0, Theme.dp(this, 9));
            chip.setBackground(Theme.ripple(Theme.roundRect(this, 12, Theme.CARD, Theme.LINE, 1), 0x22FFFFFF));
            chip.setClickable(true);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    perDay.setText(String.valueOf(val));
                }
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            lp.rightMargin = Theme.dp(this, 6);
            quick.addView(chip, lp);
        }
        c.addView(quick, Theme.matchW());
        c.addView(Theme.space(this, 20));

        // ---------------------------------------------------- pacchetto
        LinearLayout packRow = Theme.row(this);
        packRow.setGravity(Gravity.TOP);

        LinearLayout left = Theme.col(this);
        left.addView(Theme.label(this, "Sigarette per pacchetto"), Theme.matchW());
        left.addView(Theme.space(this, 8));
        perPack = Theme.input(this, "20", "20", InputType.TYPE_CLASS_NUMBER);
        left.addView(perPack, Theme.matchW());
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp1.rightMargin = Theme.dp(this, 8);
        packRow.addView(left, lp1);

        LinearLayout right = Theme.col(this);
        right.addView(Theme.label(this, "Prezzo del pacchetto"), Theme.matchW());
        right.addView(Theme.space(this, 8));
        price = Theme.input(this, "5.50", "5.50",
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        right.addView(price, Theme.matchW());
        packRow.addView(right, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        c.addView(packRow, Theme.matchW());
        c.addView(Theme.space(this, 28));

        TextView go = Theme.buttonGradient(this, "Inizia →", Theme.GREEN, Theme.BLUE);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        c.addView(go, Theme.matchW());
        c.addView(Theme.space(this, 14));

        TextView note = Theme.text(this, "Tutto resta su questo telefono: nessun account, "
                + "nessuna pubblicità, nessun dato inviato.", 11.5f, Theme.DIM, Theme.regular());
        note.setGravity(Gravity.CENTER);
        c.addView(note, Theme.matchW());

        ScrollView sv = Ui.screen(this, c);
        sv.setBackgroundColor(Theme.BG);
        setContentView(sv);
    }

    private void pickDate() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(quitAt);
        DatePickerDialog d = new DatePickerDialog(this, android.R.style.Theme_Material_Dialog_Alert,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int y, int m, int day) {
                        cal.set(Calendar.YEAR, y);
                        cal.set(Calendar.MONTH, m);
                        cal.set(Calendar.DAY_OF_MONTH, day);
                        new TimePickerDialog(SetupActivity.this,
                                android.R.style.Theme_Material_Dialog_Alert,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker v, int h, int min) {
                                        cal.set(Calendar.HOUR_OF_DAY, h);
                                        cal.set(Calendar.MINUTE, min);
                                        cal.set(Calendar.SECOND, 0);
                                        quitAt = Math.min(cal.getTimeInMillis(), System.currentTimeMillis());
                                        quitLabel.setText(Fmt.capitalize(Fmt.dateLong(quitAt))
                                                + " alle " + Fmt.time(quitAt));
                                    }
                                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        d.getDatePicker().setMaxDate(System.currentTimeMillis());
        d.show();
    }

    private void save() {
        int day = parseInt(perDay.getText().toString(), 20);
        int pack = parseInt(perPack.getText().toString(), 20);
        float pr = parseFloat(price.getText().toString(), 5.50f);

        p.setName(name.getText().toString());
        p.setQuitAt(quitAt);
        p.setCigsPerDay(day);
        p.setCigsPerPack(pack);
        p.setPricePerPack(pr);
        p.setSetupDone(true);
        Notifications.schedule(this);

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private static int parseInt(String s, int def) {
        try {
            int v = Integer.parseInt(s.trim());
            return v > 0 ? v : def;
        } catch (Exception e) {
            return def;
        }
    }

    private static float parseFloat(String s, float def) {
        try {
            float v = Float.parseFloat(s.trim().replace(",", ".").replace("€", ""));
            return v > 0 ? v : def;
        } catch (Exception e) {
            return def;
        }
    }
}
