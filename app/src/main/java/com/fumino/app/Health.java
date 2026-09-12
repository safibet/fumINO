package com.fumino.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Cronologia del recupero della salute dopo l'ultima sigaretta.
 * Tempi basati sulle linee guida OMS / NHS / American Cancer Society.
 */
public class Health {

    public static final long MIN = 60000L;
    public static final long HOUR = 3600000L;
    public static final long DAY = 86400000L;
    public static final long WEEK = 7 * DAY;
    public static final long MONTH = (long) (30.4375 * DAY);
    public static final long YEAR = (long) (365.25 * DAY);

    public final String emoji;
    public final String title;
    public final String detail;
    public final long after;   // millisecondi dall'ultima sigaretta
    public final String when;  // etichetta del tempo ("20 minuti", "1 anno", ...)

    private Health(String emoji, String when, long after, String title, String detail) {
        this.emoji = emoji;
        this.when = when;
        this.after = after;
        this.title = title;
        this.detail = detail;
    }

    private static final List<Health> ALL = new ArrayList<>();

    static {
        add("❤️", "20 minuti", 20 * MIN, "Battito e pressione tornano normali",
                "La frequenza cardiaca e la pressione del sangue, alzate dalla nicotina, scendono ai valori di una persona che non fuma. Anche le mani e i piedi tornano a scaldarsi.");
        add("🫁", "2 ore", 2 * HOUR, "La circolazione migliora",
                "Il sangue circola meglio nelle estremità. Possono iniziare i primi sintomi di astinenza: è il segno che il corpo sta ripulendo se stesso.");
        add("🪶", "8 ore", 8 * HOUR, "Nicotina ridotta del 93%",
                "Il livello di nicotina nel sangue è quasi azzerato e l'ossigeno risale verso la normalità.");
        add("💨", "12 ore", 12 * HOUR, "Monossido di carbonio eliminato",
                "Il monossido di carbonio esce dal sangue: l'emoglobina torna a trasportare ossigeno puro a tutti gli organi.");
        add("🩺", "24 ore", DAY, "Inizia a calare il rischio di infarto",
                "Dopo un solo giorno il rischio di attacco cardiaco comincia già a ridursi. Il tuo cuore lavora meno sotto sforzo.");
        add("👅", "48 ore", 2 * DAY, "Gusto e olfatto rinascono",
                "Le terminazioni nervose danneggiate ricrescono: i cibi riacquistano sapore e torni a sentire gli odori. La nicotina è completamente fuori dal corpo.");
        add("🫁", "72 ore", 3 * DAY, "Respiro più libero, energia in aumento",
                "I bronchi si rilassano e la capacità polmonare cresce. È spesso il picco dell'astinenza: dopo questo momento si va solo in discesa.");
        add("💪", "1 settimana", WEEK, "Superata la settimana più dura",
                "Chi arriva a 7 giorni ha una probabilità molto più alta di smettere per sempre. Le voglie diventano più rare e più corte.");
        add("🏃", "2 settimane", 2 * WEEK, "Camminare e muoversi diventa facile",
                "Circolazione e funzione polmonare migliorano fino al 30%: salire le scale non toglie più il fiato come prima.");
        add("🧼", "1 mese", MONTH, "Pelle più luminosa, meno tosse",
                "La pelle riceve più ossigeno e appare più sana. Tosse, congestione nasale e affaticamento iniziano a diminuire.");
        add("🫁", "3 mesi", 3 * MONTH, "Polmoni molto più efficienti",
                "La funzione polmonare è migliorata in modo netto e la fertilità aumenta. Le ciglia bronchiali stanno ricrescendo.");
        add("🧹", "9 mesi", 9 * MONTH, "Polmoni puliti, meno infezioni",
                "Le ciglia polmonari sono rigenerate: espellono muco e sporcizia. Tosse e fiato corto sono in gran parte spariti e ti ammali meno.");
        add("💚", "1 anno", YEAR, "Rischio cardiaco dimezzato",
                "Il rischio di malattia coronarica è circa la metà di quello di chi continua a fumare.");
        add("🧠", "5 anni", 5 * YEAR, "Rischio di ictus come chi non fuma",
                "Le arterie si allargano di nuovo. Il rischio di ictus torna simile a quello di chi non ha mai fumato e quello dei tumori di bocca, gola ed esofago è dimezzato.");
        add("🌿", "10 anni", 10 * YEAR, "Rischio di tumore al polmone dimezzato",
                "Il rischio di morire di tumore al polmone è circa la metà di quello di un fumatore. Cala anche il rischio di tumori a laringe e pancreas.");
        add("🏆", "15 anni", 15 * YEAR, "Come se non avessi mai fumato",
                "Il rischio di malattie cardiache e coronariche è pari a quello di una persona che non ha mai acceso una sigaretta.");
    }

    private static void add(String emoji, String when, long after, String title, String detail) {
        ALL.add(new Health(emoji, when, after, title, detail));
    }

    public static List<Health> all() {
        return ALL;
    }

    public boolean done(long elapsed) {
        return elapsed >= after;
    }

    /** Percentuale 0..1 di avvicinamento a questa tappa. */
    public float progress(long elapsed) {
        if (elapsed >= after) return 1f;
        return Math.max(0f, Math.min(1f, (float) ((double) elapsed / (double) after)));
    }

    /** Data in cui la tappa è stata (o sarà) raggiunta. */
    public long date(long quitAt) {
        return quitAt + after;
    }

    public long remaining(long elapsed) {
        return Math.max(0L, after - elapsed);
    }

    /** Prossima tappa non ancora raggiunta, oppure null se sono tutte fatte. */
    public static Health next(long elapsed) {
        for (Health h : ALL) if (!h.done(elapsed)) return h;
        return null;
    }

    public static int completed(long elapsed) {
        int n = 0;
        for (Health h : ALL) if (h.done(elapsed)) n++;
        return n;
    }
}
