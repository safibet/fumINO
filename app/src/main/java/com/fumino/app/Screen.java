package com.fumino.app;

import android.view.View;

/** Una scheda dell'app. La vista viene costruita una sola volta e poi aggiornata. */
public abstract class Screen {

    protected final MainActivity act;
    protected final Prefs p;
    private View root;

    public Screen(MainActivity act) {
        this.act = act;
        this.p = act.prefs();
    }

    public View root() {
        if (root == null) root = build();
        return root;
    }

    /** Ricostruisce la vista da zero (dopo modifiche ai dati). */
    public void rebuild() {
        root = null;
    }

    protected abstract View build();

    /** Aggiornamento leggero (chiamato ogni secondo sulla scheda visibile). */
    public void refresh() {
    }

    public boolean needsTick() {
        return false;
    }
}
