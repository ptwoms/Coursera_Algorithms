import edu.princeton.cs.algs4.StdRandom;

/**
 * PercolationStats.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 12/3/17.
 */
public class PercolationStats {
    private double[] trailResults;
    private int mTrials;
    private double mMean;
    private double mStdDev;
    public PercolationStats(int n, int trials)    // perform trials independent experiments on an n-by-n grid
    {
        if (n <= 0 || trials <= 0){
            throw new IllegalArgumentException("n and trials must be greater than 0");
        }
        mTrials = trials;
        double totalSites = n * n;
        trailResults = new double[mTrials];
        double totalSum = 0;
        for (int i = 0; i < mTrials; i++) {
            Percolation percolation = new Percolation(n);
            while (!percolation.percolates()){
                percolation.open(StdRandom.uniform(n)+1, StdRandom.uniform(n)+1);
            }
            trailResults[i] = (double) percolation.numberOfOpenSites()/totalSites;
            totalSum += trailResults[i];
        }
        mMean = totalSum/mTrials;

        totalSum = 0;
        for (int i = 0; i < mTrials; i++) {
            double diff = (trailResults[i]-mMean);
            totalSum += diff * diff;
        }
        mStdDev = Math.sqrt(totalSum/(mTrials-1));
    }

    public double mean()                          // sample mean of percolation threshold
    {
        return mMean;
    }

    public double stddev()                        // sample standard deviation of percolation threshold
    {
        return mStdDev;
    }

    public double confidenceLo()                  // low  endpoint of 95% confidence interval
    {
        return mMean - (1.96 * mStdDev/Math.sqrt(mTrials));
    }

    public double confidenceHi()                  // high endpoint of 95% confidence interval
    {
        return mMean + (1.96 * mStdDev/Math.sqrt(mTrials));
    }

    public static void main(String[] args)        // test client (described below)
    {
        PercolationStats mStatus = new PercolationStats(200, 100);
        System.out.println("mean:                   " + mStatus.mean());
        System.out.println("stddev:                 " + String.format("%s",mStatus.stddev()));
        System.out.println("95% confidence interval: [" + mStatus.confidenceLo() + ", " + mStatus.confidenceHi() + "]");
    }
}
