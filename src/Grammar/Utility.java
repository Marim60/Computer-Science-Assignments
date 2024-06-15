package Grammar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
public class Utility {
    public static void writeFile(File output, String line) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output,true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
    public static void runnPDA(File output, List<String> lines , PDA pda )throws IOException
    {
        for (String line : lines)
        {
            if(pda.simulate(line))
            {
                writeFile(output, "Accepted");
                System.out.println(line + " -> True");
            }
            else
            {
                writeFile(output,"Not Accepted");
                System.out.println(line + " -> False");

            }
        }
        System.out.println("--------------------");

    }
    public static void chooseProblemPDA(File output, List<String> lines )throws IOException
    {
        int choice = Integer.parseInt(lines.get(0));
        writeFile(output,lines.get(0)); // number of problem
        System.out.println("Problem "+lines.get(0)+":");
        lines.remove(0);
        switch (choice) {
            case 1: {
                PDA pda = new PDA();
                State q0 = pda.addState();
                State q1 = pda.addState();
                State q2 = pda.addState();
                State q3 = pda.addState();
                q0.addTransition('ε','ε','$',q1);
                q1.addTransition('a','ε','a',q1);
                q1.addTransition('b','a','ε',q2);
                q2.addTransition('b','a','ε',q2);
                q2.addTransition('ε','$','ε',q3);
                pda.setStartState(q0);
                pda.addAcceptState(q3);
                pda.addAcceptState(q0);
                runnPDA(output,lines,pda);
            break;
            }
            case 2:
            {
                PDA pda = new PDA();
                State q0 = pda.addState();
                State q1 = pda.addState();
                State q2 = pda.addState();
                State q3 = pda.addState();
                State q4 = pda.addState();
                State q5 = pda.addState();
                q0.addTransition('ε','ε','$',q1);
                q1.addTransition('a','ε','a',q1);
                q1.addTransition('b','ε','ε',q2);
                q2.addTransition('b','a','ε',q3);
                q3.addTransition('b','a','ε',q4);
                q4.addTransition('b','ε','ε',q2);
                q4.addTransition('ε','Z','ε',q5);
                pda.setStartState(q0);
                pda.addAcceptState(q5);
                pda.addAcceptState(q0);
                runnPDA(output,lines,pda);
                break;
            }
            case 3:
            {
                PDA pda = new PDA();
                State q0 = pda.addState();
                State q1 = pda.addState();
                State q2 = pda.addState();
                q0.addTransition('ε','ε','Z',q1);
                q1.addTransition('{','ε','{',q1);
                q1.addTransition('}','{','ε',q1);
                q1.addTransition('ε','Z','ε',q2);
                pda.setStartState(q0);
                pda.addAcceptState(q2);
                pda.addAcceptState(q0);
                runnPDA(output,lines,pda);
                break;
            }


            case 4:{
                PDA pda = new PDA();
                State q0 = pda.addState();
                State q1 = pda.addState();
                State q2 = pda.addState();
                State q3 = pda.addState();
                State q4 = pda.addState();
                q0.addTransition('ε','ε','$',q1);
                q1.addTransition('a','ε','a',q1);
                q1.addTransition('b','a','ε',q2);
                q2.addTransition('b','a','ε',q2);
                q2.addTransition('c','a','ε',q3);
                q3.addTransition('c','a','ε',q3);
                q3.addTransition('ε','$','ε',q4);
                pda.setStartState(q0);
                pda.addAcceptState(q4);
                pda.addAcceptState(q0);
                runnPDA(output,lines,pda);
                break;
        }
        }
    }
    public static void chooseProblemCFG(File output, List<String> lines )throws IOException
    {
        int choice = Integer.parseInt(lines.get(0));
        writeFile(output,lines.get(0)); // number of problem
        System.out.println("Problem "+lines.get(0)+":");
        lines.remove(0);
        switch (choice) {
            case 1: {
                for (String line : lines) {
                    CFG cfg = new CFG(line);
                    if (cfg.Problem1()) {
                        System.out.println(line +" ->True");
                        writeFile(output, "Accepted");
                    } else {
                        System.out.println(line + " ->False");
                        writeFile(output, "Not Accepted");
                    }
                }
                break;
            }
            case 2: {
                for (String line : lines) {
                    CFG cfg = new CFG(line);
                    if (cfg.Problem2()) {
                        System.out.println(line +" ->True");
                        writeFile(output, "Accepted");
                    } else {
                        System.out.println(line +" ->False");
                        writeFile(output, "Not Accepted");
                    }
                }

                break;
            }
            case 3: {
                for (String line : lines) {
                    CFG cfg = new CFG(line);
                    if (cfg.Problem3()) {
                        System.out.println(line +" ->True");
                        writeFile(output, "Accepted");
                    } else {
                        System.out.println(line + " ->False");
                        writeFile(output, "Not Accepted");
                    }
                }
                break;
            }

            case 4: {
                for (String line : lines) {
                    CFG cfg = new CFG(line);
                    if (cfg.Problem4()) {
                        System.out.println(line +" ->True");
                        writeFile(output, "Accepted");
                    } else {
                        System.out.println(line + " ->False");
                        writeFile(output, "Not Accepted");
                    }
                }
                break;
            }
        }
        writeFile(output,"*******************************************************************************************************");
    }

}
