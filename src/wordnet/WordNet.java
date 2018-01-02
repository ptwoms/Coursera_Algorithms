import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.*;

/**
 * WordNet.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 20/2/17.
 */

public class WordNet {
	
	private class SynSet{
	    public int id;
	    public String[] synonyms;
//	    public String definition;
	}
	
    private List<SynSet> allSynSets;
    private Digraph mDiagraph;
    private SAP mSap;
    private HashMap<String, Set<Integer>> allNouns;

    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null){
            throw new NullPointerException("Input files must not be null");
        }

        In synReader = new In(synsets);
        allSynSets = new ArrayList<>();
        allNouns = new HashMap<>();
        while (synReader.hasNextLine()){
            String synLine = synReader.readLine();
            String[] synData = synLine.split(",");
            if (synData.length >= 3){
                SynSet newSynSet = new SynSet();
                newSynSet.id = Integer.parseInt(synData[0].trim());
                String[] synonyms = synData[1].trim().split(" ");
                for (int i = 0; i < synonyms.length; i++) {
                    synonyms[i] = synonyms[i].trim();
                    Set<Integer> existingNounsSet = allNouns.get(synonyms[i]);
                    if (existingNounsSet == null){
                        existingNounsSet = new HashSet<>();
                    }
                    existingNounsSet.add(newSynSet.id);
                    allNouns.put(synonyms[i], existingNounsSet);
                }
                newSynSet.synonyms = synonyms;
//                newSynSet.definition = synData[2].trim();
                allSynSets.add(newSynSet);
            }
        }
        synReader.close();

        In hyperReader = new In(hypernyms);
        mDiagraph = new Digraph(allSynSets.size());
        while (hyperReader.hasNextLine()){
            String edgeLine = hyperReader.readLine();
            String[] edgeData = edgeLine.split(",");
            if (edgeData.length >= 2){
                for (int i = 1; i < edgeData.length ; i++) {
                    mDiagraph.addEdge(Integer.valueOf(edgeData[0]), Integer.valueOf(edgeData[i]));
                }
            }
        }
        int numOfRoots = 0;
        for (int i = 0; i < mDiagraph.V() ; i++) {
            if (mDiagraph.outdegree(i) == 0){
                numOfRoots++;
                if (numOfRoots > 1){
                    break;
                }
            }
        }
        if (numOfRoots != 1){//check for single root (1 only)
            throw new IllegalArgumentException("Input files doesn't correspond to rooted DAG");
        }
        DirectedCycle directedCycle = new DirectedCycle(mDiagraph);
        if (directedCycle.hasCycle()){
            throw new IllegalArgumentException("Input files has directed cycle.");
        }
        mSap = new SAP(mDiagraph);

        //IllegalArgumentException if input files doesn't correspond to rooted DAG
    }

    public Iterable<String> nouns(){
        return allNouns.keySet();
    }

    public boolean isNoun(String word){
        if (word == null){
            throw new NullPointerException("Both nounA and nounB must not be null");
        }
        return allNouns.get(word) != null;
    }

    private void checkNouns(String nounA, String nounB) throws IllegalArgumentException, NullPointerException{
        if (!isNoun(nounA) || !isNoun(nounB)){
            throw new IllegalArgumentException("Both must be WordNet nouns");
        }
    }

    public int distance(String nounA, String nounB){
        checkNouns(nounA, nounB);
        return mSap.length(allNouns.get(nounA), allNouns.get(nounB));
    }

    public String sap(String nounA, String nounB){
        checkNouns(nounA, nounB);
        int ansectorIndex = mSap.ancestor(allNouns.get(nounA), allNouns.get(nounB));
        if (ansectorIndex >= 0 && ansectorIndex < allSynSets.size()){
            StringBuilder str = new StringBuilder();
            String[] allSyns = allSynSets.get(ansectorIndex).synonyms;
            str.append(allSyns[0]);
            for (int i = 1; i < allSyns.length; i++) {
                str.append(" " + allSyns[i]);
            }
            return str.toString();
        }
        return null;
    }

    public static void main(String[] args) {
        String synSetFile = ClassLoader.getSystemClassLoader().getResource("wordnet/synsets8.txt").toString();
        String hypernymFile = ClassLoader.getSystemClassLoader().getResource("wordnet/hypernyms8WrongBFS.txt").toString();
        WordNet wordNet = new WordNet(synSetFile, hypernymFile);
        wordNet.checkNouns("a", null);
    }

}
