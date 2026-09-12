package com.fumino.app;

import java.util.Calendar;

/** Frasi motivazionali, consigli anti-voglia e curiosità. */
public final class Motivation {

    private Motivation() {
    }

    public static final String[] QUOTES = {
            "Non devi smettere per sempre. Devi solo non fumare adesso.",
            "La voglia dura tre minuti. Tu duri molto di più.",
            "Ogni sigaretta non fumata è una piccola vittoria che nessuno ti può togliere.",
            "Non stai rinunciando a niente: stai riprendendoti tutto.",
            "Il momento più difficile è sempre quello che stai attraversando. E lo stai attraversando.",
            "Sei già più forte di ieri. Domani lo sarai ancora di più.",
            "Il fumo prometteva calma e vendeva dipendenza. Hai smesso di comprare.",
            "Respira. Quella voglia sta già passando mentre la leggi.",
            "Non sei un fumatore che non fuma. Sei una persona libera.",
            "La nicotina crea il problema che poi finge di risolvere.",
            "Nessuno si è mai pentito di aver smesso di fumare.",
            "Il tuo corpo si sta riparando in questo preciso istante.",
            "Una sola sigaretta riporta indietro tutto. Non vale il prezzo.",
            "La libertà non ha il sapore del fumo.",
            "Hai già fatto la parte più difficile: hai iniziato.",
            "Il coraggio non è non avere voglia. È avere voglia e dire no.",
            "Ogni volta che resisti, la voglia successiva è più debole.",
            "Non contare i giorni. Fai in modo che i giorni contino.",
            "Sei tu a decidere cosa entra nei tuoi polmoni.",
            "Il fumo ti rubava tempo. Stai riprendendoti ogni minuto.",
            "Le cose difficili diventano facili solo dopo averle fatte a lungo.",
            "Cadere non è fallire. Restare a terra sì.",
            "Non c'è nessuna sigaretta speciale: sono tutte uguali, tutte una trappola.",
            "Chi ti vuole bene sta tifando per te, anche se non lo dice.",
            "Il tuo respiro di domani dipende da questa scelta di oggi.",
            "Hai smesso di bruciare soldi. Letteralmente.",
            "La voglia è un'onda: non combatterla, lasciala passare.",
            "Sei molto più della tua dipendenza.",
            "Ricorda perché hai iniziato a smettere.",
            "Tra un anno vorrai aver iniziato oggi. Hai già iniziato.",
            "I polmoni perdonano. Dagli tempo.",
            "Non aspettare la motivazione: la motivazione arriva dopo l'azione.",
            "Ogni no alla sigaretta è un sì a te stesso.",
            "Non stai combattendo contro di te. Stai combattendo per te.",
            "Un giorno racconterai a qualcuno come ce l'hai fatta.",
            "Le voglie non sono ordini. Sono solo pensieri.",
            "La sigaretta non risolve lo stress: lo aggiunge.",
            "Chi smette a 40 anni guadagna in media 9 anni di vita.",
            "Il tuo cuore batte più tranquillo da quando hai smesso.",
            "Non serve essere forti sempre. Basta esserlo per i prossimi cinque minuti.",
            "Hai già superato voglie peggiori di questa.",
            "L'unica sigaretta pericolosa è la prossima.",
            "Fai un passo. Poi un altro. È sempre stato così.",
            "Il gusto del cibo sta tornando. Non rovinare la festa.",
            "Il tempo passa comunque: che passi senza fumo.",
            "Sei l'unica persona che può farlo per te. Ed è una buona notizia.",
            "Quando la voglia arriva, bevi un bicchiere d'acqua e conta fino a cento.",
            "Il desiderio di fumare si spegne da solo, se non gli dai ossigeno.",
            "Ogni giorno che passa la tua identità di non fumatore diventa più solida.",
            "Non sei in punizione: ti stai curando.",
            "Ci sarà un giorno in cui non ci penserai più. Arriverà.",
            "Le tue tasche, i tuoi vestiti e la tua casa ti ringraziano.",
            "Ti stai dimostrando che le tue decisioni valgono qualcosa.",
            "L'abitudine si spezza ripetendo la nuova scelta.",
            "Non ti manca la sigaretta: ti manca il gesto. Cambia gesto.",
            "Il fumo era una risposta sbagliata a una domanda vera. Trova la risposta giusta.",
            "Oggi hai una cosa che il fumatore di ieri non aveva: il controllo.",
            "Sei più vicino al traguardo di quanto pensi.",
            "Rispetta la fatica che hai già fatto: non buttarla via per tre minuti di voglia.",
            "Il tuo prossimo obiettivo è a un passo. Continua.",
            "Anche oggi hai scelto di respirare.",
            "La libertà è sapere che puoi fumare e decidere di no.",
            "Un fumatore in meno nel mondo. Sei tu."
    };

