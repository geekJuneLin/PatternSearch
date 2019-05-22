import java.util.*;
class REcompiler {
    private static char[] inputCharArray;  //To store all the characters from input
    private static ArrayList<State> stateList = new  ArrayList<State>(); //To store every state
    private static int index = 0;    // inputCharArray[index]
    private static int state = 1;    //The current state number
    private static boolean hasMatchBracket = false;
    private static boolean hasError = false;
    private static ArrayList<Integer> termList = new ArrayList<Integer>();
    private static int count = 0;
    private static int currBranchState = 0;
    private static int currIndex = 0;
    private static int[] bracketArray = new int[128]; //Store all the unmatched open brackets' index
    private static int bracketIndex = 0; //Trace the brackets in bracketArray
    private static int innerBracket = 0; //Store the innermost bracket index
    public static void main (String[] args){
        if (args.length != 1){    // Check if there is the input regular expression
            System.out.println("Usage: java REcompiler <RegExp>");
            return;
        }
        String input = args [0]; //Get the input regular expression
        inputCharArray = input.toCharArray();  //Convert the input string into char array
        set_state (0,"START",0,0);  //Add the start state into the list
        int i = expression();      // Call the expression()
        if (hasError == true) System.out.println("GRAMMER ERROR");    // IF there is any error, display the error message
        else {
            set_state (state, "END", 0,0);   //Add the end state into the list
            int n1 , n2;
            n1 = n2 = getStartState();       // Get the start state of the FSM
            stateList.get(0).setN1(n1); stateList.get(0).setN2(n2);   // Set the 'START' state's n1 and n2 to the start state
            stateList.forEach (state -> {             //Print out all the elements in the list
                System.out.println(state.getState()+","+state.getStr()+","+state.getN1()+","+state.getN2());
            });
        }
    }
    
    public static int expression() {
        int r, n1, n2;
        int f = state-1;
        // E -> T
        r = n1 = term();                                           // Call term()
        termList.add(r);                                           // Store every term number into a list so that they can be used for the next states
        if (r == -1) return r;
        if (isEnd()) return r;
        char c = inputCharArray [index];                           // Get the next character
        // E -> TE
        if (isLiteral(c) || c == '\\' || c == '.' || c == '[' || c == '^' || c=='('){  // If the char is any one of these special chars
            expression();                                          // Call expression()
        }
        // E -> T|E
        else if (c == '|'){                                        // If it is a '|'
            if (bracketArray[bracketIndex] != -1 ){                    // If the current bracket has not found a matched close bracket
                n1 = innerBracket+1;                                   // n1 is the next value of most inner bracket
                if ((n1+1)<stateList.size() && stateList.get(n1+1).getStr() == "BR")       // if it is followed by a branch
                    n1 = n1+1;                                          // Set n1 as that branch
            }
            else n1 = termList.get(0);                                  // Otherwise, it is the first term
            n2 = state+1;                                           // Set n2 state
            set_state(state,"BR",n1,n2);
            currBranchState = state;                                 // Set the current start state
            count = 0;
            r = state;
            f = state -1;
            update();                                                 // Update the state and index
            expression();                                             // Call expression
            if (stateList.get(f).getN1() == stateList.get(f).getN2()) {     // Update n1 and n2 states
                stateList.get(f).setN1(state);
            }
            stateList.get(f).setN2(state);
        }
        return r;
    }
    
    public static int term() {
        int f = state-1;
        int r, n1, n2;
        String s="";
        // T -> F
        r = n2 = factor();                            // Call factor()
        if (r == -1) return r;
        if (isEnd()) return r;
        char c = inputCharArray [index];              // Get the next character
        // T -> F*
        if (c == '*'){                                // If it is a '*'
            currBranchState = state;
            n1 = n2; n2 = state+1;
            set_state (state, "BR", n1, n2);          // Add the current state into the arraylist
            if(bracketArray[0] ==-1){                 // if the first element of the bracket array is set to -1, i.e there are no not-matched bracket there.
                stateList.get(state-2).setN1(state); // set the two next states
                stateList.get(state-2).setN2(state);
            }
            r = state;
            update();                                 // Update the index and state
        }
        // T -> F?
        else if (c == '?') {                           // If the current symbol is '?'
            currBranchState = state;
            n1 = r ; n2 = state + 1;                // Set n1 and n2 values
            set_state (state,"BR", n1, n2);         // Add the current state into the arraylist
            stateList.get(state-1).setN1(state+1);  // set the two next states
            stateList.get(state-1).setN2(state+1);
            r = state;
            update();                               // Update the index and state
        }
        return r;
    }
    
