package de.mide.pegsolitaire;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import static de.mide.pegsolitaire.SpielfeldEnum.LEER;
import static de.mide.pegsolitaire.SpielfeldEnum.KEIN_FELD;
import static de.mide.pegsolitaire.SpielfeldEnum.BESETZT;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBarKonfigurieren();
        erzeugeGridLayout();
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
     * GridLayout mit Spielfeld programmatisch erzeugen.
     */
    private void erzeugeGridLayout() {

        GridLayout.LayoutParams gridLayoutParams = new GridLayout.LayoutParams();
        gridLayoutParams.height = GridLayout.LayoutParams.MATCH_PARENT;
        gridLayoutParams.width = GridLayout.LayoutParams.MATCH_PARENT;
        gridLayoutParams.setMargins(10, 10, 10, 10);

        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setLayoutParams(gridLayoutParams);

        int anzahlZeilen = SPIELFELD_VORLAGE_ARRAY.length;
        int anzahlSpalten = SPIELFELD_VORLAGE_ARRAY[0].length; // 2D-Array ist rechteckig!

        _imageButtonArray = new ImageButton[anzahlZeilen][anzahlSpalten];

        gridLayout.setRowCount(anzahlZeilen);
        gridLayout.setColumnCount( anzahlSpalten );
        gridLayout.setBackgroundColor(0xFF0000);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams( WRAP_CONTENT, WRAP_CONTENT );

        int buttonId = 1;
        for (int i = 0; i < anzahlZeilen; i++) {

            for (int j = 0; j < anzahlZeilen; j++) {

                /*
                ImageButton imageButton = new ImageButton(this);
                imageButton.setLayoutParams(layoutFuerGridElement);

                if (i % 2 == 0) {

                    imageButton.setBackgroundColor(0x00FF00);

                } else {

                    imageButton.setBackgroundColor(0xFF0000);
                }
                 */

                Button button = new Button(this);
                button.setText("abc");
                button.setLayoutParams(lp);
                button.setId( buttonId );

                GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                GridLayout.Spec colSpan = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams( rowSpan, colSpan );

                gridLayout.addView(button, gridParam);

                //_imageButtonArray[i][j] = imageButton;

                Log.i(TAG4LOGGING, "Spielfeld erzeugt: i=" + i + ", j=" + j + ".");

                buttonId++;
            }
        }


        LinearLayout oberstesLayout = findViewById(R.id.HauptLayoutMainActivity);
        oberstesLayout.addView(gridLayout);
    }
}