/**
 * BkTree class, main data structure to store the words
 * of the english dictionary.
 * In a first moment stores only the words with their variations,
 * e.g. verbs conjugation, capital letter initialization...
 * Later, stores word frequencies as well.
 *
 *
 * @authors  MATSUMOTO Guilherme, PETRY Gabriel
 * @version 1.0
 * @since   2017-01-21
 */

package com.thesearch.dictionary_manager;

import com.thesearch.dictionary_manager.Match;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.String.format;


/**
 * BkTree is the class defining the data structure used to store the dictionary words.
 * The idea is that a random word will be put as global root, and each branch that comes out of the global root contains only words that have the same
 * Levenhstein distance to it. That porperty is maintained throughout all nodes of the tree.
 */
public final class BkTree {

    private Node _root;

    public BkTree() {
        this._root = null;
    }

    public void add(String element) {
        if (element == null) throw new NullPointerException();

        if (this._root == null)
            this._root = new Node(element);
        else {
            Node node = this._root;
            while (!node.getElement().equals(element)) {
                int dist = levDist(node.getElement(), element);
                Node parent = node;
                node = parent.getChildrenNode(dist);
                if (node == null) {
                    node = new Node(element);
                    parent.childrenNode.put(dist, node);
                    break;
                }
            }
        }
    }

    /**
     * setFreq
     *
     * @param word
     * @param freq Initially all words have frequency 0.0, as that accounts for all words, including those that could not be found in the big.txt document, which
     *             is used to calculate word frequencies in english. Because of that, and the fact that a Node's frequency is private, we need a setFreq function
     *             so qe can change it.
     */
    protected void setFreq(String word, Double freq) {
        if (word == null) throw new NullPointerException();
        if (freq == null) throw new NullPointerException();

        Node node = this._root;
        while (!node.getElement().equals(word)) {
            int dist = levDist(node.getElement(), word);
            Node parent = node;
            node = parent.getChildrenNode(dist);
            if (node == null) {
                break;
            }
        }
        if (node != null) {
            node.setFrequency(freq);
            //System.out.println(node.getElement() + " -> " + node.getFrequency());
        }

        node = this._root;
        while (!node.getElement().toLowerCase().equals(word)) {
            int dist = levDist(node.getElement(), word);
            Node parent = node;
            node = parent.getChildrenNode(dist);
            if (node == null) {
                break;
            }
        }
        if (node != null) {
            node.setFrequency(freq);
        }


    }

    /**
     * getRoot
     *
     * @return Gets the global root.
     */
    public Node getRoot() {
        return this._root;
    }


    static int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    static int levDist(CharSequence a, CharSequence b) {
        int[][] dist = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i < a.length() + 1; i++)
            dist[i][0] = i;
        for (int j = 0; j < b.length() + 1; j++)
            dist[0][j] = j;
        for (int i = 1; i < a.length() + 1; i++)
            for (int j = 1; j < b.length() + 1; j++)
                dist[i][j] = min(dist[i - 1][j] + 1, dist[i][j - 1] + 1, dist[i - 1][j - 1] + ((a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1));

        return dist[a.length()][b.length()];
    }

    public Set<Match> search(String word, int maxDist){
        if (word == null) throw new NullPointerException();
        if (maxDist < 0) throw new IllegalArgumentException("Distance maximale doit etre positif");

        Set<Match> matches = new HashSet<>();

        Queue<Node> queue = new ArrayDeque<>();
        queue.add(_root);

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
}
    /**
     * class Node
     * Stores a node of the BkTree.
     * It contains an english word, its frequency and its childs.
     */
    final class Node {
        private String _element;
        private double _frequency;
        final Map<Integer, Node> childrenNode = new HashMap<>();

        public Node(String e) {
            if (e == null) throw new NullPointerException();
            this._element = e;
            this._frequency = 0.0;
        }

        public String getElement() {
            return this._element;
        }

        public double getFrequency() {
            return this._frequency;
        }

        public void setFrequency(double freq) {
            this._frequency = freq;
        }

        public Node getChildrenNode(Integer dist) {
            return childrenNode.get(dist);
        }
    }


    /*
    fin class Node
     */