package object;
public class Edge {

	private String fromnode;
	private String tonode;
	private int weight;
	private int fromtime;
	private int totime;

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
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
