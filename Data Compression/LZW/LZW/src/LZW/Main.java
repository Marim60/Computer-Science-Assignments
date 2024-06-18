package LZW;

import LZW.Decoder;
import LZW.Encoder;

import java.io.*;
import java.util.Scanner;
import java.util.Vector;

public class Main {
   public static void main(String[] args) throws IOException {
       StringBuilder Text = new StringBuilder();
       FileReader fr = new FileReader("DataToCompressed");
       File file = new File("CompressedData");
       Scanner scanner = new Scanner(file);
       int i;
       while ((i = fr.read()) != -1) {
           String s  = (char) i  + "";
           Text.append(s);
       }
       Text.append("$");
       Encoder encoder = new Encoder(Text.toString());
       encoder.encode();
       Vector<Integer> compressed = new Vector<>();
        while (scanner.hasNext())
        {
            compressed.add(scanner.nextInt());
        }
        Decoder decoder = new Decoder(compressed);
        decoder.decode();

  }

}
