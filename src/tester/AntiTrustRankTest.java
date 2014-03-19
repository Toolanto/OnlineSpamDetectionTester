package tester;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.law.stat.KendallTau;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.ImmutableSubgraph;
import it.unimi.dsi.webgraph.algo.ParallelBreadthFirstVisit;

public class AntiTrustRankTest implements Test{

	public int idStart; // nodo da cui si fa partire la BFS
	public static final int increment = 500;
	
	protected ImmutableGraph graph;
	protected HashSet<Integer> seedBadNodes;
	protected String namePath;
	
	public AntiTrustRankTest(ImmutableGraph graph,int idStart, HashSet<Integer> seedBadNodes, String namePath){
		this.graph = graph;
		this.seedBadNodes = seedBadNodes;
		this.namePath = namePath;
		this.idStart=idStart;
	}
	
	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub
        System.out.println("Esecuzione di: antitrustranktest");
		System.out.println("Esecuzione della visita dei nodi...");
		ImmutableGraph graphBFS = graph;
		ParallelBreadthFirstVisit bfs = new ParallelBreadthFirstVisit(graphBFS,
				0, false, null);
		bfs.visit(idStart);
		System.out.println("Nodi visitati partendo dal nodo " + idStart + ":"
				+ bfs.queue.size());
		System.out
				.println("Calcolo tau di AntiTrustrank per ogni dimensione della visita...");
		double[] antiTrustrank = Utility.readRank("antiTrustrank.txt");
		double[] tauKendall = new double[bfs.queue.size()];
		FileWriter kendallTau = new FileWriter(namePath);
		BufferedWriter bf = new BufferedWriter(kendallTau);
		int i = 0;
		while ( i < bfs.queue.size()) {
			int[] t = new int[i + 1];
			// copio in t gli elementi della coda di nodi visitati pari a i
			bfs.queue.getElements(0, t, 0, i + 1);
			System.out.println("Numero nodi array " + i + ": " + t.length);
			IntSet ts = new IntArraySet(t);
			ImmutableSubgraph subGraph = new ImmutableSubgraph(graphBFS, ts);
			System.out.println("Nodi sottografo " + i + ": "
					+ subGraph.numNodes());
			AntiTrustRank trustSubGraph = new AntiTrustRank(subGraph);
			HashSet<Integer> newSeed = new HashSet<Integer>();
			for (int j : t)
				if (seedBadNodes.contains(j)) {
					newSeed.add(subGraph.fromSupergraphNode(j));
				}
			trustSubGraph.setSeeds(newSeed);
			trustSubGraph.compute();
			double[] temp = new double[antiTrustrank.length];
			for (int f=0; f<temp.length;f++)
				temp[f] = 0.0;
			
			for (int j = 0; j < trustSubGraph.getRank().length; j++) {
				temp[subGraph.toSupergraphNode(j)] = trustSubGraph.getRank()[j];
			}
			tauKendall[i] = KendallTau.compute(antiTrustrank, temp);
			bf.write(i + " " + tauKendall[i] + "\n");
			bf.flush();
			System.err.println("Iterazione: "+i);
			i+= increment;
		}
	}

}
