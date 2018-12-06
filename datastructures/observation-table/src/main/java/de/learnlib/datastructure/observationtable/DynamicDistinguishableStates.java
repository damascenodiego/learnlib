package de.learnlib.datastructure.observationtable;

import net.automatalib.words.Word;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DynamicDistinguishableStates<I,D> {

    private Map<Word<I>, List<D>> observationMap;
    private List<I> suffixes;
    private Set<Set<Word<I>>> distinguishedStates;
    private Set<Integer> eSubset;
    private boolean isSingleton;

    public DynamicDistinguishableStates(Map<Word<I>, List<D>> omap) {
        this.observationMap = omap;
    }

    public DynamicDistinguishableStates(
            Map<Word<I>, List<D>> omap,
            Set<Set<Word<I>>> states,
            Set<Integer> esubset) {
        this(omap);
        this.distinguishedStates = states;
        this.eSubset = esubset;
        this.isSingleton = false;
        for (Set<Word<I>> set : this.distinguishedStates) {
            if (set.size() != 1) {
                return;
            }
        }
        this.isSingleton = true;
    }

    public Set<Set<Word<I>>> getDistinguishedStates() {
        return distinguishedStates;
    }

    public Set<Integer> getESubset() {
        return eSubset;
    }

//    public ObservationTable<String, Word<Word<String>>> getObservationTable() {
//        return observationTable;
//    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setDistinguishedStates(Set<Set<Word<I>>> states) {
        this.distinguishedStates = states;
        for (Set<Word<I>> set : this.distinguishedStates) {
            if (set.size() != 1) {
                return;
            }
        }
        this.isSingleton = true;
    }

    public void setESubset(Set<Integer> eSubset) {
        this.eSubset = eSubset;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof DynamicDistinguishableStates) {
            return this.distinguishedStates.equals(((DynamicDistinguishableStates) obj).distinguishedStates);
        }
        return false;
    }


}