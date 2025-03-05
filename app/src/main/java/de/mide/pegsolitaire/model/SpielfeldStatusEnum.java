package de.mide.pegsolitaire.model;


/**
 * Aufzählungstyp für den Status der einzelnen Position im GridLayout.
 */
public enum SpielfeldStatusEnum {

    /** Zellen im GridLayout außerhalb des Rands haben diesen Zustand. */
    KEIN_FELD,

    /** Spielfeld hat keinen Spielstein */
    LEER,

    /** Spielfeld hat einen Spielstein */
    BESETZT
}
