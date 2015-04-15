package object;
public class Edge {

	private String fromnode;
	private String tonode;
	private double weight;
	private int fromtime;
	private int totime;

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getTonode() {
		return tonode;
	}

	public void setTonode(String tonode) {
		this.tonode = tonode;
	}

	public String getFromnode() {
		return fromnode;
	}

	public void setFromnode(String fromnode) {
		this.fromnode = fromnode;
	}

	public int getFromtime() {
		return fromtime;
	}

	public void setFromtime(int fromtime) {
		this.fromtime = fromtime;
	}

	public int getTotime() {
		return totime;
	}

	public void setTotime(int totime) {
		this.totime = totime;
	}

}
