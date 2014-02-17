package tester;



import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.law.rank.PageRank;
import it.unimi.dsi.law.rank.PageRankPowerMethod;
import it.unimi.dsi.law.rank.PageRank.IterationNumberStoppingCriterion;
import it.unimi.dsi.law.rank.PageRank.NormDeltaStoppingCriterion;
import it.unimi.dsi.webgraph.BVGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.Transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import edu.uci.ics.jung.graph.Graph;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		try {
			ImmutableGraph graph = BVGraph.loadMapped("../dataset/uk-2007-05@100000");
			LabelReader lr = new LabelReader("../dataset1/WEBSPAM-UK2007-SET1-labels.txt");
            ArrayList<LabelNode> labelNode = lr.reade();
            HashSet<Integer> seedTrustRank = new HashSet<Integer>();
            for (LabelNode n:labelNode)
            	if (n.getLabel().equals("nonspam")){
            		seedTrustRank.add(n.getId());
            	}	
            
			TrustRank t = new TrustRank(graph);
			t.setGoodSeeds(seedTrustRank);
			t.compute();
	
	}catch (IOException e){
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
