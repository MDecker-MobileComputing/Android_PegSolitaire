package de.mide.pegsolitaire;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import static de.mide.pegsolitaire.SpielfeldEnum.LEER;
import static de.mide.pegsolitaire.SpielfeldEnum.KEIN_FELD;
import static de.mide.pegsolitaire.SpielfeldEnum.BESETZT;

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

    /** 2D-Array mit den Bildern für die einzelnen Spielfelder. */
    private static ImageButton[][] _imageButtonArray = null;

    private int _anzahlZeilen = SPIELFELD_VORLAGE_ARRAY.length;
    private int _anzahlSpalten = SPIELFELD_VORLAGE_ARRAY[0].length;

    private int _displayBreite = -1;
    private int _displayHoehe = -1;


    private int _anzahlSpielsteineZuBeginn = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG4LOGGING, "Zeilen=" + _anzahlZeilen + ", Spalten=" + _anzahlSpalten + "px.");


        holeDisplayAufloesung();
        actionBarKonfigurieren();
        fuelleSpielfeld();
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
    }

    /**
     * Konfiguration der ActionBar; siehe auch Methode {@link #onCreateOptionsMenu(Menu)}.
     */
    private void actionBarKonfigurieren() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {

            Toast.makeText(this, "Keine ActionBar vorhanden.", Toast.LENGTH_LONG).show();
            return;
        }

        actionBar.setTitle( "Peg Solitaire" );
        actionBar.setSubtitle( "Brettspiel für eine Person" );
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
    private void fuelleSpielfeld() {

        GridLayout gridLayout = findViewById(R.id.spielfeldGridLayout);

        int minDisplayHoeheBreite = Math.min( _displayBreite, _displayHoehe );

        int quadratSeite = minDisplayHoeheBreite / _anzahlZeilen;

        ViewGroup.LayoutParams layoutParamFuerFeld = new ViewGroup.LayoutParams( quadratSeite, quadratSeite);

        _anzahlSpielsteineZuBeginn = 0;

        for (int i = 0; i < _anzahlZeilen; i++) {

            for (int j = 0; j < _anzahlZeilen; j++) {

                switch (SPIELFELD_VORLAGE_ARRAY[i][j]) {

                    case BESETZT:
                            Button buttonBesetzt = new Button(this);
                            buttonBesetzt.setText("");
                            buttonBesetzt.setLayoutParams(layoutParamFuerFeld);
                            _anzahlSpielsteineZuBeginn++;
                            gridLayout.addView(buttonBesetzt);
                        break;

                    case LEER:
                            Button buttonLeer = new Button(this);
                            buttonLeer.setText("L");
                            //buttonLeer.setBackgroundColor(0x000000);
                            buttonLeer.setLayoutParams(layoutParamFuerFeld);
                            gridLayout.addView(buttonLeer);
                        break;

                    case KEIN_FELD:
                            Space space = new Space(this);
                            gridLayout.addView(space);
                        break;

                    default: Log.e(TAG4LOGGING, "Unerwarteter Wert für initialen Zustand von Feld.");
                }

            }
        }

        Log.i(TAG4LOGGING, "Anzahl Spielsteine: " + _anzahlSpielsteineZuBeginn);
    }
}