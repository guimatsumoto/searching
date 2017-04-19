package com.thesearch.trigrams;
import com.thesearch.dictionary_manager.BkTree;
import com.thesearch.dictionary_manager.Dictionary;
import com.thesearch.dictionary_manager.Suggestion;
import com.thesearch.trigrams.Trigram;
import com.thesearch.dictionary_manager.Match;

import java.io.*;
import java.util.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by guilhermematsumoto on 17/04/17.
 */
public class TrigramDict {
    private HashMap<Trigram, Double> _trigrams;
    final static Charset ENCODING = StandardCharsets.UTF_8;
    private Dictionary _dict;

    public TrigramDict(String p, String dictFile){
        //this.createFile("big-v2.txt", p);
        _trigrams = this.readTrigrams(p);
        _dict = new Dictionary(dictFile);
    }

    /**
     *
     * @param query
     * @return returns a better query or null if no better query was found;
     */
    public Suggestion contextSensitiveCorrection(String query, boolean changes){
        String proposition = "";

        boolean hasChanges = changes; //check if any word was corrected;
        String[] words = query.toLowerCase().split("[^\\p{L}0-9']+");

        Trigram t = new Trigram("", "", "", 0.0);
        Set<Trigram> aux = new HashSet<>();
        List<Set<Trigram>> candidates = new ArrayList<Set<Trigram>>();


        //if a position in the list is null, that means there were no candidates found
        int i;
        for (i = 0; i < words.length - 2; ++i){
            t = new Trigram(words[i], words[i+1], words[i+2], 0.0);
            if (_trigrams.containsKey(t)) {
                aux = new HashSet<>();
                t.setFreq(_trigrams.get(t));
                aux.add(t);
                candidates.add(aux);
                continue;
            }else {
                candidates.add(this.findTrigramSubstitutes(t));
            }
        }

        Iterator it, j, k;
        Trigram t1, t2, t3;

        //case the phrase is only three words long
        if (words.length == 3){
            if (!(candidates.get(0) == null)) {
                t = new Trigram("", "", "", 0.0);
                it = candidates.get(0).iterator();
                while(it.hasNext()){
                    t1 = (Trigram) it.next();
                    if (t1.getFreq() > t.getFreq()) {
                        t = t1;
                        hasChanges = true;
                    }
                }
            }
            words[0] = t.getFirst();
            words[1] = t.getSecond();
            words[2] = t.getThird();

            proposition = Arrays.toString(words).replace(", ", " ").replaceAll("[\\[\\]]", "");
            Suggestion sugg = new Suggestion(proposition, hasChanges);
            return sugg;
        }


        //probably won't need the next part, the next for loop will treat all cases... PROBABLY!! if it fails come back HERE
        //Nop, needed it

        //i == 0
        if ((candidates.get(0) == null) || (candidates.get(1) == null)){
            //nop, just to lazy to invert logic
        }else {
            it = candidates.get(0).iterator();
            j = candidates.get(1).iterator();
            t = new Trigram("", "", "", 0.0);
            t1 = new Trigram("", "", "", 0.0);
            t2 = new Trigram("", "", "", 0.0);

            while (it.hasNext()) {
                t1 = (Trigram) it.next();
                if (t1 == null)
                    break; //exit
                j = candidates.get(1).iterator();
                while (j.hasNext()) {
                    t2 = (Trigram) j.next();
                    if ((t1.getFreq() > t.getFreq()) && (t1.getSecond().equals(t2.getFirst())) && (t1.getThird().equals(t2.getSecond()))) {
                        t = t1;
                        //break;
                    }
                }
            }

            //recalculating candidate Trigrams
            if (t.getFreq() != 0.0) {
                hasChanges = true;
                words[0] = t.getFirst();
                words[1] = t.getSecond();
                words[2] = t.getThird();
                t = new Trigram(words[1], words[2], words[3], 0.0);

                if (_trigrams.containsKey(t))
                    t.setFreq(_trigrams.get(t));
                candidates.set(1, this.findTrigramSubstitutes(t));

                if (words.length != 4) {
                    t = new Trigram(words[2], words[3], words[4], 0.0);
                    if (_trigrams.containsKey(t))
                        t.setFreq(_trigrams.get(t));
                    candidates.set(2, this.findTrigramSubstitutes(t));
                }
            }
        }

        //System.out.println(" " + words[0] + " " + words[1] + " " + words[2]);

        for (i = 1; i < candidates.size() - 3; ++i){
            //this prevents 5 word phrases form entering this loop and guarantees that they are correctly evaluated after this loop
            if ((words.length == 5) || (words.length == 4))
                continue;
            if ((candidates.get(i-1) == null) || (candidates.get(i) == null) || (candidates.get(i+1) == null))
                continue;
            it = candidates.get(i-1).iterator();
            j = candidates.get(i).iterator();
            k = candidates.get(i+1).iterator();
            t = new Trigram("", "", "", 0.0);
            t1 = new Trigram("", "", "", 0.0);
            t2 = new Trigram("", "", "", 0.0);
            t3 = new Trigram("", "", "", 0.0);

            //System.out.println("i = " + i);

            while (it.hasNext()) {
                t1 = (Trigram) it.next();
                j = candidates.get(i).iterator();;
                while (j.hasNext()){
                    t2 = (Trigram) j.next();
                    k = candidates.get(i+1).iterator();
                    if (t2 == null)
                        break;
                    while (k.hasNext()) {
                        t3 = (Trigram) k.next();
                        if ((t2.getFreq() > t.getFreq()) && (t2.getFirst().equals(t1.getSecond())) && (t2.getSecond().equals(t1.getThird())) && (t2.getSecond().equals(t3.getFirst())) && (t2.getThird().equals(t3.getSecond())) ) {
                            t = t2;
                        }

                    }

                }
            }

            if (t.getFreq() != 0.0) {
                hasChanges = true;
                words[i] = t.getFirst();
                words[i+1] = t.getSecond();
                words[i+2] = t.getThird();

                t = new Trigram(words[i + 1], words[i + 2], words[i + 3], 0.0);
                //System.out.println(t.getCompoundWord());
                if (_trigrams.containsKey(t))
                    t.setFreq(_trigrams.get(t));
                candidates.set(i + 1, this.findTrigramSubstitutes(t));
                t = new Trigram(words[i + 2], words[i + 3], words[i + 4], 0.0);
                if (_trigrams.containsKey(t))
                    t.setFreq(_trigrams.get(t));
                candidates.set(i + 2, this.findTrigramSubstitutes(t));
            }
            //System.out.println(" " + words[i-1] + " " + words[i] + " " + words[i+1]);

        }

        //next section probably not needed... NOP, it is needed
        //i == candidates.size() - 3
        t = new Trigram("", "", "", 0.0);
        if ((candidates.get(candidates.size()-1) == null) || (candidates.get(candidates.size()-2) == null)) {
            //nop
        }else{
            j = candidates.get(candidates.size()-2).iterator();
            k = candidates.get(candidates.size()-1).iterator();
            t2 = new Trigram("", "", "", 0.0);
            t3 = new Trigram("", "", "", 0.0);
            while (j.hasNext()) {
                t2 = (Trigram) j.next();
                k = candidates.get(candidates.size()-1).iterator();
                while (k.hasNext()) {
                    t3 = (Trigram) k.next();
                    if ((t3.getFreq() > t.getFreq()) && (t2.getSecond().equals(t3.getFirst())) && (t2.getThird().equals(t3.getSecond()))) {
                        t = t3;
                        //break;
                    }
                }
            }

            //recalculating candidate Trigrams
            if (t.getFreq() != 0.0) {
                hasChanges = true;
                words[words.length - 3] = t.getFirst();
                words[words.length - 2] = t.getSecond();
                words[words.length - 1] = t.getThird();
            }
        }


        //System.out.println(" " + words[words.length - 3] + " " + words[words.length - 2] + " " + words[words.length - 1]);

        //System.out.println("passou");

        proposition = Arrays.toString(words).replace(", ", " ").replaceAll("[\\[\\]]", "");
        Suggestion sugg = new Suggestion(proposition, hasChanges);
        return sugg;
    }

