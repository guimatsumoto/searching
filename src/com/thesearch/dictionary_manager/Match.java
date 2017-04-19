/**
 * Match class, made public.
 * Used to store search results in Dictionary.java
 * and also to receive search results in the main class
 *
 *
 * @authors  MATSUMOTO Guilherme, PETRY Gabriel
 * @version 1.0
 * @since   2017-01-21
 */

package com.thesearch.dictionary_manager;

/**
 * Created by guilhermematsumoto on 07/03/17.
 * class Match
 * Stores a match, composed by a word, a frequency and a distance to the searched word.
 */
public class Match {
    private String _match;
    private int _dist;
    private Double _freq;

    public Match(String word, int distance, Double freq){
        if (word == null) throw new NullPointerException();
        if (distance < 0) throw new IllegalArgumentException("Distance maximale doit etre positif");

        this._match = word;
        this._dist = distance;
        this._freq = freq;
    }

    public void update(String word, int distance, Double freq){
        this._match = word;
        this._dist = distance;
        this._freq = freq;
    }

    public String getMatch(){
        return _match;
    }

    public int getDist(){
        return _dist;
    }

    public Double getFreq() {return _freq;}
}
