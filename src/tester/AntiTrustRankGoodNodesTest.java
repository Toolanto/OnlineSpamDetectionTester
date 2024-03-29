package tester;


import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.law.stat.KendallTau;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.ImmutableSubgraph;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.LinkedList;

public class AntiTrustRankGoodNodesTest extends AntiTrustRankTest implements
		Test {

	private HashSet<Integer> seedGoodNodes;

	public AntiTrustRankGoodNodesTest(ImmutableGraph graph, IntArrayList bfs,
			HashSet<Integer> seedBadNodes, HashSet<Integer> seedGoodNodes,
			String namePath, int mode) {
		super(graph, bfs, seedBadNodes, namePath, mode);
		this.seedGoodNodes = seedGoodNodes;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Calcolo di AntitrustRankGoodNodes");
		System.out.println("Esecuzione della visita dei nodi...");
		ImmutableGraph graphBFS = graph;

		System.out.println("Nodi visitati: " + bfs.size());
		System.out
				.println("Calcolo tau di trustrank per ogni dimensione della visita...");

		double[] antiTrustRank = Utility.readRank("antiTrustrank.txt");

		// nel vettore di antitrustrank imposto a 0 tutti i valori che non sono
		// etichettati come good
		// in questo modo faccio un confronto con la tao di kendall solo tra
		// nodi buoni
		// se tolgo il ciclio faccio il confronto con tutti i nodi
		System.out.println(seedGoodNodes.size());
		for (int t = 0; t < antiTrustRank.length; t++)
			if (!seedGoodNodes.contains(t))
				antiTrustRank[t] = 0;

		double[] tauKendallGoodNodes = new double[bfs.size()];

		FileWriter kendallTau = new FileWriter(namePath);
		BufferedWriter bf = new BufferedWriter(kendallTau);
		int i = 0;
		while (i < bfs.size()) {
			int[] t = new int[i + 1];
			// copio in t gli elementi della coda di nodi visitati pari a i
			bfs.getElements(0, t, 0, i + 1);
			System.out.println("Numero nodi array " + i + ": " + t.length);
			IntSet ts = new IntArraySet(t);
			ImmutableSubgraph subGraph = new ImmutableSubgraph(graphBFS, ts);
			System.out.println("Nodi sottografo " + i + ": "
					+ subGraph.numNodes());
			AntiTrustRank antiTrustSubGraph = new AntiTrustRank(subGraph);
			HashSet<Integer> newSeed = new HashSet<Integer>();
			for (int j : t)
				if (seedBadNodes.contains(j)) {
					newSeed.add(subGraph.fromSupergraphNode(j));
				}
			antiTrustSubGraph.setSeeds(newSeed);
			antiTrustSubGraph.compute();
			double[] temp = new double[antiTrustRank.length];
			int numeroNodiGood = 0;

			
			for (int j = 0; j < antiTrustSubGraph.getRank().length; j++) {
				// prendo in consideranzion solo i nodi buoni gli altri li
				// lascio a 0
				if (seedGoodNodes.contains(subGraph.toSupergraphNode(j))) {
					temp[subGraph.toSupergraphNode(j)] = antiTrustSubGraph
							.getRank()[j];
					numeroNodiGood++;
				}
			}
			if (mode == 0)
				tauKendallGoodNodes[i] = KendallTau
						.compute(antiTrustRank, temp);
			else if (mode >= 1) {
				
				int[] nodiGood = new int [numeroNodiGood];
				int valueIncrement = 0;
				for(int j:t){
					if(seedGoodNodes.contains(j)){
						nodiGood[valueIncrement] = j;
						valueIncrement++;
					}
				}
				double[] portionOfantitrustrank = new double[nodiGood.length];
				double[] portionOfantitrustrankSub = new double[nodiGood.length];
				for (int v = 0; v < nodiGood.length; v++) {
					portionOfantitrustrank[v] = antiTrustRank[nodiGood[v]];
					portionOfantitrustrankSub[v] = temp[nodiGood[v]];

				}
				if (portionOfantitrustrank.length > 0
						&& portionOfantitrustrankSub.length > 0)
					tauKendallGoodNodes[i] = KendallTau.compute(
							portionOfantitrustrank, portionOfantitrustrankSub);
			}
			
			bf.write(numeroNodiGood + " " + tauKendallGoodNodes[i] + "\n");
			bf.flush();
			System.err.println("Iterazione: " + i);
			i += increment;
		}
	}

}
