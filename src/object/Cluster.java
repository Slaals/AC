package object;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

	private String edgeList[][]; // list of edges (from node to node)
	private List<Double> edgeWeight = new ArrayList<Double>();
	// double edgeweight[][];//weight of each edge
	private int nbEdge; // quantity of initial edges
	private int nbNode; // quantity of nodes
	private String nodeList[]; // list of nodes names
	private ArrayList<Integer> finalLabel; // final label of clusters
	private double subSpace[][]; // subspace where the kmeans works
	private String clusterLabel[][]; // table with the final cluster labels node by node
	private int pointPerCluster[][];
	private double incidenceMatrix[][];
	private int nodeInc;
	private int wInc;
	private int wTotalInc;
	private double laplacian[][];
	private int edgeAccum;
	private int nodeAccum;
	private double incidenceW[];
	private double diagonal[][];
	private double wAdjacency[][];
	private double dLapla[][]; // Delta Laplace
	private double dDiago[][]; // Delta diago matrix
	private double eigenVector2[][];
	private double eigenVector2Aux[][];
	private double eigenVector3[][];
	private double eigenVector3Aux[][];
	private double eigenValue2;
	private double eigenValue3;
	private double dEigenValue2;
	private double dEigenValue3;
	private String accumNodeList[];
	private double laplAdapt[][];
	private double diagoAdapt[][];
	private int result[][];
	private ArrayList<Double> eigenVectorII = new ArrayList<Double>();
	private ArrayList<Double> eigenVectorIII = new ArrayList<Double>();
	// /////////////////////
	private String edges[][];
	private ArrayList<String> timeNodes = new ArrayList<String>();
	private List<String> fNode = new ArrayList<String>();
	private List<String> tNode = new ArrayList<String>();
	private List<Double> weight = new ArrayList<Double>();
	private int edgesnumber = 0; // ??
	private ArrayList<String> nodeListAux = new ArrayList<String>();
	private int nod = 0; // ??
	private int dNode = 0;
	private ArrayList<String> fromNode = new ArrayList<String>();
	private ArrayList<String> toNode = new ArrayList<String>();
	private int wInc2 = 0;
	
	public void loadGraph(String[] edges, double[] edgeWeight) {
		fNode.add(edges[0]);
		tNode.add(edges[1]);
		weight.add(edgeWeight[0]);
		timeNodes.add(edges[0]);
		timeNodes.add(edges[1]);
		edgesnumber += 1;
	}
	
	public void removeRedundant(int index) {
		fNode.remove(index);
		tNode.remove(index);
		weight.remove(index);
		edgesnumber -= 1;
	}
	
	public void addFromNode(String value) {
		fromNode.add(value);
	}
	
	public void addToNode(String value) {
		toNode.add(value);
	}
	
	public void fillEdgeWeight(List<Double> weight) {
		edgeWeight.addAll(weight);
	}
	
	public void fillEigenVectorII(ArrayList<Double> values) {
		eigenVectorII.addAll(values);
	}
	
	public void fillEigenVectorIII(ArrayList<Double> values) {
		eigenVectorIII.addAll(values);
	}
	
	public void fillAccumNodeList(ArrayList<String> nodeList) {
		for (int i = 0; i < nodeList.size(); i++) {
			accumNodeList[i] = nodeList.get(i);
		}
	}
	
	public void setAccuNodeListValue(String value, int index) {
		accumNodeList[index] = value;
	}
	
	public void incDeltaNode() {
		this.dNode += 1;
	}
	
	public void incW2() {
		this.wInc2 += 1;
	}
	
	public void setNod(int nod) {
		this.nod = nod;
	}
	
	public void addEigenVectorIII(double value) {
		eigenVectorIII.add(value);
	}
	
	public void setNodeListAux(ArrayList<String> nodeListAux) {
		this.nodeListAux = nodeListAux;
	}
	
	public void setEdges(String[][] edges) {
		this.edges = edges;
	}
	
	public ArrayList<String> getTimeNodes() {
		return timeNodes;
	}
	
	public List<Double> getWeight() {
		return weight;
	}
	
	public int getWTotalInc() {
		return wTotalInc;
	}
	
	public ArrayList<String> getNodeListAux() {
		return nodeListAux;
	}
	
	public int getNodeAccum() {
		return nodeAccum;
	}
	
	public int getIncW() {
		return wInc;
	}
	
	public double[][] getIncidenceMatrix() {
		return incidenceMatrix;
	}
	
	public int getEdgeAccum() {
		return edgeAccum;
	}
	
	public ArrayList<String> getFromNode() {
		return fromNode;
	}
	
	public ArrayList<String> getToNode() {
		return toNode;
	}
	
	public int getNodeInc() {
		return nodeInc;
	}
	
	public double getEigenValue2() {
		return eigenValue2;
	}
	
	public double[][] getEigenVector2() {
		return eigenVector2;
	}
	
	public double[][] getEigenVector2Aux() {
		return eigenVector2Aux;
	}
	
	public double[][] getEigenVector3Aux() {
		return eigenVector3Aux;
	}
	
	public double getEigenValue3() {
		return eigenValue3;
	}
	
	public double[][] getEigenVector3() {
		return eigenVector3;
	}
	
	public ArrayList<Double> getEigenVectorII() {
		return eigenVectorII;
	}
	
	public double[][] getDiagonalAdapt() {
		return diagoAdapt;
	}
	
	public double[][] getDeltaDiagonal() {
		return dDiago;
	}
	
	public double[][] getDeltaLaplacian() {
		return dLapla;
	}
	
	public void setEigenVectorII(ArrayList<Double> values) {
		this.eigenVectorII = values;
	}
	
	public void setEigenVectorIII(ArrayList<Double> values) {
		this.eigenVectorIII = values;
	}
	
	public ArrayList<Double> getEigenVectorIII() {
		return eigenVectorIII;
	}
	
	public double[] getIncidenceW() {
		return incidenceW;
	}
	
	public int getIncW2() {
		return wInc2;
	}
	
	public int getDeltaNode() {
		return dNode;
	}
	
	public double[][] getDiagonal() {
		return diagonal;
	}
	
	public double[][] getSubSpace() {
		return subSpace;
	}
	
	public List<String> getFNode() {
		return fNode;
	}
	
	public double getDeltaEigenValue2() {
		return dEigenValue2;
	}
	
	public double getDeltaEigenValue3() {
		return dEigenValue3;
	}
	
	public int getNod() {
		return nod;
	}
	
	public String[][] getEdges() {
		return edges;
	}
	
	public List<String> getTNode() {
		return tNode;
	}
	
	public double[][] getLaplacian() {
		return laplacian;
	}
	
	public String[][] getClusterLabel() {
		return clusterLabel;
	}
	
	public String[] getAccumNodeList() {
		return accumNodeList;
	}
	
	public String[][] getEdgeList() {
		return edgeList;
	}
	
	public String[] getNodeList() {
		return nodeList;
	}
	
	public List<Double> getEdgeWeight() {
		return edgeWeight;
	}
	
	public int getNbEdge() {
		return nbEdge;
	}
	
	public int getNbNode() {
		return nbNode;
	}
	
	public int totalIncrementw(int newValue) {
		this.wTotalInc = wTotalInc + newValue;
		return wTotalInc;
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
		this.laplAdapt = newValue;
		return laplAdapt;
	}

	public double[][] diagoAdapt(double newValue[][]) {
		this.diagoAdapt = newValue;
		return diagoAdapt;
	}

	public String[] accumnodeList(String[] newValue) {
		this.accumNodeList = newValue;
		return accumNodeList;
	}

	public double deltaEvalue2(double newValue) {
		this.dEigenValue2 = newValue;
		return dEigenValue2;
	}

	public double deltaEvalue3(double newValue) {
		this.dEigenValue3 = newValue;
		return dEigenValue3;
	}

	public double[][] eigenVector2aux(double newValue[][]) {
		this.eigenVector2Aux = newValue;
		return eigenVector2Aux;
	}

	public double[][] eigenVector2(double newValue[][]) {
		this.eigenVector2 = newValue;
		return eigenVector2;
	}

	public double[][] eigenVector3(double newValue[][]) {
		this.eigenVector3 = newValue;
		return eigenVector3;
	}

	public double[][] eigenVector3aux(double newValue[][]) {
		this.eigenVector3Aux = newValue;
		return eigenVector3Aux;
	}

	public double eigenValue2(double newValue) {
		this.eigenValue2 = newValue;
		return eigenValue2;
	}

	public double eigenValue3(double newValue) {
		this.eigenValue3 = newValue;
		return eigenValue3;
	}

	public double[][] deltaDiago(double newValue[][]) {
		this.dDiago = newValue;
		return dDiago;
	}

	public double[][] deltaLapla(double newValue[][]) {
		this.dLapla = newValue;
		return dLapla;
	}

	public double[] incidenceW(List<Double> newValue) {
		this.incidenceW = new double[newValue.size()];

		for (int i = 0; i < newValue.size(); i++) {
			incidenceW[i] = newValue.get(i);
		}

		return incidenceW;
	}

	public int nodeAccum(int newValue) {
		this.nodeAccum = newValue;
		return nodeAccum;
	}

	public int edgeAccum(int newValue) {
		this.edgeAccum = newValue;
		return edgeAccum;
	}

	public double[][] incidenceMatrix(double newValue[][]) {
		this.incidenceMatrix = newValue;
		return incidenceMatrix;
	}

	public int[][] pointPercluster(int newValue[][]) {
		this.pointPerCluster = newValue;
		return pointPerCluster;
	}

	public String[][] clusterLabel(String newValue[][]) {
		this.clusterLabel = newValue;
		return clusterLabel;
	}

	public String[] nodeList(String newValue[]) {
		this.nodeList = newValue;
		return nodeList;
	}

	public double[][] subSpace(double newValue[][]) {
		this.subSpace = newValue;
		return subSpace;
	}

	public String[][] edgeList(String newValue[][]) {
		this.edgeList = newValue;
		return edgeList;
	}

	public int edgeNumber(int newValue) {
		this.nbEdge = newValue;
		return nbEdge;
	}

	public int Nodes(int newValue) {
		this.nbNode = newValue;
		return nbNode;
	}

	public int nodeIncrement(int newValue) {
		this.nodeInc = newValue;
		return nodeInc;
	}

	public int incrementW(int newValue) {
		this.wInc = newValue;
		return wInc;
	}

	// public int[][] finalLabel(ArrayList<Double>){
	// this.finallabel = newValue;
	// return finallabel;
	// }

	public double[][] Laplacian(double[][] newValue) {
		this.laplacian = newValue;
		return laplacian;
	}

	public double[][] Diagonal(double[][] newValue) {
		this.diagonal = newValue;
		return diagonal;
	}

	public double[][] Wadjacency(double[][] newValue) {
		this.wAdjacency = newValue;
		return wAdjacency;
	}

}
