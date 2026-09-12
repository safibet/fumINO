package com.fumino.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stato dell'app: dati del fumatore, statistiche derivate e diario.
 * Tutto salvato in SharedPreferences, nessun dato esce dal telefono.
 */
public class Prefs {

    private static final String FILE = "fumino";

    private static final String K_SETUP = "setup_done";
    private static final String K_QUIT = "quit_at";
    private static final String K_CIGS_DAY = "cigs_day";
    private static final String K_CIGS_PACK = "cigs_pack";
    private static final String K_PRICE = "price_pack";
    private static final String K_NAME = "name";
    private static final String K_REASONS = "reasons";
    private static final String K_CRAVINGS = "cravings";
    private static final String K_CRAVING_LOG = "craving_log";
    private static final String K_RELAPSES = "relapses";
    private static final String K_BEST = "best_streak";
    private static final String K_SEEN_GOALS = "seen_goals";
    private static final String K_REMINDER = "reminder_on";
    private static final String K_REMINDER_H = "reminder_h";
    private static final String K_REMINDER_M = "reminder_m";
    private static final String K_FIRST_RUN = "first_run";
    private static final String K_PLEDGE = "pledge";

    /** Minuti di vita guadagnati per ogni sigaretta non fumata (stima OMS/BMJ). */
    public static final double LIFE_MIN_PER_CIG = 11.0;
    /** Catrame medio inalato per sigaretta, in mg. */
    public static final double TAR_MG_PER_CIG = 10.0;
    /** Nicotina media assorbita per sigaretta, in mg. */
    public static final double NICOTINE_MG_PER_CIG = 1.1;
    /** CO2 emessa nel ciclo di vita di una sigaretta, in grammi. */
    public static final double CO2_G_PER_CIG = 14.0;

    private final SharedPreferences sp;

    public Prefs(Context ctx) {
        sp = ctx.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    // ---------------------------------------------------------------- setup

    public boolean isSetupDone() {
        return sp.getBoolean(K_SETUP, false);
    }

    public void setSetupDone(boolean v) {
        sp.edit().putBoolean(K_SETUP, v).apply();
    }

    public long quitAt() {
        return sp.getLong(K_QUIT, System.currentTimeMillis());
    }

    public void setQuitAt(long t) {
        sp.edit().putLong(K_QUIT, t).apply();
    }

    public int cigsPerDay() {
        return sp.getInt(K_CIGS_DAY, 20);
    }

    public void setCigsPerDay(int v) {
        sp.edit().putInt(K_CIGS_DAY, Math.max(1, v)).apply();
    }

    public int cigsPerPack() {
        return sp.getInt(K_CIGS_PACK, 20);
    }

    public void setCigsPerPack(int v) {
        sp.edit().putInt(K_CIGS_PACK, Math.max(1, v)).apply();
    }

    public float pricePerPack() {
        return sp.getFloat(K_PRICE, 5.50f);
    }

    public void setPricePerPack(float v) {
        sp.edit().putFloat(K_PRICE, Math.max(0.01f, v)).apply();
    }

    public String name() {
        return sp.getString(K_NAME, "");
    }

    public void setName(String v) {
        sp.edit().putString(K_NAME, v == null ? "" : v.trim()).apply();
    }

    // -------------------------------------------------------------- motivi

    public List<String> reasons() {
        String raw = sp.getString(K_REASONS, "");
        List<String> out = new ArrayList<>();
        if (raw != null && !raw.isEmpty()) {
            for (String s : raw.split("\n")) {
                if (!s.trim().isEmpty()) out.add(s.trim());
            }
        }
        return out;
    }

    public void setReasons(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (s == null || s.trim().isEmpty()) continue;
            if (sb.length() > 0) sb.append('\n');
            sb.append(s.trim().replace("\n", " "));
        }
        sp.edit().putString(K_REASONS, sb.toString()).apply();
    }

    public void addReason(String s) {
        List<String> l = reasons();
        l.add(s);
        setReasons(l);
    }

    public void removeReason(int i) {
        List<String> l = reasons();
        if (i >= 0 && i < l.size()) {
            l.remove(i);
            setReasons(l);
        }
    }

    public String pledge() {
        return sp.getString(K_PLEDGE, "");
    }

    public void setPledge(String s) {
        sp.edit().putString(K_PLEDGE, s == null ? "" : s.trim()).apply();
    }

    // ------------------------------------------------------------- voglie

    public int cravingsResisted() {
        return sp.getInt(K_CRAVINGS, 0);
    }

    public void addCravingResisted() {
        long now = System.currentTimeMillis();
        List<Long> log = cravingLog();
        log.add(0, now);
        while (log.size() > 200) log.remove(log.size() - 1);
        StringBuilder sb = new StringBuilder();
        for (Long l : log) {
            if (sb.length() > 0) sb.append(',');
            sb.append(l);
        }
        sp.edit()
                .putInt(K_CRAVINGS, cravingsResisted() + 1)
                .putString(K_CRAVING_LOG, sb.toString())
                .apply();
    }

