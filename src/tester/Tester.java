package tester;

import it.unimi.dsi.webgraph.ImmutableGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * 
 * @author antonio
 * 
 *         Classe di testing
 * */
public class Tester {

	public static final String TRUSTRANK_FILE = "trustrank.txt";
	public static final String ANTITRUSTRANK_FILE = "antiTrustrank.txt";
	public static final String GRAPHPATH = "../dataset2/bvuk-2007-05";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashSet<Integer> seedTrustRank = Utility.readeLabel("nonspam");
		HashSet<Integer> seedAntiTrustRank = Utility.readeLabel("spam");

		try {
			File trustRankFile = new File(TRUSTRANK_FILE);
			File antiTrustRankFile = new File(ANTITRUSTRANK_FILE);
			if (!trustRankFile.exists()) {
				System.out.println("Calcolo trustrank sul grafo completo....");
				ImmutableGraph graph = ImmutableGraph.load(GRAPHPATH);
				TrustRank t = new TrustRank(graph);
				t.setSeeds(seedTrustRank);
				t.compute();
				System.out.println("Srittura trustrank su file....");
				FileWriter rankTrustRank = new FileWriter(TRUSTRANK_FILE);
				BufferedWriter bf = new BufferedWriter(rankTrustRank);
				for (int i = 0; i < t.getRank().length; i++) {
					bf.write(i + " " + t.getRank()[i] + "\n");
				}
				bf.flush();
			} else if (!antiTrustRankFile.exists()) {
				System.out
						.println("Calcolo AntiTrustrank sul grafo completo....");
				ImmutableGraph graph = ImmutableGraph.load(GRAPHPATH);
				AntiTrustRank t = new AntiTrustRank(graph);
				t.setSeeds(seedAntiTrustRank);
				t.compute();
				System.out.println("Srittura AntiTrustrank su file....");
				FileWriter rankAntiTrustRank = new FileWriter(
						ANTITRUSTRANK_FILE);
				BufferedWriter bf = new BufferedWriter(rankAntiTrustRank);
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

		try {
			// Ciclo for
			// Test test = new KendallTauTest(ImmutableGraph.load(GRAPHPATH),
			// seedTrustRank);
			// test.run();
			// HashSet<Integer> seedBad = Utility.readeLabel("spam");
			// Test test1 = new
			// TrustRankBadNodeTest(ImmutableGraph.load(GRAPHPATH),
			// seedTrustRank,seedBad);
			// test1.run();
			// Test test = new AntiTrustRankTest(ImmutableGraph.load(GRAPHPATH),
			// seedAntiTrustRank);
			// test.run();
			Test test3 = new AntiTrustRankGoodNodesTest(
					ImmutableGraph.load(GRAPHPATH), seedAntiTrustRank, seedTrustRank);
			test3.run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
