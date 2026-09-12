package com.fumino.app;

import java.util.ArrayList;
import java.util.List;

/** Un obiettivo sbloccabile: tempo, sigarette evitate, risparmio o voglie superate. */
public class Goal {

    public static final int TIME = 0;
    public static final int CIGS = 1;
    public static final int MONEY = 2;
    public static final int CRAVINGS = 3;

    public static final String[] CATEGORIES = {
            "Tempo senza fumare", "Sigarette mai accese", "Soldi risparmiati", "Voglie superate"
    };
    public static final String[] CATEGORY_EMOJI = {"⏱️", "🚭", "💰", "💪"};

    public final String id;
    public final String emoji;
    public final String title;
    public final String desc;
    public final int type;
    public final double target;

    public Goal(String id, String emoji, String title, int type, double target, String desc) {
        this.id = id;
        this.emoji = emoji;
        this.title = title;
        this.type = type;
        this.target = target;
        this.desc = desc;
    }

    // ------------------------------------------------------------ elenco

    private static final List<Goal> ALL = new ArrayList<>();

    private static void g(String id, String emoji, String title, int type, double target, String desc) {
        ALL.add(new Goal(id, emoji, title, type, target, desc));
    }

    static {
        long MIN = Health.MIN, HOUR = Health.HOUR, DAY = Health.DAY, YEAR = Health.YEAR;

        // ---- tempo
        g("t_20m", "🌱", "Primo passo", TIME, 20 * MIN,
                "20 minuti senza fumare. Il battito è già tornato normale: hai iniziato davvero.");
        g("t_1h", "⏰", "Un'ora libera", TIME, HOUR,
                "60 minuti in cui hai scelto tu, non la sigaretta.");
        g("t_6h", "🌤️", "Mezza giornata", TIME, 6 * HOUR,
                "Sei ore: le voglie vanno e vengono, tu resti.");
        g("t_12h", "🌙", "Dodici ore", TIME, 12 * HOUR,
                "Il monossido di carbonio ha lasciato il tuo sangue.");
        g("t_1d", "📅", "Un giorno intero", TIME, DAY,
                "24 ore complete. Il rischio di infarto ha già iniziato a scendere.");
        g("t_2d", "👅", "Due giorni", TIME, 2 * DAY,
                "Niente più nicotina in circolo. Gusto e olfatto stanno tornando.");
        g("t_3d", "🫁", "Tre giorni", TIME, 3 * DAY,
                "Hai passato il picco dell'astinenza: da qui in poi è tutta discesa.");
        g("t_5d", "🔥", "Cinque giorni", TIME, 5 * DAY,
                "La tua determinazione è più forte dell'abitudine.");
        g("t_1w", "🏅", "Una settimana", TIME, 7 * DAY,
                "Sette giorni: chi arriva qui ha molte più probabilità di smettere per sempre.");
        g("t_10d", "🔟", "Dieci giorni", TIME, 10 * DAY,
                "Doppia cifra. Le voglie durano meno di tre minuti: passano sempre.");
        g("t_2w", "🏃", "Due settimane", TIME, 14 * DAY,
                "Circolazione e respiro migliorati fino al 30%.");
        g("t_3w", "🧠", "Tre settimane", TIME, 21 * DAY,
                "Il tempo che serve al cervello per costruire una nuova abitudine.");
        g("t_1mo", "🌙", "Un mese", TIME, 30 * DAY,
                "30 giorni da non fumatore. Pelle più luminosa e più energia.");
        g("t_6w", "✨", "Sei settimane", TIME, 42 * DAY,
                "L'idea di una sigaretta inizia a sembrare lontana.");
        g("t_2mo", "💫", "Due mesi", TIME, 61 * DAY,
                "Respiri meglio, dormi meglio, ti ammali meno.");
        g("t_100d", "💯", "100 giorni", TIME, 100 * DAY,
                "Cento giorni di libertà. Un traguardo che pochissimi raggiungono.");
        g("t_6mo", "🌿", "Sei mesi", TIME, 182 * DAY,
                "Mezzo anno. Tosse e affanno sono ormai un ricordo.");
        g("t_9mo", "🫁", "Nove mesi", TIME, 274 * DAY,
                "I polmoni hanno rigenerato le ciglia: si puliscono da soli.");
        g("t_1y", "🎉", "Un anno", TIME, YEAR,
                "Il rischio di malattia coronarica è dimezzato. Sei un'altra persona.");
        g("t_500d", "👑", "500 giorni", TIME, 500 * DAY,
                "Cinquecento giorni senza una sola sigaretta.");
        g("t_18mo", "🌈", "Un anno e mezzo", TIME, (long) (1.5 * YEAR),
                "La sigaretta non fa più parte della tua vita.");
        g("t_2y", "🌟", "Due anni", TIME, 2 * YEAR,
                "Due anni da non fumatore: sei un esempio per chi sta iniziando.");
        g("t_3y", "💎", "Tre anni", TIME, 3 * YEAR,
                "Il tuo cuore ti ringrazia ogni giorno.");
        g("t_5y", "🏆", "Cinque anni", TIME, 5 * YEAR,
                "Rischio di ictus pari a chi non ha mai fumato.");
        g("t_10y", "👑", "Dieci anni", TIME, 10 * YEAR,
                "Rischio di tumore al polmone dimezzato. Hai vinto alla grande.");
        g("t_15y", "🥇", "Quindici anni", TIME, 15 * YEAR,
                "Il tuo corpo è come se non avesse mai conosciuto il fumo.");

        // ---- sigarette evitate
        g("c_10", "🚫", "Dieci in meno", CIGS, 10,
                "Dieci sigarette che non hai acceso. Dieci volte che hai detto di no.");
        g("c_20", "📦", "Un pacchetto intero", CIGS, 20,
                "Un pacchetto che è rimasto sullo scaffale.");
        g("c_50", "✅", "Cinquanta", CIGS, 50,
                "Due pacchetti e mezzo mai comprati.");
        g("c_100", "💪", "Cento sigarette", CIGS, 100,
                "Un chilo di veleno in meno nei tuoi polmoni.");
        g("c_200", "🧼", "Duecento", CIGS, 200,
                "Dieci pacchetti evitati: i tuoi bronchi se ne sono accorti.");
        g("c_500", "🌬️", "Cinquecento", CIGS, 500,
                "Mezzo migliaio di sigarette mai accese.");
        g("c_1000", "🎯", "Mille sigarette", CIGS, 1000,
                "Cinquanta pacchetti. Un traguardo enorme.");
        g("c_2000", "🚀", "Duemila", CIGS, 2000,
                "Cento pacchetti mai aperti.");
        g("c_5000", "🌍", "Cinquemila", CIGS, 5000,
                "Una montagna di catrame che non hai respirato.");
        g("c_10000", "👑", "Diecimila", CIGS, 10000,
                "Diecimila volte che hai scelto la tua salute.");

        // ---- risparmio
        g("m_10", "🪙", "Primi 10 euro", MONEY, 10,
                "Un pranzo fuori pagato dal tuo non fumare.");
        g("m_25", "🍽️", "25 euro", MONEY, 25,
                "Una pizza per due, offerta dalle sigarette mai comprate.");
        g("m_50", "👟", "50 euro", MONEY, 50,
                "Un paio di scarpe da camminata: ora hai anche il fiato per usarle.");
        g("m_100", "🎁", "100 euro", MONEY, 100,
                "Tre cifre risparmiate. Regalati qualcosa che dura.");
        g("m_200", "🎧", "200 euro", MONEY, 200,
                "Un bel paio di cuffie, o una spesa intera.");
        g("m_500", "✈️", "500 euro", MONEY, 500,
                "Un weekend fuori, pagato dal fumo che non hai comprato.");
        g("m_1000", "🏝️", "1.000 euro", MONEY, 1000,
                "Una vacanza vera. Quattro cifre che erano letteralmente fumo.");
        g("m_2000", "💻", "2.000 euro", MONEY, 2000,
                "Un computer nuovo, o un bel fondo di emergenza.");
        g("m_5000", "🚗", "5.000 euro", MONEY, 5000,
                "Il valore di un'auto usata, bruciato ogni anno da molti fumatori. Non da te.");
        g("m_10000", "🏡", "10.000 euro", MONEY, 10000,
                "Cinque cifre risparmiate: una cifra che cambia i progetti.");

        // ---- forza di volontà
        g("v_1", "🛡️", "Prima voglia superata", CRAVINGS, 1,
                "Hai sentito la voglia e non hai ceduto. Si impara così.");
        g("v_5", "🧘", "Cinque voglie superate", CRAVINGS, 5,
                "Ogni voglia superata indebolisce la successiva.");
        g("v_10", "🥊", "Dieci voglie superate", CRAVINGS, 10,
                "Stai allenando la parte di te che decide.");
        g("v_25", "⚡", "Venticinque", CRAVINGS, 25,
                "Venticinque volte che hai lasciato passare l'onda.");
        g("v_50", "🧱", "Cinquanta", CRAVINGS, 50,
                "La tua nuova abitudine ha fondamenta solide.");
        g("v_100", "🥇", "Cento voglie superate", CRAVINGS, 100,
                "Cento no. La sigaretta ha perso il suo potere su di te.");
        g("v_250", "🔱", "Duecentocinquanta", CRAVINGS, 250,
                "Sei tu al comando, senza più discussioni.");
    }

