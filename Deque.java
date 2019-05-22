class Deque{
    public static class Node{
        public String ch;
        public int index;
        public int next1;
        public int next2;
        public Node nextState;
        
        //Constructor
        //Para:
        //String s: used to store the character
        public Node(String s){
            ch = s;
        }
        
        //constructor
        //Para:
        //String s: used to store the character
        //int i1: next state number 1
        //int i2: next state number 2
        public Node(String s, int i1, int i2){
            ch = s;
            next1 = i1;
            next2 = i2;
        }
    }
    
    //index for each node
    private static int n = 0;
    //node for sapaerating the stack the queue
    private static Node scan;
    //node always point to the first node at the stack
    private static Node head;
    //node always point to the last node at the queue
    private static Node tail;
    
    //constructor
    //which will create a node scan to saperate the stack and queue
    public Deque(){
        scan = new Node("SCAN");
        scan.index = -1;
    }
    
    //create a new node to store the character, next state number 1, next state number2
    //Para:
    //String s: used to store the character
    //int n1: used to store the next state number 1
    //int n2: used to store the next state number 2
    public void push(String s, int n1, int n2){
        Node curr = new Node(s);
        curr.next1 = n1;
        curr.next2 = n2;
        curr.index = n;
        if(head == null){
            curr.nextState = scan;
            head = curr;
        }else{
            curr.nextState = head;
            head = curr;
        }
        n++;
        
    }
    
    //pop the first node at the stack and return it back.
    public Node pop(){
        Node re = head;
        if(re == scan){
            head = null;
            re = null;
        }else{
            head = head.nextState;
        }
        return re;
    }
    
    //put the node at the end of the queue
    //Para:
    //Node n: the node needed to be put in
    public void put(Node n){
        if(scan.nextState == null){
            scan.nextState = n;
            tail = n;
        }else{
            tail.nextState = n;
            tail = n;
        }
    }
    
    //move the scan to the position that is after the last node at the queue
     public boolean moveScan(){
         if(head != null){
            System.out.println("There is still something at the top of scan");
            return false;
         }else{
             head = scan.nextState;
             tail.nextState = scan;
             scan.nextState = null;
             tail = null;
             return true;
         }
     }
    
    //print all the node out FOR DEBUGGING PURPOSE
    public void print(){
        Node h = head;
        if(h == null){
            h = scan;
        }
        while(h != null){
            System.out.println(h.ch + " " + h.index + " " + h.next1 + " " + h.next2);
            h = h.nextState;
        }
    }
    
    //check the stack if is empty, if so then return true otherwise false
    public boolean isStackEmpty(){
        if(head == null){
            return true;
        }
        return false;
    }
    
    //check the queue if is empty, if so then return true otherwise false
    public boolean isQueueEmpty(){
        if(tail == null){
            return true;
        }
        return false;
    }
    
    //clear all the node in the deque except the scan node
    public void clear(){
        if(head != null && head != scan){
            while(head.nextState != scan){
                head = head.nextState;
            }
            head.nextState = null;
            head = null;
        }
        scan.nextState = null;
        tail = null;
    }
}
