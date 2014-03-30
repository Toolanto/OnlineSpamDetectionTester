package tester;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.law.stat.KendallTau;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.ImmutableSubgraph;
import it.unimi.dsi.webgraph.algo.ParallelBreadthFirstVisit;

public class AntiTrustRankTest implements Test {

	public int increment = 5000;

	protected ImmutableGraph graph;
	protected HashSet<Integer> seedBadNodes;
	protected String namePath;
	protected int mode;
	protected IntArrayList bfs;

	public AntiTrustRankTest(ImmutableGraph graph, IntArrayList bfs,
			HashSet<Integer> seedBadNodes, String namePath, int mode) {
		this.graph = graph;
		this.seedBadNodes = seedBadNodes;
		this.namePath = namePath;
		this.mode = mode;
		this.bfs = bfs;
	}

	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Esecuzione di: antitrustranktest");
		System.out.println("Esecuzione della visita dei nodi...");
		ImmutableGraph graphBFS = graph;

		System.out.println("Nodi visitati: "+ bfs.size());
		System.out
				.println("Calcolo tau di AntiTrustrank per ogni dimensione della visita...");
		double[] antiTrustrank = Utility.readRank("antiTrustrank.txt");
		double[] tauKendall = new double[bfs.size()];
		FileWriter kendallTau = new FileWriter(namePath );
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
			AntiTrustRank antitrustSubGraph = new AntiTrustRank(subGraph);
			HashSet<Integer> newSeed = new HashSet<Integer>();
			for (int j : t)
				if (seedBadNodes.contains(j)) {
					newSeed.add(subGraph.fromSupergraphNode(j));
				}
			antitrustSubGraph.setSeeds(newSeed);
			antitrustSubGraph.compute();
			double[] temp = new double[antiTrustrank.length];

			for (int f = 0; f < temp.length; f++)
				temp[f] = 0.0;

			for (int j = 0; j < antitrustSubGraph.getRank().length; j++) {
				temp[subGraph.toSupergraphNode(j)] = antitrustSubGraph.getRank()[j];
			}
			if (mode == 0)
				tauKendall[i] = KendallTau.compute(antiTrustrank, temp);
			else if(mode>=1){
				double[] portionOfantitrustrank = new double[t.length];
				double[] portionOfantitrustrankSub = new double[t.length];
				for(int v=0;v<t.length;v++){
					portionOfantitrustrank[v] = antiTrustrank[t[v]];
				    portionOfantitrustrankSub[v] = temp[t[v]];
				    
				}
				tauKendall[i] = KendallTau.compute(portionOfantitrustrank, portionOfantitrustrankSub);
			}
			bf.write(i+1 + " " + tauKendall[i] + "\n");
			bf.flush();
			System.err.println("Iterazione: " + i);
			i += increment;
		}
	}

}
