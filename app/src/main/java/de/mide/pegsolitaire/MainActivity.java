package de.mide.pegsolitaire;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static de.mide.pegsolitaire.model.SpielfeldEnum.LEER;
import static de.mide.pegsolitaire.model.SpielfeldEnum.KEIN_FELD;
import static de.mide.pegsolitaire.model.SpielfeldEnum.BESETZT;

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

import de.mide.pegsolitaire.model.SpielfeldEnum;
import de.mide.pegsolitaire.model.SpielfeldPosition;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    public static final String TAG4LOGGING = "PegSolitaire";

    /** Textfarbe für Button mit aktivem Spielstein. */
    private static final int TEXTFARBE_ROT = 0xffff0000;

    /** Textfarbe für Sprung ausgewählten Button/Spielstein. */
    private static final int TEXTFARBE_GRAU = 0xff808080;

    /**
     * Array mit zwei Dimensionen für initialen Zustand Spielfeld.
     * Der erste Index ist die Zeile, der zweite Index ist die Spalte.
     */
    private static final SpielfeldEnum[][] SPIELFELD_VORLAGE_ARRAY =
        {
            { KEIN_FELD, KEIN_FELD, BESETZT, BESETZT, BESETZT, KEIN_FELD, KEIN_FELD },
            { KEIN_FELD, KEIN_FELD, BESETZT, BESETZT, BESETZT, KEIN_FELD, KEIN_FELD },
            { BESETZT  , BESETZT  , BESETZT, BESETZT, BESETZT, BESETZT  , BESETZT   },
            { BESETZT  , BESETZT  , BESETZT, LEER   , BESETZT, BESETZT  , BESETZT   },
            { BESETZT  , BESETZT  , BESETZT, BESETZT, BESETZT, BESETZT  , BESETZT   },
            { KEIN_FELD, KEIN_FELD, BESETZT, BESETZT, BESETZT, KEIN_FELD, KEIN_FELD },
            { KEIN_FELD, KEIN_FELD, BESETZT, BESETZT, BESETZT, KEIN_FELD, KEIN_FELD }
        };

    /** Aktueller Zustand der einzelnen Felder. */
    private SpielfeldEnum[][] _spielfeldArray = null;

    private int _anzahlZeilen = SPIELFELD_VORLAGE_ARRAY.length;
    private int _anzahlSpalten = SPIELFELD_VORLAGE_ARRAY[0].length;

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
    private Button _quelleButton = null;


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
     * {@link #aktualisierenAnzeigeAnzahlSpielsteine()}.
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
        _spielfeldArray = new SpielfeldEnum[_anzahlZeilen][_anzahlSpalten];

        for (int i = 0; i < _anzahlZeilen; i++) {

            for (int j = 0; j < _anzahlZeilen; j++) {

                SpielfeldEnum spielfeldStatus = SPIELFELD_VORLAGE_ARRAY[i][j];

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
        aktualisierenAnzeigeAnzahlSpielsteine();
    }

    private void erzeugeButtonBesetzt(GridLayout gridLayout, int indexZeile, int indexSpalte) {

        Button buttonBesetzt = new Button(this);
        buttonBesetzt.setText("■"); // Unicode-Zeichen "Black Square" für "Spielstein"
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

        gridLayout.addView(buttonLeer);

        _buttonArray[indexZeile][indexSpalte] = buttonLeer;

        SpielfeldPosition pos = new SpielfeldPosition(indexZeile, indexSpalte);
        buttonLeer.setTag(pos);
    }

    private void aktualisierenAnzeigeAnzahlSpielsteine() {

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
     *             immer ein Button-Objekt welches als "Tag" ein Objekt der
     *             Klasse {@link SpielfeldPosition} referenziert.
     */
    @Override
    public void onClick(View view) {

        SpielfeldPosition position = (SpielfeldPosition) view.getTag();

        Log.i(TAG4LOGGING, "Spielfeld angeklickt: " + position);

        // Herausfinden, ob auf ein leeres oder ein besetztes Feld geklickt wurde
        int indexZeile = position.getIndexZeile();
        int indexSpalte = position.getIndexSpalte();

        SpielfeldEnum spielfeldStatus = _spielfeldArray[indexZeile][indexSpalte];

        switch (spielfeldStatus) {

            case BESETZT:

                if (_quelleButton != null) {

                    Toast.makeText(this, "Ungültiger Zug: Zielfeld muss leer sein!",
                            Toast.LENGTH_LONG).show();
                    _quelleButton.setTextColor(TEXTFARBE_ROT);
                    _quelleButton = null;

                } else {

                    Button button = (Button)view;
                    button.setTextColor(TEXTFARBE_GRAU);
                    _quelleButton = button;
                }
                break;

            case LEER:

                if (_quelleButton == null) {

                    Toast.makeText(this, "Ungültiger Zug: Zuerst einen Spielstein wählen!", Toast.LENGTH_LONG).show();;

                } else {

                    SpielfeldPosition startPosition = (SpielfeldPosition)  _quelleButton.getTag();
                    if (pruefeGueltigerSprung(startPosition, position)) {

                    } else {

                        _quelleButton.setTextColor(TEXTFARBE_ROT);
                        _quelleButton = null;
                        Toast.makeText(this, "Ungültiger Zug!", Toast.LENGTH_LONG).show();
                    }
                }

                break;

            default:
                Log.e(TAG4LOGGING, "Interner Fehler: Unerwarteter Status von angeklicktem Spielfeld.");
        }
    }

    private boolean pruefeGueltigerSprung(SpielfeldPosition startPos, SpielfeldPosition zielPos) {

        return false;
    }

}