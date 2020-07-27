package com.groupfive.satapp.commons;

import com.groupfive.satapp.models.inventariable.Inventariable;

import java.util.Comparator;

public class NameComparator implements Comparator {
    public int compare(Object o1,Object o2){
        Inventariable iv1 = (Inventariable) o1;
        Inventariable iv2 = (Inventariable) o2;

        return iv1.getNombre().compareTo(iv2.getNombre());
    }
}
