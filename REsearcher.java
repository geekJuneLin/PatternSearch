import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.FileReader;

class REsearcher{
    private static Deque q = new Deque(); //used for matching pattern
    private static ArrayList<String> chAr = new ArrayList<String>(); //used for storing char
    private static ArrayList<Integer> stAr = new ArrayList<Integer>(); //used for storing state number
    private static ArrayList<Integer> n1Ar = new ArrayList<Integer>(); //used for storing next state number 1
    private static ArrayList<Integer> n2Ar = new ArrayList<Integer>(); //used for storing next state number 2
    private static ArrayList<MatchLine> matchList = new ArrayList<MatchLine>(); //used for storing all matches
    public static void main(String args []){
        String name; //used for storing name of the file need to be read
        if(args.length != 1){ //check the user if enter the name of the file needed to search
            System.out.println("Usage: java REsearcher <Name of the file to search>");
            return;
        }
        name = args[0]; //parse the name of the file
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); //get the FSM info from the terminal
        try{
            String i;
            String [] ar;
            while((i=br.readLine()) != null){ //while it has next line
                ar = i.split(","); //turn the string into array
                if(ar.length != 4){ //check the ouput from the REcompiler if is the correct format
                    System.out.println("The format of the data has been read is not correct!");
                    return;
                }else{
                    stAr.add(Integer.parseInt(ar[0])); //add state number
                    chAr.add(Integer.parseInt(ar[0]), ar[1]); //add the char
                    n1Ar.add(Integer.parseInt(ar[0]), Integer.parseInt(ar[2])); //add the next state number 1
                    n2Ar.add(Integer.parseInt(ar[0]), Integer.parseInt(ar[3])); //add the next state number 2
                }
            }
            BufferedReader br2 = new BufferedReader(new FileReader(name)); //open the file needed to search
            String [] sub;
            int len;
            int stIndex = 0, chIndex = 0;
            Deque.Node n;
            String ch;
            int n1, n2;
            int lineNum = 0;
            boolean flag = false; //used to detect if it needs to read a new line from the file
            while((i=br2.readLine()) != null){ //while it has next line in the file
                lineNum++; //increment the number of the line
                String s = "";
                ar = i.split(""); //turn the string into array
                chIndex = 0;
                stIndex = 0;
                flag = false;
                q.push(chAr.get(stIndex), n1Ar.get(stIndex), n2Ar.get(stIndex)); //put the start state into the deque
                while(chIndex < ar.length){ //while current char is not the last char in the string
                    while(true){ //
                        while(!q.isStackEmpty()){ //check the stack in the deque if is empty
                            n = q.pop(); //pop the first node at the stack
                            if(n == null){ //check if it's empty at the stack
                                break;
                            }
                            if(n.ch.equals("END")){ //if the ch of the node is END then it means it went through the FSM completely
                                matchList.add(new MatchLine(lineNum,i,s)); //add the line into the match list
                                flag = true; //set the flag to be true and ready to read next new line
                                break;
                            }
                            if(chIndex >= ar.length){ //if the current chIndex is already greater than the length of the string, it means it doesn't match
                                flag = true; //set the flag to be true and ready to read next new line
                                break;
                            }
                            ch = n.ch;
                            if(ch.contains("\\")){ //check if it contains '\'
                                ch = ch.substring(1);
                            }
                            sub = ch.split("");
                            if(sub[0].equals(".")){ //check if it contains '.'
                                if(n.next1 == n.next2){
                                    q.put(new Deque.Node(chAr.get(n.next1), n1Ar.get(n.next1), n2Ar.get(n.next1)));
                                }else{
                                    q.put(new Deque.Node(chAr.get(n.next1), n1Ar.get(n.next1), n2Ar.get(n.next1)));
                                    q.put(new Deque.Node(chAr.get(n.next2), n1Ar.get(n.next2), n2Ar.get(n.next2)));
                                }
                            }else if(ch.contains("^")){ //check if it is ^[]
                                ch = ch.substring(1, ch.length() - 1); //get the text inside the []
                                sub = ch.split("");
                                int z = 0;
                                while(z<sub.length){ //go thru each char in the string arrray
                                    if(sub[z].equals(ar[chIndex])){ //if it equals one of the char in [], then break
                                        break;
                                    }else{ //else continue til finish entire string array
                                        z++;
                                        continue;
                                    }
                                }
                                if(z >= sub.length){ //check if it finishes the entire array of the string, if so, it means not match with the char inside the []
                                    if(n.next1 == n.next2){
                                        q.put(new Deque.Node(chAr.get(n.next1), n1Ar.get(n.next1), n2Ar.get(n.next1)));
                                    }else{
                                        q.put(new Deque.Node(chAr.get(n.next1), n1Ar.get(n.next1), n2Ar.get(n.next1)));
                                        q.put(new Deque.Node(chAr.get(n.next2), n1Ar.get(n.next2), n2Ar.get(n.next2)));
                                    }
                                }
                            }else if(ch.contains("[")){ //check if it is []
                                ch = ch.substring(1, ch.length() - 1); //get the string inside the []
                                sub = ch.split("");
                                for(int z=0; z<sub.length; z++){ //go thru each char inside the array
                                    if(sub[z].equals(ar[chIndex])){ //if it match, then break the loop
                                        if(n.next1 == n.next2){
                                            q.put(new Deque.Node(chAr.get(n.next1), n1Ar.get(n.next1), n2Ar.get(n.next1)));
                                        }else{
                                            q.put(new Deque.Node(chAr.get(n.next1), n1Ar.get(n.next1), n2Ar.get(n.next1)));
                                            q.put(new Deque.Node(chAr.get(n.next2), n1Ar.get(n.next2), n2Ar.get(n.next2)));
                                        }
                                        break;
                                    }else if(!sub[z].equals(ar[chIndex])){ //else until it finishes the loop or find a match at some point inside the loop
                                        continue;
                                    }
                                }
                            }else if(ch.equals("START")){ //check if it is the start state
                                q.push(chAr.get(n.next1), n1Ar.get(n.next1), n2Ar.get(n.next1));
                            }else if(ch.equals("BR")){ //check if it is the BR -  Branch Machine
                                q.push(chAr.get(n.next1), n1Ar.get(n.next1), n2Ar.get(n.next1));
                                q.push(chAr.get(n.next2), n1Ar.get(n.next2), n2Ar.get(n.next2));
                            }else if(ch.equals(ar[chIndex])){ //check if it match the current char of the string
                                if(n.next1 == n.next2){
                                    q.put(new Deque.Node(chAr.get(n.next1), n1Ar.get(n.next1), n2Ar.get(n.next1)));
                                }else{
                                    q.put(new Deque.Node(chAr.get(n.next1), n1Ar.get(n.next1), n2Ar.get(n.next1)));
                                    q.put(new Deque.Node(chAr.get(n.next2), n1Ar.get(n.next2), n2Ar.get(n.next2)));
                                    s+=String.valueOf(ar[chIndex]);
                                }
                            }else if(!ch.equals(ar[chIndex])){ //not match continue to check next state
                                continue;
                            }
                        }
                        if(flag == true){ //check if the flag is true, if so, then it means need to read a new line
                            q.clear();
                            break;
                        }
                        if(q.isQueueEmpty()){ //check the queue of the deque if is empty, if so then need to advance the input and start from the beginning of the FSM
                            q.push(chAr.get(stIndex), n1Ar.get(stIndex), n2Ar.get(stIndex));
                            chIndex++;
                            break;
                        }else{ //if the queue is not empty, then it need to move the scan down and check the next possible state
                            q.moveScan();
                            chIndex++;
                        }
                    }
                    if(flag == true){ //if the flag is true then break the loop and go the readline loop read a new line
                        break;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        matchList.forEach (match -> { //print all the match out
            System.out.println("Line Num: "+match.lineNum+" ,Line: "+match.line);
        });
    }
    
    private static class MatchLine {
        private int lineNum;
        private String line;
        private String strMatch;
        private MatchLine (int lineNum, String line, String strMatch) {
            this.lineNum = lineNum;
            this.line = line;
            this.strMatch = strMatch;
        }
    }
    
}

