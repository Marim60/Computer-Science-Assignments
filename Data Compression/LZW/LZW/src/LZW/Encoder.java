package LZW;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class Encoder {
    String Text;
    Map<String, Integer> dictionary = new LinkedHashMap<>();
    public Vector<Integer> index = new Vector<>();

    public Encoder(String Text) {
        this.Text = Text;
    }

    public void encode() throws IOException {
        for (int i = 0; i < 256; i++) {
            String s = "";
            s += (char) i;
            dictionary.put(s, i);
        }

        String subString = "";
        FileWriter Dictionary = new FileWriter("Dictionary");
        Dictionary.write("Index     Character\n");
        int id = 256;
        for (int i = 0; i < Text.length() - 1; i++) {
            for (int j = i; j < Text.length(); j++) //J =2
            {

                subString += Text.charAt(j);
                System.out.println(dictionary.get(subString) + "  " + subString);

                if (dictionary.get(subString) == null) // do not find in dic
                {
                    dictionary.put(subString, id);
                    id++;
                    break;
                }
            }

            subString = subString.substring(0, subString.length() - 1);

            if (subString.length() > 1)
                i += (subString.length() - 1);

            System.out.println(" RES = " + dictionary.get(subString) + " " + subString);
            Dictionary.write( dictionary.get(subString) + "           " + subString + "\n");
            index.add(dictionary.get(subString));
            subString = "";
        }
        int originalSize = Text.length() * 8 ;
        int Max =  Collections.max(index);
        int CompressedSize = (int)(Math.log(Max) / Math.log(2)) * index.size();
        System.out.println( "\n original Size =  "+originalSize + "\n Compressed Size = " + CompressedSize);
        Dictionary.write("\n original Size =  " + originalSize + "\n Compressed Size = " + CompressedSize);
        Dictionary.close();
        System.out.print("Compressed Data: ");
        for (int i = 0; i < index.size(); i++) {
            System.out.print(index.get(i) + " ");
        }
        FileWriter fWriter = new FileWriter("CompressedData");
        for (int i = 0; i < index.size(); i++) {
            fWriter.write(index.get(i) + " ");
        }
        fWriter.close();
        //System.out.println("\n File is created successfully with the content.");
    }

}

