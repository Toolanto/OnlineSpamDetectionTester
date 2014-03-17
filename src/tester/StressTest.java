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

/**
 * Questa classe esegue un test di confronto tra trustrank e antitrustrank
 * eseguito sull'interno grafo e e quello eseguito durank una BFS.
 * Inoltre la grund-truth sarà manipolata in modo tale da avere gli n-ultimi nodi della
 * BFS come seed set di nodi non spam o spam a seconda si utilizzi trustrank o antitrustrank
 * 
 * @author antonio
 *
 */

public class StressTest implements Test {

	protected ImmutableGraph graph;
	protected int numberOfnodes;
	protected static final int idStart = 62;
	protected int increment = 500;
	protected String namePahtTrust;
	protected String namePahtAntiTrust;
	
	/**
	 * 
	 * @param graph il grafo completo su cui effettuare i test
	 * @param numberOfnode numero di nodi finali da considerare come seedset
	 */
	public StressTest(ImmutableGraph graph, int numberOfnodes,String namePathTrust, String namePathAntiTrust){
		this.graph = graph;
		this.numberOfnodes = numberOfnodes;
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
		//imposto i seedset con gli ultimi n-nodi
		HashSet<Integer> seedSet = new HashSet<Integer>();
		
		int numberIt = bfs.queue.size()-numberOfnodes;
		
		for(int i=bfs.queue.size()-1;i>numberIt;i--){
			seedSet.add(bfs.queue.get(i));
		}
		
		double[] trustKendall = new double[trustrank.length];
		double[]antitrustKendall = new double[antitrustrank.length];

		FileWriter trustFile = new FileWriter(namePahtTrust);
		BufferedWriter tf = new BufferedWriter(trustFile);
		FileWriter antitrustFile = new FileWriter(namePahtAntiTrust);
		BufferedWriter atf = new BufferedWriter(antitrustFile);

			

		int iteration = 0;
		while ( iteration < bfs.queue.size()) {
			int[] t = new int[iteration + 1];
			// copio in t gli elementi della coda di nodi visitati pari a iteration
			bfs.queue.getElements(0, t, 0, t.length);

            IntSet ts = new IntArraySet(t);
			ImmutableSubgraph subGraph = new ImmutableSubgraph(graph, ts);
			
			System.out.println("Nodi sottografo " + iteration + ": "
					+ subGraph.numNodes());
			
			
			
			TrustRank trustSubGraph = new TrustRank(subGraph);
			
			AntiTrustRank antiTrustSubGraph = new AntiTrustRank(subGraph);
			HashSet<Integer> newSeed = new HashSet<Integer>();
			for (int j : t){
				if (seedSet.contains(j)) {
					newSeed.add(subGraph.fromSupergraphNode(j));
				}
			}
			
			System.out.println("Dimensione seed = "+newSeed.size());
			trustSubGraph.setSeeds(newSeed);
			trustSubGraph.setAlpha(0.0005);
			trustSubGraph.compute();
			antiTrustSubGraph.setSeeds(newSeed);
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
