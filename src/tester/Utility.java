package tester;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class Utility {
	
	public static final String LABELPATH = "../dataset1/WEBSPAM-UK2007-SET1-labels.txt";
	public static final String LABELPATH2 = "../dataset1/WEBSPAM-UK2007-SET2-labels.txt";
	public static final String VISIT = "visit62.txt";
	public static final String VISIT2 = "visit112.txt";

	
	public static ArrayList<LabelNode> readLabel(String file){
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
	
	public static double[] readRank(String name){
		
		DoubleArrayList trustRank = new DoubleArrayList();
		File nameFile = new File(name);
		if (nameFile.isFile()) {
			try {
				BufferedReader input = new BufferedReader(new FileReader(nameFile));
				String text;
				while ((text = input.readLine()) != null){
					String[] temp = text.split(" ");
					trustRank.add(Integer.parseInt(temp[0]), (Double.parseDouble(temp[1])));
				}
				input.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
        return trustRank.toDoubleArray();
	}
	
	public static HashSet<Integer> readeLabel(String type,String path) {
		System.out.println("Lettura etichette....");
		ArrayList<LabelNode> labelNode = Utility.readLabel(path);
		HashSet<Integer> seed = new HashSet<Integer>();
		for (LabelNode n : labelNode)
			if (n.getLabel().equals(type))
				seed.add(n.getId());
		return seed;
	}
	
	public static IntArrayList readVisit(String path){
		IntArrayList v = new IntArrayList();
		File name = new File(path);
		if (name.isFile()) {
			try {
				BufferedReader input = new BufferedReader(new FileReader(name));
				String text;
				while ((text = input.readLine()) != null){
					v.add(Integer.parseInt(text));
				}
				input.close();
                return v;
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
		return null;
	}

}
