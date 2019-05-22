/*
 * State class which stores the state number, the string, next 1 and next 2 state
 */
public class State {
    private int state_num;
    private String str;
    private int n1;
    private int n2;
    //Constructor: to initialize a new state object
    public State(int state_num, String str, int n1, int n2){
        this.state_num = state_num;
        this.str = str;
        this.n1 = n1;
        this.n2 = n2;
    }
    // Get the statenum
    public int getState () {
        return this.state_num;
    }
    // Get the string
    public String getStr () {
        return this.str;
    }
    // Get the next1
    public int getN1() {
        return this.n1;
    }
    // Get the next2
    public int getN2() {
        return this.n2;
    }
    // Set the next1 value
    public void setN1(int n1) {
        this.n1 = n1;
    }
    // Set the next2 value
    public void setN2(int n2) {
        this.n2 = n2;
    }
}
