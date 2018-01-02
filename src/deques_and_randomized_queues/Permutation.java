import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/**
 * Permutation.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 12/3/17.
 */
public class Permutation {
    public static void main(String[] args){
        int noOfTextToShow = Integer.parseInt(args[0]);
        RandomizedQueue<String> randQueue = new RandomizedQueue<>();

        int totalStrCount = 0;
        String[] currentSamples = new String[noOfTextToShow];
        while (!StdIn.isEmpty()){
            totalStrCount++;
            String curStr = StdIn.readString();
            if (totalStrCount <= noOfTextToShow){
                currentSamples[totalStrCount-1] = curStr;
            }else {
                int prob = StdRandom.uniform(0, totalStrCount);
                if (prob < noOfTextToShow){
                    currentSamples[prob] = curStr;
                }
            }
        }

        for (int i = 0; i < noOfTextToShow; i++) {
            randQueue.enqueue(currentSamples[i]);
        }

        for (String curStr:randQueue) {
            StdOut.println(curStr);
        }
    }
}
