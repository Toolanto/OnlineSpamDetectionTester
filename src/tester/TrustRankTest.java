package tester;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.law.stat.KendallTau;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.ImmutableSubgraph;
import it.unimi.dsi.webgraph.algo.ParallelBreadthFirstVisit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;

/**
 * Questa classe ha il compito di calcolare la tau di kendall tra i valori di
 * trustrank di tutto il grafo con i valori di trustrank ottenuti lungo una
 * visita in ampiezza
 * 
 * @author antonio luca
 * 
 */
public class TrustRankTest implements Test {
	public static final int increment = 5000;
	protected String namePath;
	protected ImmutableGraph graph;
	protected HashSet<Integer> seedGoodNodes;
	protected int mode;
	protected IntArrayList bfs;

	/**
	 * 
	 * @param graph
	 *            il grafo su cui eseguire il test
	 * @param bfs 
	 * 			 l'array di nodi della visita    
	 * @param idStart
	 *            il nodo da cui deve partire la BFS
	 * @param seedGoodNodes
	 *            il seedset di trustrank
	 * @param namePath
	 *            il file su cui salvare i risultati
	 * @param mode
	 *            indica se esegurire il confronto tra i vettori di trustrank
	 *            (del grafo completo e della BFS) in modo completo o su una
	 *            porzione di essi; 0 per confrontare in modo completo, 1 per
	 *            confrontare solo una porzione
	 *            
	 */
	public TrustRankTest(ImmutableGraph graph, IntArrayList bfs,
	HashSet<Integer> seedGoodNodes, String namePath,
			int mode) {
		this.graph = graph;
		this.seedGoodNodes = seedGoodNodes;
		this.namePath = namePath;
		this.mode = mode;
		this.bfs = bfs;
	}

	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub

		System.out.println("Esecuzione della visita dei nodi...");
		ImmutableGraph graphBFS = graph;

		System.out.println("Nodi visitati: "
				+ bfs.size());
		System.out
				.println("Calcolo tau di trustrank per ogni dimensione della visita...");
		double[] trustrank = Utility.readRank("trustrank.txt");
		double[] tauKendall = new double[bfs.size()];
		FileWriter kendallTau = new FileWriter(namePath);
		BufferedWriter bf = new BufferedWriter(kendallTau);
		int iteration = 0;
		while (iteration < bfs.size()) {
			int[] t = new int[iteration + 1];
			// copio in t gli elementi della coda di nodi visitati pari a i
			bfs.getElements(0, t, 0, iteration + 1);
			System.out.println("Numero nodi array " + iteration + ": "
					+ t.length);
			IntSet ts = new IntArraySet(t);
			ImmutableSubgraph subGraph = new ImmutableSubgraph(graphBFS, ts);
			System.out.println("Nodi sottografo " + iteration + ": "
					+ subGraph.numNodes());
			TrustRank trustSubGraph = new TrustRank(subGraph);
			HashSet<Integer> newSeed = new HashSet<Integer>();
			for (int j : t)
				if (seedGoodNodes.contains(j)) {
					newSeed.add(subGraph.fromSupergraphNode(j));
				}
			trustSubGraph.setSeeds(newSeed);
			trustSubGraph.compute();

			double[] temp = new double[trustrank.length];
			int[] idNode = new int[trustSubGraph.getRank().length]; // in caso
																	// si vuole
																	// il modo 1

			for (int f = 0; f < temp.length; f++)
				temp[f] = 0.0;

			for (int j = 0; j < trustSubGraph.getRank().length; j++) {
				temp[subGraph.toSupergraphNode(j)] = trustSubGraph.getRank()[j];
				if (mode >= 1)
					idNode[j] = subGraph.toSupergraphNode(j);
			}

			if (mode == 0)
				tauKendall[iteration] = KendallTau.compute(trustrank, temp);
			else if (mode >= 1) {
				double[] portionOftrustrank = new double[trustSubGraph
						.getRank().length];
				double[] portionOftrustrankSub = new double[trustSubGraph
						.getRank().length];
				for (int v = 0; v < trustSubGraph.getRank().length; v++) {
					portionOftrustrank[v] = trustrank[idNode[v]];
					portionOftrustrankSub[v] = temp[idNode[v]];

					// System.out.println("id: "+v+" trustrank: "+portionOftrustrank[v]+" trustranksub: "+portionOftrustrankSub[v]+" lunghezza: "+idNode.length+
					// " kendall: "+KendallTau.compute(portionOftrustrank,
					// portionOftrustrankSub));
				}
				tauKendall[iteration] = KendallTau.compute(portionOftrustrank,
						portionOftrustrankSub);
			}

			bf.write(iteration + 1 + " " + tauKendall[iteration] + "\n");
			bf.flush();
			System.err.println("Iterazione: " + iteration);
			iteration += increment;

		}

	}

}
