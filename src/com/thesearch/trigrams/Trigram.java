/**
 * @authors  MATSUMOTO Guilherme, PETRY Gabriel
 * @version 1.0
 * @since   2017-01-21
 */

package com.thesearch.trigrams;

/**
 * Class Trigram.
 * Stores a strucutre containg three words and a frequency.
 */
public class Trigram {
    private String _first;
    private String _second;
    private String _third;
    private double _freq;

    /**
     * Creates a Trigram with initial values passed as parameter.
     * @param f
     * @param s
     * @param t
     * @param freq
     */
    public Trigram(String f, String s, String t, double freq){
        this.setTrigram(f, s, t);
        this.setFreq(freq);
    }

    /**
     * Check Trigram equality.
     * @param t
     * @return Whether this is equal to t.
     */
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

    /**
     * Used mainly in debbugging.
     *
     * @return concatenation fo the tree words forming th Trigram.
     */
    public String getCompoundWord(){
        return (this._first + this._second + this._third);
    }

    /**
     * Overrides parent class equals().
     *
     * @param obj
     * @return whether this objects are the same.
     */
    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Trigram) || (obj == null))
            return false;
        if (obj == this)
            return true;

        return ((this._first.equals(((Trigram) obj).getFirst())) && (this._second.equals(((Trigram) obj).getSecond())) && this._third.equals(((Trigram) obj).getThird()));
    }

    /**
     * Overrides parent class hashCode(), so now its based on the three words forming each Trigram. Since we are hashing
     * strings, the best way to hash a compound set of strings is to simply add their individual hashes.
     * @return the object's hash code.
     */
    @Override
    public int hashCode() {
        return this._first.hashCode()+this._second.hashCode()+this._third.hashCode();
    }


}
