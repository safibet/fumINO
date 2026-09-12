package com.fumino.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Riprogramma notifiche e avvisi dopo il riavvio del telefono. */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        try {
            Notifications.scheduleAll(c);
        } catch (Exception ignored) {
        }
    }
}
