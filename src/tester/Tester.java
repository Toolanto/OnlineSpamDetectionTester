package tester;

import it.unimi.dsi.webgraph.NodeIterator;

import java.io.IOException;

import javax.print.attribute.standard.PageRanges;

import edu.uci.ics.jung.algorithms.scoring.PageRank;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		try {
			Loader graph = new Loader("../dataset1/uk-2007-05");
			NodeIterator node = graph.getGraph().nodeIterator();
			System.out.println(graph.getGraph().numNodes());
			System.out.println(graph.getGraph().numArcs());
			LabelReader lr = new LabelReader("../dataset1/WEBSPAM-UK2007-SET1-labels.txt");
			System.out.println(lr.reade().size());
			while (node.hasNext()){
				System.out.println(node.next().toString());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block	
			e.printStackTrace();
		}
		
	}

}
