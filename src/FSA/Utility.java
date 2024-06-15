package FSA;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.io.IOException;
import java.util.Map;
import java.util.Set;


public  class Utility {
    public static void writeFile(File output, String line) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output,true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

    }
    public static void runFiniteAutomata(File output, List<String> lines , DFA dfa )throws IOException
    {
        for (String line : lines)
        {
            if(dfa.isAccepted(line))
            {
                writeFile(output, "True");
                System.out.println(line + " -> True");
            }
            else
            {
                writeFile(output,"False");
                System.out.println(line + " -> False");

            }
            dfa.reset();
        }
        System.out.println("--------------------");

    }
    public static void runnfa(File output, List<String> lines , NFA nfa )throws IOException
    {
        for (String line : lines)
        {
            if(nfa.simulate(line))
            {
                writeFile(output, "True");
                System.out.println(line + " -> True");
            }
            else
            {
                writeFile(output,"False");
                System.out.println(line + " -> False");

            }
        }
        System.out.println("--------------------");

    }
    public static void addState(Set<Integer> states, Integer n){
        for (int i = 0; i < n; i++)
            states.add(i);
    }



    public static void chooseProblem(File output, List<String> lines )throws IOException
    {
        int choice = Integer.parseInt(lines.get(0));
        writeFile(output,lines.get(0)); // number of problem
        System.out.println("Problem "+lines.get(0)+":");
        lines.remove(0);
        switch (choice)
        {
            case 1:
            {
                //  Problem 01
                Integer initialState = 0;
                Set<Integer> finalState = new HashSet<>();
                finalState.add(0);
                finalState.add(1);
                Set<Integer> states = new HashSet<>();

                addState(states, 2);

                Set<Character> alphabet = new HashSet<>();
                alphabet.add('a');
                alphabet.add('b');
                Map<Integer, Map<Character,Integer>> transitions = Map.of(
                        0, Map.of('a', 0, 'b', 1),
                        1, Map.of('a',-1,'b', 1)
                );

                DFA dfa = new DFA(states, alphabet, finalState, transitions, initialState);
                runFiniteAutomata(output, lines, dfa);

                break;
            }
            case 2:
            {
                //  Problem 02
                Integer initialState = 0;
                Set<Integer> finalState = new HashSet<>();
                finalState.add(2);
                Set<Integer> states = new HashSet<>();
                addState(states, 3);
                states.add(-1);
                Set<Character> alphabet = new HashSet<>();
                alphabet.add('0');
                alphabet.add('1');
                // even number of 0's followed by single number of 1's
                Map<Integer, Map<Character,Integer>> transitions = Map.of(
                        0, Map.of('0', 1, '1', 2),
                        1, Map.of('0', 0, '1', 1),
                        2, Map.of('0', 1, '1', -1),
                        -1, Map.of('0', -1, '1', -1)
                );

                DFA dfa = new DFA(states, alphabet, finalState, transitions, initialState);
                runFiniteAutomata(output, lines, dfa);


                break;
            }

            case 3:
            {
                //  Problem 03
                //  Design a DFA that accepts all strings that contains odd number of x's over {x, y}.
                Integer initialState = 0;
                Set<Integer> finalState = new HashSet<>();
                finalState.add(1);
                Set<Integer> states = new HashSet<>();

                addState(states, 2);

                Set<Character> alphabet = new HashSet<>();
                alphabet.add('x');
                alphabet.add('y');
                Map<Integer, Map<Character,Integer>> transitions = Map.of(
                        0, Map.of('x', 1, 'y', 0),
                        1, Map.of('x', 0, 'y', 1)
                );

                DFA dfa = new DFA(states, alphabet, finalState, transitions, initialState);
                runFiniteAutomata(output, lines, dfa);
                break;
            }
            case 4:
            {
                //  Problem 04
                Integer initialState = 0;
                Set<Integer> finalState = new HashSet<>();

                finalState.add(3);
                finalState.add(4);
                Set<Integer> states = new HashSet<>();
                addState(states, 5);
                Set<Character> alphabet = new HashSet<>();
                alphabet.add('a');
                alphabet.add('b');
                Map<Integer, Map<Character,Integer>> transitions = Map.of(
                        0, Map.of('a', 1, 'b', 2),
                        1, Map.of('a', 3, 'b', 1),
                        2, Map.of('a', 2, 'b', 4),
                        3, Map.of('a', 3, 'b', 1),
                        4, Map.of('a', 2, 'b', 4)
                );
                DFA dfa = new DFA(states, alphabet, finalState, transitions, initialState);
                runFiniteAutomata(output, lines, dfa);

                break;
            }
            case 5:
            {
                //  Problem 05
                // Design a DFA that accepts all the strings that binary integers divisible by 4 over {0,1}.
                Integer initialState = 0;
                Set<Integer> finalState = new HashSet<>();
                finalState.add(1);
                finalState.add(2);
                Set<Integer> states = new HashSet<>();
                addState(states, 3);
                Set<Character> alphabet = new HashSet<>();
                alphabet.add('0');
                alphabet.add('1');
                Map<Integer, Map<Character,Integer>> transitions = Map.of(
                        0, Map.of('0', 1, '1', 0),
                        1, Map.of('0', 2, '1', 0),
                        2, Map.of('0', 2, '1', 0)
                );
                DFA dfa = new DFA(states, alphabet, finalState, transitions, initialState);
                runFiniteAutomata(output, lines, dfa);
                break;
            }

            case 6:
            {
                //  Problem 06
                Integer initialState=0;
                Set<Integer> finalState = new HashSet<>();
                finalState.add(0);
                finalState.add(1);
                finalState.add(4);

                Set<Integer> states = new HashSet<>();
                addState(states, 5);

                Set<Character> alphabet = new HashSet<>();
                alphabet.add('0');
                alphabet.add('1');
                Map<Integer, Map<Character,Integer>> transitions = Map.of(
                        0, Map.of('0', 4, '1', 1),
                        1, Map.of('0', 4, '1', 2),
                        2, Map.of('0', 4, '1', 3),
                        3, Map.of('0', 4, '1', 4),
                        4, Map.of('0', 4, '1', 4)

                        );
                DFA dfa = new DFA(states, alphabet, finalState, transitions, initialState);
                runFiniteAutomata(output, lines, dfa);
                break;
            }
            case 7:
            {
                //  Problem 07
                NFA nfa = new NFA();

                // Define states and transitions
                State q0 = nfa.addState();
                State q1 = nfa.addState();
                State q2 = nfa.addState();
                State q3 = nfa.addState();
                State q4 = nfa.addState();

                q0.addTransition('0', q1);
                q0.addTransition('1', q2);
                q1.addTransition('0', q1);
                q1.addTransition('1', q1);
                q1.addTransition('0', q3);
                q2.addTransition('0', q2);
                q2.addTransition('1', q2);
                q2.addTransition('1', q4);

                nfa.setStartState(q0);
                nfa.addAcceptState(q3);
                nfa.addAcceptState(q4);

                runnfa(output, lines, nfa);

                break;
            }
            case 8:
            {
                //  Problem 08
                NFA nfa = new NFA();
                State q0 = nfa.addState();
                State q1 = nfa.addState();
                State q2 = nfa.addState();
                State q3 = nfa.addState();
                State q4 = nfa.addState();
                State q5 = nfa.addState();
                State q6 = nfa.addState();

                q0.addTransition('0', q0);
                q0.addTransition('1', q0);
                q0.addTransition('0', q1);
                q0.addTransition('1', q2);
                q1.addTransition('1', q3);
                q2.addTransition('0', q4);
                q3.addTransition('0', q5);
                q4.addTransition('1', q6);
                q5.addTransition('1', q5);
                q5.addTransition('0', q5);
                q6.addTransition('0', q6);
                q6.addTransition('1', q6);

                nfa.setStartState(q0);
                nfa.addAcceptState(q6);
                nfa.addAcceptState(q5);

                runnfa(output, lines, nfa);

                break;
            }
            case 9:
            {
                //  Problem 09
                NFA nfa = new NFA();
                State q0 = nfa.addState();
                State q1 = nfa.addState();
                State q2 = nfa.addState();
                State q3 = nfa.addState();
                State q4 = nfa.addState();

                q0.addTransition('0', q1);
                q0.addTransition('1', q2);
                q1.addTransition('1', q3);
                q2.addTransition('0', q4);
                q3.addTransition('0', q1);
                q4.addTransition('1', q2);
                nfa.setStartState(q0);
                nfa.addAcceptState(q3);
                nfa.addAcceptState(q4);
                nfa.addAcceptState(q1);
                nfa.addAcceptState(q2);

                runnfa(output, lines, nfa);

                break;
            }

            case 10:
            {
                //  Problem 10
                NFA nfa = new NFA();

                // Define states and transitions
                State q0 = nfa.addState();
                State q1 = nfa.addState();
                State q2 = nfa.addState();

                q0.addTransition('1', q0);
                q0.addTransition('0', q1);
                q1.addTransition('1', q2);
                q2.addTransition('0', q1);
                q2.addTransition('1', q2);

                nfa.setStartState(q0);
                nfa.addAcceptState(q0);
                nfa.addAcceptState(q2);

                runnfa(output, lines, nfa);

                break;
            }
        }
        writeFile(output,"*******************************************************************************************************");

    }


}
