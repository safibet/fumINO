package com.fumino.app;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/** Anello di progresso con sfumatura e puntino luminoso in testa. */
public class RingView extends View {

    private final Paint track = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint arc = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dot = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF box = new RectF();

    private float progress = 0f;
    private int[] colors = {Theme.GREEN, Theme.BLUE, Theme.GREEN};
    private float strokeDp = 13f;
    private ValueAnimator anim;

    public RingView(Context c) {
        super(c);
        track.setStyle(Paint.Style.STROKE);
        track.setColor(0x14FFFFFF);
        track.setStrokeCap(Paint.Cap.ROUND);
        arc.setStyle(Paint.Style.STROKE);
        arc.setStrokeCap(Paint.Cap.ROUND);
        dot.setStyle(Paint.Style.FILL);
        dot.setColor(0xFFFFFFFF);
        glow.setStyle(Paint.Style.STROKE);
        glow.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setStrokeDp(float v) {
        strokeDp = v;
        invalidate();
    }

    public void setColors(int a, int b) {
        colors = new int[]{a, b, a};
        invalidate();
    }

    public void setProgress(float p, boolean animate) {
        float target = Math.max(0f, Math.min(1f, p));
        if (!animate) {
            if (anim != null) anim.cancel();
            progress = target;
            invalidate();
            return;
        }
        if (anim != null) anim.cancel();
        anim = ValueAnimator.ofFloat(progress, target);
        anim.setDuration(900);
        anim.setInterpolator(new DecelerateInterpolator(1.6f));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator a) {
                progress = (Float) a.getAnimatedValue();
                invalidate();
            }
        });
        anim.start();
    }

    public float getProgress() {
        return progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth(), h = getHeight();
        float stroke = Theme.dp(getContext(), strokeDp);
        float pad = stroke / 2f + Theme.dp(getContext(), 4);
        float size = Math.min(w, h) - pad * 2;
        float cx = w / 2f, cy = h / 2f;
        box.set(cx - size / 2, cy - size / 2, cx + size / 2, cy + size / 2);

        track.setStrokeWidth(stroke);
        canvas.drawArc(box, 0, 360, false, track);

        arc.setStrokeWidth(stroke);
        arc.setShader(new SweepGradient(cx, cy, colors, new float[]{0f, 0.55f, 1f}));

        float sweep = 360f * progress;
        if (sweep > 0.5f) {
            glow.setStrokeWidth(stroke + Theme.dp(getContext(), 7));
            glow.setShader(new SweepGradient(cx, cy, new int[]{
                    Theme.withAlpha(colors[0], 0x30),
                    Theme.withAlpha(colors[1], 0x30),
                    Theme.withAlpha(colors[0], 0x30)}, new float[]{0f, 0.55f, 1f}));
            canvas.drawArc(box, -90, sweep, false, glow);
            canvas.drawArc(box, -90, sweep, false, arc);

            double ang = Math.toRadians(-90 + sweep);
            float dx = cx + (float) Math.cos(ang) * size / 2f;
            float dy = cy + (float) Math.sin(ang) * size / 2f;
            dot.setColor(0x55FFFFFF);
            canvas.drawCircle(dx, dy, stroke * 0.62f, dot);
            dot.setColor(0xFFFFFFFF);
            canvas.drawCircle(dx, dy, stroke * 0.30f, dot);
        }
    }
}
