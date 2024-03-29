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
import java.util.LinkedList;
import java.util.Set;

public class TrustRankBadNodeTest extends TrustRankTest implements Test {

	private HashSet<Integer> seedBadNodes;

	public TrustRankBadNodeTest(ImmutableGraph graph, IntArrayList bfs,
			HashSet<Integer> seedGoodNodes, HashSet<Integer> seedBadNodes,
			String namePath, int mode) {
		super(graph, bfs, seedGoodNodes, namePath, mode);
		this.seedBadNodes = seedBadNodes;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub

		System.out.println("Esecuzione della visita dei nodi...");
		ImmutableGraph graphBFS = graph;

		System.out.println("Nodi visitati: " + bfs.size());
		System.out
				.println("Calcolo tau di trustrank per ogni dimensione della visita...");

		double[] trustrank = Utility.readRank("trustrank.txt");

		// nel vettore di trustrank imposto a 0 tutti i valori che non sono
		// etichettati come bad
		// in questo modo faccio un confronto con la tao di kendall solo tra
		// nodi cattivi

		for (int t = 0; t < trustrank.length; t++)
			if (!seedBadNodes.contains(t))
				trustrank[t] = 0;

		double[] tauKendallBadNodes = new double[bfs.size()];

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
			for (int j : t){
				if (seedGoodNodes.contains(j)) {
					newSeed.add(subGraph.fromSupergraphNode(j));
				}				
			}	
			
			trustSubGraph.setSeeds(newSeed);
			trustSubGraph.compute();
			double[] temp = new double[trustrank.length];
			int numeroNodiBad = 0;

			for (double f : temp)
				f = 0.0;
			for (int j = 0; j < trustSubGraph.getRank().length; j++) {
				// prendo in consideranzion solo i nodi cattivi gli altri li
				// lascio a 0
				if (seedBadNodes.contains(subGraph.toSupergraphNode(j))) {
					temp[subGraph.toSupergraphNode(j)] = trustSubGraph
							.getRank()[j];
					numeroNodiBad++;
				}
			}
			if (mode == 0)
				tauKendallBadNodes[iteration] = KendallTau.compute(trustrank,
						temp);
			else if (mode >= 1) {
				int[] nodiBad = new int [numeroNodiBad];
				int valueIncrement = 0;
				for(int j:t){
					if(seedBadNodes.contains(j)){
						nodiBad[valueIncrement] = j;
						valueIncrement++;
					}
				}
				double[] portionOftrustrank = new double[nodiBad.length];
				double[] portionOftrustrankSub = new double[nodiBad.length];
				for (int v = 0; v < nodiBad.length; v++) {
					portionOftrustrank[v] = trustrank[nodiBad[v]];
					portionOftrustrankSub[v] = temp[nodiBad[v]];
				}
				if (portionOftrustrank.length > 0
						&& portionOftrustrankSub.length > 0)
					tauKendallBadNodes[iteration] = KendallTau.compute(
							portionOftrustrank, portionOftrustrankSub);

			}

			bf.write(numeroNodiBad + " " + tauKendallBadNodes[iteration] + "\n");
			bf.flush();
			System.err.println("Iterazione: " + iteration);
			iteration += increment;
		}

	}

}
