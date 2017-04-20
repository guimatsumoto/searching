/**
 * This is a very simple class, used only so we can return two values from a function.
 *
 * @authors  MATSUMOTO Guilherme, PETRY Gabriel
 * @version 1.0
 * @since   2017-01-21
 */

package com.thesearch.dictionary_manager;

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
