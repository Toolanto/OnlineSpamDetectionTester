package tester;

import it.unimi.dsi.webgraph.BVGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			File trustRankFile = new File("trustrank.txt");
			if (!trustRankFile.exists()) {
				ImmutableGraph graph = BVGraph
						.loadMapped("../dataset/uk-2007-05@100000");
				LabelReader lr = new LabelReader(
						"../dataset1/WEBSPAM-UK2007-SET1-labels.txt");
				ArrayList<LabelNode> labelNode = lr.reade();
				HashSet<Integer> seedTrustRank = new HashSet<Integer>();
				for (LabelNode n : labelNode)
					if (n.getLabel().equals("nonspam")) {
						seedTrustRank.add(n.getId());
					}

				TrustRank t = new TrustRank(graph);
				t.setGoodSeeds(seedTrustRank);
				t.compute();

				FileWriter rankTrustRank = new FileWriter("trustrank.txt");
				BufferedWriter bf = new BufferedWriter(rankTrustRank);
				for (int i = 0; i < t.getRank().length; i++) {
					bf.write(i + " " + t.getRank()[i] + "\n");
				}
				bf.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
