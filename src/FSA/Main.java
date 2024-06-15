package FSA;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        File inputFile = new File("src/FSA/input.txt");
        File outputFile = new File("src/FSA/output.txt");
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.equalsIgnoreCase("end")) {
                    // skip new line
                    reader.readLine();
                   Utility.chooseProblem(outputFile, lines);
                    lines.clear();
                }

                else
                    lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}