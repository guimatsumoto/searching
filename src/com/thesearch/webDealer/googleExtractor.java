/**
 * @authors  MATSUMOTO Guilherme, PETRY Gabriel
 * @version 1.0
 * @since   2017-01-21
 */

package com.thesearch.webDealer;

import java.io.IOException;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * This class is used to maintain the url to search the requested query and the google suggestion to correct that query.
 * We also can obtain the number of results of any given query, so that we can compare the efficiency in the query correction algorithms.
 */
public class googleExtractor {
    private String _url;
    private String _suggestion;

    public googleExtractor(){
        _url = "";
        _suggestion = "";
    }

    /**
     * Generates a google url with parameter search?q=<query>, so that google suggests a better query
     * @param query
     */
    public void generateURL(String query){
        String[] words = query.split("[^\\p{L}0-9']+");
        _url = "https://www.google.com/search?q=";
        int i = 0;
        _url += words[i];
        for (i = 1; i < words.length; ++i){
            _url += "+" + words[i];
        }
        //System.out.println(_url);
    }

    /**
     * Generates a google url with parameter search?q=<query>&oq=<query>, so that google searches exactly the query, not showing results for a better query
     * @param query
     */
    public void generateFinalURL(String query){
        String[] words = query.split("[^\\p{L}0-9']+");
        _url = "https://www.google.com/search?q=";
        int i = 0;
        _url += words[i];
        for (i = 1; i < words.length; ++i){
            _url += "+" + words[i];
        }

        //the nfpr calls specify that google should show results for that specific query, not what it thinks we meant.
        _url += "&nfpr=1";
        /*
        i = 0;
        _url += words[i];
        for (i = 1; i < words.length; ++i){
            _url += "+" + words[i];
        }
        */
        //System.out.println(_url);
    }

    public void setSugg(String sugg){
        _suggestion = sugg;
    }

    public String getURL(){
        return _url;
    }

    public String getSugg(){
        return _suggestion;
    }

    /**
     * Open the URL (a google search) and retrieves what google believes is a better query.
     *
     * @return the google suggestion to the query.
     */
    public String extractSuggestion(){
        String body = "";
        try {
            Document doc = Jsoup.connect(_url).get();
            if ((doc.select("a.spell").first() == null) || !(doc.select("a.spell").first().hasText()) || (doc.select("a.spell").first().text() == "") || (doc.select("a.spell").first().text() == null))
                return "";
            Element link = doc.select("a.spell").first(); //gets the content of the tag of interest.
            //System.out.println(link.outerHtml());
            body = link.text();
        }catch(IOException e){
            System.out.println("La connexion google a echoue");
        }

        return body;
    }

    /**
     * Return the exact number of results of a givern google query. Returns 0 if no results were found.
     * @param query
     * @return
     */
    public long extractNumberOfResults(String query){
        String body = "";
        generateFinalURL(query);
        long results = 0;
        try {
            Document doc = Jsoup.connect(_url).get();
            if ((doc.select("div#resultStats").first() == null) || !(doc.select("div#resultStats").first().hasText()) || (doc.select("div#resultStats").first().text() == "") || (doc.select("div#resultStats").first().text() == null))
                return -1;
            Element link = doc.select("div#resultStats").first(); //gets the content of the tag of interest.
            //System.out.println(link.outerHtml());
            body = link.text();
            String[] words = body.split("[^\\p{L}0-9']+");
            int count = 1;
            int aux = 0;
            while (true){
                try{
                    aux = Integer.parseInt(words[count]);
                } catch (NumberFormatException e){
                    break;
                }
                results = results*1000;
                results += Integer.parseInt(words[count]);
                //System.out.println(results);
                count++;
            }
            //System.out.println(body);
            //System.out.println(results);
        }catch(IOException e){
            System.out.println("La connexion google a echoue");
        }

        return results;
    }
}
