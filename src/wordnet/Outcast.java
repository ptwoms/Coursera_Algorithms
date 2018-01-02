import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * Outcast.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 22/2/17.
 */
public class Outcast {
    private WordNet mWordNet;
    public Outcast(WordNet wordNet) {
        mWordNet = wordNet;
    }

    private int matrixToIndex(int i, int j, int N)
    {
        if (i <= j)
            return i * N - (i - 1) * i / 2 + j - i;
        else
            return j * N - (j - 1) * j / 2 + i - j;
    }

    public String outcast(String[] nouns){
        int nounsLength = nouns.length;
        int maxDistance = Integer.MIN_VALUE;
        int maxDistanceIndex = -1;
        int[] dataCache = new int[(nounsLength+1)*nounsLength/2];
        for (int i = 0; i < nounsLength; i++) {
            for (int j = i+1; j < nounsLength; j++) {
                dataCache[matrixToIndex(i,j,nounsLength)] = mWordNet.distance(nouns[i], nouns[j]);
            }
        }
        for (int i = 0; i < nounsLength; i++){
            int totalDistance = 0;
            for (int j = 0; j < nounsLength; j++){
                if (i == j)continue;
                totalDistance += dataCache[matrixToIndex(i,j,nounsLength)];
            }
            if (totalDistance > maxDistance){
                maxDistance = totalDistance;
                maxDistanceIndex = i;
            }
        }
        if (maxDistance != -1){
            return nouns[maxDistanceIndex];
        }
        return null;
    }

    public static void main(String[] args){
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
