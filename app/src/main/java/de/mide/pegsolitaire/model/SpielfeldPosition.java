package de.mide.pegsolitaire.model;

/**
 * Ein Objekt dieser Klasse beschreibt eine Spielfeldposition
 * (z.B. für eine Spielfigur oder eine leere Position).
 */
public class SpielfeldPosition {

    /** 0-basierter Index der Zeile */
    private int _indexZeile = -1;

    /** 0-basierter Index der Spalte */
    private int _indexSpalte = -1;


    /**
     * Konstruktor, um beide Attribute der Klasse zu setzen.
     *
     * @param indexZeile 0-basierter Index der Zeile
     * @param indexSpalte 0-basierter Index Spalte
     */
    public SpielfeldPosition(int indexZeile, int indexSpalte) {

        _indexZeile  = indexZeile;
        _indexSpalte = indexSpalte;
    }


    /**
     * Getter für Zeile.
     * @return 0-basierter Index der Zeile
     */
    public int getIndexZeile() {

        return _indexZeile;
    }


    /**
     * Getter für Spalte
     * @return 0-basierter Index der Spalte.
     */
    public int getIndexSpalte() {

        return _indexSpalte;
    }


    /**
     * Berechnet laufenden Index der Position (eine Zahl!)
     *
     * @param anzahlSpalten Anzahl der Spalten im Layout
     * @return 0-basierter Index der Position
     */
    public int getLaufenderIndex(int anzahlSpalten) {

        return anzahlSpalten*_indexZeile + _indexSpalte;
    }


    /**
     * Methode für String-Repräsentation des aufrufenden Objekts.
     *
     * @return String mit Zeile und Spalte
     */
    @Override
    public String toString() {

        return "IndexZeile=" + _indexZeile + ", IndexSpalte=" + _indexSpalte;
    }

}
