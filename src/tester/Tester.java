package tester;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.ImmutableSubgraph;
import it.unimi.dsi.webgraph.algo.ParallelBreadthFirstVisit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

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
	public static final String VISIT = "visit62.txt";
	public static final String VISIT2 = "visit112.txt";

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
			ImmutableGraph g = ImmutableGraph.load(GRAPHPATH);
			IntArrayList nodes;
			
			File visitFile = new File(VISIT2);
			if(!visitFile.exists()){
				ParallelBreadthFirstVisit bfs = new ParallelBreadthFirstVisit(g, 0,false, null);
				bfs.visit(112);
				nodes = bfs.queue;
				FileWriter visitWriter = new FileWriter(
						VISIT2);
				BufferedWriter bf = new BufferedWriter(visitWriter);
				for (int i = 0; i < bfs.queue.size(); i++) {
					bf.write(bfs.queue.getInt(i)+"\n");
				}
				bf.flush();
			}else{
			
			  nodes = Utility.readVisit(VISIT2);
			}
	
			
			ArrayList<Test> testRun = new ArrayList<Test>();
			//testRun.add(new TrustRankTest(g,nodes,
			//	seedTrustRank,"trustranktestMode1_112.txt",1));
			//HashSet<Integer> seedBad = Utility.readeLabel("spam",Utility.LABELPATH);
			//testRun.add(new TrustRankBadNodeTest(
			//		g,nodes, seedTrustRank, seedBad,"trustrankBadNodesTestMode1_112.txt",1));
			//testRun.add(new AntiTrustRankTest(g,nodes,
			//		seedAntiTrustRank,"antiTrustrankTestMode1_112.txt",1));
			//testRun.add(new AntiTrustRankGoodNodesTest(g, nodes, seedAntiTrustRank, seedTrustRank,"antiTrustraktGoodNodesTestMode1_112.txt",1));
			//testRun.add(new StressTest(g,nodes,3776,"stressTrustTestMode1_62_set3776_alpha0005.txt","stressAntiTrustTestMode1_62_set3776_alpha0005.txt",1));
			//testRun.add(new StressTest(g,nodes,222,"stressTrustTestMode1_62_set222_alpha0005.txt","stressAntiTrustTestMode1_62_set222_alpha005.txt",1));
			testRun.add(new AvaregeTest(g,nodes,seedTrustRank,seedAntiTrustRank,"averageTest_trust_112.txt","averageTest_antitrust_112.txt"));


			for (int i = 0; i < testRun.size(); i++)
				testRun.get(i).run(); 
		
			 

		} catch (Exception e) {
			e.printStackTrace();
		}


		/*
		try {
			ImmutableGraph g = ImmutableGraph.load(GRAPHPATH);
			ImmutableGraph g1 = ImmutableGraph.load(GRAPHPATH);

			ParallelBreadthFirstVisit bfs = new ParallelBreadthFirstVisit(g,
					0, false, null);
			bfs.visit(62);
			IntArrayList t = bfs.queue;
			bfs.visit(62);
			IntArrayList t1 = bfs.queue;
			for(int i=0;i<t.size();i++){
				if(t.get(i)!=t1.get(i))
					System.out.println("........"+t.get(i)+" "+t1.get(i));
			}
			
			ParallelBreadthFirstVisit bfs1 = new ParallelBreadthFirstVisit(g1,
					0, false, null);
			bfs1.visit(62);
			System.out.println(bfs.queue.size()+" "+bfs1.queue.size());
			for(int i=0;i<bfs.queue.size();i++){
				if(bfs.queue.get(i)!=bfs1.queue.getInt(i))
					System.out.println(bfs.queue.getInt(i)+" "+bfs1.queue.getInt(i));
			}
			ImmutableSubgraph sg = new ImmutableSubgraph(g, new IntArraySet(bfs.queue));
			ImmutableSubgraph sg1 = new ImmutableSubgraph(g1, new IntArraySet(bfs1.queue));
			for (int i=0;i<sg.numNodes();i++){
				if(sg.fromSupergraphNode(i)!= sg1.fromSupergraphNode(i))
					System.out.println("....."+i+" "+sg.toSupergraphNode(i)+" "+sg1.toSupergraphNode(i));
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/

	}

}
