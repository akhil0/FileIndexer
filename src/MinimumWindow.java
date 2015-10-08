import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
 
public class MinimumWindow {
 
        public static WindowObject getMinWin(char[] s, char[] c){
 
                WindowObject minWindow = null;
 
                /* Creating HashSet in order to query the chars in O(1) */
                HashSet<Character> dic = new HashSet<Character>();
                for(int i = 0; i < c.length; i++){
                        dic.add(c[i]);
                }
 
                /* In order to keep track of "where I've seen it" */           
                TreeMap<Character, Integer> where = new TreeMap<Character, Integer>();
                /* In order to keep the indexes sorted */
                TreeSet<Integer> indexInWindow = new TreeSet<Integer>();
 
                for(int i = 0; i < s.length; i++){
                        if(dic.contains(s[i])){
                                /* If I've already seen this char */
                                if(where.containsKey(s[i])){
                                        /* Remove its old index */
                                        indexInWindow.remove(where.get(s[i]));
                                }
                                /* Update/Insert its index */
                                where.put(s[i], i);
                                indexInWindow.add(i);
 
                                if(indexInWindow.size() == dic.size()){ /* I've seen all of them*/
                                        /* These are the window's edges */
                                        int firstIndex = indexInWindow.first();
                                        int lastIndex = indexInWindow.last();
                                        WindowObject currentWindow = new WindowObject(s, firstIndex, lastIndex);
                                        if(minWindow == null){ /* It's the first windows I see */
                                                minWindow = currentWindow;
                                        }else{
                                                if(minWindow.compareTo(currentWindow) > 0){
                                                        /* If the minWindow bigger than what I just found */
                                                        minWindow = currentWindow;
                                                }
 
                                        }
 
                                }
 
                        }
                }
 
                return minWindow;
        }
 
        public static void main(String args[]){
                String s = "ADOBECODEBANC";
                String c = "ABC";
                WindowObject wo = MinimumWindow.getMinWin(s.toCharArray(), c.toCharArray());
                if(wo == null){
                        System.out.println("Sorry! No windows were found!");
                }else{
                        System.out.println("Found a Min Window with lenght: ["+wo.getWindow().length+"]");
                        System.out.println("Here it is: "+wo);
                }
        }
 
        static class WindowObject implements Comparable<WindowObject>{
                private char[] window;
                private int firstIndex, lastIndex;
 
                public WindowObject(char[] s, int firstIndex, int lastIndex){
                        this.window = Arrays.copyOfRange(s, firstIndex, lastIndex+1);
                        this.firstIndex = firstIndex;
                        this.lastIndex = lastIndex;
                }
 
                public char[] getWindow(){
                        return this.window;
                }
 
                @Override
                public int compareTo(WindowObject w) {
                        if(this.window.length < w.getWindow().length){
                                return -1;
                        }
                        if(this.window.length > w.getWindow().length){
                                return 1;
                        }
                        return 0;
                }
 
                @Override
                public int hashCode(){
                        return this.window.hashCode();
                }
 
                @Override
                public boolean equals(Object o){
                        if(!(o instanceof WindowObject)){
                                return false;
                        }
                        WindowObject w = (WindowObject) o;
                        return this.window.length == w.getWindow().length;
                }
 
                @Override
                public String toString(){
                        String s = "";
                        for(int i = 0; i < this.window.length; i++){
                                s += this.window[i];
                        }
                        return s+" ["+this.firstIndex+"]["+this.lastIndex+"]";
                }      
 
        }      
 
}