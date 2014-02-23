package tester;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.law.graph.BFS;
import it.unimi.dsi.law.stat.KendallTau;
import it.unimi.dsi.webgraph.BVGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.ImmutableSubgraph;
import it.unimi.dsi.webgraph.algo.ParallelBreadthFirstVisit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
/**
 * 
 * @author antonio
 *
 * Classe di testing
 * */
public class Tester {

	public static final String LABELPAHT = "../dataset1/WEBSPAM-UK2007-SET1-labels.txt";
	public static final String TRUSTRANK_FILE = "trustrank.txt";
	public static final String GRAPHPATH = "../dataset2/bvuk-2007-05";
	public static final int idStart= 62; // nodo da cui si fa partire la BFS
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Lettura etichette....");
		ArrayList<LabelNode> labelNode = Utility.readeLabel(LABELPAHT);
		HashSet<Integer> seedTrustRank = new HashSet<Integer>();
		for (LabelNode n : labelNode)
			if (n.getLabel().equals("nonspam")) {
				seedTrustRank.add(n.getId());
			}
		try {
			System.out.println("Calcolo trustrank sul grafo completo....");
			File trustRankFile = new File(TRUSTRANK_FILE);
			if (!trustRankFile.exists()) {
				ImmutableGraph graph = ImmutableGraph
						.load(GRAPHPATH);
				TrustRank t = new TrustRank(graph);
				t.setGoodSeeds(seedTrustRank);
				t.compute();
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
			System.out.println("Esecuzione della visita dei nodi...");
			ImmutableGraph graphBFS =BVGraph.loadMapped(GRAPHPATH);
			ParallelBreadthFirstVisit bfs = new ParallelBreadthFirstVisit(graphBFS, 0, false, null);
			bfs.visit(idStart);
			System.out.println("Nodi visitati partendo dal nodo "+idStart+":"+bfs.queue.size());
			System.out.println("Calcolo tau di trustrank per ogni dimensione della visita...");
			double[] trustrank = Utility.readeTrustrank("trustrank.txt");
			double[] tauKendall = new double[bfs.queue.size()];
			for(int i=0;i<bfs.queue.size();i++){
				int[] t = new int[i+1];
				//copio in t gli elementi della coda di nodi visitati pari a i
				bfs.queue.getElements(0, t, 0, i+1);
				System.out.println("Numero nodi array "+i+": "+t.length);
				IntSet ts = new IntArraySet(t);
				ImmutableSubgraph subGraph = new ImmutableSubgraph(graphBFS, ts);
				System.out.println("Nodi sottografo "+i+": "+subGraph.numNodes());
				TrustRank trustSubGraph = new TrustRank(subGraph);
				HashSet<Integer> newSeed = new HashSet<Integer>();
				for (int j:t)
					if(seedTrustRank.contains(j)){
						newSeed.add(subGraph.fromSupergraphNode(j));
					}
				trustSubGraph.setGoodSeeds(newSeed);
				trustSubGraph.compute();
				double[] temp = new double[trustrank.length];
				for(double f:temp)
					f=0.0;
				for(int j=0;j<trustSubGraph.getRank().length;j++){
					temp[subGraph.toSupergraphNode(j)] = trustSubGraph.getRank()[j];
				}
                tauKendall[i] = KendallTau.compute(trustrank, temp);
			}
			for (double k:tauKendall)
				System.out.println(k);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
