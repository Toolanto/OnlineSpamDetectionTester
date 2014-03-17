package tester;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.law.rank.PageRank;
import it.unimi.dsi.law.rank.PageRank.IterationNumberStoppingCriterion;
import it.unimi.dsi.law.rank.PageRank.NormDeltaStoppingCriterion;
import it.unimi.dsi.law.rank.PageRankPowerMethod;
import it.unimi.dsi.webgraph.ImmutableGraph;

import java.util.HashSet;

public class TrustRank{

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
	
	public DoubleList getStart(){
		return pr.start;
	}


	public void setSeeds(HashSet<Integer> seeds) {
		
		if(seeds.size()>0){
		int numNodes = g.numNodes();
		double[] arr = new double[numNodes];
		double sum = 0.0;
		for (int i = 0; i < numNodes; i++)
			if (seeds.contains(new Integer(i))){
				arr[i] = 1.0;
				sum += 1.0;
			}
			else
				arr[i] = 0.0;
		for (int i = 0;i<arr.length;i++)
			arr[i] = arr[i]/sum;
        
		pr.start = DoubleArrayList.wrap(arr);
		}else{
			pr.start = null;
		}
	}



	public void compute() throws Exception {
		pr.alpha = alpha;
		pr.stepUntil(PageRank.or(new NormDeltaStoppingCriterion(threshold),
				new IterationNumberStoppingCriterion(numberOfIteration)));
	}


	private double getNorm(double[] arr){
		double sum = 0.0;
		for (double i:arr){
			sum+= Math.pow(Math.abs(i),2);
		}
		return Math.sqrt(sum);
		
	}

}