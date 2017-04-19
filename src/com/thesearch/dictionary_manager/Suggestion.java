package com.thesearch.dictionary_manager;

/**
 * Created by guilhermematsumoto on 15/04/17.
 */
public class Suggestion {
    private String _suggestion;
    private boolean _hasChanges;

    public Suggestion(String sugg, boolean changes){
        setSugg(sugg);
        setChanges(changes);
    }

    public String getSugg(){
        return _suggestion;
    }

    public boolean getChanges(){
        return _hasChanges;
    }

    public void setSugg(String sugg){
        _suggestion = sugg;
    }

    public void setChanges(boolean changes){
        _hasChanges = changes;
    }
}
