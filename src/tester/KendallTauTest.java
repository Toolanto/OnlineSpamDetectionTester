package tester;

import java.util.HashSet;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.law.stat.KendallTau;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.ImmutableSubgraph;
import it.unimi.dsi.webgraph.algo.ParallelBreadthFirstVisit;

/**
 * Questa classe ha il compito di calcolare la tau dikendall tra i valori di
 * trustrank di tutto il grafo con i valoori di trustrank ottenuti lungo una
 * visita in ampiezza
 * 
 * @author antonio
 * 
 */
public class KendallTauTest implements Test {
	public static final int idStart = 62; // nodo da cui si fa partire la BFS
	private ImmutableGraph graph;
	private HashSet<Integer> seedTrustRank;

	public KendallTauTest(ImmutableGraph graph, HashSet<Integer> seedTrustrank) {
		this.graph = graph;
		this.seedTrustRank = seedTrustrank;
	}

	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub

		System.out.println("Esecuzione della visita dei nodi...");
		ImmutableGraph graphBFS = graph;
		ParallelBreadthFirstVisit bfs = new ParallelBreadthFirstVisit(graphBFS,
				0, false, null);
		bfs.visit(idStart);
		System.out.println("Nodi visitati partendo dal nodo " + idStart + ":"
				+ bfs.queue.size());
		System.out
				.println("Calcolo tau di trustrank per ogni dimensione della visita...");
		double[] trustrank = Utility.readeTrustrank("trustrank.txt");
		double[] tauKendall = new double[bfs.queue.size()];
		for (int i = 0; i < bfs.queue.size(); i++) {
			int[] t = new int[i + 1];
			// copio in t gli elementi della coda di nodi visitati pari a i
			bfs.queue.getElements(0, t, 0, i + 1);
			System.out.println("Numero nodi array " + i + ": " + t.length);
			IntSet ts = new IntArraySet(t);
			ImmutableSubgraph subGraph = new ImmutableSubgraph(graphBFS, ts);
			System.out.println("Nodi sottografo " + i + ": "
					+ subGraph.numNodes());
			TrustRank trustSubGraph = new TrustRank(subGraph);
			HashSet<Integer> newSeed = new HashSet<Integer>();
			for (int j : t)
				if (seedTrustRank.contains(j)) {
					newSeed.add(subGraph.fromSupergraphNode(j));
				}
			trustSubGraph.setGoodSeeds(newSeed);
			trustSubGraph.compute();
			double[] temp = new double[trustrank.length];
			for (double f : temp)
				f = 0.0;
			for (int j = 0; j < trustSubGraph.getRank().length; j++) {
				temp[subGraph.toSupergraphNode(j)] = trustSubGraph.getRank()[j];
			}
			tauKendall[i] = KendallTau.compute(trustrank, temp);
		}
		for (double k : tauKendall)
			System.out.println(k);
	}

}
