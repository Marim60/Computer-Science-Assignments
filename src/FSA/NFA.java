package FSA;

import java.util.*;

// Class representing a state in the NFA
class State {
    private final int id;
    private final Map<Character, Set<State>> transitions;

    public State(int id) {
        this.id = id;
        this.transitions = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    // Add a transition to another state on a given input symbol
    public void addTransition(char input, State nextState) {
        transitions.computeIfAbsent(input, k -> new HashSet<>()).add(nextState);
    }

    // Get the set of states reachable from this state on a given input symbol
    public Set<State> getTransitions(char input) {
        return transitions.getOrDefault(input, Collections.emptySet());
    }
}

// Class representing the Non-Deterministic Finite Automaton
class NFA {
    private final Set<State> states;
    private final State startState;
    private final Set<State> acceptStates;

    public NFA() {
        this.states = new HashSet<>();
        this.startState = new State(0);
        this.acceptStates = new HashSet<>();
        states.add(startState);
    }

    // Add a new state to the NFA
    public State addState() {
        State newState = new State(states.size());
        states.add(newState);
        return newState;
    }

    // Define the start state of the NFA
    public void setStartState(State startState) {
        this.startState.addTransition('ε', startState);
    }

    // Define a state as an accept state
    public void addAcceptState(State acceptState) {
        acceptStates.add(acceptState);
    }

    // Simulate the NFA on the input string
    public boolean simulate(String input) {
        Set<State> currentStates = epsilonClosure(Collections.singleton(startState));

        for (char symbol : input.toCharArray()) {
            Set<State> nextStates = new HashSet<>();
            for (State state : currentStates) {
                nextStates.addAll(epsilonClosure(state.getTransitions(symbol)));
            }
            currentStates = nextStates;
        }

        // Check if any of the current states are accept states
        for (State state : currentStates) {
            if (acceptStates.contains(state)) {
                return true;
            }
        }
        return false;
    }

    // Compute the epsilon closure of a set of states
    private Set<State> epsilonClosure(Set<State> states) {
        Set<State> epsilonClosure = new HashSet<>(states);
        Deque<State> stack = new ArrayDeque<>(states);

        while (!stack.isEmpty()) {
            State currentState = stack.pop();
            for (State nextState : currentState.getTransitions('ε')) {
                if (epsilonClosure.add(nextState)) {
                    stack.push(nextState);
                }
            }
        }
        return epsilonClosure;
    }
}


