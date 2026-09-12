# fumINO 🚭

App Android per smettere di fumare: contatore in tempo reale, **53 obiettivi**,
**16 tappe di recupero della salute** (ognuna con la data esatta in cui arriva),
pronto soccorso anti-voglia con respirazione guidata e una carica motivazionale
ogni giorno.

Tutto in italiano, tutto offline: nessun account, nessuna pubblicità, nessun dato
che esce dal telefono.

**APK pronto all'uso: [`dist/fumINO.apk`](dist/fumINO.apk)**

---

## Cosa fa

### 🏠 Casa
- Contatore vivo `giorni · ore · minuti · secondi` dall'ultima sigaretta
- Anello di progresso verso il prossimo obiettivo
- Sigarette non fumate, soldi risparmiati, vita guadagnata, voglie superate
- Prossimo obiettivo e prossimo recupero del corpo, **con data e ora previste**
- Frase del giorno e pulsante SOS sempre a portata di pollice

### ❤️ Salute — *quando si ripristinano le cose*
Cronologia completa del recupero, da 20 minuti a 15 anni: battito e pressione,
monossido di carbonio, gusto e olfatto, bronchi, circolazione, funzione
polmonare, ciglia bronchiali, rischio cardiaco, ictus, tumore al polmone.
Per ogni tappa vedi:
- la **data e l'ora esatte** in cui è stata raggiunta, oppure in cui la
  raggiungerai (e quanto manca),
- la percentuale di avvicinamento,
- cosa sta succedendo nel corpo, in parole semplici.

### 🏆 Obiettivi
53 traguardi in quattro categorie, ognuno con **la data di sblocco prevista o
raggiunta**:
- ⏱️ tempo senza fumare: da 20 minuti a 15 anni (26 obiettivi)
- 🚭 sigarette mai accese: da 10 a 10.000 (10 obiettivi)
- 💰 soldi risparmiati: da 10 € a 10.000 € (10 obiettivi)
- 💪 voglie superate: da 1 a 250 (7 obiettivi)

Filtri per categoria, per "da sbloccare" e per "raggiunti", festa quando ne
sblocchi uno e condivisione del traguardo.

### ✨ Carica
- 63 frasi motivazionali (una del giorno, sempre diversa)
- "I miei perché": i motivi che scrivi tu, da rileggere nei momenti difficili
- 20 strategie concrete per superare la voglia
- 10 curiosità e dati sul fumo

### 🆘 SOS voglia
- Respirazione guidata 4-7-8 con cerchio animato
- Conto alla rovescia di 3 minuti (la durata media di una voglia)
- Consigli immediati, i tuoi motivi e quello che perderesti cedendo
- "Ce l'ho fatta": ogni voglia superata diventa un obiettivo

### ⚙️ Profilo
Dati modificabili (data dell'ultima sigaretta, sigarette al giorno, prezzo del
pacchetto), promemoria quotidiano all'ora che scegli, statistiche personali,
condivisione dei progressi e — senza giudizio — il pulsante per ripartire dopo
una ricaduta, che conserva il tuo record personale.

## Installazione sul telefono

1. Copia `dist/fumINO.apk` sul telefono (cavo, Drive, Telegram...).
2. Aprilo dal gestore file e conferma **"Installa comunque"**: Android avvisa
   perché l'app non arriva dal Play Store.
3. Se serve, attiva *Consenti da questa origine* per l'app da cui apri il file.
4. Al primo avvio scegli quando hai fumato l'ultima sigaretta e quanto fumavi.

Requisiti: Android 7.0 (API 24) o successivo. Occupa meno di 1 MB.

## Ricompilare l'APK

Non serve Android Studio: la toolchain minima (aapt2, android.jar, dx,
apksigner) viene scaricata da pacchetti pubblici.

```bash
./tools/fetch-toolchain.sh     # una volta sola, mette tutto in .toolchain/
./tools/build-apk.sh           # produce dist/fumINO.apk firmato
```

Serve solo un JDK (17 o 21) e Python 3. La chiave di firma viene creata al primo
build in `.keystore/fumino.jks`: **conservala**, serve per installare gli
aggiornamenti sopra la versione già installata.

In alternativa, con Android Studio: togli l'attributo `package="com.fumino.app"`
da `app/src/main/AndroidManifest.xml` e usa il progetto Gradle incluso.

## Struttura

```
app/src/main/java/com/fumino/app/
├── MainActivity.java        schede e navigazione
├── HomeScreen.java          contatore, statistiche, prossimi traguardi
├── HealthScreen.java        cronologia del recupero del corpo
├── GoalsScreen.java         i 53 obiettivi, con filtri
├── MotivationScreen.java    frasi, motivi personali, consigli
├── ProfileScreen.java       dati, promemoria, ricadute
├── SosActivity.java         respirazione guidata anti-voglia
├── SetupActivity.java       prima configurazione
├── Goal.java / Health.java  obiettivi e tappe di salute (dati e calcoli)
├── Motivation.java          frasi, consigli, curiosità
├── Prefs.java               stato e statistiche (SharedPreferences)
├── Theme.java / Cards.java  design system e componenti
└── RingView, BarView, BreathView   viste disegnate a mano
```

## Note

I tempi di recupero della salute seguono le indicazioni di OMS, NHS e American
Cancer Society. Il risparmio, le sigarette evitate e il tempo di vita guadagnato
(≈ 11 minuti per sigaretta) sono stime basate sui dati che inserisci.
L'app non sostituisce il parere del medico: se puoi, affianca a questo percorso
un supporto sanitario, raddoppia le probabilità di farcela.
