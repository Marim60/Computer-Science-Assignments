/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PositionalIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author ehab
 */
public class TestPositional {

    public static void main(String args[]) throws IOException {
        Index5 index = new Index5();
        //|**  change it to your collection directory 
        //|**  in windows "C:\\tmp11\\rl\\collection\\"       
        String files = "tmp11/rl/collection/";

        File file = new File(files);
        //|** String[] 	list()
        //|**  Returns an array of strings naming the files and directories in the directory denoted by this abstract pathname.
        String[] fileList = file.list();

        fileList = index.sort(fileList);
        index.N = fileList.length;

        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = files + fileList[i];
        }
        index.buildIndex(fileList);
        index.store("index");
        index.printDictionary();

        String test3 = "data  should plain greatest comif"; // data  should plain greatest comif
        System.out.println("Boolean Model result = \n" + index.find_24_01(test3, 1));

        String phrase = "";
        do {
            System.out.println("Print search phrase: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            phrase = in.readLine();

            String answer = index.find_24_01(phrase, 1);
            if(answer.isEmpty())
                System.out.println("Boolean Model result for '" + phrase + "' = None\n");
            else
                System.out.println("Boolean Model result for '" + phrase + "' = \n" + answer);
        } while (!phrase.isEmpty());

    }
}
