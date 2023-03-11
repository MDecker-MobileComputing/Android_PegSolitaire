package de.mide.pegsolitaire;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static de.mide.pegsolitaire.model.SpielfeldStatusEnum.LEER;
import static de.mide.pegsolitaire.model.SpielfeldStatusEnum.KEIN_FELD;
import static de.mide.pegsolitaire.model.SpielfeldStatusEnum.BESETZT;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Space;
import android.widget.Toast;

import de.mide.pegsolitaire.model.SpielfeldStatusEnum;
import de.mide.pegsolitaire.model.SpielfeldPosition;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    public static final String TAG4LOGGING = "PegSolitaire";

    /** Textfarbe für Button mit aktivem Spielstein. */
    private static final int TEXTFARBE_ROT = 0xffff0000;

    /** Textfarbe für zum Sprung ausgewählten Button/Spielstein. */
    private static final int TEXTFARBE_GRAU = 0xff808080;

    /** // Unicode-Zeichen "Black Square" für einen "Spielstein". */
    private static final String SPIELSTEIN_ZEICHEN = "■";

    /**
     * Array mit zwei Dimensionen für initialen Zustand Spielfeld.
     * Der erste Index ist die Zeile, der zweite Index ist die Spalte.
     */
    private static final SpielfeldStatusEnum[][] SPIELFELD_VORLAGE_ARRAY =
        {
            { KEIN_FELD, KEIN_FELD, BESETZT, BESETZT, BESETZT, KEIN_FELD, KEIN_FELD },
            { KEIN_FELD, KEIN_FELD, BESETZT, BESETZT, BESETZT, KEIN_FELD, KEIN_FELD },
            { BESETZT  , BESETZT  , BESETZT, BESETZT, BESETZT, BESETZT  , BESETZT   },
            { BESETZT  , BESETZT  , BESETZT, LEER   , BESETZT, BESETZT  , BESETZT   },
            { BESETZT  , BESETZT  , BESETZT, BESETZT, BESETZT, BESETZT  , BESETZT   },
            { KEIN_FELD, KEIN_FELD, BESETZT, BESETZT, BESETZT, KEIN_FELD, KEIN_FELD },
            { KEIN_FELD, KEIN_FELD, BESETZT, BESETZT, BESETZT, KEIN_FELD, KEIN_FELD }
        };

    private int _anzahlZeilen = SPIELFELD_VORLAGE_ARRAY.length;

    private int _anzahlSpalten = SPIELFELD_VORLAGE_ARRAY[0].length;

    /** Aktueller Zustand der einzelnen Felder als 2-D-Array. */
    private SpielfeldStatusEnum[][] _spielfeldArray = null;

    /** Array mit Buttons für die einzelnen Spielfeldpositionen. */
    private Button[][] _buttonArray = new Button[_anzahlZeilen][_anzahlSpalten];


    /** Seitenlänge für einen quadratischen Spielstein in Pixel. */
    private int _seitenlaengeSpielstein = -1;

    /**
     * Aktuelle Anzahl der Spielsteine auf dem Brett; wenn der Wert 1 ist,
     * dann hat man das Spiel gewonnen.
     */
    private int _anzahlSpielsteineAktuell = -1;

    /** Layout-Parameter für die einzelnen Buttons auf dem Spielfeld mit quadratischer Größe. */
    private ViewGroup.LayoutParams _layoutFuerSpielfeld = null;

    /**
     * Ausgewählter Spielstein, der "springen" soll; wenn {@code null}, dann ist keiner
     * ausgewählt. Wenn diese Variable den Wert {@code null} hat, dann muss ein
     * besetztes Feld ausgewählt werden; wenn diese Variable nicht den Wert {@code null}
     * hat, dann muss ein leeres Feld ausgewählt werden.
     */
    private Button _startButton = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG4LOGGING, "Zeilen=" + _anzahlZeilen + ", Spalten=" + _anzahlSpalten + "px.");

        holeDisplayAufloesung();
        actionBarKonfigurieren();
        initialisiereSpielfeld();
    }

    /**
     * Liest Auflösung von Display aus  und
     * schreibt die Werte in die entsprechenden Member-Variablen.
     */
    private void holeDisplayAufloesung() {

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int displayBreite = displayMetrics.widthPixels;

        Log.i(TAG4LOGGING, "displayBreite=" + displayBreite + "px");

        // Die MainActivity ist in der Manifest-Datei auf Portrait-Modus festgesetzt, also
        // gilt immer: _displayBreite < _displayHoehe
        _seitenlaengeSpielstein = displayBreite / _anzahlSpalten;

        _layoutFuerSpielfeld = new ViewGroup.LayoutParams(_seitenlaengeSpielstein, _seitenlaengeSpielstein);
    }

    /**
     * Konfiguration der ActionBar; siehe auch Methode {@link #onCreateOptionsMenu(Menu)}.
     * Der Untertitel mit der aktuellen Anzahl der Spielsteine wird von der Methode
     * {@link #aktualisiereAnzeigeAnzahlSpielsteine()}.
     */
    private void actionBarKonfigurieren() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {

            Toast.makeText(this, "Keine ActionBar vorhanden.", Toast.LENGTH_LONG).show();
            return;
        }

        actionBar.setTitle( "Peg Solitaire" );
    }

    /**
     * Menü-Einträge für ActionBar aus Ressourcendatei laden.
     * Zugehörige Event-Handler-Methode:
     * {@link #onOptionsItemSelected(MenuItem)}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu_items, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Event-Handler für Menü-Einträge der Action-Bar; die Menü-Einträge werden
     * in der Methode {@link #onCreateOptionsMenu(Menu)} aus einer Ressourcen-
     * Datei geladen.
     *
     * @param item Ausgewählter Menü-Eintrag.
     *
     * @return Es wird genau dann <i>true</i> zurückgegeben, wenn wir
     *         in dieser Methode das Ereignis verarbeiten konnten.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_neues_spiel) {

            sicherheitsabfrageFuerNeuesSpiel();
            return true;

        } else {

            return super.onOptionsItemSelected(item);
        }
    }

    public void sicherheitsabfrageFuerNeuesSpiel() {

        DialogInterface.OnClickListener onJaButtonListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                initialisiereSpielfeld();
                _startButton = null;
            }
        };

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Sicherheitsabfrage");
        dialogBuilder.setMessage("Wollen Sie wirklich ein neues Spiel beginnen?");

        dialogBuilder.setPositiveButton("Ja", onJaButtonListener);
        dialogBuilder.setNegativeButton("Nein", null);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }


    /**
     * GridLayout mit Spielfeld programmatisch befüllen.
     */
    private void initialisiereSpielfeld() {

        GridLayout gridLayout = findViewById(R.id.spielfeldGridLayout);

        if (gridLayout.getRowCount() == 0) {

            gridLayout.setColumnCount(_anzahlSpalten);
            gridLayout.setColumnCount(_anzahlZeilen);

        } else { // Methode wird nicht zum ersten Mal aufgerufen

            gridLayout.removeAllViews();
        }

        _anzahlSpielsteineAktuell = 0;
        _spielfeldArray = new SpielfeldStatusEnum[_anzahlZeilen][_anzahlSpalten];

        for (int i = 0; i < _anzahlZeilen; i++) {

            for (int j = 0; j < _anzahlZeilen; j++) {

                SpielfeldStatusEnum spielfeldStatus = SPIELFELD_VORLAGE_ARRAY[i][j];

                _spielfeldArray[i][j] = spielfeldStatus;

                switch (spielfeldStatus) {

                    case BESETZT:
                        erzeugeButtonBesetzt(gridLayout, i, j);
                        break;

                    case LEER:
                        erzeugeButtonLeer(gridLayout, i, j);
                        break;

                    case KEIN_FELD: // außerhalb von Rand
                            Space space = new Space(this); // Dummy-Element
                            gridLayout.addView(space);
                        break;

                    default: Log.e(TAG4LOGGING,
                            "Unerwarteter Wert für initialen Zustand von Feld.");
                }

            }
        }

        Log.i(TAG4LOGGING, "Anzahl Spielsteine: " + _anzahlSpielsteineAktuell);
        aktualisiereAnzeigeAnzahlSpielsteine();
    }

    private void erzeugeButtonBesetzt(GridLayout gridLayout, int indexZeile, int indexSpalte) {

        Button buttonBesetzt = new Button(this);
        buttonBesetzt.setText(SPIELSTEIN_ZEICHEN);
        buttonBesetzt.setTextColor(TEXTFARBE_ROT);
        buttonBesetzt.setTextSize(22.0f); // "large" als Schriftgröße
        buttonBesetzt.setLayoutParams(_layoutFuerSpielfeld);
        buttonBesetzt.setOnClickListener(this);

        gridLayout.addView(buttonBesetzt);
        _buttonArray[indexZeile][indexSpalte] = buttonBesetzt;

        _anzahlSpielsteineAktuell++;

        SpielfeldPosition pos = new SpielfeldPosition(indexZeile, indexSpalte);
        buttonBesetzt.setTag(pos);
    }

    private void erzeugeButtonLeer(GridLayout gridLayout, int indexZeile, int indexSpalte) {

        Button buttonLeer = new Button(this);
        buttonLeer.setText("");
        buttonLeer.setLayoutParams(_layoutFuerSpielfeld);
        buttonLeer.setOnClickListener(this);

        // Einstellungen für den Fall, dass der Button mal ein besetztes Feld repräsentiert
        buttonLeer.setTextColor(TEXTFARBE_ROT);
        buttonLeer.setTextSize(22.0f);

        gridLayout.addView(buttonLeer);

        _buttonArray[indexZeile][indexSpalte] = buttonLeer;

        SpielfeldPosition pos = new SpielfeldPosition(indexZeile, indexSpalte);
        buttonLeer.setTag(pos);
    }

    private void aktualisiereAnzeigeAnzahlSpielsteine() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            actionBar.setSubtitle( "Anzahl Spielsteine: " + _anzahlSpielsteineAktuell );
        }
    }

    /**
     * Event-Handler-Methode für alle Buttons.
     *
     * @param view Die Activity-Instanz wird nur den Buttons auf dem Spielfeld als
     *             Event-Handler-Objekt zugewiesen, deswegen ist das Argument
     *             immer ein Button-Objekt, welches als "Tag" ein Objekt der
     *             Klasse {@link SpielfeldPosition} referenziert.
     */
    @Override
    public void onClick(View view) {

        Button geklicktButton = (Button)view;

        SpielfeldPosition position = (SpielfeldPosition) geklicktButton.getTag();

        // Herausfinden, ob auf ein leeres oder ein besetztes Feld geklickt wurde
        int indexZeile = position.getIndexZeile();
        int indexSpalte = position.getIndexSpalte();

        SpielfeldStatusEnum spielfeldStatus = _spielfeldArray[indexZeile][indexSpalte];
        switch (spielfeldStatus) {

            case BESETZT:

                if (_startButton != null) {

                    Toast.makeText(this, "Ungültiger Zug!",
                            Toast.LENGTH_LONG).show();
                    _startButton.setTextColor(TEXTFARBE_ROT);
                    _startButton = null;

                    Log.w(TAG4LOGGING, "Zielfeld von Zug war nicht leer.");

                } else {

                    geklicktButton.setTextColor(TEXTFARBE_GRAU);
                    _startButton = geklicktButton;
                }
                break;

            case LEER:

                if (_startButton == null) {

                    Toast.makeText(this, "Ungültiger Zug: Zuerst einen Spielstein wählen!",
                            Toast.LENGTH_LONG).show();

                } else {

                    SpielfeldPosition startPosition = (SpielfeldPosition)  _startButton.getTag();
                    SpielfeldPosition uebersprungPosition = getUebersprungenerStein(startPosition, position);
                    if (uebersprungPosition != null) {

                        Log.i(TAG4LOGGING, "Zug war gültig.");

                        sprungDurchfuehren(_startButton, geklicktButton, uebersprungPosition);


                    } else {

                        _startButton.setTextColor(TEXTFARBE_ROT);
                        _startButton = null;
                        Toast.makeText(this, "Ungültiger Zug!", Toast.LENGTH_LONG).show();
                    }
                }

                break;

            default:
                Log.e(TAG4LOGGING, "Interner Fehler: Unerwarteter Status von angeklicktem Spielfeld: " + spielfeldStatus);
        }
    }

    /**
     * Sprung durchführen. Diese Methode darf nur aufgerufen werden, wenn vorher festgestellt wurde,
     * dass es sich um einen gültigen Zug handelt.
     */
    private void sprungDurchfuehren(Button startButton, Button zielButton, SpielfeldPosition uebersprungenPos) {

        startButton.setText("");
        int startButtonZeile = ((SpielfeldPosition)startButton.getTag()).getIndexZeile();
        int startButtonSpalte = ((SpielfeldPosition)startButton.getTag()).getIndexSpalte();
        _spielfeldArray[startButtonZeile][startButtonSpalte] = LEER;

        zielButton.setText(SPIELSTEIN_ZEICHEN);
        int zielButtonZeile = ((SpielfeldPosition)zielButton.getTag()).getIndexZeile();
        int zielButtonSpalte = ((SpielfeldPosition)zielButton.getTag()).getIndexSpalte();
        _spielfeldArray[zielButtonZeile][zielButtonSpalte] = BESETZT;

        _anzahlSpielsteineAktuell--;
        aktualisiereAnzeigeAnzahlSpielsteine();

        _startButton = null;

        if (_anzahlSpielsteineAktuell == 1) {

            zeigeGewonnenDialog();
        }
    }

    private void zeigeGewonnenDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Glückwunsch!");
        dialogBuilder.setMessage("Sie haben das Spiel gelöst!");
        dialogBuilder.setPositiveButton("Weiter", null);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }


    /**
     * Überprüft, ob ein Zug von {@code startPos} zu {@code zielPos} gültig ist, also ob genau
     * ein Stein übersprungen wird.
     *
     * @param startPos Position eines besetzten Felds
     * @param zielPos Position eines leeres Felds
     * @return {@code null} wenn ungültiger Zug; bei gültigem Zug Position des übersprungen
     *         Spielsteins (das jetzt entfernt werden muss)
     */
    private SpielfeldPosition getUebersprungenerStein(SpielfeldPosition startPos, SpielfeldPosition zielPos) {

        int startPositionZeile = startPos.getIndexZeile();
        int startPositionSpalte = startPos.getIndexSpalte();

        int zielPositionZeile = zielPos.getIndexZeile();
        int zielPositionSpalte = zielPos.getIndexSpalte();

        int uebersprungenZeile = -1;
        int uebersprungenSpalte = -1;

        if (startPositionZeile == zielPositionZeile) { // Zug in horizontaler Richtung

            int deltaSpalte = zielPositionSpalte - startPositionSpalte;
            if (Math.abs(deltaSpalte) != 2) {

                Log.w(TAG4LOGGING, "Es wurde nicht genau ein Feld bei einem horizontalen Zug übersprungen.");
                return null;
            }

            // ist übersprungenes Feld besetzt?
            uebersprungenZeile = startPositionZeile;
            uebersprungenSpalte = startPositionSpalte + deltaSpalte/2;

            if (_spielfeldArray[uebersprungenZeile][uebersprungenSpalte] != BESETZT) {

                Log.w(TAG4LOGGING, "Horizontal übersprungenes Feld ist nicht besetzt!");
                return null;

            } else {

                return new SpielfeldPosition(uebersprungenZeile, uebersprungenSpalte);
            }


        } else if (startPositionSpalte == zielPositionSpalte) { // Zug in vertikaler Richtung

            // Sprung-Differenz muss genau 1 betragen

            int deltaZeile = zielPositionZeile - startPositionZeile;
            if (Math.abs(deltaZeile) != 2) {

                Log.w(TAG4LOGGING, "Es wurde nicht genau ein Feld bei einem vertikalen Zug übersprungen.");
                return null;
            }

            // ist übersprungenes Feld besetzt?
            uebersprungenZeile = startPositionZeile + deltaZeile/2;
            uebersprungenSpalte = startPositionSpalte;

            if (_spielfeldArray[uebersprungenZeile][uebersprungenSpalte] != BESETZT) {

                Log.w(TAG4LOGGING, "Vertikal übersprungenes Feld ist nicht besetzt!");
                return null;

            } else {

                return new SpielfeldPosition(uebersprungenZeile, uebersprungenSpalte);
            }
        }

        Log.w(TAG4LOGGING, "Diagonale Züge sind nicht zulässig.");
        return null;
    }

}