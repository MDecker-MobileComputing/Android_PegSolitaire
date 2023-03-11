package de.mide.pegsolitaire;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import static de.mide.pegsolitaire.SpielfeldEnum.LEER;
import static de.mide.pegsolitaire.SpielfeldEnum.KEIN_FELD;
import static de.mide.pegsolitaire.SpielfeldEnum.BESETZT;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /** Zweidimensionalery Array für initialen Zustand Spielfeld. */
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
     * Event-Handler für Menü-Einträge der Action-Bar.
     *
     * @param item The menu item that was selected.
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

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.height = GridLayout.LayoutParams.MATCH_PARENT;
        layoutParams.width = GridLayout.LayoutParams.MATCH_PARENT;
        layoutParams.setMargins(10, 10, 10, 10);

        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setLayoutParams(layoutParams);

        LinearLayout hauptLayout = findViewById(R.id.HauptLayoutMainActivity);
        hauptLayout.addView(gridLayout);
    }
}