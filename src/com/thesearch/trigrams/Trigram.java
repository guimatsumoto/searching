package com.thesearch.trigrams;

/**
 * Created by guilhermematsumoto on 17/04/17.
 */
public class Trigram {
    private String _first;
    private String _second;
    private String _third;
    private double _freq;

    public Trigram(String f, String s, String t, double freq){
        this.setTrigram(f, s, t);
        this.setFreq(freq);
    }

    public boolean equals(Trigram t){
        return ((this._first.equals(t.getFirst())) && (this._second.equals(t.getSecond())) && (this._third.equals(t.getThird())));
    }

    public String getFirst(){
        return _first;
    }

    public String getSecond(){
        return _second;
    }

    public String getThird(){
        return _third;
    }

    public double getFreq(){
        return _freq;
    }

    public void setTrigram(String f, String s, String t){
        _first = f;
        _second = s;
        _third = t;
    }

    public void setFreq(double f){
        _freq = f;
    }

    public String getCompoundWord(){
        return (this._first + this._second + this._third);
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Trigram) || (obj == null))
            return false;
        if (obj == this)
            return true;

        return ((this._first.equals(((Trigram) obj).getFirst())) && (this._second.equals(((Trigram) obj).getSecond())) && this._third.equals(((Trigram) obj).getThird()));
    }

    @Override
    public int hashCode() {
        return this._first.hashCode()+this._second.hashCode()+this._third.hashCode();
    }


}
