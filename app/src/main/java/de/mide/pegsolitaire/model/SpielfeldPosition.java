package de.mide.pegsolitaire.model;

/**
 * Ein Objekt dieser Klasse beschreibt eine Spielfeldposition
 * (z.B. für eine Spielfigur oder eine leere Position).
 */
public class SpielfeldPosition {

    private int _indexZeile = -1;

    private int _indexSpalte = -1;

    /**
     * Konstruktor, um beide Attribute der Klasse zu setzen.
     *
     * @param indexZeile 0-basierter Index der Zeile
     * @param indexSpalte 0-basierter Index Spalte
     */
    public SpielfeldPosition(int indexZeile, int indexSpalte) {

        _indexZeile = indexZeile;
        _indexSpalte = indexSpalte;
    }

    public int getIndexZeile() {

        return _indexZeile;
    }

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

    @Override
    public String toString() {

        return "IndexZeile=" + _indexZeile + ", IndexSpalte=" + _indexSpalte;
    }

}
