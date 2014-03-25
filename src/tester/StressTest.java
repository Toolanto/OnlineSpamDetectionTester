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

/**
 * Questa classe esegue un test di confronto tra trustrank e antitrustrank
 * eseguito sul grafo ottenuto dalla BFS e e quello eseguito durank una BFS a
 * ogni passo. Inoltre la grund-truth sarà manipolata in modo tale da avere gli
 * n-ultimi nodi della BFS come seed set di nodi non spam o spam a seconda si
 * utilizzi trustrank o antitrustrank
 * 
 * @author antonio
 * 
 */

public class StressTest implements Test {

	protected ImmutableGraph graph;
	protected int numberOfnodes;
	protected int idStart;
	protected int increment = 500;
	protected String namePahtTrust;
	protected String namePahtAntiTrust;
	protected int mode;

	/**
	 * 
	 * @param graph
	 *            il grafo completo su cui effettuare i test
	 * @param numberOfnode
	 *            numero di nodi finali da considerare come seedset
	 */
	public StressTest(ImmutableGraph graph, int idStart, int numberOfnodes,
			String namePathTrust, String namePathAntiTrust, int mode) {
		this.graph = graph;
		this.numberOfnodes = numberOfnodes;
		this.namePahtTrust = namePathTrust;
		this.namePahtAntiTrust = namePathAntiTrust;
		this.idStart = idStart;
		this.mode = mode;
	}

	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Esecuzione della visita dei nodi...");
		ParallelBreadthFirstVisit bfs = new ParallelBreadthFirstVisit(graph, 0,
				false, null);
		bfs.visit(idStart);
		System.out.println("Nodi visitati partendo dal nodo " + idStart + ":"
				+ bfs.queue.size());

		double[] trustrank = new double[graph.numNodes()];
		double[] antitrustrank = new double[graph.numNodes()];

		// imposto i seedset con gli ultimi n-nodi
		HashSet<Integer> seedSet = new HashSet<Integer>();

		int numberIt = bfs.queue.size() - numberOfnodes;

		for (int i = bfs.queue.size() - 1; i > numberIt; i--) {
			seedSet.add(bfs.queue.get(i));
		}

		// calcolo trustrank non sul grafo completo ma sul grafo della visita
		// faccio la stessa cosa per antitrust

		ImmutableSubgraph subGraphComplete = new ImmutableSubgraph(graph,
				new IntArraySet(bfs.queue));

		TrustRank BFStrust = new TrustRank(subGraphComplete);
		AntiTrustRank BFSantitrust = new AntiTrustRank(subGraphComplete);
		HashSet<Integer> traslateSeedSetSub = new HashSet<Integer>();
		for (int n : bfs.queue) {
			if (seedSet.contains(n))
				traslateSeedSetSub.add(subGraphComplete.fromSupergraphNode(n));
		}
		BFStrust.setSeeds(traslateSeedSetSub);
		BFSantitrust.setSeeds(traslateSeedSetSub);
		BFStrust.compute();
		BFSantitrust.compute();
		for (int f = 0; f < graph.numNodes(); f++) {
			trustrank[f] = 0.0;
			antitrustrank[f] = 0.0;
		}

		for (int j = 0; j < BFStrust.getRank().length; j++) {
			trustrank[subGraphComplete.toSupergraphNode(j)] = BFStrust
					.getRank()[j];
		}
		for (int j = 0; j < BFSantitrust.getRank().length; j++) {
			antitrustrank[subGraphComplete.toSupergraphNode(j)] = BFSantitrust
					.getRank()[j];
		}

		double[] trustKendall = new double[BFStrust.getRank().length];
		double[] antitrustKendall = new double[BFSantitrust.getRank().length];
		
		

		FileWriter trustFile = new FileWriter(namePahtTrust + idStart + ".txt");
		BufferedWriter tf = new BufferedWriter(trustFile);
		FileWriter antitrustFile = new FileWriter(namePahtAntiTrust + idStart
				+ ".txt");
		BufferedWriter atf = new BufferedWriter(antitrustFile);

