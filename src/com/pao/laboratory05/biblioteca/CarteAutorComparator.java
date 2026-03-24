package com.pao.laboratory05.biblioteca;
import java.util.Comparator;

//după autor alfabetic
class CarteAutorComparator implements Comparator<Carte> {
    @Override
    public int compare(Carte c1, Carte c2) {
        return c1.getAutor().compareTo(c2.getAutor());
    }
}