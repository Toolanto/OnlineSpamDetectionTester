package tester;

public class LabelNode {

	private int id;
	private String label;
	
	public LabelNode(int id, String label){
		this.id = id;
		this.label = label;
	}
	
	public int getId(){
		return id;
	}
	
	public String getLabel(){
		return label;
	}
}
