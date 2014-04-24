package tester;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.law.stat.KendallTau;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.ImmutableSubgraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * 
 * @author antonio
 *
 * Questa classe esegue il seguente test: per ogni passo della BFS si prendono i nodi spam
 * e i nodi non spam si calcala la media dei nodi spam e non spam e si esegue la defferenza tra le due medie
 */

public class AvaregeTest implements Test {

	private HashSet<Integer> seedBadNodes;
    private HashSet<Integer> seedGoodNodes;
	private ImmutableGraph graph;
	private IntArrayList bfs;
	private String namePathTrust, namePathAntitrust;
	private int increment = 5000;
	
	public AvaregeTest(ImmutableGraph graph, IntArrayList bfs,
			HashSet<Integer> seedGoodNodes, HashSet<Integer> seedBadNodes,
			String namePathTrust, String namePathAntitrust) {
		this.seedBadNodes = seedBadNodes;
		this.seedGoodNodes = seedGoodNodes;
		this.graph = graph;
		this.bfs = bfs;
		this.namePathTrust = namePathTrust;
		this.namePathAntitrust = namePathAntitrust;
	}
	
	@Override
	public void run() throws Exception {
		// TODO Auto-generated method stub

		System.out.println("Esecuzione della visita dei nodi...");
		ImmutableGraph graphBFS = graph;

		System.out.println("Nodi visitati: " + bfs.size());
		System.out
				.println("Calcolo tau di trustrank per ogni dimensione della visita...");


		FileWriter differenceTrust = new FileWriter(namePathTrust);
		FileWriter differenceAntitrust = new FileWriter(namePathAntitrust);
		BufferedWriter bufferFileTrust = new BufferedWriter(differenceTrust);
		BufferedWriter bufferFileAntitrust = new BufferedWriter(differenceAntitrust);
		
		
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
			AntiTrustRank antiTrustSubGraph = new AntiTrustRank(subGraph);
			HashSet<Integer> seedGoodSub = new HashSet<Integer>();
			HashSet<Integer> seedBadSub = new HashSet<Integer>();
			
			for (int j : t){
				if (seedGoodNodes.contains(j)) {
					seedGoodSub.add(subGraph.fromSupergraphNode(j));
				}else if (seedBadNodes.contains(j)){
					seedBadSub.add(subGraph.fromSupergraphNode(j));
				}
			}
			
			
			trustSubGraph.setSeeds(seedGoodSub);
			trustSubGraph.compute();
			antiTrustSubGraph.setSeeds(seedBadSub);
			antiTrustSubGraph.compute();
			
			double sumTrustGoodNodes = 0;
			double sumTrustBadNodes = 0;			
			double sumAntiTrustGoodNodes = 0;
			double sumAntiTrustBadNodes = 0;
			
			int numberGood = 0;
			int numberBad = 0;
			
			for(int j=0;j<trustSubGraph.getRank().length;j++){
				if (seedGoodNodes.contains(subGraph.toSupergraphNode(j))) {
                     sumTrustGoodNodes += trustSubGraph.getRank()[j];
                     numberGood ++;
				}else if(seedBadNodes.contains(subGraph.toSupergraphNode(j))){
                    sumTrustBadNodes += trustSubGraph.getRank()[j];
                    numberBad ++;
				}
			}
			
			double mediaTrustGood = sumTrustGoodNodes/numberGood;
			double mediaTrustBad = sumTrustBadNodes/numberBad;

			double difference = mediaTrustGood - mediaTrustBad;

			bufferFileTrust.write(iteration+1 + " " +difference+ "\n");
			bufferFileTrust.flush();
			
			numberGood = 0;
			numberBad = 0;
			
			for(int j=0;j<antiTrustSubGraph.getRank().length;j++){
				if (seedGoodNodes.contains(subGraph.toSupergraphNode(j))) {
                     sumAntiTrustGoodNodes += antiTrustSubGraph.getRank()[j];
                     numberGood ++;
				}else if(seedBadNodes.contains(subGraph.toSupergraphNode(j))){
                    sumAntiTrustBadNodes += antiTrustSubGraph.getRank()[j];
                    numberBad ++;
				}
			}
			
			double mediaAntiTrustGood = sumAntiTrustGoodNodes/numberGood;
			double mediaAntiTrustBad = sumAntiTrustBadNodes/numberBad;
 
			difference = mediaAntiTrustGood - mediaAntiTrustBad;

			bufferFileAntitrust.write(iteration+1 + " "+difference+ "\n");
			bufferFileAntitrust.flush();
			
			System.err.println("Iterazione: " + iteration);
			iteration += increment;
		}

	}


}
