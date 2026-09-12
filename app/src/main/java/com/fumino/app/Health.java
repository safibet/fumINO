package com.fumino.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Cronologia del recupero della salute dopo l'ultima sigaretta.
 * Ogni tappa riporta la fonte: linee guida di OMS, NHS, American Cancer Society
 * e CDC, oppure lo studio scientifico da cui arriva il dato.
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
    public final String source;
    public final long after;   // millisecondi dall'ultima sigaretta
    public final String when;  // etichetta del tempo ("20 minuti", "1 anno", ...)

    private Health(String emoji, String when, long after, String title, String detail, String source) {
        this.emoji = emoji;
        this.when = when;
        this.after = after;
        this.title = title;
        this.detail = detail;
        this.source = source;
    }

    private static final List<Health> ALL = new ArrayList<>();

    static {
        add("❤️", "20 minuti", 20 * MIN, "Battito e pressione tornano normali",
                "La frequenza cardiaca e la pressione del sangue, alzate dalla nicotina, scendono ai "
                        + "valori di una persona che non fuma. Anche le mani e i piedi tornano a scaldarsi.",
                "US Surgeon General / CDC • American Cancer Society");

        add("🫁", "2 ore", 2 * HOUR, "La circolazione migliora",
                "Il sangue circola meglio nelle estremità. Possono iniziare i primi sintomi di "
                        + "astinenza: è il segno che il corpo sta ripulendo se stesso.",
                "NHS (Regno Unito)");

        add("🪶", "8 ore", 8 * HOUR, "Nicotina ridotta di oltre il 90%",
                "Il livello di nicotina nel sangue è quasi azzerato e l'ossigeno risale verso la normalità.",
                "NHS (Regno Unito)");

        add("💨", "12 ore", 12 * HOUR, "Monossido di carbonio eliminato",
                "Il monossido di carbonio esce dal sangue: l'emoglobina torna a trasportare ossigeno "
                        + "puro a tutti gli organi.",
                "American Cancer Society • CDC");

        add("🩺", "24 ore", DAY, "Inizia a calare il rischio di infarto",
                "Dopo un solo giorno il rischio di attacco cardiaco comincia già a ridursi. "
                        + "Il tuo cuore lavora meno sotto sforzo.",
                "American Cancer Society");

        add("👅", "48 ore", 2 * DAY, "Gusto e olfatto rinascono",
                "Le terminazioni nervose danneggiate ricrescono: i cibi riacquistano sapore e torni a "
                        + "sentire gli odori. La nicotina è ormai fuori dal corpo.",
                "NHS • American Cancer Society");

        add("🫁", "72 ore", 3 * DAY, "Respiro più libero, energia in aumento",
                "I bronchi si rilassano e la capacità polmonare cresce. È spesso il picco "
                        + "dell'astinenza: dopo questo momento si va solo in discesa.",
                "NHS (Regno Unito)");

        add("💪", "1 settimana", WEEK, "Superata la settimana più dura",
                "Chi resta senza fumare per i primi sette giorni ha una probabilità molto più alta di "
                        + "smettere davvero: l'astinenza precoce è il migliore indicatore di successo a "
                        + "lungo termine. Le voglie diventano più rare e più corte.",
                "Studi su astinenza precoce e ricaduta (West & Stapleton) • NCSCT");

        add("🏃", "2 settimane", 2 * WEEK, "Camminare e muoversi diventa facile",
                "Circolazione e funzione polmonare migliorano in modo misurabile: salire le scale non "
                        + "toglie più il fiato come prima.",
                "NHS • US Surgeon General");

        add("🛌", "4 settimane", 4 * WEEK, "Il sonno torna migliore di prima",
                "La nicotina è uno stimolante e frammenta il sonno. Nei primi giorni senza sigarette "
                        + "dormire può essere più difficile, ma dalla seconda-terza settimana il sonno "
                        + "migliora e intorno alla quarta-sesta settimana è mediamente migliore di quando "
                        + "fumavi.",
                "Revisioni su sonno e astinenza da nicotina (Jaehne et al.)");

        add("🩹", "4 settimane", 4 * WEEK + HOUR, "Ferite e operazioni: rischio dimezzato",
                "Uno studio dell'OMS con l'Università di Newcastle e la Federazione mondiale degli "
                        + "anestesisti mostra che chi smette almeno 4 settimane prima di un intervento ha "
                        + "molte meno complicanze: il rischio di problemi alla ferita cala di circa un "
                        + "terzo e ogni settimana senza fumo in più migliora i risultati del 19%.",
                "OMS / WFSA / Univ. di Newcastle, 2020");

        add("🧼", "1 mese", MONTH, "Pelle più luminosa, meno tosse",
                "La pelle riceve più ossigeno e appare più sana. Tosse, congestione nasale e "
                        + "affaticamento iniziano a diminuire.",
                "American Cancer Society • NHS");

        add("🧠", "6 settimane", 6 * WEEK, "Ansia, depressione e stress più bassi",
                "Contrariamente a quello che si crede, smettere migliora l'umore. Una meta-analisi di 26 "
                        + "studi pubblicata sul BMJ mostra che dopo almeno sei settimane ansia, depressione "
                        + "e stress calano rispetto a chi continua a fumare, con un effetto paragonabile a "
                        + "quello dei farmaci antidepressivi, anche in chi ha disturbi psichiatrici.",
                "Taylor et al., BMJ 2014 (meta-analisi, 26 studi)");

        add("❤️", "2 mesi", 2 * MONTH, "Migliora anche la vita sessuale",
                "Il fumo danneggia i vasi sanguigni che servono all'erezione e riduce il desiderio. "
                        + "Con il recupero della circolazione, tra le 2 e le 12 settimane molti uomini "
                        + "notano un miglioramento; negli studi la funzione erettile migliora in una quota "
                        + "importante di chi smette e in nessuno di chi continua a fumare.",
                "Sexual Medicine Reviews 2023 • studi su cessazione e funzione erettile");

        add("🫁", "3 mesi", 3 * MONTH, "Polmoni molto più efficienti",
                "La funzione polmonare è migliorata in modo netto e la fertilità aumenta. "
                        + "Le ciglia bronchiali stanno ricrescendo.",
                "NHS • American Cancer Society");

        add("🩸", "6 mesi", 6 * MONTH, "Sangue e colesterolo migliorano",
                "Gli indici di stress ossidativo migliorano nel giro di una-due settimane, quelli "
                        + "dell'infiammazione tra le 2 e le 12 settimane, e i valori di colesterolo e "
                        + "dell'emocromo si riportano gradualmente verso la norma nell'arco di 3-8 mesi.",
                "Revisione sistematica sui biomarcatori dopo la cessazione");

        add("🧹", "9 mesi", 9 * MONTH, "Polmoni puliti, meno infezioni",
                "Le ciglia polmonari sono rigenerate: espellono muco e sporcizia. Tosse e fiato corto "
                        + "sono in gran parte spariti e ti ammali meno.",
                "American Cancer Society");

        add("💚", "1 anno", YEAR, "Rischio cardiaco dimezzato",
                "Il rischio di malattia coronarica è circa la metà di quello di chi continua a fumare.",
                "American Cancer Society • US Surgeon General");

        add("🫁", "2 anni", 2 * YEAR, "I polmoni invecchiano più lentamente",
                "Nel Lung Health Study la capacità respiratoria è migliorata nei due anni successivi "
                        + "alla cessazione e poi è calata molto più lentamente: 28 ml l'anno in chi ha "
                        + "smesso davvero contro 62 ml l'anno in chi ha continuato a fumare. Tradotto: i "
                        + "tuoi polmoni invecchiano a meno della metà della velocità.",
                "Lung Health Study (Anthonisen et al.)");

        add("🧠", "5 anni", 5 * YEAR, "Rischio di ictus come chi non fuma",
                "Le arterie si allargano di nuovo. Il rischio di ictus torna simile a quello di chi non "
                        + "ha mai fumato e quello dei tumori di bocca, gola, esofago e vescica è dimezzato.",
                "American Cancer Society");

        add("🧪", "5 anni", 5 * YEAR + HOUR, "Infiammazione tornata normale",
                "Gli indicatori di infiammazione che il fumo tiene alti — fibrinogeno, proteina C "
                        + "reattiva, globuli bianchi — scendono progressivamente e nell'arco di circa "
                        + "cinque anni tornano ai livelli di chi non ha mai fumato: è il motivo per cui il "
                        + "rischio cardiovascolare continua a calare nel tempo.",
                "Northwick Park Heart Study • MONICA • analisi NHANES III, PLOS Medicine 2005");

        add("🌿", "10 anni", 10 * YEAR, "Rischio di tumore al polmone dimezzato",
                "Il rischio di morire di tumore al polmone è circa la metà di quello di chi continua a "
                        + "fumare. Cala anche il rischio di tumori a laringe, pancreas, rene e vescica.",
                "American Cancer Society");

        add("🏆", "15 anni", 15 * YEAR, "Cuore come chi non ha mai fumato",
                "Il rischio di malattie cardiache e coronariche è pari a quello di una persona che non "
                        + "ha mai acceso una sigaretta.",
                "American Cancer Society • US Surgeon General");

        add("👑", "20 anni", 20 * YEAR, "Rischio di morte come chi non ha mai fumato",
                "Il rischio di morire per cause legate al fumo — malattie polmonari e tumori "
                        + "compresi — scende al livello di chi non ha mai fumato, e il rischio di "
                        + "tumore al polmone si avvicina a quello di un non fumatore.",
                "American Cancer Society");
    }

    private static void add(String emoji, String when, long after, String title, String detail,
                            String source) {
        ALL.add(new Health(emoji, when, after, title, detail, source));
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

    /** Fonti citate nell'app, per la sezione in fondo alla scheda Salute. */
    public static final String[] SOURCES = {
            "Organizzazione Mondiale della Sanità (OMS) — tabelle sui benefici della cessazione e "
                    + "studio 2020 su fumo e complicanze chirurgiche, con WFSA e Università di Newcastle",
            "NHS (servizio sanitario britannico) — cronologia “What happens when you quit”",
            "American Cancer Society — “Health Benefits of Quitting Smoking Over Time”",
            "US Surgeon General / CDC — rapporti “The Health Consequences of Smoking Cessation”",
            "Taylor G. et al., “Change in mental health after smoking cessation”, BMJ 2014;348:g1151",
            "Anthonisen N. et al., Lung Health Study — declino della funzione polmonare dopo la cessazione",
            "Northwick Park Heart Study, MONICA e analisi NHANES III (PLOS Medicine, 2005) — "
                    + "indicatori di infiammazione dopo la cessazione",
            "Yoshida K. et al., “Tobacco smoking and somatic mutations in human bronchial "
                    + "epithelium”, Nature 2020 — rigenerazione dell'epitelio bronchiale",
            "Jha P. et al., New England Journal of Medicine 2013 — mortalità e beneficio di "
                    + "smettere prima dei 40 anni",
            "Sexual Medicine Reviews 2023 — fumo, cessazione e funzione erettile"
    };
}
