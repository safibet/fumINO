package com.fumino.app;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

/** Barra di progresso arrotondata con sfumatura. */
public class BarView extends View {

    private final Paint track = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF box = new RectF();
    private float progress = 0f;
    private int colorA = Theme.GREEN, colorB = Theme.BLUE;
    private ValueAnimator anim;

    public BarView(Context c) {
        this(c, 8f);
    }

    public BarView(Context c, float heightDp) {
        super(c);
        track.setColor(0x16FFFFFF);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, Theme.dp(c, heightDp));
        setLayoutParams(p);
    }

    public void setColors(int a, int b) {
        colorA = a;
        colorB = b;
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
        anim.setDuration(700);
        anim.setInterpolator(new DecelerateInterpolator(1.5f));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator a) {
                progress = (Float) a.getAnimatedValue();
                invalidate();
            }
        });
        anim.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float h = getHeight(), w = getWidth();
        float r = h / 2f;
        box.set(0, 0, w, h);
        canvas.drawRoundRect(box, r, r, track);
        float fw = Math.max(progress > 0 ? h : 0, w * progress);
        if (fw > 0) {
            fill.setShader(new LinearGradient(0, 0, Math.max(fw, 1), 0,
                    colorA, colorB, Shader.TileMode.CLAMP));
            box.set(0, 0, fw, h);
            canvas.drawRoundRect(box, r, r, fill);
        }
    }
}
