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
	public static final String GRAPHPATH = "../dataset2/bvuk-2007-05";


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashSet<Integer> seedTrustRank = Utility.readeLabel("nonspam");

		try {
			System.out.println("Calcolo trustrank sul grafo completo....");
			File trustRankFile = new File(TRUSTRANK_FILE);
			if (!trustRankFile.exists()) {
				ImmutableGraph graph = ImmutableGraph.load(GRAPHPATH);
				TrustRank t = new TrustRank(graph);
				t.setGoodSeeds(seedTrustRank);
				t.compute();
				System.out.println("Srittura trustrank su file....");
				FileWriter rankTrustRank = new FileWriter(TRUSTRANK_FILE);
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

		try {
		  // Ciclo for
          //Test test = new KendallTauTest(ImmutableGraph.load(GRAPHPATH), seedTrustRank);
          //test.run();
  		  HashSet<Integer> seedBad = Utility.readeLabel("spam");
          Test test1 = new KendallTauBadNodeTest(ImmutableGraph.load(GRAPHPATH), seedTrustRank,seedBad);
          test1.run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}
