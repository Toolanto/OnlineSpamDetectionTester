package tester;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		try {
			Loader graph = new Loader("../dataset1/uk-2007-05");
			LabelReader lr = new LabelReader("../dataset1/WEBSPAM-UK2007-SET1-labels.txt");
            ArrayList<LabelNode> labelNode = lr.reade();
            HashSet<Integer> seedTrustRank = new HashSet<Integer>();
            HashSet<Integer> seedAntiTrustRank = new HashSet<Integer>();            
            for (LabelNode n:labelNode){
            	if (n.getLabel().equals("nonspam")){
            		seedTrustRank.add(n.getId());
            	}else if(n.getLabel().equals("spam")){
            		seedAntiTrustRank.add(n.getId());
            	}
            }

            /*
             *  compute trustrank and antitrustrank
             */
			
            System.out.println(Runtime.getRuntime().totalMemory());
            TrustRank tr = new TrustRank(graph.getGraph());
            tr.setGoodSeeds(seedTrustRank);
            try {
				tr.compute();
				System.out.println(tr.getRank());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block	
			e.printStackTrace();
		}
		
	}

}
