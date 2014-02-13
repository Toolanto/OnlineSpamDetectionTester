package tester;



import java.util.HashSet;

import it.unimi.dsi.law.rank.PageRank;
import it.unimi.dsi.law.rank.PageRankPowerMethod;
import it.unimi.dsi.law.rank.PageRank.IterationNumberStoppingCriterion;
import it.unimi.dsi.law.rank.PageRank.NormDeltaStoppingCriterion;
import it.unimi.dsi.webgraph.*;
import it.unimi.dsi.fastutil.doubles.*;



public class TrustRank {


    private ImmutableGraph g;
    private PageRankPowerMethod pr;
    private double threshold = PageRank.DEFAULT_THRESHOLD;
    private int numberOfIteration = PageRank.DEFAULT_MAX_ITER;
    private double alpha = PageRank.DEFAULT_ALPHA;

    public TrustRank(ImmutableGraph g) {
        this.g = g;
        pr = new PageRankPowerMethod(g);
    }


    public void setThreshold(double t) {
        threshold = t;
    }


    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }


    public void setIteration(int n) {
        numberOfIteration = n;
    }


    public double[] getRank() {
        return pr.rank;
    }


    /**

     * set the biased good seeds

     * @param seeds

     */

    public void setGoodSeeds(HashSet<Integer> seeds) {
        int numNodes = g.numNodes();
        double[] arr = new double[numNodes];			
        int seedSize = seeds.size();
        for (int i = 0; i < numNodes; i++)
            if (seeds.contains(i))
                arr[i] = 1/seedSize;       	
        pr.start = DoubleArrayList.wrap(arr);
        arr = null;
        System.gc();
    }


    /**

     * compute the TR scores

     * @throws Exception

     */

    public void compute() throws Exception {
        pr.alpha = alpha;
        pr.stepUntil(PageRank.or(new NormDeltaStoppingCriterion(threshold),new IterationNumberStoppingCriterion(numberOfIteration)));
    }

}