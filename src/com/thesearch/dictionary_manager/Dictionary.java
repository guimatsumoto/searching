/**
 * Dictionary class, used to store and search words,
 * based on distance to query word and its frequency
 * in the english language
 *
 *
 * @authors  MATSUMOTO Guilherme, PETRY Gabriel
 * @version 1.0
 * @since   2017-01-21
 */

package com.thesearch.dictionary_manager;
import java.io.*;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import static java.lang.String.format;
import static java.lang.Math.max;
import java.net.*;

import static com.thesearch.dictionary_manager.BkTree.levDist;
import com.thesearch.dictionary_manager.Match;
import com.thesearch.dictionary_manager.Suggestion;


/**
 * class Dictionary
 * Stores a dictionary and all its functions.
 * Initially, the words are found in a dictionary document, generated with scowl (its a free spell checking dynamic dictionary. The frequencies, on the other
 * hand, are obtained from a file containing huge amounts of english texts, from all fields on spoken/written english.
 *
 * Soon, to improve performance, we will need to instead of calculating occurences every time the dicitionary is created, we store occurences in the
 * dictionary file and after the last word we put the total number of words, so we can get frequencies for all words.
 * Final update: new dictionary files contains words and frequencies and it works.
 */
public class Dictionary {
    private BkTree _dict = new BkTree();
    final static Charset ENCODING = StandardCharsets.UTF_8;

    public Dictionary(String dictFreqFile) {

        //createFreqDict("english.txt", "big-v2.txt", "dictfreq.txt");

        initializeDictWithFreq(dictFreqFile);
    }

    private void initializeDictWithFreq(String dictFreqFile){
        //Path path = Paths.get("src/com/thesearch/dictionary_manager", dictFreqFile);
        InputStream path = Dictionary.class.getResourceAsStream(dictFreqFile);
        BufferedReader br = null;
        _dict = new BkTree();
        String[] words;

        try {
            //br = Files.newBufferedReader(path, ENCODING);
            br = new BufferedReader(new InputStreamReader(path));
            String Line = br.readLine();
            double wordCount = Double.parseDouble(Line);
            while ((Line = br.readLine()) != null) {
                words = Line.split(" ");
                _dict.add(words[0]);
                _dict.setFreq(words[0], Double.parseDouble(words[1])/wordCount);
            }
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
    }

    private void initializeDictOld(String FileName, String FreqFile){
        Path path = Paths.get("src/com/thesearch/dictionary_manager", FileName);
        BufferedReader br = null;
        /**
         * First part of this function, simply stores all the different words,
         * all of which are initialized with frequency zero.
         */
        try {
            br = Files.newBufferedReader(path, ENCODING);
            String Line = null;
            while ((Line = br.readLine()) != null) {
                _dict.add(Line);
            }
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

        /**
         * Now we will calculate the word frequencies
         * and assign them to the respective words in the BkTree
         */
        HashMap<String, Double> freqs = calculateFrequencies(FreqFile);
        Iterator it = freqs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Double> pair = (Map.Entry) it.next();
            _dict.setFreq(pair.getKey(), pair.getValue());
        }
    }

    /**
     * correctString
     * @param query
     * @return
     * Given a string it will correct it.
     * It is still rudimentary, so we will only correct based on frequency and distance.
     * For the correction of the first word of a phrase we will not consider "decapitalizing" a word a correction, as our dicionary doesn't contain all
     * words capitalized. ("decapitalization": Yes -> yes, as our dictionary doesn't contain the word Yes, but it contains Yes)
     */
    public Suggestion correctQuery(String query){
        String proposition = "";

        boolean hasChanges = false; //check if any word was corrected;
        String[] words = query.split("[^\\p{L}0-9']+");
        String[] substitutes = new String[words.length];
        for (int i = 0; i < words.length; ++i){
            //System.out.println(words[i]);
            substitutes[i] = findMostSuitableCandidate(words[i]);
            if (i == 0 && !(words[i].toLowerCase().equals(substitutes[i])) && !(words[i].equals(substitutes[i]))) //First word of a phrase can start with a capital letter even if it's not a proper name
                hasChanges = true;
            if (!(words[i].equals(substitutes[i])) && i != 0) //Other words will only start with a capital letter if they are proper names
                hasChanges = true;
        }

        proposition = Arrays.toString(substitutes).replace(", ", " ").replaceAll("[\\[\\]]", "");

        Suggestion sugg = new Suggestion(proposition, hasChanges);

        return sugg;
    }

    /**
     * This functions will not analyse the query semantically,
     * it will just analyse the ortographe and word frequency,
     * the suggestion algorithm is temporary.
     */
    private String findMostSuitableCandidate(String word){
        int dist = 0;
        Set<Match> m = search(word, dist);
        //System.out.println(word);
        boolean foundOnDistZero = false;
        if (!m.isEmpty()) {foundOnDistZero = true;}
        m = search(word.toLowerCase(), dist);
        if (!m.isEmpty()) {foundOnDistZero = true;}
        dist += 1;
        while (m.isEmpty()){
            m = search(word, dist);
            dist++;
        }
        Set<Match> aux = search(word, dist);
        Match candidate = new Match("", Integer.MAX_VALUE, 0.0);
        Match res = new Match("", Integer.MAX_VALUE, -1.0);
        Iterator<Match> it = m.iterator();
        while (it.hasNext()){
            candidate = it.next();
            if ((candidate.getDist() <= res.getDist()) && (candidate.getFreq() > res.getFreq()))
                res = candidate;
        }
        /**
         * We're still gonna check the next distance

        it = aux.iterator();
        while (it.hasNext()){
            candidate = it.next();
            if ((candidate.getFreq() >= 100000*res.getFreq()) && (!foundOnDistZero))
                res = candidate;
        }
        */
        if (foundOnDistZero)
            return word;
        return res.getMatch();
    }

    /**
     * This function will calculate word frequencies based on the file
     * passed as parameter.
     */
    public HashMap<String, Double> calculateFrequencies(String FreqFile){
        Path path = Paths.get("src/com/thesearch/dictionary_manager", FreqFile);
        BufferedReader br = null;
        HashMap<String, Double> FreqMap = new HashMap<>();
        int WordCount = 0;
        String[] LineWords;
        try{
            br = Files.newBufferedReader(path, ENCODING);
            String Line = null;
            while((Line = br.readLine()) != null){
                LineWords = Line.split("[^\\p{L}0-9']+");
                WordCount += LineWords.length;
                for(int i = 0; i < LineWords.length; ++i) {
                    if (FreqMap.containsKey(LineWords[i]))
                        FreqMap.put(LineWords[i], FreqMap.get(LineWords[i]) + 1.0);
                    else
                        FreqMap.put(LineWords[i], 1.0);
                }

            }
            //System.out.println("Unique word count: " + FreqMap.size()    );
        }catch(IOException e){
            System.out.println("La lecture de l'archive a echoue");
        } finally {
            try{
                if (br != null)
                    br.close();
            }catch(IOException e){
                System.out.println("La fermeture de l'archive a echoue");
            }
        }

        /**
         * After having the occurence list, we are going to change the
         * occurance count in the HashMap, to a relative frequency.
         */
        Iterator it = FreqMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, Double> pair = (Map.Entry)it.next();
            //System.out.println(pair.getKey() + " -> " + pair.getValue());
            FreqMap.put(pair.getKey(), pair.getValue()/(double)WordCount);
            //System.out.println(pair.getKey() + " -> " + FreqMap.get(pair.getKey()));
        }
        return FreqMap;
    }

