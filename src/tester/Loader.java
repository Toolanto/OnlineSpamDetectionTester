package tester;

import java.io.IOException;

import it.unimi.dsi.webgraph.BVGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;

/**
 * 
 * @author antonio
 *
 * Rappresenta il grafo caricato in memoria
 */
public class Loader {
    
	private BVGraph ig;
	
	public Loader(String basename) throws IOException{
		ig = BVGraph.loadMapped(basename);
	}
	
	public BVGraph getGraph(){
		return ig;
	}

}
