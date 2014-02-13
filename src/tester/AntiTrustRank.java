package tester;

import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.Transform;
import edu.uci.ics.jung.algorithms.scoring.PageRank;

/**
 * 
 * @author antonio
 * rispetto a trustrank cambia due fattori:
 * 		il grafo trasposto
 * 		il seed set formato da nodi spam
 */

public class AntiTrustRank extends TrustRank{

	public AntiTrustRank(ImmutableGraph g) {
		/**
		 *  transpose graph
		 */
		super(Transform.transpose(g));
		// TODO Auto-generated constructor stub
	}

	
}