    public static final String[][] TIPS = {
            {"💧", "Bevi un bicchiere d'acqua", "Lentamente, a piccoli sorsi. Occupa le mani e la bocca e aiuta il corpo a eliminare le tossine."},
            {"🫁", "Respira 4-7-8", "Inspira 4 secondi, trattieni 7, espira 8. Ripeti quattro volte: calma il sistema nervoso in un minuto."},
            {"🚶", "Cambia stanza o esci", "La voglia è spesso legata a un luogo e a un'abitudine. Spezza il contesto e la voglia perde forza."},
            {"🦷", "Lavati i denti", "Il sapore di menta rende l'idea della sigaretta molto meno attraente."},
            {"🍎", "Mangia qualcosa di croccante", "Carota, mela, sedano, frutta secca: danno alla bocca qualcosa da fare."},
            {"📱", "Scrivi a qualcuno", "Dire ad alta voce 'ho voglia di fumare ma non lo farò' riduce subito l'intensità."},
            {"💪", "Fai 20 flessioni o salta sul posto", "Il movimento brucia l'adrenalina dell'astinenza e libera endorfine."},
            {"⏱️", "Rimanda di 10 minuti", "Non dire mai 'mai più'. Dì 'non adesso'. Nella maggior parte dei casi la voglia sparisce da sola."},
            {"🎵", "Metti la tua canzone preferita", "Tre minuti di musica durano quanto una voglia."},
            {"✍️", "Rileggi i tuoi motivi", "Nella scheda Motivazione trovi i perché che hai scritto tu. Sono la tua arma migliore."},
            {"🧊", "Sciacqua la bocca con acqua fredda", "Il freddo interrompe il circuito automatico del gesto."},
            {"👐", "Tieni le mani occupate", "Una penna, una pallina antistress, le chiavi: la dipendenza è anche gestualità."},
            {"💶", "Guarda quanto hai risparmiato", "Apri la schermata principale: quei soldi sono tuoi e stanno crescendo."},
            {"🧘", "Osserva la voglia senza giudicarla", "Chiediti: dove la sento nel corpo? Quanto dura? Guardarla la fa sgonfiare."},
            {"☕", "Cambia la tua routine", "Caffè, pausa, aperitivo: cambia sedia, mano o ordine dei gesti per rompere l'associazione."},
            {"🛌", "Dormi se sei esausto", "La stanchezza abbassa le difese. Riposare è una strategia, non una resa."},
            {"🌿", "Esci a prendere aria", "Cinque minuti di camminata all'aperto ossigenano e distraggono."},
            {"🎯", "Guarda il prossimo obiettivo", "Ti manca poco per sbloccarlo: sarebbe un peccato ricominciare da capo adesso."},
            {"🙌", "Fatti i complimenti", "Ogni voglia superata va celebrata. Registrala nell'app: diventa un obiettivo."},
            {"🚫", "Ricorda: una tira l'altra", "Non esiste 'solo una'. Esiste 'di nuovo da capo'. Non vale la pena."}
    };

    public static final String[][] FACTS = {
            {"🧪", "7.000 sostanze chimiche", "Il fumo di sigaretta contiene oltre 7.000 sostanze, di cui almeno 70 cancerogene."},
            {"⏳", "11 minuti a sigaretta", "Ogni sigaretta accorcia la vita in media di circa 11 minuti."},
            {"❤️", "Il cuore ringrazia subito", "Dopo 20 minuti battito e pressione sono già tornati normali."},
            {"🫁", "I polmoni si puliscono", "Dopo 9 mesi le ciglia bronchiali sono rigenerate e rimuovono muco e sporco."},
            {"💸", "Un pacchetto al giorno", "Un pacchetto al giorno significa circa 2.000 euro l'anno andati in fumo."},
            {"👩‍⚕️", "Chi smette vive di più", "Smettere a 30 anni restituisce quasi 10 anni di aspettativa di vita."},
            {"🧠", "La dipendenza è chimica, non morale", "Se hai ricadute non sei debole: la nicotina agisce sui circuiti della ricompensa."},
            {"🔁", "Servono più tentativi", "In media servono diversi tentativi per smettere davvero: ogni tentativo aumenta le probabilità."},
            {"📉", "Le voglie diminuiscono", "Dopo la prima settimana la frequenza delle voglie cala in modo netto."},
            {"👶", "Fumo passivo", "Smettere protegge anche chi ti sta accanto: non esiste una soglia sicura di fumo passivo."},
            {"\uD83E\uDDEC", "I polmoni si ripopolano di cellule sane", "Uno studio su Nature (Yoshida et al., 2020) ha scoperto che chi smette fa ripartire l'epitelio bronchiale da cellule \"dormienti\" rimaste intatte: negli ex fumatori sono quattro volte più frequenti che in chi fuma ancora. Vale anche dopo decenni di sigarette."},
            {"\uD83D\uDE42", "Smettere migliora l'umore", "Una meta-analisi di 26 studi sul BMJ (Taylor, 2014) mostra che dopo sei settimane ansia, depressione e stress sono più bassi rispetto a chi continua a fumare: l'effetto è paragonabile a quello di un antidepressivo."},
            {"\u23F3", "Smettere prima dei 40 anni", "Chi smette prima dei 40 anni evita circa il 90% del rischio di morte in più legato al fumo e guadagna in media quasi dieci anni di vita (Jha et al., NEJM 2013)."},
            {"\uD83E\uDE7A", "Prima di un'operazione", "Secondo l'OMS ogni settimana senza fumo dopo le prime quattro migliora del 19% l'esito di un intervento chirurgico, e le complicazioni della ferita calano di circa un terzo."},
            {"\uD83C\uDFC6", "Dopo vent'anni", "A vent'anni dall'ultima sigaretta il rischio di morire per cause legate al fumo torna pari a quello di chi non ha mai fumato (American Cancer Society)."}
    };

    /** Frase del giorno: stabile per 24 ore, diversa ogni giorno. */
    public static String quoteOfTheDay() {
        Calendar c = Calendar.getInstance();
        int idx = (c.get(Calendar.YEAR) * 366 + c.get(Calendar.DAY_OF_YEAR)) % QUOTES.length;
        return QUOTES[Math.abs(idx)];
    }

    public static String randomQuote() {
        return QUOTES[(int) (Math.random() * QUOTES.length)];
    }

    public static String[] randomTip() {
        return TIPS[(int) (Math.random() * TIPS.length)];
    }
}
