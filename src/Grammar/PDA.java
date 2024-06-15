package Grammar;

import java.util.*;

class State {
    private final int id;
    private final Map<Character, Set<Transition>> transitions;

    public State(int id) {
        this.id = id;
        this.transitions = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    // Add a transition to another state on a given input symbol
    public void addTransition(char input, char stackPop, char stackPush, State nextState) {
        transitions.computeIfAbsent(input, k -> new HashSet<>())
                .add(new Transition(stackPop, stackPush, nextState));
    }

    // Get the set of transitions for a given input symbol
    public Set<Transition> getTransitions(char input) {
        return transitions.getOrDefault(input, Collections.emptySet());
    }
}

class Transition {
    private final char stackPop;
    private final char stackPush;
    private final State nextState;

    public Transition(char stackPop, char stackPush, State nextState) {
        this.stackPop = stackPop;
        this.stackPush = stackPush;
        this.nextState = nextState;
    }

    public char getStackPop() {
        return stackPop;
    }

    public char getStackPush() {
        return stackPush;
    }

    public State getNextState() {
        return nextState;
    }
}

class PDA {
    private final Set<State> states;
    private final State startState;
    private final Set<State> acceptStates;

    public PDA() {
        this.states = new HashSet<>();
        this.startState = new State(0);
        this.acceptStates = new HashSet<>();
        states.add(startState);
    }

    // Add a new state to the PDA
    public State addState() {
        State newState = new State(states.size());
        states.add(newState);
        return newState;
    }

    // Define the start state of the PDA
    public void setStartState(State startState) {
        this.startState.addTransition('ε', 'ε', 'Z', startState);
    }

    // Define a state as an accept state
    public void addAcceptState(State acceptState) {
        acceptStates.add(acceptState);
    }

    // Simulate the PDA on the input string
    public boolean simulate(String input) {
        //
        Set<State> currentStates = epsilonClosure(Collections.singleton(startState));
        Deque<Character> stack = new ArrayDeque<>();

        for (char symbol : input.toCharArray()) {
            Set<State> nextStates = new HashSet<>();
            for (State state : currentStates) {
                for (Transition transition : state.getTransitions(symbol)) {
                    if (transition.getStackPop() == 'ε' || !stack.isEmpty() && stack.peek() == transition.getStackPop()) {
                        if (transition.getStackPop() != 'ε') {
                            stack.pop();
                        }
                        if (transition.getStackPush() != 'ε') {
                            stack.push(transition.getStackPush());
                        }
                        nextStates.addAll(epsilonClosure(Collections.singleton(transition.getNextState())));
                    }
                }
            }
            currentStates = nextStates;
        }

        // Check if any of the current states are accept states
        for (State state : currentStates) {
            if (acceptStates.contains(state)) {
                if (stack.isEmpty())
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
            for (Transition transition : currentState.getTransitions('ε')) {
                if (epsilonClosure.add(transition.getNextState())) {
                    stack.push(transition.getNextState());
                }
            }
        }
        return epsilonClosure;
    }
}

