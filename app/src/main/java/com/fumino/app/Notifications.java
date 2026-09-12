package com.fumino.app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Notifiche dell'app:
 * - la carica quotidiana all'ora scelta;
 * - un avviso automatico quando sblocchi un obiettivo o quando il corpo
 *   raggiunge una tappa di recupero, anche ad app chiusa.
 */
public final class Notifications {

    public static final String CHANNEL_DAILY = "fumino_daily";
    public static final String CHANNEL_GOALS = "fumino_goals";

    public static final String ACTION_DAILY = "com.fumino.app.DAILY";
    public static final String ACTION_MILESTONE = "com.fumino.app.MILESTONE";

    private static final String EXTRA_KIND = "kind";
    private static final String EXTRA_ID = "id";

    private static final int REQ_DAILY = 1001;
    private static final int REQ_MILESTONE_BASE = 3000;
    private static final int NOTIF_DAILY = 2001;

    /** Quante tappe future teniamo programmate per volta. */
    private static final int LOOKAHEAD = 8;

    private Notifications() {
    }

    // ------------------------------------------------------------- canali

    public static void ensureChannels(Context c) {
        if (Build.VERSION.SDK_INT < 26) return;
        NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        NotificationChannel daily = new NotificationChannel(CHANNEL_DAILY, "Carica quotidiana",
                NotificationManager.IMPORTANCE_DEFAULT);
        daily.setDescription("Una frase motivazionale al giorno con i tuoi progressi");
        daily.enableVibration(false);
        nm.createNotificationChannel(daily);

        NotificationChannel goals = new NotificationChannel(CHANNEL_GOALS, "Obiettivi e salute",
                NotificationManager.IMPORTANCE_HIGH);
        goals.setDescription("Ti avvisa quando sblocchi un obiettivo o il corpo raggiunge una tappa");
        goals.enableVibration(true);
        nm.createNotificationChannel(goals);
    }

    /** Programma tutto: carica quotidiana e avvisi dei traguardi. */
    public static void scheduleAll(Context c) {
        ensureChannels(c);
        schedule(c);
        scheduleMilestones(c);
    }

    // --------------------------------------------------- carica quotidiana

