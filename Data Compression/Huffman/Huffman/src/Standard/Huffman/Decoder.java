package Standard.Huffman;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Decoder {
    public PrintWriter printWriter;
    Map<String,String> HuffmanTable = new LinkedHashMap<>();
    public void fillHuffmanTable() throws FileNotFoundException {
        File file = new File("EncodingCode"); // Change this to your file name
        String code = null;
        String ch = null;
        int cnt = 0;
        Scanner scan = new Scanner(file);
        while (scan.hasNext()) {
            if(cnt % 2 == 0)
            {
                ch = scan.next();
            }
            else
            {
              code = scan.next();
              HuffmanTable.put(code,ch);
            }
            cnt++;
      }
    }
    public void decode(String text) throws IOException {

        String Result = "";
        for (int i = 0; i < text.length(); i++)
        {
            String subString = "";
            for (int j = i; j < text.length(); j++)
            {
                subString += text.charAt(j);
                if(HuffmanTable.get(subString) != null)
                {
                    Result += HuffmanTable.get(subString);
                    break;
                }
            }
            i += subString.length()-1;
        }
        printWriter = new PrintWriter("DecodeResult");
        printWriter.println("Original Text = " + Result);
        printWriter.close();
        System.out.println(Result);
    }
}