		int iteration = 0;
		while (iteration < bfs.queue.size()) {
			int[] t = new int[iteration + 1];
			// copio in t gli elementi della coda di nodi visitati pari a
			// iteration
			bfs.queue.getElements(0, t, 0, t.length);

			IntSet ts = new IntArraySet(t);
			ImmutableSubgraph subGraph = new ImmutableSubgraph(graph, ts);

			System.out.println("Nodi sottografo " + iteration + ": "
					+ subGraph.numNodes());

			TrustRank trustSubGraph = new TrustRank(subGraph);
			AntiTrustRank antiTrustSubGraph = new AntiTrustRank(subGraph);
			HashSet<Integer> newSeed = new HashSet<Integer>();
			for (int j : t) {
				if (seedSet.contains(j)) {
					newSeed.add(subGraph.fromSupergraphNode(j));
				}
			}

			trustSubGraph.setSeeds(newSeed);
			trustSubGraph.compute();
			antiTrustSubGraph.setSeeds(newSeed);
			antiTrustSubGraph.compute();

			double[] tempTrust = new double[graph.numNodes()];
			int[] idNodeTrust = new int[trustSubGraph.getRank().length]; // in caso si vuole il modo 1
			for (int f = 0; f < tempTrust.length; f++)
				tempTrust[f] = 0.0;

			double[] tempAntiTrust = new double[graph.numNodes()];
			int[] idNodeAntitrust = new int[antiTrustSubGraph.getRank().length]; // in caso si vuole il modo 1
			for (int f = 0; f < tempAntiTrust.length; f++)
				tempAntiTrust[f] = 0.0;

			for (int j = 0; j < trustSubGraph.getRank().length; j++) {
				tempTrust[subGraph.toSupergraphNode(j)] = trustSubGraph
						.getRank()[j];
				if(mode>=1)
					idNodeTrust[j]=subGraph.toSupergraphNode(j);
			}

			for (int j = 0; j < antiTrustSubGraph.getRank().length; j++) {
				tempAntiTrust[subGraph.toSupergraphNode(j)] = antiTrustSubGraph
						.getRank()[j];
				idNodeAntitrust[j]=subGraph.toSupergraphNode(j);
			}

			if(mode==0){
				trustKendall[iteration] = KendallTau.compute(trustrank, tempTrust);
				antitrustKendall[iteration] = KendallTau.compute(antitrustrank,
						tempAntiTrust);
			}else if(mode>=1){
				double[] portionOftrustrank = new double[trustSubGraph.getRank().length];
				double[] portionOftrustrankSub = new double[trustSubGraph.getRank().length];
				double[] portionOfantitrustrank = new double[antiTrustSubGraph.getRank().length];
				double[] portionOfantitrustrankSub = new double[antiTrustSubGraph.getRank().length];
				for(int v=0;v<trustSubGraph.getRank().length;v++){
					portionOftrustrank[v] = trustrank[idNodeTrust[v]];
				    portionOftrustrankSub[v] = tempTrust[idNodeTrust[v]];
				    
				}
				for(int v=0;v<antiTrustSubGraph.getRank().length;v++){
					portionOfantitrustrank[v] = antitrustrank[idNodeAntitrust[v]];
				    portionOfantitrustrankSub[v] = tempAntiTrust[idNodeAntitrust[v]];
				    
				}
				
				trustKendall[iteration] = KendallTau.compute(portionOftrustrank, portionOftrustrankSub);
				antitrustKendall[iteration] = KendallTau.compute(portionOfantitrustrank, portionOfantitrustrankSub);
			}
			
			tf.write(iteration + " " + trustKendall[iteration] + "\n");
			tf.flush();

			atf.write(iteration + " " + antitrustKendall[iteration] + "\n");
			atf.flush();

			System.err.println("Iterazione: " + iteration);
			iteration += increment;

		}

	}

}