    /**
     * bestTrigramSubstitute
     * @param t
     * @return the best substitute for a trigram not in the database or null if no substitute was found
     */
    private Set<Trigram> findTrigramSubstitutes(Trigram t){
        Trigram tri = new Trigram("", "", "", 0.0);
        Set<Trigram> candidates = new HashSet<>();
        Set<Match> first = new HashSet<>();
        Set<Match> second = new HashSet<>();
        Set<Match> third = new HashSet<>();
        int i = 1;

        //getting all word candidates
        i = 0;
        while (i<2){
            if( _dict.search(t.getFirst(), i) != null)
                first.addAll(_dict.search(t.getFirst(), i));
            if( _dict.search(t.getSecond(), i) != null)
                second.addAll(_dict.search(t.getSecond(), i));
            if( _dict.search(t.getThird(), i) != null)
                third.addAll(_dict.search(t.getThird(), i));
            i++;
        }

        //if there are no substitutes for any of the words we return null
        if ((first.isEmpty()) || (second.isEmpty()) || (third.isEmpty()))
            return null;

        Iterator it = first.iterator();
        Iterator j = second.iterator();
        Iterator k = third.iterator();
        String w1, w2;

        //how to do it?
        while (it.hasNext()) {
            j = second.iterator();
            w1 = ((Match) it.next()).getMatch();
            while (j.hasNext()){
                k = third.iterator();
                w2 = ((Match) j.next()).getMatch();
                while (k.hasNext()){
                    tri = new Trigram(w1, w2, ((Match) k.next()).getMatch(), 0.0);
                    if (_trigrams.containsKey(tri)) {
                        tri.setFreq(_trigrams.get(tri));
                        candidates.add(tri);
                        //System.out.println("cand: " + tri.getFirst() + " " + tri.getSecond() + " " + tri.getThird());
                    }

                }
            }
        }

        return candidates;
    }

