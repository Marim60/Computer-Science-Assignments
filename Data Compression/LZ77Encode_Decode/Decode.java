package Mariam.com;

import javax.swing.plaf.IconUIResource;
import java.util.Scanner;
import java.util.Vector;

public class Decode {
   public static void decode(Vector<Tag> tag)
    {
        String text = "";
        for (int i = 0; i < tag.size(); i++) {
            if (tag.get(i).length == 0 && tag.get(i).position == 0) {
                text += tag.get(i).nextSymbol;
                continue;
            }
            int len = text.length();
            int start = len - tag.get(i).position;
            text += text.substring(start, tag.get(i).length + start);
            text += tag.get(i).nextSymbol;
        }
        System.out.println(text);
    }
    public static void main(String[] args) {
        int sizeOfTag;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Number of tags: ");
        sizeOfTag = scanner.nextInt();
        Vector<Tag>tag = new Vector<Tag>(sizeOfTag);
        System.out.println("Enter tags: ");
        for (int i = 0; i < sizeOfTag; i++)
        {
            int length , position;
            char nextSymbol;
            position = scanner.nextInt();
            length = scanner.nextInt();
            nextSymbol = scanner.next().charAt(0);
            if(nextSymbol == '$')
                nextSymbol = ' ';
            Tag t = new Tag(position,length,nextSymbol);
            tag.add(i, t);
        }
        decode(tag);
    }

}
// Text
//          8
//        0 0 A
//        0 0 B
//        2 1 A
//        3 2 B
//        5 3 B
//        2 2 B
//        5 5 B
//        1 1 A