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

public class StressTesterTwo implements Test {
	protected ImmutableGraph graph;
	protected static final int idStart = 62;
	protected int increment = 500;
	protected String namePahtTrust;
	protected String namePahtAntiTrust;

	public StressTesterTwo(ImmutableGraph graph, 
			String namePathTrust, String namePathAntiTrust) {
		this.graph = graph;
		this.namePahtTrust = namePathTrust;
		this.namePahtAntiTrust = namePathAntiTrust;
	}

	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Esecuzione della visita dei nodi...");
		ParallelBreadthFirstVisit bfs = new ParallelBreadthFirstVisit(graph,
				0, false, null);
		bfs.visit(idStart);
		System.out.println("Nodi visitati partendo dal nodo " + idStart + ":"
				+ bfs.queue.size());
		double[] trustrank = Utility.readRank("trustrank.txt");
		double[] antitrustrank = Utility.readRank("antiTrustrank.txt");


		double[] trustKendall = new double[trustrank.length];
		double[]antitrustKendall = new double[antitrustrank.length];

		FileWriter trustFile = new FileWriter(namePahtTrust);
		BufferedWriter tf = new BufferedWriter(trustFile);
		FileWriter antitrustFile = new FileWriter(namePahtAntiTrust);
		BufferedWriter atf = new BufferedWriter(antitrustFile);

			

		int iteration = 0;
		while ( iteration < bfs.queue.size()) {
			int[] t = new int[iteration + 1];
			int numberIt = t.length-Math.abs(t.length*10/100); // prendo il 10% di nodi a ogni passo della visita
												     	// come seedset
			// copio in t gli elementi della coda di nodi visitati pari a iteration
			bfs.queue.getElements(0, t, 0, t.length);
			//imposto i seedset con gli ultimi n-nodi
			System.out.println(numberIt);
            IntSet ts = new IntArraySet(t);
			ImmutableSubgraph subGraph = new ImmutableSubgraph(graph, ts);
			
			HashSet<Integer> seedSet = new HashSet<Integer>();
			for(int j=t.length-1;j>numberIt;j--){
				seedSet.add(subGraph.fromSupergraphNode(t[j]));
			}
			
			System.out.println("Nodi sottografo " + iteration + ": "
					+ subGraph.numNodes());
			
			
			
			TrustRank trustSubGraph = new TrustRank(subGraph);
			
			AntiTrustRank antiTrustSubGraph = new AntiTrustRank(subGraph);

			
			System.out.println("Dimensione seed = "+seedSet.size());
			trustSubGraph.setSeeds(seedSet);
			trustSubGraph.setAlpha(0.0005);
			trustSubGraph.compute();
			antiTrustSubGraph.setSeeds(seedSet);
			antiTrustSubGraph.setAlpha(0.0005);
			antiTrustSubGraph.compute();
			
			double[] tempTrust = new double[trustrank.length];
			for (int f=0; f<tempTrust.length;f++)
				tempTrust[f] = 0.0;
			
			double[] tempAntiTrust = new double[antitrustrank.length];
			for (int f=0; f<tempAntiTrust.length;f++)
				tempAntiTrust[f] = 0.0;
			
			
			for (int j = 0; j < trustSubGraph.getRank().length; j++) {
				tempTrust[subGraph.toSupergraphNode(j)] = trustSubGraph.getRank()[j];
			}
			
			for (int j = 0; j < antiTrustSubGraph.getRank().length; j++) {
				tempAntiTrust[subGraph.toSupergraphNode(j)] = antiTrustSubGraph.getRank()[j];
			}
			
			trustKendall[iteration] = KendallTau.compute(trustrank, tempTrust);
			tf.write(iteration + " " + trustKendall[iteration] +"\n");
			tf.flush();

			
			antitrustKendall[iteration] = KendallTau.compute(antitrustrank, tempAntiTrust);
			atf.write(iteration + " " + antitrustKendall[iteration] + "\n");
			atf.flush();
			
			System.err.println("Iterazione: "+iteration);
			iteration+= increment;
			
			
			
		}

	}

}
