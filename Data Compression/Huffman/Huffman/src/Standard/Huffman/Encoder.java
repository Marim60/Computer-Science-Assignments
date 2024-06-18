package Standard.Huffman;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Encoder {
    Node root;
    Map<Character,String> HuffmanTable = new LinkedHashMap<>();
    private static int[] array;
    private PriorityQueue<Node> pq = new PriorityQueue<Node>(new NodeComparator());

    static {
        array = new int[26];
    }
    class Node {
        public int freq;
        public Node left;
        public Node right;
        public int code;
        public char aChar =' ';

    }
    class NodeComparator implements Comparator<Node> {
        // Overriding compare()method of Comparator
        // for descending order of ascending
        public int compare(Node n1, Node n2) {
            if (n1.freq < n2.freq)
                return -1;
            else if (n1.freq > n2.freq)
                return 1;
            return 0;
        }
    }
    public void encode(String text) {
        for (int i = 0; i < text.length(); i++) {
            int index = text.charAt(i) - 'A' ;
            array[index]++;
        }
        for (int i = 0; i < 26; i++) {
            Node tempNode = new Node();
            if (array[i] != 0) {
                tempNode.freq = array[i];
                tempNode.aChar = (char) (i + 'A');
                System.out.println( tempNode.aChar + " "+ tempNode.freq);
                pq.add(tempNode);

            }
        }
        root = constructSubTree();

    }
    public Node constructSubTree() {
        while (!pq.isEmpty()) {
            Node sum = new Node();
            Node suml = new Node();
            Node sumR = new Node();
            suml = pq.poll();

            if (!pq.isEmpty()) {
                sumR = pq.poll();
            }
            else  {
                return suml;
            }
            sum.freq = sumR.freq+suml.freq;
            sum.left = suml;
            sum.left.code = 0;
            sum.right = sumR;
            sum.right.code = 1;

            pq.add(sum);
        }
        return null;
    }
    public PrintWriter printWriter;
    public void code(Node root, String s) throws FileNotFoundException {
        printWriter = new PrintWriter("EncodingCode");
        if(root.left == null && root.right == null&& Character.isLetter(root.aChar) )
        {
             HuffmanTable.put(root.aChar,s);
             System.out.println(root.aChar + " " + s);
             return;
        }
        assert root.left != null;
        code(root.left, s + "0");
        code(root.right, s + "1");
        printWriter.close();
    }

    String printOnFile(String text) throws FileNotFoundException {
        printWriter = new PrintWriter("EncodingCode");
        for (Map.Entry<Character,String> entry : HuffmanTable.entrySet())
        {
            printWriter.println(entry.getKey() + " " + entry.getValue());
        }
        String textCode = "";
        for (int i = 0; i < text.length(); i++)
        {
            textCode += HuffmanTable.get(text.charAt(i));
        }
        printWriter.println("Code = " + textCode);
        System.out.println("Code = " + textCode);
        printWriter.println("Original Size = " + text.length() * 8 + " bit");
        printWriter.println("Compression Size = " + (textCode.length() + HuffmanTable.size() * 8) + " bit");
        printWriter.close();
        return textCode;
    }


}
