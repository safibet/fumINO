package com.fumino.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Avvio: logo animato e smistamento verso configurazione o schermata principale. */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        LinearLayout root = Theme.col(this);
        root.setBackgroundColor(Theme.BG);
        root.setGravity(Gravity.CENTER);

        TextView logo = Theme.text(this, "🚭", 60, Theme.TEXT, Theme.regular());
        logo.setGravity(Gravity.CENTER);
        root.addView(logo, Theme.matchW());

        TextView name = Theme.text(this, "fumINO", 34, Theme.TEXT, Theme.bold());
        name.setGravity(Gravity.CENTER);
        root.addView(name, Theme.margins(Theme.matchW(), this, 0, 10, 0, 0));

        TextView claim = Theme.text(this, "un respiro alla volta", 14, Theme.GREEN, Theme.medium());
        claim.setGravity(Gravity.CENTER);
        root.addView(claim, Theme.margins(Theme.matchW(), this, 0, 6, 0, 0));

        setContentView(root);

        logo.setAlpha(0f);
        name.setAlpha(0f);
        claim.setAlpha(0f);
        logo.animate().alpha(1f).scaleX(1.06f).scaleY(1.06f).setDuration(520).start();
        name.animate().alpha(1f).setStartDelay(160).setDuration(480).start();
        claim.animate().alpha(1f).setStartDelay(320).setDuration(480).start();

        final View decor = root;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) return;
                Prefs p = new Prefs(SplashActivity.this);
                Intent i = new Intent(SplashActivity.this,
                        p.isSetupDone() ? MainActivity.class : SetupActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 1100);
        decor.setKeepScreenOn(false);
    }
}