    private static PendingIntent dailyIntent(Context c) {
        Intent i = new Intent(c, ReminderReceiver.class).setAction(ACTION_DAILY);
        return PendingIntent.getBroadcast(c, REQ_DAILY, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    /** Programma (o annulla) il promemoria giornaliero. */
    public static void schedule(Context c) {
        ensureChannels(c);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        PendingIntent pi = dailyIntent(c);
        Prefs p = new Prefs(c);
        if (!p.reminderOn()) {
            am.cancel(pi);
            return;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, p.reminderHour());
        cal.set(Calendar.MINUTE, p.reminderMinute());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
    }

    // ------------------------------------------------ avvisi dei traguardi

    /** Una tappa futura da annunciare. */
    private static class Upcoming {
        final String key;   // "goal:t_1w" oppure "health:5"
        final long when;

        Upcoming(String key, long when) {
            this.key = key;
            this.when = when;
        }
    }

    private static List<Upcoming> upcoming(Prefs p) {
        long now = System.currentTimeMillis();
        List<Upcoming> out = new ArrayList<>();
        for (Goal g : Goal.all()) {
            if (g.reached(p)) continue;
            long w = g.when(p);
            if (w > now) out.add(new Upcoming("goal:" + g.id, w));
        }
        List<Health> hs = Health.all();
        for (int i = 0; i < hs.size(); i++) {
            long w = hs.get(i).date(p.quitAt());
            if (w > now) out.add(new Upcoming("health:" + i, w));
        }
        // ordinamento per data (lista corta: bastano due cicli)
        for (int i = 0; i < out.size(); i++) {
            for (int j = i + 1; j < out.size(); j++) {
                if (out.get(j).when < out.get(i).when) {
                    Upcoming t = out.get(i);
                    out.set(i, out.get(j));
                    out.set(j, t);
                }
            }
        }
        return out.size() > LOOKAHEAD ? out.subList(0, LOOKAHEAD) : out;
    }

    /** Codice richiesta stabile: deve coincidere fra programmazione e annullamento. */
    private static int req(String key) {
        return REQ_MILESTONE_BASE + Math.abs(key.hashCode() % 1000);
    }

    private static PendingIntent milestoneIntent(Context c, String key) {
        String[] parts = key.split(":", 2);
        Intent i = new Intent(c, ReminderReceiver.class)
                .setAction(ACTION_MILESTONE)
                .setData(Uri.parse("fumino://traguardo/" + key))
                .putExtra(EXTRA_KIND, parts[0])
                .putExtra(EXTRA_ID, parts.length > 1 ? parts[1] : "");
        return PendingIntent.getBroadcast(c, req(key), i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    /** Riprogramma gli avvisi per i prossimi traguardi. */
    public static void scheduleMilestones(Context c) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        Prefs p = new Prefs(c);

        // prima cancelliamo quelli già programmati
        for (String key : p.scheduledMilestones()) {
            am.cancel(milestoneIntent(c, key));
        }
        p.setScheduledMilestones(new HashSet<String>());

        if (!p.isSetupDone() || !p.milestoneNotifOn()) return;

        Set<String> keys = new HashSet<>();
        for (Upcoming u : upcoming(p)) {
            PendingIntent pi = milestoneIntent(c, u.key);
            // finestra di 10 minuti: non serve il permesso per le sveglie esatte
            am.setWindow(AlarmManager.RTC_WAKEUP, u.when, 10 * 60 * 1000L, pi);
            keys.add(u.key);
        }
        p.setScheduledMilestones(keys);
    }

    /** Mostra l'avviso di un traguardo raggiunto e riprogramma i successivi. */
    public static void showMilestone(Context c, Intent intent) {
        Prefs p = new Prefs(c);
        if (!p.isSetupDone() || !p.milestoneNotifOn()) return;
        ensureChannels(c);

        String kind = intent.getStringExtra(EXTRA_KIND);
        String id = intent.getStringExtra(EXTRA_ID);
        String title, body, big;
        int tab;

        if ("health".equals(kind)) {
            int idx;
            try {
                idx = Integer.parseInt(id);
            } catch (Exception e) {
                return;
            }
            List<Health> all = Health.all();
            if (idx < 0 || idx >= all.size()) return;
            Health h = all.get(idx);
            title = h.emoji + " " + h.title;
            body = "Sono passate " + h.when + " dall'ultima sigaretta.";
            big = body + "\n\n" + h.detail;
            tab = 1;
        } else {
            Goal g = Goal.byId(id);
            if (g == null) return;
            title = "🏆 Obiettivo raggiunto: " + g.title;
            body = g.desc;
            big = g.desc + "\n\n" + Fmt.human(p.elapsed()) + " senza fumare · "
                    + Fmt.intNum(p.cigsAvoided()) + " sigarette evitate · "
                    + Fmt.money(p.moneySaved()) + " risparmiati\n"
                    + Goal.reachedCount(p) + " obiettivi su " + Goal.all().size() + " completati.";
            tab = 2;
        }

        notify(c, CHANNEL_GOALS, Math.abs(("m" + kind + id).hashCode() % 100000) + 10000,
                title, body, big, tab, true);

        // armiamo i prossimi
        scheduleMilestones(c);
    }

    // --------------------------------------------------------- costruzione

    /** Costruisce e mostra la notifica del giorno. */
    public static void showDaily(Context c) {
        Prefs p = new Prefs(c);
        if (!p.isSetupDone() || !p.reminderOn()) return;
        ensureChannels(c);

        String title = Fmt.capitalize(Fmt.human(p.elapsed())) + " senza fumare 💪";
        StringBuilder body = new StringBuilder(Motivation.quoteOfTheDay());
        body.append("\n\n").append(Fmt.intNum(p.cigsAvoided())).append(" sigarette evitate · ")
                .append(Fmt.money(p.moneySaved())).append(" risparmiati");

        Goal g = Goal.next(p);
        if (g != null) {
            long w = g.when(p);
            body.append("\nProssimo obiettivo: ").append(g.title);
            if (w > 0) body.append(" tra ").append(Fmt.human(w - System.currentTimeMillis()));
        }
        Health h = Health.next(p.elapsed());
        if (h != null) {
            long w = h.date(p.quitAt());
            body.append("\nProssimo recupero: ").append(h.title)
                    .append(" tra ").append(Fmt.human(w - System.currentTimeMillis()));
        }

        notify(c, CHANNEL_DAILY, NOTIF_DAILY, title, Motivation.quoteOfTheDay(),
                body.toString(), 0, false);
    }

    /** Notifica di prova, per controllare che i permessi siano a posto. */
    public static void showTest(Context c) {
        ensureChannels(c);
        Prefs p = new Prefs(c);
        notify(c, CHANNEL_GOALS, 9999, "🔔 Le notifiche funzionano",
                "Riceverai un avviso a ogni traguardo.",
                "Riceverai un avviso quando sblocchi un obiettivo e quando il tuo corpo "
                        + "raggiunge una tappa di recupero.\n\nIn questo momento: "
                        + Fmt.human(p.elapsed()) + " senza fumare.", 2, false);
    }

    private static void notify(Context c, String channel, int id, String title, String text,
                               String big, int tab, boolean alert) {
        Intent open = new Intent(c, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra("tab", tab);
        PendingIntent pi = PendingIntent.getActivity(c, 100 + tab, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder b = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(c, channel)
                : new Notification.Builder(c);
        b.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new Notification.BigTextStyle().bigText(big))
                .setAutoCancel(true)
                .setColor(Theme.GREEN)
                .setContentIntent(pi);
        if (Build.VERSION.SDK_INT < 26) {
            b.setPriority(alert ? Notification.PRIORITY_HIGH : Notification.PRIORITY_DEFAULT);
            if (alert) b.setDefaults(Notification.DEFAULT_VIBRATE);
        }

        NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            try {
                nm.notify(id, b.build());
            } catch (SecurityException ignored) {
                // permesso notifiche non concesso
            }
        }
    }

    /** true se l'utente ha concesso il permesso di notifica (sempre vero prima di Android 13). */
    public static boolean allowed(Context c) {
        if (Build.VERSION.SDK_INT >= 33) {
            return c.checkSelfPermission("android.permission.POST_NOTIFICATIONS")
                    == android.content.pm.PackageManager.PERMISSION_GRANTED;
        }
        NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        return nm == null || nm.areNotificationsEnabled();
    }
}
