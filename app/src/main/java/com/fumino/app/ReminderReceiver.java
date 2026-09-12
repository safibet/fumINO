package com.fumino.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Riceve le sveglie e mostra la notifica giusta. */
public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        try {
            if (i != null && Notifications.ACTION_MILESTONE.equals(i.getAction())) {
                Notifications.showMilestone(c, i);
            } else {
                Notifications.showDaily(c);
            }
        } catch (Exception ignored) {
        }
    }
}
