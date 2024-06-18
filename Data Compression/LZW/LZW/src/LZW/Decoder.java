package LZW;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class Decoder {
    Map<Integer, String> dictionary = new LinkedHashMap<>();
    Vector<Integer> compressed = new Vector<>();
    public Decoder(Vector<Integer> compressed)
    {
        this.compressed = compressed;
    }
    public void decode() throws IOException {
        for (int i = 0; i < 256; i++) {
            String symbol = "";
            symbol += (char) i;
            dictionary.put(i, symbol);
        }
        String Text = "";
        int oldIndex = this.compressed.get(0);
        int id = 256;
        String subString = dictionary.get(oldIndex);
        char currentChar = subString.charAt(0);
        Text += (subString);
        for (int i = 1; i < this.compressed.size(); i++) {
            int currentIndex = this.compressed.get(i);
            if (dictionary.get(currentIndex) == null) {
                subString = dictionary.get(oldIndex) + currentChar;
            } else
                subString = dictionary.get(currentIndex);
            Text += (subString);
            currentChar =  subString.charAt(0);
            dictionary.put(id, dictionary.get(oldIndex) + currentChar);
            id++;
            oldIndex = currentIndex;
        }
        FileWriter fileWriter = new FileWriter("DecompressedText");
        fileWriter.write(Text);
        fileWriter.close();
        System.out.println("\n Decompressed Data: " + Text);
    }

}