    public static List<Goal> all() {
        return ALL;
    }

    public static List<Goal> byCategory(int type) {
        List<Goal> out = new ArrayList<>();
        for (Goal g : ALL) if (g.type == type) out.add(g);
        return out;
    }

    public static Goal byId(String id) {
        for (Goal g : ALL) if (g.id.equals(id)) return g;
        return null;
    }

    // ------------------------------------------------------------ calcoli

    public double current(Prefs p) {
        switch (type) {
            case TIME:
                return p.elapsed();
            case CIGS:
                return p.cigsAvoided();
            case MONEY:
                return p.moneySaved();
            default:
                return p.cravingsResisted();
        }
    }

    public boolean reached(Prefs p) {
        return current(p) >= target;
    }

    public float progress(Prefs p) {
        double c = current(p) / target;
        return (float) Math.max(0, Math.min(1, c));
    }

    /** Data (timestamp) in cui l'obiettivo è stato o sarà raggiunto; -1 se non stimabile. */
    public long when(Prefs p) {
        switch (type) {
            case TIME:
                return p.quitAt() + (long) target;
            case CIGS:
                return p.whenCigs(target);
            case MONEY:
                return p.whenMoney(target);
            default:
                return -1L;
        }
    }

    public String targetLabel() {
        switch (type) {
            case TIME:
                return Fmt.compact((long) target);
            case CIGS:
                return Fmt.intNum(target) + " sig.";
            case MONEY:
                return Fmt.money(target);
            default:
                return ((int) target) + " voglie";
        }
    }

