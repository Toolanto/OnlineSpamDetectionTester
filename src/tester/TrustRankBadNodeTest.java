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

public class TrustRankBadNodeTest extends TrustRankTest implements Test {
	public static final int increment = 500;

	private HashSet<Integer> seedBadNodes;

	public TrustRankBadNodeTest(ImmutableGraph graph, int idStart,
			HashSet<Integer> seedGoodNodes, HashSet<Integer> seedBadNodes,
			String namePath, int mode) {
		super(graph, idStart, seedGoodNodes, namePath, mode);
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

		double[] trustrank = Utility.readRank("trustrank.txt");

		// nel vettore di trustrank imposto a 0 tutti i valori che non sono
		// etichettati come bad
		// in questo modo faccio un confronto con la tao di kendall solo tra
		// nodi cattivi
		// se tolgo il ciclio faccio il confronto con tutti i nodi
		for (int t = 0; t < trustrank.length; t++)
			if (!seedBadNodes.contains(t))
				trustrank[t] = 0;

		double[] tauKendallBadNodes = new double[bfs.queue.size()];

		FileWriter kendallTau = new FileWriter(namePath + "" + idStart + ".txt");
		BufferedWriter bf = new BufferedWriter(kendallTau);
		int iteration = 0;
		while (iteration < bfs.queue.size()) {
			int[] t = new int[iteration + 1];
			// copio in t gli elementi della coda di nodi visitati pari a i
			bfs.queue.getElements(0, t, 0, iteration + 1);
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
			int numeroNodiBad = 0;

			for (double f : temp)
				f = 0.0;
			for (int j = 0; j < trustSubGraph.getRank().length; j++) {
				// prendo in consideranzion solo i nodi cattivi gli altri li
				// lascio a 0
				if (seedBadNodes.contains(subGraph.toSupergraphNode(j))) {
					temp[subGraph.toSupergraphNode(j)] = trustSubGraph
							.getRank()[j];
					if (mode >= 1)
						idNode[j] = subGraph.toSupergraphNode(j);
					    numeroNodiBad++;
				}
			}
			if (mode == 0)
				tauKendallBadNodes[iteration] = KendallTau.compute(trustrank,
						temp);
			else if(mode>=1){
				double[] portionOftrustrank = new double[trustSubGraph.getRank().length];
				double[] portionOftrustrankSub = new double[trustSubGraph.getRank().length];
				for(int v=0;v<trustSubGraph.getRank().length;v++){
					portionOftrustrank[v] = trustrank[idNode[v]];
				    portionOftrustrankSub[v] = temp[idNode[v]];
				    
				    //System.out.println("id: "+v+" trustrank: "+portionOftrustrank[v]+" trustranksub: "+portionOftrustrankSub[v]+" lunghezza: "+idNode.length+ " kendall: "+KendallTau.compute(portionOftrustrank, portionOftrustrankSub));
				}
				tauKendallBadNodes[iteration] = KendallTau.compute(portionOftrustrank, portionOftrustrankSub);
			}

			bf.write(numeroNodiBad + " " + tauKendallBadNodes[iteration] + "\n");
			bf.flush();
			System.err.println("Iterazione: " + iteration);
			iteration += increment;
		}

	}

}