    public void createFile(String p, String n) {
        Path path = Paths.get("src/com/thesearch/trigrams", p);
        BufferedReader br = null;
        HashMap<Trigram, Double> FreqMap = new HashMap<>();
        int trigramCount = 0;
        String[] LineWords;
        Trigram t = new Trigram("", "", "", 0.0);
        try {
            br = Files.newBufferedReader(path, ENCODING);
            String Line = null;
            while ((Line = br.readLine()) != null) {
                LineWords = Line.split("[^\\p{L}0-9']+");
                for (int i = 0; i < LineWords.length - 2; ++i) {
                    t = new Trigram(LineWords[i].toLowerCase(), LineWords[i + 1].toLowerCase(), LineWords[i + 2].toLowerCase(), 0.0);
                    if (FreqMap.containsKey(t))
                        FreqMap.put(t, FreqMap.get(t) + 1.0);
                    else {
                        trigramCount++;
                        FreqMap.put(t, 1.0);
                    }
                }

            }
            //System.out.println("Unique word count: " + FreqMap.size()    );
        } catch (IOException e) {
            System.out.println("La lecture de l'archive a echoue");
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                System.out.println("La fermeture de l'archive a echoue");
            }
        }


        BufferedWriter bw = null;
        path = Paths.get("src/com/thesearch/trigrams", n);
        try{
            bw = Files.newBufferedWriter(path, ENCODING);
            String Line = "";

            Iterator it = FreqMap.entrySet().iterator();
            bw.write(Double.toString(trigramCount) + "\n");
            while (it.hasNext()){
                Map.Entry<Trigram, Double> pair = (Map.Entry)it.next();

                Line = pair.getKey().getFirst() + " " + pair.getKey().getSecond() + " " + pair.getKey().getThird() + " " + pair.getValue().toString() + "\n";

                bw.write(Line);
            }
        }catch(IOException e){
            System.out.println("Failed to open the file");
        }finally {
            try{
                if (bw != null)
                    bw.close();
            }catch(IOException e){
                System.out.println("Failed to close the file");
            }
        }

    }

    public HashMap<Trigram, Double> readTrigrams(String n){
        BufferedReader br = null;
        //Path path = Paths.get("src/com/thesearch/trigrams", n);
        InputStream path = Trigram.class.getResourceAsStream(n);
        //Path path = Paths.get(n);
        String[] LineWords;
        Trigram t;
        HashMap<Trigram, Double> map = new HashMap<>();
        Double trigramCount = 0.0;
        try {
            //br = Files.newBufferedReader(path, ENCODING);
            br = new BufferedReader(new InputStreamReader(path));
            String Line = null;
            Line = br.readLine();
            trigramCount = Double.parseDouble(Line);
            while ((Line = br.readLine()) != null) {
                LineWords = Line.split("[^\\p{L}0-9']+");
                t = new Trigram(LineWords[0].toLowerCase(), LineWords[1].toLowerCase(), LineWords[2].toLowerCase(), Double.parseDouble(LineWords[3])/trigramCount);
                map.put(t, t.getFreq());
            }

            //System.out.println("Unique word count: " + FreqMap.size()    );
        } catch (IOException e) {
            System.out.println("La lecture de l'archive a echoue");
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                System.out.println("La fermeture de l'archive a echoue");
            }
        }
        return map;
    }

    public Suggestion correctWords(String query){
        return this._dict.correctQuery(query);
    }
}