    private void calculateStaticOccurences(String file){

    }

    public BkTree getTree(){
        return this._dict;
    }

    public Set<Match> search(String word, int maxDist){
        if (word == null) throw new NullPointerException();
        if (maxDist < 0) throw new IllegalArgumentException("Distance maximale doit etre positif");

        Set<Match> matches = new HashSet<>();

        Queue<Node> queue = new ArrayDeque<>();
        queue.add(_dict.getRoot());

        while (!queue.isEmpty()){
            Node node = queue.remove();
            String mot = node.getElement();
            Double freq = node.getFrequency();
            int distance = levDist(mot, word);

            if (distance < 0) throw new IllegalArgumentException(format("Distance (%d) entre les mots (%s) et (%s)", distance, word, mot));
            if (distance <= maxDist) matches.add(new Match(mot, distance, freq));

            int distSearchMin = max(distance - maxDist, 0);
            int distSearchMax = distance + maxDist;

            for (int distSearch = distSearchMin; distSearch <= distSearchMax; ++distSearch){
                Node child = node.getChildrenNode(distSearch);
                if (child != null)
                    queue.add(child);
            }

        }

        return matches;
    }


    /**
     * createFreqDict
     * @param dictFile
     * @param freqFile
     * @param freqDictFile
     *
     * Receives a dictionary of words, a training sequence (the gigantic text) and the name of a file to be the new freq+words dict.
     * This funcition will copy all words from dictFile into freqDictFile and add the frequences calculated through freqFile.
     * The format of the new dictionary will be <word> <number of occurrences>, and the last word is gonna be the total number of words
     */
    public void createFreqDict(String dictFile, String freqFile, String freqDictFile){
        Path path = Paths.get("src/com/thesearch/dictionary_manager", dictFile);
        BufferedReader br = null;
        BkTree bk = new BkTree();
        HashMap<String, Double> FreqMap = new HashMap<>();
        /**
         * First part of this function, simply stores all the different words,
         * all of which are initialized with frequency zero.
         */
        try {
            br = Files.newBufferedReader(path, ENCODING);
            String Line = null;
            while ((Line = br.readLine()) != null) {
                bk.add(Line);
                FreqMap.put(Line, 0.0);
            }
        } catch (IOException e) {
            System.out.println("Failed to read the file");
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                System.out.println("Failed to close the file");
            }
        }

        path = Paths.get("src/com/thesearch/dictionary_manager", freqFile);
        br = null;
        int WordCount = 0;
        String[] LineWords;
        try{
            br = Files.newBufferedReader(path, ENCODING);
            String Line = null;
            while((Line = br.readLine()) != null){
                LineWords = Line.split("[^\\p{L}0-9']+");
                Set<Match> m = new HashSet<>();
                for(int i = 0; i < LineWords.length; ++i) {
                    m = bk.search(LineWords[i], 0);
                    if (!(m.isEmpty())) {
                        WordCount++;
                        FreqMap.put(LineWords[i], FreqMap.get(LineWords[i]) + 1.0);
                    }
                }

            }
            //System.out.println("Unique word count: " + FreqMap.size()    );
        }catch(IOException e){
            System.out.println("Failed to read the file");
        } finally {
            try{
                if (br != null)
                    br.close();
            }catch(IOException e){
                System.out.println("Failed to close the file");
            }
        }

        BufferedWriter bw = null;
        path = Paths.get("src/com/thesearch/dictionary_manager", freqDictFile);
        try{
            bw = Files.newBufferedWriter(path, ENCODING);
            String Line = "";

            Iterator it = FreqMap.entrySet().iterator();
            bw.write(Double.toString(WordCount) + "\n");
            while (it.hasNext()){
                Map.Entry<String, Double> pair = (Map.Entry)it.next();

                Line = pair.getKey() + " " + pair.getValue().toString() + "\n";

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
}
