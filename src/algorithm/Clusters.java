package algorithm;
import java.util.ArrayList;
import java.util.List;

public class Clusters {

	String edgelist[][]; // list of edges (from node to node)
	List<Double> edgeweight = new ArrayList<Double>();
	// double edgeweight[][];//weight of each edge
	int edgenumber; // quantity of initial edges
	int nodes; // quantity of nodes
	String nodelist[]; // list of nodes names
	ArrayList<Integer> finallabel; // final label of clusters
	double subspace[][]; // subspace where the kmeans works
	String clusterlabel[][]; // table with the final cluster labels node by node
	int pointpercluster[][];
	double incidencematrix[][];
	int nodeincrement;
	int incrementw;
	int totalincrementw;
	double laplacian[][];
	int edgeaccum;
	int nodeaccum;
	double incidencew[];
	double diagonal[][];
	double wadjacency[][];
	double deltalapla[][];
	double deltadiago[][];
	double eigenvector2[][];
	double eigenvector2aux[][];
	double eigenvector3[][];
	double eigenvector3aux[][];
	double eigenvalue2;
	double eigenvalue3;
	double deltaevalue2;
	double deltaevalue3;
	String accumnodelist[];
	double lapladapt[][];
	double diagoadapt[][];
	int result[][];
	ArrayList<Double> evectorII = new ArrayList<Double>();
	ArrayList<Double> evectorIII = new ArrayList<Double>();
	// /////////////////////
	String edges[][];
	ArrayList<String> timenodes = new ArrayList<String>();
	List<String> fnode = new ArrayList<String>();
	List<String> tnode = new ArrayList<String>();
	List<Double> weight = new ArrayList<Double>();
	int edgesnumber = 0;
	ArrayList<String> nodeslistaux = new ArrayList<String>();
	int nod = 0;
	int deltanodes = 0;
	ArrayList<String> fromnode = new ArrayList<String>();
	ArrayList<String> tonode = new ArrayList<String>();
	int incrementw2 = 0;

	public int totalIncrementw(int newValue) {
		this.totalincrementw = totalincrementw + newValue;
		return totalincrementw;
	}

	public void EvectorII(ArrayList<Double> newValue) {

		// System.out.println(this.evectorII.addAll(newValue));
		// System.out.println(evectorII.size());
		// return evectorII;
	}

	public void EvectorIII(ArrayList<Double> newValue) {
		// this.evectorII = newValue;
		// return evectorIII;
	}

	public int[][] Result(int newValue[][]) {
		this.result = newValue;
		return result;
	}

	public double[][] laplAdapt(double newValue[][]) {
		this.lapladapt = newValue;
		return lapladapt;
	}

	public double[][] diagoAdapt(double newValue[][]) {
		this.diagoadapt = newValue;
		return diagoadapt;
	}

	public void setAcumNodeList(String[] newValue) {
		this.accumnodelist = newValue;
	}

	public double deltaEvalue2(double newValue) {
		this.deltaevalue2 = newValue;
		return deltaevalue2;
	}

	public double deltaEvalue3(double newValue) {
		this.deltaevalue3 = newValue;
		return deltaevalue3;
	}

	public double[][] eigenVector2aux(double newValue[][]) {
		this.eigenvector2aux = newValue;
		return eigenvector2aux;
	}

	public double[][] eigenVector2(double newValue[][]) {
		this.eigenvector2 = newValue;
		return eigenvector2;
	}

	public double[][] eigenVector3(double newValue[][]) {
		this.eigenvector3 = newValue;
		return eigenvector3;
	}

	public double[][] eigenVector3aux(double newValue[][]) {
		this.eigenvector3aux = newValue;
		return eigenvector3aux;
	}

	public double eigenValue2(double newValue) {
		this.eigenvalue2 = newValue;
		return eigenvalue2;
	}

	public double eigenValue3(double newValue) {
		this.eigenvalue3 = newValue;
		return eigenvalue3;
	}

	public double[][] deltaDiago(double newValue[][]) {
		this.deltadiago = newValue;
		return deltadiago;
	}

	public double[][] deltaLapla(double newValue[][]) {
		this.deltalapla = newValue;
		return deltalapla;
	}

	public void setIncidenceW(List<Double> newValue) {
		this.incidencew = new double[newValue.size()];

		for (int i = 0; i < newValue.size(); i++) {
			incidencew[i] = newValue.get(i);
		}
	}

	public void setNodeAccum(int newValue) {
		this.nodeaccum = newValue;
	}

	public void setEdgeAccum(int newValue) {
		this.edgeaccum = newValue;
	}

	public void setIncidenceMatrix(double newValue[][]) {
		this.incidencematrix = newValue;
	}

	public int[][] pointPercluster(int newValue[][]) {
		this.pointpercluster = newValue;
		return pointpercluster;
	}

	public String[][] clusterLabel(String newValue[][]) {
		this.clusterlabel = newValue;
		return clusterlabel;
	}

	public void setNodeList(String newValue[]) {
		this.nodelist = newValue;
	}

	public double[][] subSpace(double newValue[][]) {
		this.subspace = newValue;
		return subspace;
	}

	public void setEdgeList(String newValue[][]) {
		this.edgelist = newValue;
	}

	public void setEdgeNumber(int newValue) {
		this.edgenumber = newValue;
	}

	public void setNode(int newValue) {
		this.nodes = newValue;
	}

	public int nodeIncrement(int newValue) {
		this.nodeincrement = newValue;
		return nodeincrement;
	}

	public int incrementW(int newValue) {
		this.incrementw = newValue;
		return incrementw;
	}

	// public int[][] finalLabel(ArrayList<Double>){
	// this.finallabel = newValue;
	// return finallabel;
	// }

	public void setLaplacian(double[][] newValue) {
		this.laplacian = newValue;
	}

	public void setDiagonal(double[][] newValue) {
		this.diagonal = newValue;
	}

	public double[][] Wadjacency(double[][] newValue) {
		this.wadjacency = newValue;
		return wadjacency;
	}

}
