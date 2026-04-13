package com.example.cosmosjaatelo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class LocationIterator implements Iterator<CrewMember> {

    private List<CrewMember> filtered;
    private int index = 0;

    public LocationIterator(Collection<CrewMember> all, Location loc) {
        filtered = new ArrayList<>();
        for (CrewMember c : all) {
            if (c.getLocation() == loc) {
                filtered.add(c);
            }
        }
    }
//hasNext and next for the iterator
    @Override
    public boolean hasNext() {
        return index < filtered.size();
    }

    @Override
    public CrewMember next() {
        return filtered.get(index++);
    }
}