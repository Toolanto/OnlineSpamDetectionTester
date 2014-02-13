package tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LabelReader {

	private String file;
	
	public LabelReader(String file){
	  this.file = file;	
	}
	
	public ArrayList<LabelNode> reade(){
		ArrayList<LabelNode>  labelNodes= new ArrayList<LabelNode>();
		File name = new File(file);
		if (name.isFile()) {
			try {
				BufferedReader input = new BufferedReader(new FileReader(name));
				String text;
				while ((text = input.readLine()) != null){
					/**
					 *  le label sono cosi disposte
					 *  id label ecc
					 *  prendo i primi due campo
					 */
					String[] temp = text.split(" ");
					//System.out.println(temp[0]+" "+temp[1]);
					labelNodes.add(new LabelNode(Integer.parseInt(temp[0]), temp[1]));
				}
				input.close();
                return labelNodes;
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
		return null;
	}
}
