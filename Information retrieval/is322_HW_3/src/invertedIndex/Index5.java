/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static java.lang.Math.log10;
import static java.lang.Math.sqrt;

/**
 *
 * @author ehab
 */

public class Index5 {
    int N = 0;
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.
    public HashMap<String, DictEntry> index; // THe inverted index
    //--------------------------------------------

    SortedScore sortedScore;
    //--------------------------------------------
    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }

    public void setN(int n) {
        N = n;
    }
    //---------------------------------------------
    // print the posting list
    public void printPostingList(Posting p) {
        System.out.print("[");
        while (p != null) {
            System.out.print("" + p.docId);
            System.out.print(" (" + p.dtf + ")");
            p = p.next;
            if (p != null) // if not the last id print comma
                System.out.print(", ");
        }
        System.out.println("]");
    }
    //---------------------------------------------
    // print the stored inverted index by iterating over HashMap 'index' and print pair of term
    // and his document frequency, then call printPostingList to print posting list that
    // associated with the current term
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry dd = (DictEntry) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "]       =--> ");
            printPostingList(dd.pList);
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }
    //----------------------------------------------------------------------------
    // build the inverted index from the internet
    public int buildIndex(String ln, int fid) {
        int flen = 0;
        // split the line into words
        String[] words = ln.split("\\W+");
        //  String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");

        // add the line length to update the document length
        flen += words.length;

        // iterate over the words
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
                continue;
            }
            word = stemWord(word);
            // check to see if the word is not in the dictionary
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
                // if term not exist before, posting list will be null, so we need
                // to handle this the case, else add the new doc id to the posting list
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
            }
            //set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {
                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }
        }
        return flen;
    }
    //----------------------------------------------------------------------------
    // check if the word is a stop word
    boolean stopWord(String word) {
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {
            return true;
        }
        return false;

    }
    //----------------------------------------------------------------------------
    String stemWord(String word) { //skip for now
        return word;
//        Stemmer s = new Stemmer();
//        s.addString(word);
//        s.stem();
//        return s.toString();
    }
    //---------------------------------
    String[] sort(String[] words) {  //bubble sort
        boolean sorted = false;
        String sTmp;
        //-------------------------------------------------------
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {
                int compare = words[i].compareTo(words[i + 1]);
                if (compare > 0) {
                    sTmp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }
    //==========================================================
    public String find_07(String phrase) {
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;
        sortedScore = new SortedScore();

        double scores[] = new double[N];
        double query_term_weight[] = new double[len];
        double query_term_normalization[] = new double[len];

        //1 float Scores[N] = 0
        for (int i = 0; i < N; i++)
            scores[i] = 0.0;

        //2 Initialize Length[N]
        double Length[] = new double[N];

        // compute query normalization
        int idx = 0;
        double query_accumulator_weight = 0.0;

        // for each query term t
        for (String term : words) {
            // preprocessing
            term = term.toLowerCase();
            if (stopWord(term))
                continue;
            term = stemWord(term);

            // check if the term exist in the inverted index
            if(!index.containsKey(term))
                continue;

            //4 do calculate w(t, q)
            int tf = 1; // as this only one term for now (we don't combine, so we will consider each with 1 frequency)
            int df = index.get(term).doc_freq; // number of documents that contains the term
            double idf = df > 0 ? log10(N / (double)df) : 0;

            // compute query weight for each term in the query
            query_term_weight[idx++] = tf * idf;
            query_accumulator_weight += (idf * idf); // to normalize the weights
        }
        query_accumulator_weight = sqrt(query_accumulator_weight);
        // for each query term t
        for (int i = 0; i < idx ; i++) {
            // normalize the query term
            query_term_normalization[i] = query_term_weight[i] / query_accumulator_weight;
        }

        // compute the score with each document
        idx = 0;
        //3 for each query term t
        for (String term : words) {
            // preprocessing
            term = term.toLowerCase();
            if (stopWord(term))
                continue;
            term = stemWord(term);

            // check if the term exist in the inverted index
            if(!index.containsKey(term))
                continue;

            //4 do calculate w(t, q) and fetch postings list for t
            Posting p = index.get(term).pList; //fetch postings list

            //4.a compute idf
            int df = index.get(term).doc_freq; // number of documents that contains the term
            double idf = df > 0 ? log10(N / (double)df) : 0;

            // print some details about the term
            //System.out.print("term= " + term + " \t " + " df= " + df+ " \t " + " idf= " + String.format("%.3f", idf) +
            //                 " \t " + " posting list= "); printPostingList(p);

            //5 for each pair(doc_id, dtf) in postings list
            while (p != null) {
                //6 add the term score for (term/doc) to score of each doc
                double tf = p.dtf > 0 ? (1 + log10(p.dtf)) : 0;
                scores[p.docId] += (tf * idf) * query_term_normalization[idx];
                p = p.next;
            }
            idx++;
        }

        //Normalize for the length of the doc
        //7 Read the array Length[d]
        for (int fid = 0; fid < N; fid++) {
            Length[fid] = 1 + log10(sources.get(fid).length);
        }

        //8 for each d
        for (int fid = 0; fid < N; fid++) {
            //9 do Scores[d] = Scores[d]/Length[d]
            scores[fid] = Length[fid] > 0 ? scores[fid] / Length[fid] : 0.0; //Math.sqrt(Length[fid])
            sortedScore.insertScoreRecord(scores[fid], fid, sources.get(fid).URL, sources.get(fid).title, sources.get(fid).text);
        }

        //10 return Top K components of Scores[]
        System.out.println("Cosine similarity ranking model result for '" + phrase + "' :");
        result = sortedScore.printScores(); // we edit this function to get only top 10
        System.out.println();

        return result;
    }
    /////---------------------------------
    public void searchLoop() {
        String phrase;
        do {
            System.out.print("Write search phrase: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            try {
                phrase = in.readLine();
                find_07(phrase);
                /*if(answer.isEmpty())
                    System.out.println("Cosine similarity ranking model result for '" + phrase + "' = None\n");
                else
                    System.out.println("Cosine similarity ranking model result for '" + phrase + "' = \n" + answer);*/
            }
            catch (Exception e) {
                e.printStackTrace();
                break;
            }
        } while (!phrase.isEmpty());
    }

    //---------------------------------
    public void store(String storageName) {
        try {
            String pathToStorage = "tmp11/rl/"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", URL = " + entry.getValue().URL + ", Title = " + entry.getValue().title + ", Text = " + entry.getValue().text);
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ","); //String formattedDouble = String.format("%.2f", fee );
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }
            wr.write("section2" + "\n");

            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                //  System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting p = dd.pList;
                while (p != null) {
                    //    System.out.print( p.docId + "," + p.dtf + ":");
                    wr.write(p.docId + "," + p.dtf + ":");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============EBD STORE=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //=========================================
    public boolean storageFileExists(String storageName){
        java.io.File f = new java.io.File("tmp11/rl/"+storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;
            
    }
    //----------------------------------------------------
    public void createStore(String storageName) {
        try {
            String pathToStorage = "tmp11/"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            wr.write("end" + "\n");
            wr.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}