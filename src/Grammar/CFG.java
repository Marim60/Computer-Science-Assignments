package Grammar;

public class CFG {
    private String input;
    private int index;

    public CFG(String input) {
        this.input = input;
        this.index = 0;
    }

    private void undo() {
        if(index > 0)
        index--;
    }
    private boolean match(char expected) {
        if (index < input.length() && input.charAt(index) == expected) {
            index++;
            return true;
        }
        return false;
    }
    public boolean Problem1() {
        // Rules:
        // S -> aB | bA | ε
        // A -> aS | bAA
        // B -> bS | aBB
        return S() && index == input.length(); // Ensure we've consumed the entire input
    }
    private boolean S() {
        if (match('a')) {
            if (B()) {
                return true;
            }
            undo();
        } else if (match('b')) {
            if (A()) {
                return true;
            }
            undo();
        }
        return true; // Handle ε production
    }
    private boolean A() {
        if (match('a')) {
            if (S()) {
                return true;
            }
            undo();
        } else if (match('b')) {
            if (A() && A()) {
                return true;
            }
            undo();
        }
        return false;
    }
    private boolean B() {
        if (match('b')) {
            if (S()) {
                return true;
            }
            undo();
        } else if (match('a')) {
            if (B() && B()) {
                return true;
            }
            undo();
        }
        return false;
    }
    public boolean Problem2() {
        // Rules:
        //T ->AAB | ABA| BAA |ε
        //A -> aT
        //B -> bT
        return T() && index == input.length(); // Ensure we've consumed the entire input
    }
    private boolean T() {
    int startIndex = index;
    if (a() && a() && b()) // T -> AAB
        return true;
    index = startIndex;
    if (a() && b() && a()) // T -> ABA
        return true;
    index = startIndex;
    if (b() && a() && a()) // T -> BAA
        return true;
    index = startIndex;
    return true; // T -> ε (empty)
}
    private boolean a() {
        if (match('a')) {
            return T();
        }
        return false;
    }
    private boolean b() {
        if (match('b')) {
            return T();
        }
        return false;
    }
    // Problem 3
    //Rules:
    // G-> aGa | bGb | ε | a | b
    public boolean Problem3() {
        //Rules:
        // G-> aGa | bGb | ε
        if(input.length()%2==0)
            return G() && index == input.length(); // Ensure we've consumed the entire input
        else
            //Rules:
            // G-> aGa | bGb | a | b
            return oddPalindrome() && index == input.length();
    }
    private boolean G() {
        if (match('a')) {
            if (G() && match('a')) {
                return true;
            }
            undo();
        } else if (match('b')) {
            if (G() && match('b')) {
                return true;
            }
            undo();
        }
        return true; // Handle ε production
    }

    // handle odd Palindrome
    private boolean oddPalindrome() {
        int start = index;
        if (match('a') && oddPalindrome() && match('a')) {
            return true;
        }
        index = start;
        if (match('b') && oddPalindrome() && match('b')) {
            return true;
        }
        index = start;
        if (match('a')) {
            return true;
        }
        index = start;
        if (match('b')) {
            return true;
        }
        index = start;
        return false;
    }
    // Problem 4
    public boolean Problem4() {
        // Rules:
        // C-> aaaF | ε
        // F-> aaFb | ε

        return C() && index == input.length(); // Ensure we've consumed the entire input
    }
    private boolean C() {
        int start = index;
        if (match('a') && match('a') && match('a') && F()) {
            return true;
        }
        index = start;
        return true;
    }
    private boolean F() {
        int start = index;
        if (match('a') && match('a') && F() && match('b')) {
            return true;
        }
        index = start;
        return true;
}





}
