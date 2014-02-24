package tester;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.law.stat.KendallTau;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.ImmutableSubgraph;
import it.unimi.dsi.webgraph.algo.ParallelBreadthFirstVisit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;

public class KendallTauBadNodeTest extends KendallTauTest implements Test {
	public static final int idStart = 62; // nodo da cui si fa partire la BFS
	public static final int increment = 500;
	private static final String KENDALLTAUBADNODES = "kendalTauBadNodes.txt";
	private static final String KENDALLTAUBADNODESALL = "kendalTauBadNodesAll.txt";

	
	private HashSet<Integer> seedBadNodes;
	
	public KendallTauBadNodeTest(ImmutableGraph graph,
			HashSet<Integer> seedGoodNodes,HashSet<Integer> seedBadNodes) {
		super(graph, seedGoodNodes);
		this.seedBadNodes = seedBadNodes;
		// TODO Auto-generated constructor stub
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
		
		double[] trustrank = Utility.readTrustrank("trustrank.txt");
		
		//nel vettore di trustrank imposto a 0 tutti i valori che non sono etichettati come bad
		//in questo modo faccio un confronto con la tao di kendall solo tra nodi cattivi
		
		for (int t=0;t<trustrank.length;t++)
			if (!seedBadNodes.contains(t))
				trustrank[t] = 0;

		double[] tauKendallBadNodes = new double[bfs.queue.size()];
		
		FileWriter kendallTau = new FileWriter(KENDALLTAUBADNODES);
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
			TrustRank trustSubGraph = new TrustRank(subGraph);
			HashSet<Integer> newSeed = new HashSet<Integer>();
			for (int j : t)
				if (seedGoodNodes.contains(j)) {
					newSeed.add(subGraph.fromSupergraphNode(j));
				}
			trustSubGraph.setGoodSeeds(newSeed);
			trustSubGraph.compute();
			double[] temp = new double[trustrank.length];
			for (double f : temp)
				f = 0.0;
			for (int j = 0; j < trustSubGraph.getRank().length; j++) {
				//prendo in consideranzion solo i nodi cattivi gli altri li lascio a 0
				if(seedBadNodes.contains(subGraph.toSupergraphNode(j)))
				temp[subGraph.toSupergraphNode(j)] = trustSubGraph.getRank()[j];
			}
			tauKendallBadNodes[i] = KendallTau.compute(trustrank, temp);
			bf.write(i + " " + tauKendallBadNodes[i] + "\n");
			bf.flush();
			System.err.println("Iterazione: "+i);
			i+= increment;
		}

	}

}
