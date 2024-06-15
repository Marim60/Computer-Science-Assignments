package Grammar;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of the problem you want to solve: ");
        System.out.println("1. PDA");
        System.out.println("2. CFG");
        int choice = scanner.nextInt();
        String fileInput;
        String fileOutput;
        switch (choice) {
            case 1:
                fileInput = "src/Grammar/input_pda.txt";
                fileOutput = "src/Grammar/output_pda";
                break;
            case 2:
                fileInput = "src/Grammar/input_cfg.txt";
                fileOutput = "src/Grammar/output_cfg";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + choice);
        }

        File inputFile = new File(fileInput);
        File outputFile = new File(fileOutput);
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.equals("end")) {
                    switch (choice) {
                        case 1:
                            Utility.chooseProblemPDA(outputFile, lines);
                            break;
                        case 2:
                            Utility.chooseProblemCFG(outputFile, lines);
                            break;
                    }
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
