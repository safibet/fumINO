package com.fumino.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Riceve la sveglia quotidiana e mostra la notifica. */
public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        try {
            Notifications.showDaily(c);
        } catch (Exception ignored) {
        }
    }
}
