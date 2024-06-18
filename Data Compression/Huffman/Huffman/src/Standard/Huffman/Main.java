package Standard.Huffman;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        String text="";
        FileReader fr = new FileReader("TextToCompressed");
        int i;
        while ((i = fr.read()) != -1) {
            String s  = (char) i  + "";
            text += s;
        }
        text = text.toUpperCase();
        //Encoder
        Encoder encoder = new Encoder();
        encoder.encode(text);
        encoder.code(encoder.root,"");
        String code = encoder.printOnFile(text);
        //Decoder
        Decoder decoder = new Decoder();
        decoder.fillHuffmanTable();
        decoder.decode(code);
    }
}
//BCAADDDCCACACACB
//1100101011111111100100100100110