    public String currentLabel(Prefs p) {
        switch (type) {
            case TIME:
                return Fmt.compact((long) current(p));
            case CIGS:
                return Fmt.intNum(current(p));
            case MONEY:
                return Fmt.money(current(p));
            default:
                return String.valueOf((int) current(p));
        }
    }

    public String categoryName() {
        return CATEGORIES[type];
    }

    public static int reachedCount(Prefs p) {
        int n = 0;
        for (Goal g : ALL) if (g.reached(p)) n++;
        return n;
    }

    /** Prossimo obiettivo da sbloccare (il più vicino in percentuale). */
    public static Goal next(Prefs p) {
        Goal best = null;
        float bestP = -1f;
        for (Goal g : ALL) {
            if (g.reached(p)) continue;
            float pr = g.progress(p);
            if (pr > bestP) {
                bestP = pr;
                best = g;
            }
        }
        return best;
    }

    /** I prossimi n obiettivi, dal più vicino. */
    public static List<Goal> upcoming(Prefs p, int n) {
        List<Goal> pending = new ArrayList<>();
        for (Goal g : ALL) if (!g.reached(p)) pending.add(g);
        List<Goal> out = new ArrayList<>();
        while (out.size() < n && !pending.isEmpty()) {
            Goal best = null;
            int bestIdx = -1;
            float bp = -1f;
            for (int i = 0; i < pending.size(); i++) {
                float pr = pending.get(i).progress(p);
                if (pr > bp) {
                    bp = pr;
                    best = pending.get(i);
                    bestIdx = i;
                }
            }
            pending.remove(bestIdx);
            out.add(best);
        }
        return out;
    }
}