    public static int factor() {
        int r = 0;      //result
        int n1, n2 = 0;
        char c = inputCharArray [index];   // Get the current character from input array
        String s = String.valueOf(c);      // Set the string to the
        // F-> literal || F-> '.'
        if (isLiteral(c) || c == '.') {    // If it is a literal or a wildcard
            n1 = n2 = state+1;         // Set the next two states to
            set_state (state, s , n1, n2);  // Add the current state into the arraylist
            r = state;
            update();                 // Update the state and index
            return r;
        }
        // F -> '\'
        else if (!isEnd() && c == '\\'){    // If current character is an escaped character
            if (!isEnd()) index++;     // If it is not the end of the input array, move to the next character
            c = inputCharArray[index];
            s += s.valueOf(c);         // set the string value same as the character
            n1 = n2 = state+1;         // set the next two states
            set_state (state, s, n1, n2);  // Add the current state into the state list
            r = state;
            update();                 // Update the state and index
        }
        // F-> (E)
        else if (!isEnd() && c =='('){     // If current character is a open bracket
            bracketArray[bracketIndex] = index;    // Store the index of this bracket into an array
            innerBracket = index;         // Set the most inner index of bracket
            bracketIndex++;               // Move to the next position in the bracket array
            int tmp = state - 1;          // Get the previous state
            if (!isEnd()) index++;    // If it is not the end of the input array, move to the next character
            r = expression();         // Call the expression()
            if (!isEnd() && inputCharArray [index] == ')') {     // Right after the expression, there should be a closed bracket
                bracketArray[bracketIndex] = -1;           // If there is a closed bracket, set the current index as -1, i.e. this bracket has a match
                innerBracket = bracketIndex;             // Change the most inner index of the bracket
                bracketIndex--;                          // Go back one position to the second closest open bracket
                index++;                                 // read one more character from the input array
                if (!isEnd()) {
                    c = inputCharArray[index];                 // If it is not the end of the input array, move to the next character
                }
                if (c == '*' || c == '?') currBranchState = state;   // If it is a branch machine, set the state to the currBranchstate
                if (tmp == currBranchState)  {                   // If the previous state equals to currBranchstate
                    if(stateList.get(tmp).getN1() == stateList.get(tmp).getN2()){   //if the two next states have the same value
                        stateList.get(tmp).setN1(currBranchState+1);             // Change both values
                    }
                    stateList.get(tmp).setN2(tmp+1);
                }
                else {                                                         // Change the two next states values
                    if(stateList.get(tmp).getN1() == stateList.get(tmp).getN2()) stateList.get(tmp).setN1(currBranchState);
                    stateList.get(tmp).setN2(currBranchState);
                }
            }
            else return error();                   // If there is no matched bracket, return error message
        }
        // F -> []
        else if (!isEnd() && c == '[') {
            hasMatchBracket = false;          // Looking for the close square bracket
            if (!isEnd())index++; c = inputCharArray[index];
            if (c==']'){                  // Check if the next character is ']', if true, put ']' into a string
                s += String.valueOf(c);  // Add all the characters into one string
                if (!isEnd()) index ++;  // Point it to the next states
            }
            while (!isEnd()){
                c = inputCharArray[index];
                if (c != ']') {
                    s += String.valueOf(c);
                    if(!isEnd()) index++;
                    else {error(); break;}
                }
                else {
                    hasMatchBracket = true; break;
                }
            }
            if (hasMatchBracket()){                  // If there is a matched bracket
                n1 = n2 = state+1;
                s += String.valueOf(']');          //Adding closing bracket to the
                set_state (state, s, n1, n2);     // Add the current state into the array list
                r = state; update();
            }
            else return error();
        }
        // F -> ^[]
        else if (!isEnd() && c == '^'){
            if (!isEnd()) index++; c = inputCharArray[index];
            if (c == '['){                      //Check if the next character is '['
                hasMatchBracket = false;
                s += String.valueOf(c);
                if (!isEnd()) index++;
                c = inputCharArray[index];
                if (c==']'){                   // Check if the next character is ']', if true, put ']' into a string
                    s += String.valueOf(c);   // Add all the characters into one string
                    if (!isEnd()) index ++;
                }
                while (!isEnd()){
                    c = inputCharArray[index];
                    if (c != ']') {
                        s += String.valueOf(c);
                        if(!isEnd()) index++;
                        else {
                            error(); break;
                        }
                    }
                    else {
                        hasMatchBracket = true; break;
                    }
                }
                if (hasMatchBracket()){                  // If there is a matched bracket
                    n1 = n2 = state+1;
                    s += String.valueOf(']');          //Adding closing bracket to the
                    set_state (state, s, n1, n2);     // Add the current state into the array list
                    r = state; update();
                }
                else {System.out.println("NO ']'");return error();}
            }
            else {                                            //'^' will be a literal
                n1 = n2 = state+1;
                set_state (state, s, n1, n2);
                r = state; update();
            }
        }
        else return error();
        return r;
    }
    
    // Check if a symbol has a special meaning. If yes, return true; else return false
    public static boolean isLiteral (char c) {
        if (c != '*' && c != '?' && c != '(' && c != '[' && c != '\\' && c!= '|' && c!='^' && c!='.' && c!=')' && c!=']') return true;
        else return false;
    }
    
    // Add a new state object into state ArrayList
    public static void set_state (int state, String str, int n1, int n2) {
        stateList.add(new State(state,str,n1,n2)); // Add state into the list
    }
    
    // If there is any error, display the error message and return -1
    public static int error () {
        System.out.println("ERROR");
        hasError = true;
        return -1;
    }
    
    //Get the start state of the FSM by searching the outermost state
    //Return the outermost state
    public static int getStartState() {
        int startState = 1;
        int n = 1;
        for (int i = 2; i <= state; i ++) {
            if (stateList.get(i).getN1() == n || stateList.get(i).getN2() == n) {
                startState = i;
                n = i;
            }
        }
        return startState;
    }
    
    // Update the status by incrementing the index by 1 and moving to the next state
    public static void update() {
        if (!isEnd()) index++;
        state++;
        return;
    }
    
    //Check if it is the end the inputCharArray
    public static boolean isEnd () {
        if (index >= inputCharArray.length) return true;
        else return false;
    }
    
    //Check if there is a matched close square bracket
    public static boolean hasMatchBracket() {
        return hasMatchBracket;
    }
}
