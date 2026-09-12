package com.fumino.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.View;

/** Cerchio che guida la respirazione 4-7-8. */
public class BreathView extends View {

    public interface PhaseListener {
        void onPhase(String name, String hint, int seconds, int cycles);
    }

    private static final long IN = 4000, HOLD = 7000, OUT = 8000;
    private static final long CYCLE = IN + HOLD + OUT;

    private final Paint core = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint halo = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ring = new Paint(Paint.ANTI_ALIAS_FLAG);

    private long start;
    private boolean running;
    private PhaseListener listener;
    private int lastPhase = -1, lastSecond = -1;

    public BreathView(Context c) {
        super(c);
        ring.setStyle(Paint.Style.STROKE);
        ring.setStrokeWidth(Theme.dp(c, 2));
        ring.setColor(0x33FFFFFF);
    }

    public void setPhaseListener(PhaseListener l) {
        listener = l;
    }

    public void start() {
        start = SystemClock.elapsedRealtime();
        running = true;
        postInvalidateOnAnimation();
    }

    public void stop() {
        running = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth(), h = getHeight();
        float cx = w / 2f, cy = h / 2f;
        float max = Math.min(w, h) / 2f - Theme.dp(getContext(), 10);

        long t = running ? (SystemClock.elapsedRealtime() - start) : 0;
        long inCycle = t % CYCLE;
        int cycles = (int) (t / CYCLE);

        float scale;
        int phase;
        int secondsLeft;
        if (inCycle < IN) {
            phase = 0;
            float f = inCycle / (float) IN;
            scale = 0.52f + 0.48f * ease(f);
            secondsLeft = (int) Math.ceil((IN - inCycle) / 1000f);
        } else if (inCycle < IN + HOLD) {
            phase = 1;
            long k = inCycle - IN;
            scale = 1f + 0.012f * (float) Math.sin(k / 420.0);
            secondsLeft = (int) Math.ceil((IN + HOLD - inCycle) / 1000f);
        } else {
            phase = 2;
            float f = (inCycle - IN - HOLD) / (float) OUT;
            scale = 1f - 0.48f * ease(f);
            secondsLeft = (int) Math.ceil((CYCLE - inCycle) / 1000f);
        }

        float r = max * scale;

        halo.setShader(new RadialGradient(cx, cy, Math.max(r * 1.45f, 1f),
                new int[]{Theme.withAlpha(Theme.GREEN, 0x40), Theme.withAlpha(Theme.BLUE, 0x18), 0x00000000},
                new float[]{0f, 0.6f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, r * 1.45f, halo);

        core.setShader(new RadialGradient(cx - r * 0.3f, cy - r * 0.3f, Math.max(r * 1.3f, 1f),
                new int[]{0xFF4ADE9B, 0xFF1E9E8A, 0xFF10617A}, null, Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, r, core);

        canvas.drawCircle(cx, cy, max, ring);

        if (listener != null && (phase != lastPhase || secondsLeft != lastSecond)) {
            lastPhase = phase;
            lastSecond = secondsLeft;
            String name = phase == 0 ? "Inspira" : phase == 1 ? "Trattieni" : "Espira";
            String hint = phase == 0 ? "dal naso, gonfiando la pancia"
                    : phase == 1 ? "senza forzare, resta morbido"
                    : "dalla bocca, lentamente";
            listener.onPhase(name, hint, secondsLeft, cycles);
        }

        if (running) postInvalidateOnAnimation();
    }

    private static float ease(float f) {
        return (float) (1 - Math.cos(Math.min(1f, Math.max(0f, f)) * Math.PI)) / 2f;
    }
}
