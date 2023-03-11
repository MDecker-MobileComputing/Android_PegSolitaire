package de.mide.pegsolitaire;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import static de.mide.pegsolitaire.SpielfeldEnum.LEER;
import static de.mide.pegsolitaire.SpielfeldEnum.KEIN_FELD;
import static de.mide.pegsolitaire.SpielfeldEnum.BESETZT;

import android.graphics.Typeface;
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
import android.widget.ImageButton;
import android.widget.Space;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String TAG4LOGGING = "PegSolitaire";

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

    private int _anzahlZeilen = SPIELFELD_VORLAGE_ARRAY.length;
    private int _anzahlSpalten = SPIELFELD_VORLAGE_ARRAY[0].length;

    /** Array mit Buttons für die einzelnen Spielfeldpositionen. */
    private Button[][] _buttonArray = new Button[_anzahlZeilen][_anzahlSpalten];

    private int _displayBreite = -1;
    private int _displayHoehe = -1;

    private int _seitenlaengeSpielstein = -1;

    private int _anzahlSpielsteineAktuell = -1;

    private ViewGroup.LayoutParams _layoutFuerSpielfeld = null;

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
     * Liest Auflösung von Display aus (Höhe und Breite) und
     * schreibt die Werte in die entsprechenden Member-Variablen.
     */
    private void holeDisplayAufloesung() {

        WindowManager windowManager = getWindowManager();
        Display display       = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        _displayBreite = displayMetrics.widthPixels;
        _displayHoehe = displayMetrics.heightPixels;

        Log.i(TAG4LOGGING, "displayBreite=" + _displayBreite + "px, displayHoehe=" + _displayHoehe);

        // Die MainActivity ist in der Manifest-Datei auf Portrait-Modus festgesetzt, also
        // gilt immer: _displayBreite < _displayHoehe
        _seitenlaengeSpielstein = _displayBreite / _anzahlSpalten;

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

            Toast.makeText(this, "Funktion noch nicht implementiert :-(", Toast.LENGTH_LONG).show();
            return true;

        } else {

            return super.onOptionsItemSelected(item);
        }
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

        for (int i = 0; i < _anzahlZeilen; i++) {

            for (int j = 0; j < _anzahlZeilen; j++) {

                switch (SPIELFELD_VORLAGE_ARRAY[i][j]) {

                    case BESETZT:
                            Button buttonBesetzt = new Button(this);
                            buttonBesetzt.setText("■"); // Unicode-Zeichen "Black Square" für "Spielstein"
                            buttonBesetzt.setTextColor(0xffff0000);
                            buttonBesetzt.setTextSize(22.0f); // "large"
                            buttonBesetzt.setLayoutParams(_layoutFuerSpielfeld);
                            gridLayout.addView(buttonBesetzt);
                            _anzahlSpielsteineAktuell++;
                            _buttonArray[i][j] = buttonBesetzt;
                        break;

                    case LEER:
                            Button buttonLeer = new Button(this);
                            buttonLeer.setText("");
                            buttonLeer.setLayoutParams(_layoutFuerSpielfeld);
                            gridLayout.addView(buttonLeer);
                            _buttonArray[i][j] = buttonLeer;
                        break;

                    case KEIN_FELD: // außerhalb von Rand
                            Space space = new Space(this);
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

    private void aktualisierenAnzeigeAnzahlSpielsteine() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            actionBar.setSubtitle( "Anzahl Spielsteine: " + _anzahlSpielsteineAktuell );
        }
    }


}