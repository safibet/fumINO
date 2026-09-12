package com.fumino.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Riprogramma il promemoria dopo il riavvio del telefono. */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        try {
            Notifications.schedule(c);
        } catch (Exception ignored) {
        }
    }
}