    public List<Long> cravingLog() {
        String raw = sp.getString(K_CRAVING_LOG, "");
        List<Long> out = new ArrayList<>();
        if (raw != null && !raw.isEmpty()) {
            for (String s : raw.split(",")) {
                try {
                    out.add(Long.parseLong(s.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return out;
    }

    /** Voglie superate nelle ultime 24 ore. */
    public int cravingsToday() {
        long limit = System.currentTimeMillis() - 86400000L;
        int n = 0;
        for (Long l : cravingLog()) if (l >= limit) n++;
        return n;
    }

    // ---------------------------------------------------------- ricadute

    public int relapses() {
        return sp.getInt(K_RELAPSES, 0);
    }

    public long bestStreak() {
        return Math.max(sp.getLong(K_BEST, 0L), elapsed());
    }

    /** Registra una ricaduta: salva il record e fa ripartire il contatore. */
    public void relapse(long newStart) {
        long best = Math.max(sp.getLong(K_BEST, 0L), elapsed());
        sp.edit()
                .putLong(K_BEST, best)
                .putInt(K_RELAPSES, relapses() + 1)
                .putLong(K_QUIT, newStart)
                .putStringSet(K_SEEN_GOALS, new HashSet<String>())
                .apply();
    }

    /** Azzera tutto e riparte da zero. */
    public void resetAll() {
        sp.edit().clear().apply();
    }

    // ------------------------------------------------------- obiettivi visti

    public Set<String> seenGoals() {
        return new HashSet<>(sp.getStringSet(K_SEEN_GOALS, Collections.<String>emptySet()));
    }

    public void markGoalsSeen(Set<String> ids) {
        sp.edit().putStringSet(K_SEEN_GOALS, new HashSet<>(ids)).apply();
    }

    // -------------------------------------------------------- promemoria

    public boolean reminderOn() {
        return sp.getBoolean(K_REMINDER, true);
    }

    public void setReminderOn(boolean v) {
        sp.edit().putBoolean(K_REMINDER, v).apply();
    }

    public int reminderHour() {
        return sp.getInt(K_REMINDER_H, 9);
    }

    public int reminderMinute() {
        return sp.getInt(K_REMINDER_M, 0);
    }

    public void setReminderTime(int h, int m) {
        sp.edit().putInt(K_REMINDER_H, h).putInt(K_REMINDER_M, m).apply();
    }

    public long firstRun() {
        long v = sp.getLong(K_FIRST_RUN, 0L);
        if (v == 0L) {
            v = System.currentTimeMillis();
            sp.edit().putLong(K_FIRST_RUN, v).apply();
        }
        return v;
    }

    // -------------------------------------------------------- statistiche

    public long elapsed() {
        return Math.max(0L, System.currentTimeMillis() - quitAt());
    }

    public double days() {
        return elapsed() / 86400000.0;
    }

    public double cigsAvoided() {
        return days() * cigsPerDay();
    }

    public double pricePerCig() {
        return pricePerPack() / (double) cigsPerPack();
    }

    public double moneySaved() {
        return cigsAvoided() * pricePerCig();
    }

    public double moneyPerDay() {
        return cigsPerDay() * pricePerCig();
    }

    /** Minuti di vita guadagnati. */
    public double lifeMinutes() {
        return cigsAvoided() * LIFE_MIN_PER_CIG;
    }

    /** Catrame non inalato, in grammi. */
    public double tarGrams() {
        return cigsAvoided() * TAR_MG_PER_CIG / 1000.0;
    }

    public double nicotineGrams() {
        return cigsAvoided() * NICOTINE_MG_PER_CIG / 1000.0;
    }

    /** CO2 non emessa, in kg. */
    public double co2Kg() {
        return cigsAvoided() * CO2_G_PER_CIG / 1000.0;
    }

    /** Pacchetti non comprati. */
    public double packsAvoided() {
        return cigsAvoided() / cigsPerPack();
    }

    /** Quando sarà risparmiata una certa cifra (timestamp). */
    public long whenMoney(double target) {
        double perMs = moneyPerDay() / 86400000.0;
        if (perMs <= 0) return -1;
        return quitAt() + (long) (target / perMs);
    }

    /** Quando saranno evitate N sigarette (timestamp). */
    public long whenCigs(double target) {
        double perMs = cigsPerDay() / 86400000.0;
        if (perMs <= 0) return -1;
        return quitAt() + (long) (target / perMs);
    }

    public String displayName() {
        String n = name();
        return n == null || n.isEmpty() ? "" : n;
    }

    public List<String> defaultReasons() {
        return new ArrayList<>(Arrays.asList(
                "Voglio respirare bene e avere più energia",
                "Voglio smettere di buttare soldi in fumo",
                "Voglio essere un esempio per chi amo",
                "Voglio riprendermi gusto e olfatto",
                "Voglio vivere più a lungo e in salute"));
    }
}
