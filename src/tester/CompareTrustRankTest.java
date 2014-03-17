package tester;

import java.util.HashSet;

import it.unimi.dsi.law.stat.KendallTau;
import it.unimi.dsi.webgraph.ImmutableGraph;

public class CompareTrustRankTest implements Test {

	protected ImmutableGraph graph;
	
	public CompareTrustRankTest(ImmutableGraph graph){
		this.graph = graph;
	}
	
	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub

		double[] trustrank = Utility.readRank("trustrank.txt");
		double[] antitrustrank = Utility.readRank("antiTrustrank.txt");
		double[] pagerankTrust = new double[trustrank.length];
		double[] pagerankAntiTrust = new double[antitrustrank.length];
		
		TrustRank t = new TrustRank(graph);
		t.setSeeds(new HashSet<Integer>());
		t.compute();
		AntiTrustRank at = new AntiTrustRank(graph);
		at.setSeeds(new HashSet<Integer>());
		at.compute();
		pagerankTrust= t.getRank();
		pagerankAntiTrust = at.getRank();
		
		System.out.println("Il valore della tao di kendal tra pagerank e trustrank è: "+KendallTau.compute(trustrank, pagerankTrust));
		System.out.println("Il valore della tao di kendal tra pagerank e antiTrustRank è: "+KendallTau.compute(antitrustrank, pagerankAntiTrust));
	}

}
