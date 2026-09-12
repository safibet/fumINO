package com.fumino.app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

/** Promemoria motivazionale quotidiano. */
public final class Notifications {

    public static final String CHANNEL = "fumino_daily";
    private static final int ALARM_ID = 1001;
    private static final int NOTIF_ID = 2001;

    private Notifications() {
    }

    public static void ensureChannel(Context c) {
        if (Build.VERSION.SDK_INT < 26) return;
        NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        NotificationChannel ch = new NotificationChannel(CHANNEL, "Carica quotidiana",
                NotificationManager.IMPORTANCE_DEFAULT);
        ch.setDescription("Una frase motivazionale al giorno con i tuoi progressi");
        ch.enableVibration(false);
        nm.createNotificationChannel(ch);
    }

    private static PendingIntent alarmIntent(Context c) {
        Intent i = new Intent(c, ReminderReceiver.class);
        return PendingIntent.getBroadcast(c, ALARM_ID, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    /** Programma (o annulla) il promemoria giornaliero. */
    public static void schedule(Context c) {
        ensureChannel(c);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        PendingIntent pi = alarmIntent(c);
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

    /** Costruisce e mostra la notifica del giorno. */
    public static void showDaily(Context c) {
        Prefs p = new Prefs(c);
        if (!p.isSetupDone() || !p.reminderOn()) return;
        ensureChannel(c);

        String title = Fmt.human(p.elapsed()) + " senza fumare 💪";
        StringBuilder body = new StringBuilder();
        body.append(Motivation.quoteOfTheDay());
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
            body.append("\nProssimo recupero: ").append(h.title).append(" (").append(h.when).append(")");
        }

        Intent open = new Intent(c, MainActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(c, 0, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder b;
        if (Build.VERSION.SDK_INT >= 26) {
            b = new Notification.Builder(c, CHANNEL);
        } else {
            b = new Notification.Builder(c);
        }
        b.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(Motivation.quoteOfTheDay())
                .setStyle(new Notification.BigTextStyle().bigText(body.toString()))
                .setAutoCancel(true)
                .setContentIntent(pi);
        if (Build.VERSION.SDK_INT >= 21) {
            b.setColor(Theme.GREEN);
        }

        NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(NOTIF_ID, b.build());
    }
}
