package com.fumino.app;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Formattazione di numeri, valute, durate e date in italiano. */
public final class Fmt {

    public static final Locale IT = Locale.ITALY;
    private static final DecimalFormatSymbols SYM = new DecimalFormatSymbols(IT);

    private Fmt() {
    }

    public static String money(double v) {
        DecimalFormat df = new DecimalFormat(v >= 1000 ? "#,##0" : "#,##0.00", SYM);
        return df.format(v) + " €";
    }

    public static String moneyExact(double v) {
        return new DecimalFormat("#,##0.00", SYM).format(v) + " €";
    }

    public static String num(double v, int decimals) {
        StringBuilder p = new StringBuilder("#,##0");
        if (decimals > 0) {
            p.append('.');
            for (int i = 0; i < decimals; i++) p.append('0');
        }
        return new DecimalFormat(p.toString(), SYM).format(v);
    }

    public static String intNum(double v) {
        return new DecimalFormat("#,##0", SYM).format(Math.floor(v));
    }

    /** "12g 04h 23m 11s" — contatore vivo. */
    public static String counter(long ms) {
        if (ms < 0) ms = 0;
        long s = ms / 1000L;
        long d = s / 86400L;
        long h = (s % 86400L) / 3600L;
        long m = (s % 3600L) / 60L;
        long sec = s % 60L;
        if (d > 0) return String.format(IT, "%dg %02dh %02dm %02ds", d, h, m, sec);
        if (h > 0) return String.format(IT, "%dh %02dm %02ds", h, m, sec);
        return String.format(IT, "%dm %02ds", m, sec);
    }

    /** "3 mesi e 12 giorni", "5 ore e 20 minuti", ... */
    public static String human(long ms) {
        if (ms < 0) ms = 0;
        long min = ms / 60000L;
        if (min < 1) return "meno di un minuto";
        if (min < 60) return plural(min, "minuto", "minuti");
        long hours = min / 60;
        if (hours < 24) {
            long rm = min % 60;
            return plural(hours, "ora", "ore") + (rm > 0 ? " e " + plural(rm, "minuto", "minuti") : "");
        }
        long days = hours / 24;
        if (days < 31) {
            long rh = hours % 24;
            return plural(days, "giorno", "giorni") + (rh > 0 ? " e " + plural(rh, "ora", "ore") : "");
        }
        long months = (long) Math.floor(days / 30.4375);
        if (months < 12) {
            long rd = days - (long) Math.floor(months * 30.4375);
            return plural(months, "mese", "mesi") + (rd > 0 ? " e " + plural(rd, "giorno", "giorni") : "");
        }
        long years = (long) Math.floor(days / 365.25);
        long rmo = (long) Math.floor((days - years * 365.25) / 30.4375);
        return plural(years, "anno", "anni") + (rmo > 0 ? " e " + plural(rmo, "mese", "mesi") : "");
    }

    /** Durata compatta: "2g 5h", "48 min". */
    public static String compact(long ms) {
        if (ms < 0) ms = 0;
        long min = ms / 60000L;
        if (min < 60) return min + " min";
        long h = min / 60;
        if (h < 48) return h + "h " + (min % 60) + "m";
        long d = h / 24;
        if (d < 60) return d + "g " + (h % 24) + "h";
        long mo = (long) Math.floor(d / 30.4375);
        if (mo < 24) return mo + " mesi";
        return num(d / 365.25, 1) + " anni";
    }

    public static String plural(long n, String one, String many) {
        return n + " " + (n == 1 ? one : many);
    }

    public static String date(long t) {
        return new SimpleDateFormat("d MMM yyyy", IT).format(new Date(t));
    }

    public static String dateLong(long t) {
        return new SimpleDateFormat("EEEE d MMMM yyyy", IT).format(new Date(t));
    }

    public static String dateTime(long t) {
        return new SimpleDateFormat("d MMM yyyy '•' HH:mm", IT).format(new Date(t));
    }

    public static String time(long t) {
        return new SimpleDateFormat("HH:mm", IT).format(new Date(t));
    }

    /** Tempo di vita guadagnato, in parole. */
    public static String life(double minutes) {
        return human((long) (minutes * 60000L));
    }

    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase(IT) + s.substring(1);
    }
}
