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
		HashSet<Integer> seedTrustRank = Utility.readeLabel("nonspam",Utility.LABELPATH);
		HashSet<Integer> seedAntiTrustRank = Utility.readeLabel("spam",Utility.LABELPATH);

		try {
			File trustRankFile = new File(TRUSTRANK_FILE);
			File antiTrustRankFile = new File(ANTITRUSTRANK_FILE);
			if (!trustRankFile.exists()) {
				System.out.println("Calcolo trustrank sul grafo completo....");
				ImmutableGraph graph = ImmutableGraph.load(GRAPHPATH);
				TrustRank t = new TrustRank(graph);
				t.setSeeds(seedTrustRank);
				t.compute();
				System.out.println("Scrittura trustrank su file....");
				FileWriter rankTrustRank = new FileWriter(TRUSTRANK_FILE);
				BufferedWriter bf = new BufferedWriter(rankTrustRank);
				for (int i = 0; i < t.getRank().length; i++) {
					bf.write(i + " " + t.getRank()[i] + "\n");
				}
				bf.flush();
			} if (!antiTrustRankFile.exists()) {
				System.out
						.println("Calcolo AntiTrustrank sul grafo completo....");
				ImmutableGraph graph = ImmutableGraph.load(GRAPHPATH);
				AntiTrustRank t = new AntiTrustRank(graph);
				t.setSeeds(seedAntiTrustRank);
				t.compute();
				System.out.println("Scrittura AntiTrustrank su file....");
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
			ArrayList<Test> testRun = new ArrayList<Test>();
			testRun.add(new TrustRankTest(ImmutableGraph.load(GRAPHPATH),112,
					seedTrustRank,"trustranktestMode1_",1));
			HashSet<Integer> seedBad = Utility.readeLabel("spam",Utility.LABELPATH);
			testRun.add(new TrustRankBadNodeTest(
					ImmutableGraph.load(GRAPHPATH),112, seedTrustRank, seedBad,"trustrankBadNodesTestMode1_",1));
			testRun.add(new AntiTrustRankTest(ImmutableGraph.load(GRAPHPATH),112,
					seedAntiTrustRank,"antiTrustrankTestMode1_",1));
			testRun.add(new AntiTrustRankGoodNodesTest(ImmutableGraph
					.load(GRAPHPATH), 112, seedAntiTrustRank, seedTrustRank,"antiTrustraktGoodNodesTestMode1_",1));
			testRun.add(new StressTest(ImmutableGraph.load(GRAPHPATH),62,200,"stressTrustTestMode1_","stressAntiTrustTestMode1_",1));
			for (int i = 0; i < testRun.size(); i++)
				testRun.get(i).run(); 
			
			 

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
