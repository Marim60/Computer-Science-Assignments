package FSA;

import java.util.Map;
import java.util.Set;

public class DFA {
    private Set<Integer> states;
    private Set<Character> alphabet;
    private Set<Integer> finalStates;
    private Map<Integer, Map<Character,Integer>> transitions;
    private int currentState;
    public DFA(Set<Integer> states, Set<Character> alphabet, Set<Integer> finalStates, Map<Integer, Map<Character,Integer>> transitions, int initialState) {
        this.states = states;
        this.alphabet = alphabet;
        this.finalStates = finalStates;
        this.transitions = transitions;
        this.currentState = initialState;
    }
    public boolean isAccepted(String input) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (!alphabet.contains(c)) {
                return false;
            }
            currentState = transitions.get(currentState).get(c);
        }
        return finalStates.contains(currentState);
    }
    public void reset() {
        currentState = 0;
    }
}
