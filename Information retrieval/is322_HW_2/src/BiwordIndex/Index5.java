/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BiwordIndex;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Math.log10;
import static java.lang.Math.sqrt;

import java.util.*;
import java.io.PrintWriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ehab
 */
public class Index5 {
    //--------------------------------------------
    int N = 0;
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.
    public HashMap<String, DictEntry> index; // The bi-word index
    //--------------------------------------------
    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }
    public void setN(int n) { N = n; }
    //---------------------------------------------
    // print the posting list
    public void printPostingList(Posting p) {
        System.out.print("[");
        while (p != null) {
            System.out.print("" + p.docId);
            p = p.next;
            if (p != null) // if not the last id print comma
                System.out.print(",");
        }
        System.out.println("]");
    }
    //---------------------------------------------
    // print the stored bi-word index by iterating over HashMap 'index' and print pair of term
    // and his document frequency, then call printPostingList to print posting list that
    // associated with the current term
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry dd = (DictEntry) pair.getValue();
            System.out.print("** [" + pair.getKey() + ", " + dd.doc_freq + "]       =--> ");
            printPostingList(dd.pList);
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //-----------------------------------------------
    // build the bi-word index from disk not from the internet
    public void buildIndex(String[] files) {
        int fid = 0;
        // iterate over file names
        for (String fileName : files) {
            // read the file
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                // check to see if the current file is already in the sources, if not
                // associate 'file id' to current file
                if (!sources.containsKey(fileName)) {
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                }
                String ln;
                int flen = 0;
                String prev_word = "";
                // read the file line by line
                while ((ln = file.readLine()) != null) {
                    // return last word in the line and flen
                    AbstractMap.SimpleEntry<String, Integer> res = indexOneLine(ln, fid, prev_word);
                    prev_word = res.getKey();
                    flen += res.getValue();
                }
                // set the length of the document
                sources.get(fid).length = flen;
            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            // increment the file id
            fid++;
        }
    }

    //----------------------------------------------------------------------------
    // add the word to the dictionary, if the word already exist, update the posting list
    public void addWord(String word, int fid){
        // check to see if the word is not in the dictionary, if not add it
        if (!index.containsKey(word)) {
            index.put(word, new DictEntry());
        }

        // add document id to the posting list
        if (!index.get(word).postingListContains(fid)) {
            index.get(word).doc_freq += 1; // update doc freq to the number of doc that contain the term
            // if term not exist before, posting list will be null, so we need
            // to handle this the case, else add the new doc id to the posting list
            if (index.get(word).pList == null) {
                index.get(word).pList = new Posting(fid);
                index.get(word).last = index.get(word).pList;
            }
            else {
                index.get(word).last.next = new Posting(fid);
                index.get(word).last = index.get(word).last.next;
            }
        }
        else {
            // if document id exist, update document term frequency
            index.get(word).last.dtf += 1;
        }
        // update the term_freq in the collection
        index.get(word).term_freq += 1;
    }

    // index one line after read it from the file, its take the line and file id
    // that this line from it
    // return the last word in the line and the length of the line
    public AbstractMap.SimpleEntry<String, Integer> indexOneLine(String ln, int fid, String prev_word) {
        int flen = 0;
        // split the line into words
        String[] words = ln.split("\\W+");
        //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        // add the line length to update the document length
        flen += words.length;

        // iterate over the words
        for (int i = 0; i < words.length; i++) {
            // preprocessing
            words[i] = words[i].toLowerCase();
            if (stopWord(words[i]) || Number(words[i])) {
                continue;
            }
            // stem the word
            words[i] = stemWord(words[i]);

            // store single terms
            addWord(words[i], fid);
            // store adjacent terms
            if (!prev_word.isEmpty()) {
                addWord(prev_word + "_" + words[i], fid);
            }

            // update the prev word to get adjacent words correctly
            prev_word = words[i];

            if (words[i].equalsIgnoreCase("lattice")) {
                System.out.println("  <<" + index.get(words[i]).getPosting(1) + ">> " + ln);
            }
        }

        return new AbstractMap.SimpleEntry<>(prev_word, flen);
    }
    //----------------------------------------------------------------------------
    // intersect 2 posting list to get the intersection between them
    Posting intersect(Posting pL1, Posting pL2) {
        Posting answer = null;
        Posting last = null;
        while (pL1 != null && pL2 != null) {
            if (pL1.docId == pL2.docId) {
                if (answer == null) {
                    answer = new Posting(pL1.docId, pL1.dtf + pL2.dtf);
                    last = answer;
                } else {
                    last.next = new Posting(pL1.docId, pL1.dtf + pL2.dtf);
                    last = last.next;
                }
                pL1 = pL1.next;
                pL2 = pL2.next;
            } else if (pL1.docId < pL2.docId) {
                pL1 = pL1.next;
            }
            else {
                pL2 = pL2.next;
            }
        }
        return answer;
    }
    //----------------------------------------------------------------------------
    // get the words from the phrase and preprocess them
    public List<String> getWords(String phrase){
        // list to save the final words that we will use in the search query
        List<String> final_words = new ArrayList<>();
        // use regular expression to match any words between double quotes " "
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(phrase);
        // find matched words between double quotes
        while (matcher.find()) {
            // get the matched word
            String res = matcher.group();
            // remove the double quotes from the matched word
            phrase = phrase.replace(res, " ");
            // split the matched word into words
            String[] words = res.split("\\W+");
            int size = 0; // save the size of the preprocessed and accepted words
            for (String word : words){
                // preprocessing
                word = word.toLowerCase();
                if (stopWord(word) || Number(word)) {
                    continue;
                }
                // stem the word
                word = stemWord(word);

                // update words with preprocessed words
                words[size] = word;
                size++;
            }

            // check if there is only one word between the double quotes
            if(size == 1)
                // add the word to the final list
                final_words.add(words[0]);

            // make each adjacent terms connected by underscore _
            for (int i = 1; i < size; i++)
                final_words.add(words[i-1] + "_" + words[i]);
        }

        // word on the rest words which not adjacent
        String[] words = phrase.split("\\W+");
        for (String word : words){
            // preprocessing
            word = word.toLowerCase();
            if (stopWord(word) || Number(word)) {
                continue;
            }
            // stem the word
            word = stemWord(word);
            // add processed word to the final list
            final_words.add(word);
        }

        // return the final list of words
        return final_words;
    }
    //----------------------------------------------------------------------------
    // search for the phrase in the index
    public String find_24_01(String phrase) { // any number of terms non-optimized search
        String result = "";
        List<String> words = getWords(phrase);

        // check if the word exist in the index, if any word not exist this mean no document contain
        // this phrase, so we will return empty result
        Posting posting = null;
        for (String word : words) {
            //System.out.println(word);
            if (index.containsKey(word)) { // word exist in the index
                // if this is the first word, we will assign the posting list to the result
                if (posting == null) {
                    posting = index.get(word).pList;
                }// if this is not the first word, we will intersect the current posting list with the new one
                else {
                    posting = intersect(posting, index.get(word).pList);
                }
            }
            else { // word not exist in the index, return empty result
                return result;
            }
        }

        while (posting != null) {
            //System.out.println("\t" + sources.get(num));
            result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
            posting = posting.next;
        }
        return result;
    }

    // check if the word is a number using regular expression
    boolean Number(String word) {
        String regex = "^[+-]?[0-9]+\\.?[0-9]*$";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(word);
        return matcher.matches();
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
    //----------------------------------------------------------------------------
    // return the list of document id intersection between 2 posting list by exploiting
    // the sorted lists
    //----------------------------------------------------------------------------


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

    //---------------------------------
    //store index into hard disk
    public void store(String storageName) {
        try {
            String pathToStorage = "tmp11/rl/"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().URL + ", Value = " + entry.getValue().title + ", Value = " + entry.getValue().text);
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
    //create a new storage file
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
    //----------------------------------------------------
    //load index from hard disk into memory
    public HashMap<String, DictEntry> load(String storageName) {
        try {
            String pathToStorage = "tmp11/rl/"+storageName;
            sources = new HashMap<Integer, SourceRecord>();
            index = new HashMap<String, DictEntry>();
            BufferedReader file = new BufferedReader(new FileReader(pathToStorage));
            String ln = "";
            int flen = 0;
            while ((ln = file.readLine()) != null) {
                if (ln.equalsIgnoreCase("section2")) {
                    break;
                }
                String[] ss = ln.split(",");
                int fid = Integer.parseInt(ss[0]);
                try {
                    System.out.println("**>>" + fid + " " + ss[1] + " " + ss[2].replace('~', ',') + " " + ss[3] + " [" + ss[4] + "]   " + ss[5].replace('~', ','));

                    SourceRecord sr = new SourceRecord(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]), Double.parseDouble(ss[4]), ss[5].replace('~', ','));
                    //   System.out.println("**>>"+fid+" "+ ss[1]+" "+ ss[2]+" "+ ss[3]+" ["+ Double.parseDouble(ss[4])+ "]  \n"+ ss[5]);
                    sources.put(fid, sr);
                } catch (Exception e) {

                    System.out.println(fid + "  ERROR  " + e.getMessage());
                    e.printStackTrace();
                }
            }
            while ((ln = file.readLine()) != null) {
                //     System.out.println(ln);
                if (ln.equalsIgnoreCase("end")) {
                    break;
                }
                String[] ss1 = ln.split(";");
                String[] ss1a = ss1[0].split(",");
                String[] ss1b = ss1[1].split(":");
                index.put(ss1a[0], new DictEntry(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));
                String[] ss1bx;   //posting
                for (int i = 0; i < ss1b.length; i++) {
                    ss1bx = ss1b[i].split(",");
                    if (index.get(ss1a[0]).pList == null) {
                        index.get(ss1a[0]).pList = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).pList;
                    } else {
                        index.get(ss1a[0]).last.next = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).last.next;
                    }
                }
            }
            System.out.println("============= END LOAD =============");
            //    printDictionary();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }
}

//=====================================================================
