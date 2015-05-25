package algorithm;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import Jama.Matrix;
import object.Edge;

import org.jblas.DoubleMatrix;
import org.jblas.SimpleBlas;

import core.App;

public class Dynamic extends Thread{
	
	public static int printmodularity = 0;
	public static int printclustermodularity = 0;
	public static int nbCluster = 3;
	
	private ArrayList<String> changes;
	private ArrayList<String> modularity;
	private ArrayList<String> modularityCluster;
	private ArrayList<String[][]> matrixNode;
	private ArrayList<String[][]> matrixEdge;
	
	private List<Double> incidenceW;
	
	private ArrayList<String> nodeList;
	
	private ArrayList<Edge> edgeList;
	
	private Clusters[] clusters;
	
	private int nbEdges;
	private String edgeLabel[][];
	private int timeList[][];
	private double edgeWeight[][];
	
	private int totalTime;
	
	private long chrono;
	private long totalChrono;
	
	public void run() {
		executeAlgorithm();
	}
	
	public Dynamic() {
		changes = new ArrayList<String>();
		modularity = new ArrayList<String>();
		modularityCluster = new ArrayList<String>();
		matrixNode = new ArrayList<String[][]>();
		matrixEdge = new ArrayList<String[][]>();
		
		incidenceW = new ArrayList<Double>();
		
		nodeList = new ArrayList<String>();
		
		edgeList = new ArrayList<Edge>();
		
		clusters = new Clusters[100];
		
		totalTime = 0;
		
		totalChrono = 0;
	}
	
	public void executeAlgorithm() {
		initMatrix();
		
		buildChanges();
	}
	
	private void startSpectralClustering(int nbNode, int nbEdgeByTime, int time) {
		chrono = System.currentTimeMillis();
		
		// Creation of the weighted adjacency matrix
		Matrix matrixWAdjency = new Matrix(new double[nbNode][nbNode]);

		// Fill the WADJACENCY MATRIX
		for (int n = 0; n < nbNode; n++) {
			for (int e1 = 0; e1 < nbEdgeByTime; e1++) {
				if (clusters[1].nodelist[n].equals(clusters[1].edgelist[e1][0])) {
					for (int m = 0; m < nbNode; m++) {
						if (clusters[1].nodelist[m].equals(clusters[1].edgelist[e1][1])) {
							matrixWAdjency.set(n, m, clusters[1].edgeweight.get(e1));
							matrixWAdjency.set(m, n, clusters[1].edgeweight.get(e1));
						}
					}
				}
			}
		}
		
		// Creation of the DIAGONAL Matrix
		Matrix matrixDiagonal = new Matrix(new double[nbNode][nbNode]);
		for (int row = 0; row < matrixWAdjency.getRowDimension(); row++) {
			for (int column = 0; column < matrixWAdjency.getColumnDimension(); column++) {
				double val = matrixDiagonal.get(row, row) + matrixWAdjency.get(row, column);
				matrixDiagonal.set(row, row, val);
			}
		}

		clusters[1].setDiagonal(matrixDiagonal.getArray());

		// Unnormalized Laplacian L=D-W
		Matrix laplacian = matrixDiagonal.minus(matrixWAdjency);
		clusters[1].setLaplacian(laplacian.getArray());

		// Transform the DIAGONAL from array to DoubleMatrix format
		DoubleMatrix doubleMatrixDiagonal = new DoubleMatrix(matrixDiagonal.getArray());

		// Transform the LAPLACIAN from array to DoubleMatrix format
		DoubleMatrix doubleMatrixLaplacien = new DoubleMatrix(laplacian.getArray());

		DoubleMatrix[] eigenSystem = generalizeEigenSystem(doubleMatrixLaplacien, doubleMatrixDiagonal);
		
		DoubleMatrix eigenVector = eigenSystem[0];
		DoubleMatrix eigenValue = eigenSystem[1];
		
		Matrix subSpaceBeta = new Matrix(new double[clusters[1].nodes][2]);
		
		// Get the 2nd and 3rd eigenvectors
		for (int i = 0; i < clusters[1].nodes; i++) {
			subSpaceBeta.set(i, 0, eigenVector.get(i, 1));
			subSpaceBeta.set(i, 1, eigenVector.get(i, 2));
		}

		// Store the 2nd and 3rd Eigenvectors
		clusters[1].eigenVector2(matrix2array(subSpaceBeta.getMatrix(0, subSpaceBeta.getArray().length - 1, 0, 0)));
		clusters[1].eigenVector3(matrix2array(subSpaceBeta.getMatrix(0, subSpaceBeta.getArray().length - 1, 1, 1)));

		// Store the 2nd and 3rd Eigenvalues
		clusters[1].eigenValue2(eigenValue.get(1));
		clusters[1].eigenValue3(eigenValue.get(2));

		//clusters[1].clusterLabel(kmeans(eigenVector.toArray2(), nbCluster, clusters[1].accumnodelist, time));
		clusters[1].clusterLabel(kmeans(subSpaceBeta.getArray(), nbCluster, clusters[1].accumnodelist, time));

		matrixNode.add(clusters[1].clusterlabel);

		ArrayList<Integer> nodespercluster = new ArrayList<Integer>();
		
		for (int b = 0; b < nbCluster; b++) {
			nodespercluster.add(0);
		}

		// Build a list which indicates how many nodes has each cluster
		for (int f = 0; f < clusters[1].clusterlabel.length; f++) {
			for (int c = 0; c < nbCluster; c++) {
				if (clusters[1].clusterlabel[f][2].equals(String.valueOf(c + 1))) {
					nodespercluster.set(c, nodespercluster.get(c) + 1);
				}
			}
		}
		
		if (printmodularity == 1) {

			// ///////////////////////////////////////////////////////////
			// MODULARITY MODULARITY MODULARITY
			// //////////////////////////////////////////////////////////////////
			// Modularity Matrix will have the fraction of edges that links
			// community i with community j
			double edgesfraction[][] = new double[nbCluster][nbCluster];

			int clusterA = 0;
			int clusterB = 0;

			for (int i = 0; i < clusters[time].edgenumber; i++) {
				clusterA = 0;
				clusterB = 0;

				for (int j = 0; j < clusters[time].nodes; j++) {

					if (clusters[time].edgelist[i][0].equals(clusters[time].clusterlabel[j][1])) {
						clusterA = Integer.valueOf(clusters[time].clusterlabel[j][2]);
					}
				}

				for (int t = 0; t < clusters[time].nodes; t++) {
					if (clusters[time].edgelist[i][0].equals(clusters[time].clusterlabel[t][1])) {
						clusterB = Integer.valueOf(clusters[time].clusterlabel[t][2]);
					}
				}

				edgesfraction[clusterA - 1][clusterB - 1] = edgesfraction[clusterA - 1][clusterB - 1] + 1;
			}

			for (int i = 0; i < (nbCluster); i++) {
				for (int j = 0; j < nbCluster; j++) {
					edgesfraction[i][j] = edgesfraction[i][j] / (double) clusters[time].edgenumber;
				}
			}

			double weightedmean = 0;
			double weightedmean2 = 0;

			for (int i = 0; i < nbCluster; i++) {
				weightedmean = weightedmean + edgesfraction[i][i] * edgesfraction[i][i];
				weightedmean2 = (edgesfraction[i][i] * ((double) nodespercluster.get(i) / clusters[time].nodes)) + weightedmean2;
			}

			String str = "Weighted (%) Mean Modularity in Time " + time + " = " + weightedmean + "\n"
					+ "Weighted (nodes) Mean Modularity2 in Time " + time + " = " + weightedmean2 + "\n"
					+ "Edges t" + time + " = " + clusters[time].edgenumber + "\n\n";
			modularity.add(str);

			if (printclustermodularity == 1) {
				str = "";
				for (int c = 0; c < edgesfraction.length; c++) {
					str += "Cluster " + c + " Modularity = " + edgesfraction[c][c] + "\n";
				}
				modularityCluster.add(str + "\n");
			}
		}
		
		long topChrono = System.currentTimeMillis() - chrono;
		
		totalChrono += topChrono;
		
		App.logConsole("Time " +  time + " : " + nodespercluster, App.PLAIN);
		
		App.logConsole(" -> Done! in  " + topChrono + "ms\n\n", App.SUCCESS);
		
		chrono = System.currentTimeMillis();
		
		nodespercluster.clear();
	}
	
	private void initMatrix() {
		// Quantity of edges at instant of time
		int nbEdgeByTime = 0;
		
		// set the instant of to analyze and filter the
		// information from the main raw data
		int time = 1;
		
		try {
			// Step 1: Load the JDBC driver. jdbc:mysql://localhost:3306/travel
			Class.forName("com.mysql.jdbc.Driver");

			// Step 2: Connection to MYSQL database and extraction of the useful
			// columns (fromnode, tonode, weight)
			Connection conn = DriverManager.getConnection("jdbc:mysql://" + App.address + ":" + App.port + "/" + App.db, App.userName, App.passwd);
			Statement st = conn.createStatement();
			ResultSet srs = st.executeQuery("SELECT * FROM " + App.TABLE_NAME + "" + "" + "" + "");
			while (srs.next()) {
				Edge edgeData = new Edge();
				edgeData.setFromnode(srs.getString("fromnode"));
				edgeData.setTonode(srs.getString("tonode"));
				edgeData.setFromtime(srs.getInt("fromtime"));
				edgeData.setTotime(srs.getInt("totime"));
				edgeData.setWeight(srs.getDouble("weight"));
				
				edgeList.add(edgeData);
			}
			
			nbEdges = edgeList.size();
			
			edgeLabel = new String[nbEdges][2];
			timeList = new int[nbEdges][2];
			edgeWeight = new double[nbEdges][1];
						
			// Fill all the elements, edges, time and weight
			for (int z = 0; z < nbEdges; z++) {
				edgeLabel[z][0] = edgeList.get(z).getFromnode();
				edgeLabel[z][1] = edgeList.get(z).getTonode();
				
				timeList[z][0] = edgeList.get(z).getFromtime();
				timeList[z][1] = edgeList.get(z).getTotime();
				
				edgeWeight[z][0] = edgeList.get(z).getWeight();
				
				// Store the max time
				if (totalTime < timeList[z][1]) {
					totalTime = timeList[z][1];
				}
			}
			
			clusters[time] = new Clusters();

			ArrayList<String> timeNode = new ArrayList<String>();
			List<String> fromNode = new ArrayList<String>();
			List<String> toNode = new ArrayList<String>();
			List<Double> weight = new ArrayList<Double>();

			// Filter the edges in the first time interval
			for (int i = 0; i < nbEdges; i++) {
				// Actual time
				if (time <= timeList[i][1] && time >= timeList[i][0]) {
					fromNode.add(edgeLabel[i][0]);
					toNode.add(edgeLabel[i][1]);
					weight.add(edgeWeight[i][0]);
					incidenceW.add(edgeWeight[i][0]);
					timeNode.add(edgeLabel[i][0]);
					timeNode.add(edgeLabel[i][1]);
					nbEdgeByTime += 1;
				}
			}

			// Initialize the vector that will contains all the accumulative nodes time by time
			String[] acumNodeList = new String[nbEdges * 2];
			
			nodeList = removeDuplicate(timeNode);

			int nbNode = nodeList.size();

			// ACCUMNODELIST WILL CONTAIN ALL THE NODES FROM THE BEGINNING TO THE ACTUAL TIME
			for (int i = 0; i < nbNode; i++) {
				acumNodeList[i] = nodeList.get(i);
			}
			clusters[1].setAcumNodeList(acumNodeList);
			
			String edgeByTime[][] = new String[fromNode.size()][2];
			
			// Create a table of edges of current time instant
			for (int i = 0; i < nbEdgeByTime; i++) {
				edgeByTime[i][0] = fromNode.get(i);
				edgeByTime[i][1] = toNode.get(i);
			}

			// store all the information for the graph at time=1
			clusters[1].setEdgeList(edgeByTime);
			clusters[1].setNode(nbNode);
			clusters[1].setNodeList(nodeList.toArray(new String[nbNode]));
			clusters[1].setEdgeNumber(nbEdgeByTime);
			clusters[1].edgeweight.addAll(weight);
			clusters[1].setEdgeAccum(nbEdgeByTime);
			clusters[1].setNodeAccum(nbNode);
			
			matrixEdge.add(clusters[1].edgelist);

			// Incidencew has all the accumulated incidence variations
			clusters[1].setIncidenceW(incidenceW);
			
			// Create the INCIDENCE MATRIX
			double incidence[][] = new double[clusters[1].edgenumber][clusters[1].nodes];
			for (int i = 0; i < clusters[1].edgenumber; i++) {
				for (int j = 0; j < clusters[1].nodes; j++) {
					if (clusters[1].edgelist[i][0].equals(clusters[1].nodelist[j])) {
						incidence[i][j] = 1;
					}
					if (clusters[1].edgelist[i][1].equals(clusters[1].nodelist[j])) {
						incidence[i][j] = -1;
					}
				}
			}
			
			clusters[1].setIncidenceMatrix(incidence);
			
			startSpectralClustering(nbNode, nbEdgeByTime, time);
		} catch (Exception e) {
			App.logConsole("Got an exception!\n\n", App.WARNING);

			e.printStackTrace();
		}
	}
	
	private void buildChanges() {
		for (int time = 2; time < totalTime + 1; time++) {
			clusters[time] = new Clusters();

			// Load the graph obtained in the actual time instant
			for (int i = 0; i < nbEdges; i++) {
				if (time <= timeList[i][1] && time >= timeList[i][0]) {
					clusters[time].fnode.add(edgeLabel[i][0]);
					clusters[time].tnode.add(edgeLabel[i][1]);
					clusters[time].weight.add(edgeWeight[i][0]);
					clusters[time].timenodes.add(edgeLabel[i][0]);
					clusters[time].timenodes.add(edgeLabel[i][1]);
					clusters[time].edgesnumber = clusters[time].edgesnumber + 1;
				}
			}

			clusters[time].edges = new String[clusters[time].fnode.size()][2];

			// take out the duplicates with the arraylist format.
			clusters[time].nodeslistaux = removeDuplicate(clusters[time].timenodes);

			clusters[time].nod = clusters[time].nodeslistaux.size();

			// Nodes list in actual time
			clusters[time].setNodeList(clusters[time].nodeslistaux.toArray(new String[clusters[time].nodeslistaux.size()]));

			// UPDATE ACCUMNODELIST, which contains all the nodes from
			// beginning to actual time.
			for (int i = 0; i < clusters[time].nodelist.length; i++) {
				int dupli = 0;

				for (int j = 0; j < clusters[time - 1].nodelist.length; j++) {
					if (clusters[time].nodelist[i].equals(clusters[time - 1].nodelist[j])) {
						dupli = 1;
					}
				}
				if (dupli == 0) {
					clusters[1].accumnodelist[clusters[time - 1].nodeaccum + 
					                  clusters[time].deltanodes] = (clusters[time].nodelist[i]);
					clusters[time].deltanodes++;

				}
			}

			// Create a table of edges of current time instant
			for (int i = 0; i < clusters[time].edgesnumber; i++) {
				clusters[time].edges[i][0] = clusters[time].fnode.get(i);
				clusters[time].edges[i][1] = clusters[time].tnode.get(i);
			}

			// store all the information for the graph at time=1
			clusters[time].setEdgeList(clusters[time].edges);
			clusters[time].setNode(clusters[time].nod);
			clusters[time].setNodeAccum(clusters[time - 1].nodeaccum + clusters[time].deltanodes);
			clusters[time].setEdgeNumber(clusters[time].edgesnumber);
			clusters[time].edgeweight.addAll(clusters[time].weight);
			
			matrixEdge.add(clusters[time].edgelist);

			int edgeDel = 0;
			// check if there is an edge deletion caused by the "to node"
			// missing
			for (int i = 0; i < clusters[time - 1].edgenumber; i++) {
				boolean var = true;
				for (int j = 0; j < clusters[time].edgenumber; j++) {
					if (clusters[time - 1].edgelist[i][0].equals(clusters[time].edgelist[j][0])
							&& clusters[time - 1].edgelist[i][1].equals(clusters[time].edgelist[j][1])) {
						var = false;
						j = clusters[time].edgenumber;
					}
				}
				if (var == true) {
					clusters[time].fromnode.add(clusters[time - 1].edgelist[i][0]);
					clusters[time].tonode.add(clusters[time - 1].edgelist[i][1]);
					incidenceW.add(-clusters[time - 1].edgeweight.get(i));
					clusters[time].incrementw2 = clusters[time].incrementw2 + 1;
					edgeDel++;
				}
			}

			// Compare if there is an edge deletion caused by the
			// "from node" missing
			for (int i = 0; i < clusters[time - 1].edgenumber; i++) {
				boolean var = true;
				for (int j = 0; j < clusters[time].edgenumber; j++) {
					if (clusters[time - 1].edgelist[i][0].equals(clusters[time].edgelist[j][0])) {
						var = false;
						j = clusters[time].edgenumber;
					}
				}
				if (var == true) {
					clusters[time].fromnode.add(clusters[time - 1].edgelist[i][0]);
					clusters[time].tonode.add(clusters[time - 1].edgelist[i][1]);
					incidenceW.add(-clusters[time - 1].edgeweight.get(i));
					clusters[time].incrementw2 = clusters[time].incrementw2 + 1;
					edgeDel++;
				}
			}
			int edgeAdd = 0;
			// loop for edges addition caused by a new "to node"
			for (int i = 0; i < clusters[time].edgenumber; i++) {
				boolean var = true;
				for (int j = 0; j < clusters[time - 1].edgenumber; j++) {
					if (clusters[time].edgelist[i][0].equals(clusters[time - 1].edgelist[j][0])
							&& clusters[time].edgelist[i][1].equals(clusters[time - 1].edgelist[j][1])) {
						var = false;
						j = clusters[time - 1].edgenumber;
					}
				}
				if (var == true) {
					clusters[time].fromnode.add(clusters[time].edgelist[i][0]);
					clusters[time].tonode.add(clusters[time].edgelist[i][1]);
					incidenceW.add(clusters[time].edgeweight.get(i));
					clusters[time].incrementw2 = clusters[time].incrementw2 + 1;
					edgeAdd++;
				}
			}

			// compare if there is an edge addition caused by a new
			// "from node"
			for (int i = 0; i < clusters[time].edgenumber; i++) {
				boolean var = true;
				for (int j = 0; j < clusters[time - 1].edgenumber; j++) {
					if (clusters[time].edgelist[i][0].equals(clusters[time - 1].edgelist[j][0])) {
						var = false;
						j = clusters[time - 1].edgenumber;
					}
				}
				if (var == true) {
					clusters[time].fromnode.add(clusters[time].edgelist[i][0]);
					clusters[time].tonode.add(clusters[time].edgelist[i][1]);
					incidenceW.add(clusters[time].edgeweight.get(i));
					clusters[time].incrementw2 = clusters[time].incrementw2 + 1;
					edgeAdd++;
				}
			}

			// Store how many variations occurs in the actual instant time
			clusters[time].incrementW(clusters[time].incrementw2);

			// store the TOTAL increment (total modifications)
			clusters[1].totalIncrementw(clusters[time - 1].totalincrementw + clusters[time].incrementw2);

			// compute node deletion
			int nodeDel = 0;
			for (int r = 0; r < clusters[time - 1].nodelist.length; r++) {
				boolean delNode = true;
				for (int e = 0; e < clusters[time].nodelist.length; e++) {
					if (clusters[time - 1].nodelist.equals(clusters[time].nodelist)) {
						delNode = false;
						e = clusters[time].nodelist.length;
					}
				}
				if (delNode == true) {
					nodeDel++;
				}
			}

			// compute node addition
			int nodeAdd = 0;
			for (int r = 0; r < clusters[time].nodelist.length; r++) {
				boolean addNode = true;
				for (int e = 0; e < clusters[time - 1].nodelist.length; e++) {
					if (clusters[time].nodelist.equals(clusters[time - 1].nodelist)) {
						addNode = false;
						e = clusters[time - 1].nodelist.length;
					}
				}
				if (addNode == true) {
					nodeAdd++;
				}
			}
			
			String str = "";
			str += "---------- Changes in time " + time + " ----------\n";
			str += "Total Nodes = " + clusters[time].nodes + "\n";
			str += "Total Edges = " + clusters[time].edgesnumber + "\n";
			str += "New Edges = " + edgeAdd + "\n";
			str += "Deleted Edges = " + edgeDel + "\n";
			str += "New Nodes = " + nodeAdd + "\n";
			str += "Deleted Nodes = " + nodeDel + "\n\n";
			
			changes.add(str);

			// Store all the ACCUMULATED incidence variations (delta W).
			clusters[time].setIncidenceW(incidenceW);

			// EdgeAccum includes the total number rows of incidence Matrix
			// (this contains all the modifications realized in the graph,
			// in the first instant is equal to the quantity of edges)
			clusters[time].setEdgeAccum(clusters[time].incrementw2 + clusters[time - 1].edgeaccum);

			// Nodeaccum includes the TOTAL nodes involved from time=0 at
			// the actual time instant
			clusters[time].setNodeAccum(clusters[time - 1].nodeaccum + clusters[time].deltanodes);

			// nodeIncrement indicates how many NEW nodes have been added in
			// the actual time instant
			clusters[time].nodeIncrement(clusters[time].deltanodes);

			// incidence matrix store the actual incidence matrix, already
			// increased.
			clusters[time].setIncidenceMatrix(increaseIncidence(
					clusters[time - 1].incidencematrix,
					clusters[time].deltanodes,
					clusters[time].incrementw2, nodeList,
					clusters[time].fromnode,
					clusters[time].tonode));

			// INCREMENT LAPLACIAN for the actual instant.
			clusters[time].setLaplacian(increaseLaplacian(
					clusters[time].incidencematrix,
					clusters[time].edgeaccum,
					clusters[time].nodeaccum,
					clusters[time].incidencew));

			// Create the INCREMENT OF DIAGONAL MATRIX for the actual time
			// instant.
			clusters[time].setDiagonal(increaseDiagonal(
					clusters[time - 1].diagonal,
					clusters[time].incidencematrix,
					clusters[time].nodeincrement,
					clusters[time].incrementw));

			// calculate the delta/variation of the Laplacian and Diagonal
			// Matrices
			clusters[time].deltaLapla(deltalaplacian(
					clusters[time - 1].laplacian,
					clusters[time].laplacian));

			clusters[time].deltaDiago(deltadiagonal(
					clusters[time - 1].diagonal,
					clusters[time].diagonal));

			// Adapt the new size of eigenvectors, there are the same of the
			// time "t-1" but have the dimension of the actual time "t".
			if (time == 2) {
				clusters[time].eigenVector2aux(adaptevector(
						clusters[time - 1].eigenvector2,
						clusters[time].nodeaccum));
				
				clusters[time].eigenVector3aux(adaptevector(
						clusters[time - 1].eigenvector3,
						clusters[time].nodeaccum));
			} else {
				clusters[time].eigenVector2aux(adaptevectorII(
						clusters[time - 1].evectorII,
						clusters[time].nodeaccum));
				
				clusters[time].eigenVector3aux(adaptevectorII(
						clusters[time - 1].evectorIII,
						clusters[time].nodeaccum));

			}
			// Get the INCREMENT OF 2ND AND 3RD EIGENVALUE
			clusters[time].deltaEvalue2(deltaEvalue(
					clusters[time].incrementw,
					clusters[time].eigenvector2aux,
					clusters[time].incidencew,
					clusters[time].incidencematrix,
					clusters[time - 1].eigenvalue2));
			clusters[time].deltaEvalue3(deltaEvalue(
					clusters[time].incrementw,
					clusters[time].eigenvector3aux,
					clusters[time].incidencew,
					clusters[time].incidencematrix,
					clusters[time - 1].eigenvalue3));

			clusters[time].eigenValue2(clusters[time - 1].eigenvalue2 + clusters[time].deltaevalue2);
			clusters[time].eigenValue3(clusters[time - 1].eigenvalue3 + clusters[time].deltaevalue3);

			// adapt the size of the laplacian and diagonal matrix of the
			// time instant "t-1" to the actual time instant "t".
			clusters[time - 1].laplAdapt(adaptLapla(
					clusters[time - 1].laplacian,
					clusters[time].nodeincrement));
			clusters[time - 1].diagoAdapt(adaptDiago(
					clusters[time - 1].diagonal,
					clusters[time].nodeincrement));

			for (int r = 0; r < clusters[time].eigenvector2aux.length; r++) {
				clusters[time].evectorII.add(clusters[time].eigenvector2aux[r][0]);
			}

			for (int i = clusters[time].incidencematrix.length
					- clusters[time].incrementw2; i < clusters[time].incidencematrix.length; i++) {

				clusters[time].evectorII = deltaEvector(
						clusters[time].evectorII, i,
						clusters[time].laplacian,
						clusters[time - 1].eigenvalue2,
						clusters[time - 1].diagoadapt,
						clusters[time].deltaevalue2,
						clusters[time].deltadiago,
						clusters[time].deltalapla,
						clusters[time].eigenvector2aux,
						clusters[time].incidencematrix,
						clusters[time].incrementw);

			}

			for (int r = 0; r < clusters[time].eigenvector3aux.length; r++) {
				clusters[time].evectorIII.add(clusters[time].eigenvector3aux[r][0]);
			}
			
			for (int i = clusters[time].incidencematrix.length
					- clusters[time].incrementw2; i < clusters[time].incidencematrix.length; i++) {
				clusters[time].evectorIII = deltaEvector(
						clusters[time].evectorIII, i,
						clusters[time].laplacian,
						clusters[time - 1].eigenvalue3,
						clusters[time - 1].diagoadapt,
						clusters[time].deltaevalue3,
						clusters[time].deltadiago,
						clusters[time].deltalapla,
						clusters[time].eigenvector3aux,
						clusters[time].incidencematrix,
						clusters[time].incrementw);
			}

			clusters[time].evectorII.addAll(clusters[time].evectorII);
			clusters[time].evectorIII.addAll(clusters[time].evectorIII);

			// Merge 2nd and 3rd Eigenvectors to construct the NEW subspace
			// for KMEANS
			clusters[time].subSpace(mergeEvectors(
					clusters[time].evectorII,
					clusters[time].evectorIII,
					clusters[1].accumnodelist,
					clusters[time].nodelist));

			// Perform KMEANS in the new incremented subspace
			clusters[time].clusterLabel(kmeans(
					clusters[time].subspace, nbCluster,
					clusters[time].nodelist, time));

			// GET THE QUANTITY OF NODES IN EACH CLUSTER
			
			ArrayList<Integer> nodesPerCluster = new ArrayList<Integer>();

			for (int b = 0; b < nbCluster; b++) {
				nodesPerCluster.add(0);
			}

			// Build a list which indicates how many nodes has each cluster
			for (int f = 0; f < clusters[time].clusterlabel.length; f++) {
				for (int c = 0; c < nbCluster; c++) {
					if (clusters[time].clusterlabel[f][2].equals(String.valueOf(c + 1))) {
						nodesPerCluster.set(c, nodesPerCluster.get(c) + 1);
					}
				}
			}

			App.logConsole("Time " +  time + " : " + nodesPerCluster, App.PLAIN);
			
			matrixNode.add(clusters[time].clusterlabel);


			if (printmodularity == 1) {

				// ///////////////////////////////////////////////////////////
				// MODULARITY MODULARITY MODULARITY
				// //////////////////////////////////////////////////////////////////
				// Modularity Matrix will have the fraction of edges that
				// links community i with community j
				double edgesfraction[][] = new double[nbCluster][nbCluster];

				int clusterA = 0;
				int clusterB = 0;

				for (int i = 0; i < clusters[time].edgenumber; i++) {
					clusterA = 0;
					clusterB = 0;

					for (int j = 0; j < clusters[time].nodes; j++) {

						if (clusters[time].edgelist[i][0].equals(clusters[time].clusterlabel[j][1])) {
							clusterA = Integer.valueOf(clusters[time].clusterlabel[j][2]);
						}
					}

					for (int t = 0; t < clusters[time].nodes; t++) {
						if (clusters[time].edgelist[i][0].equals(clusters[time].clusterlabel[t][1])) {
							clusterB = Integer.valueOf(clusters[time].clusterlabel[t][2]);
						}
					}

					edgesfraction[clusterA - 1][clusterB - 1] = edgesfraction[clusterA - 1][clusterB - 1] + 1;
				}

				for (int i = 0; i < (nbCluster); i++) {
					for (int j = 0; j < nbCluster; j++) {
						edgesfraction[i][j] = edgesfraction[i][j] / (double) clusters[time].edgenumber;
					}
				}

				double weightedmean = 0;
				double weightedmean2 = 0;

				for (int i = 0; i < nbCluster; i++) {
					weightedmean = weightedmean + edgesfraction[i][i] * edgesfraction[i][i];
					weightedmean2 = (edgesfraction[i][i] * nodesPerCluster.get(i) / clusters[time].nodes) + weightedmean2;
				}

				str = "Weighted (%) Mean Modularity in Time " + time + " = " + weightedmean + "\n"
						+ "Weighted (nodes) Mean Modularity2 in Time " + time + " = " + weightedmean2 + "\n"
						+ "Edges t" + time + " = " + clusters[time].edgenumber + "\n\n";
				
				modularity.add(str);

				if (printclustermodularity == 1) {
					str = "";
					for (int c = 0; c < edgesfraction.length; c++) {
						str += "Cluster " + c + " Modularity = " + edgesfraction[c][c] + "\n";
					}
					modularityCluster.add(str + "\n");
				}
				// modularity measures the quantity of edges that connect
				// nodes in the same cluster
				// double modularity=0;
				// modularity=trace-Math.sqrt(Math.sqrt(Math.pow(trace,
				// 2)));

			}
			nodesPerCluster.clear();
			
			long topChrono = System.currentTimeMillis() - chrono;
			
			totalChrono += topChrono;
			
			App.logConsole(" -> Done! in  " + topChrono + "ms\n\n", App.SUCCESS);
			
			chrono = System.currentTimeMillis();
		}
		
		App.logConsole("The algorithm has been successfully completed! in  " + totalChrono + "ms\n\n", App.SUCCESS);
	}

	// Merge 2nd and 3rd eigenvectors to create the NEW subspace for KMEANS
	private double[][] mergeEvectors(ArrayList<Double> evectorII,
			ArrayList<Double> evectorIII, String accumnodelist[],
			String nodelist[]) {

		// double subspace [][] = new double [evectorII.size()][2];

		double subspace[][] = new double[nodelist.length][2];

		// for(int t=0; t<evectorII.size(); t++){
		for (int t = 0; t < nodelist.length; t++) {
			for (int u = 0; u < accumnodelist.length; u++) {
				if (accumnodelist[u].equals(nodelist[t])) {

					subspace[t][0] = evectorII.get(u);
					subspace[t][1] = evectorIII.get(u);
					u = accumnodelist.length;
				}
			}
		}
		return subspace;

	}

	// Method to increase Eigenvector for EACH modification expressed in the
	// incidence Matrix as Incidence vectors
	private ArrayList<Double> deltaEvector(ArrayList<Double> evectorII,
			int incidencerow, double laplacian[][], double eigenvalue,
			double diagonal1[][], double deltaevalue,
			double deltadiagonal1[][], double deltalapla1[][],
			double[][] eigenvector1, double incidencematrix[][], int incrementw) {

		// Arraylist for spatial neighborhood
		ArrayList<Integer> spatialneighb1 = new ArrayList<Integer>();
		ArrayList<Integer> spatialneighb = new ArrayList<Integer>();

		// obtain the spatial neighborhood of the points i and j, the ones that
		// have variations in the graph: The spatial neighborhood is taken from
		// the incidence matrix.
		int w = incidencerow;
		spatialneighb1.clear();
		spatialneighb.clear();
		int i = 0;
		int j = 0;
		for (int h = 0; h < incidencematrix[0].length; h++) {
			if (incidencematrix[w][h] == 1) {
				i = h;
				h = incidencematrix[0].length;
				for (int r = 0; r < w; r++) {
					if (incidencematrix[r][i] == 1) {
						for (int c = 0; c < incidencematrix[0].length; c++) {
							if (incidencematrix[r][c] == -1) {
								spatialneighb1.add(c);
							}
						}
					} else if (incidencematrix[r][i] == -1) {
						for (int c = 0; c < incidencematrix[0].length; c++) {
							if (incidencematrix[r][c] == 1) {
								spatialneighb1.add(c);
							}
						}
					}
				}
			}
		}

		for (int y = 0; y < incidencematrix[0].length; y++) {
			if (incidencematrix[w][y] == -1) {
				j = y;
				y = incidencematrix[0].length;
				for (int r = 0; r < w; r++) {
					if (incidencematrix[r][j] == 1) {
						for (int c = 0; c < incidencematrix[0].length; c++) {
							if (incidencematrix[r][c] == -1) {
								spatialneighb1.add(c);
							}
						}
					} else if (incidencematrix[r][j] == -1) {
						for (int c = 0; c < incidencematrix[0].length; c++) {
							if (incidencematrix[r][c] == 1) {
								spatialneighb1.add(c);
							}
						}
					}
				}
			}
		}

		// eliminate duplicates in the list spatialneighb
		spatialneighb = removeDuplicateInt(spatialneighb1);

		// Create the Laplacian and Diagonal matrices only with the
		// spatialneighb, much smallers than the originals.
		double laplaneighb1[][] = new double[laplacian.length][spatialneighb.size()];
		double diagoneighb1[][] = new double[diagonal1.length][spatialneighb.size()];

		// Laplacian Matrices with only the columns of the spatial neighborhood
		for (int r = 0; r < laplacian.length; r++) {
			for (int p = 0; p < spatialneighb.size(); p++) {
				laplaneighb1[r][p] = laplacian[r][spatialneighb.get(p)];
			}
		}

		// Diagonal Matrices with only the columns of the spatial neighborhood
		for (int r = 0; r < laplacian.length; r++) {
			for (int p = 0; p < spatialneighb.size(); p++) {
				diagoneighb1[r][p] = eigenvalue * diagonal1[r][spatialneighb.get(p)];
			}
		}

		Matrix laplaneighb = Matrix.constructWithCopy(laplaneighb1);
		Matrix diagoneighb = Matrix.constructWithCopy(diagoneighb1);

		// Construct the "K" matrix of the eq. 4.14
		Matrix k = laplaneighb.minus(diagoneighb.times(deltaevalue));

		Matrix deltalapla = Matrix.constructWithCopy(deltalapla1);
		Matrix deltadiagonal = Matrix.constructWithCopy(deltadiagonal1);
		Matrix diagonal = Matrix.constructWithCopy(diagonal1);
		Matrix eigenvector = Matrix.constructWithCopy(eigenvector1);

		Matrix first = (diagonal.timesEquals(deltaevalue));
		Matrix second = deltadiagonal.times(eigenvalue);
		Matrix third = first.plus(second).minus(deltalapla);

		// Construct "h" eq 4.15
		Matrix h = third.times(eigenvector);

		if ((k.transpose().times(k)).det() != 0) {
			// calculate the deltavector with the spatial neighborhood
			Matrix deltaevector1 = (k.transpose().times(k)).inverse().times(k.transpose()).times(h);

			Matrix deltaevector = new Matrix(new double[eigenvector1.length][1]);
			for (int y = 0; y < spatialneighb.size(); y++) {
				deltaevector.set(spatialneighb.get(y), 0, deltaevector1.get(y, 0));
			}

			for (int a = 0; a < eigenvector1.length; a++) {
				evectorII.set(a, evectorII.get(a) + deltaevector.get(a, 0));
			}

			return evectorII;
		} else {
			Matrix deltaevector = new Matrix(new double[eigenvector1.length][1]);
			for (int a = 0; a < eigenvector1.length; a++) {
				evectorII.set(a, evectorII.get(a) + deltaevector.get(a, 0));
			}
			return evectorII;
		}
	}

	// Adapt ONLY the size of the laplacian matrix of the time t-1 to the actual
	// time. The Laplacian matrix for the actual time is calculated in
	// increaseLaplacian.
	private double[][] adaptLapla(double[][] laplacian, int nodeincrement) {
		double lapladapt[][] = new double[laplacian.length + nodeincrement][laplacian[0].length + nodeincrement];
		for (int i = 0; i < laplacian.length; i++) {
			for (int j = 0; j < laplacian[0].length; j++) {
				lapladapt[i][j] = laplacian[i][j];
			}
		}
		return lapladapt;
	}

	// Adapt ONLY the size of the laplacian matrix of the time t-1 to the actual
	// time. The Diagonal matrix for the actual time is calculated in
	// increaseDiagonal.
	private double[][] adaptDiago(double[][] diagonal, int nodeincrement) {
		double diagoadapt[][] = new double[diagonal.length + nodeincrement][diagonal[0].length + nodeincrement];
		for (int i = 0; i < diagonal.length; i++) {
			for (int j = 0; j < diagonal[0].length; j++) {
				diagoadapt[i][j] = diagonal[i][j];
			}
		}
		return diagoadapt;
	}

	// Method to increase Eigenvalue for EACH modification expressed in the
	// incidence Matrix as Incidence Vectors
	private double deltaEvalue(int incrementw, double[][] eigenvector,
			double[] incidencew, double[][] incidencematrix, double eigenvalue) {

		double deltaevalue = 0;

		for (int w = incidencematrix.length - incrementw; w < incidencematrix.length; w++) {
			int i = 0;
			int j = 0;
			for (int h = 0; h < incidencematrix[0].length; h++) {
				if (incidencematrix[w][h] == 1) {
					i = h;
					h = incidencematrix[0].length;
				}
			}
			for (int y = 0; y < incidencematrix[0].length; y++) {
				if (incidencematrix[w][y] == -1) {
					j = y;
					y = incidencematrix[0].length;
				}
			}

			deltaevalue = deltaevalue + incidencew[w] * (Math.pow(eigenvector[i][0] - 
					eigenvector[j][0], 2) - eigenvalue * (Math.pow(eigenvector[i][0], 2) + Math.pow(eigenvector[j][0], 2)));
		}

		return deltaevalue;
	}

	private double[][] adaptevectorII(ArrayList<Double> evectorII, int nodeaccum) {

		double evectoradapt[][] = new double[nodeaccum][1];

		for (int i = 0; i < nodeaccum; i++) {
			if (i < evectorII.size()) {
				evectoradapt[i][0] = evectorII.get(i);
			} else {
				i = nodeaccum;
			}
		}
		return evectoradapt;
	}

	private double[][] adaptevector(double[][] evector, int nodeaccum) {

		double evectoradapt[][] = new double[nodeaccum][1];

		for (int i = 0; i < nodeaccum; i++) {
			if (i < evector.length) {
				evectoradapt[i][0] = evector[i][0];
			} else {
				i = nodeaccum;
			}
		}
		return evectoradapt;
	}

	// Calculates the increment of Laplacian Matrix
	private double[][] deltalaplacian(double[][] lapla0, double[][] lapla1) {

		Matrix.constructWithCopy(lapla0);
		Matrix deltalapla = new Matrix(lapla1.length, lapla1[0].length);
		for (int i = 0; i < lapla0.length; i++) {
			for (int j = 0; j < lapla0[0].length; j++) {
				deltalapla.set(i, j, lapla0[i][j]);
			}
		}

		Matrix lapla11 = Matrix.constructWithCopy(lapla1);
		deltalapla = lapla11.minus(deltalapla);

		double[][] deltalap = matrix2array(deltalapla);
		return deltalap;
	}

	// Calculates the increment of Diagonal Matrix
	private double[][] deltadiagonal(double[][] diago0, double[][] diago1) {
		Matrix.constructWithCopy(diago0);
		Matrix deltadiago = new Matrix(diago1.length, diago1[0].length);
		for (int i = 0; i < diago0.length; i++) {
			for (int j = 0; j < diago0[0].length; j++) {
				deltadiago.set(i, j, diago0[i][j]);
			}
		}

		Matrix diago11 = Matrix.constructWithCopy(diago1);
		deltadiago = diago11.minus(deltadiago);

		double[][] deltalap = matrix2array(deltadiago);
		return deltalap;

	}

	// GENERALIZED EIGENSYSTEM
	/*
	 * Solve a general problem A x = L B x.
	 * 
	 * @param A symmetric matrix A
	 * 
	 * @param B symmetric matrix B
	 * 
	 * @return an array of matrices of length two. The first one is an array of
	 * the eigenvectors X The second one is A vector containing the
	 * corresponding eigenvalues L.
	 */
	private DoubleMatrix[] generalizeEigenSystem(DoubleMatrix A, DoubleMatrix B) {
		A.assertSquare();
		B.assertSquare();
		DoubleMatrix[] result = new DoubleMatrix[2];
		DoubleMatrix dA = A.dup();
		DoubleMatrix dB = B.dup();
		DoubleMatrix W = new DoubleMatrix(dA.rows);
		SimpleBlas.sygvd(1, 'V', 'U', dA, dB, W);
		result[0] = dA;
		result[1] = W;
		return result;
	}

	// KMEANS
	private String[][] kmeans(double subspacebeta0[][], int clusters, String nodelist[], int time) {

		int nodes = subspacebeta0.length;
		// MODIFICACION TEMPORAL
		// int nodes = nodelist.length;

		Matrix subspaceb = Matrix.constructWithCopy(subspacebeta0);
		Matrix evector22 = subspaceb.getMatrix(0, subspacebeta0.length - 1, 0, 0);
		double evector2[][] = matrix2array(evector22);
		Matrix evector33 = subspaceb.getMatrix(0, subspacebeta0.length - 1, 1, 1);
		double evector3[][] = matrix2array(evector33);

		double min1 = getMinValue(evector2);
		double min2 = getMinValue(evector3);

		double subspacebeta[][] = new double[subspacebeta0.length][2];
		for (int i = 0; i < subspacebeta0.length; i++) {
			subspacebeta[i][0] = subspacebeta0[i][0] - min1;
			subspacebeta[i][1] = subspacebeta0[i][1] - min2;
		}

		// ubicate the initial centroids in the subspace
		double centroids[][] = new double[clusters][2];

		// DETERMINATE THE INITIAL CENTROIDS WITH THE FIRST K DATAPOINTS
		for (int i = 0; i < clusters; i++) {
			centroids[i][0] = subspacebeta0[i][0];
			centroids[i][1] = subspacebeta0[i][1];

		}

		// matrix where each node is labeled in a cluster where the distance to
		// the centroid is the minimum
		int label[][] = new int[nodes][clusters];

		for (int h = 0; h < nodes; h++) {

			// //////////////////////////////////////////FIRST STEP:calculate
			// the SQUARED EUCLIDEAN DISTANCE between each datapoint and its
			// cluster initial centroid
			double distance[][] = new double[nodes][clusters];

			for (int i = 0; i < nodes; i++) {
				for (int j = 0; j < clusters; j++) {
					distance[i][j] = Math.sqrt(Math.pow(subspacebeta0[i][0] - centroids[j][0], 2)
									+ Math.pow(subspacebeta0[i][1] - centroids[j][1], 2));
				}
			}

			// vector which contains the quantity of datapoints per cluster
			int quantitydatapoints[][] = new int[clusters][1];

			// /////////////////////////////////////////SECOND STEP: Assign
			// datapoints to the closest cluster centroid (centroid fixed)
			for (int p = 0; p < nodes; p++) {
				double min = distance[p][0];
				int positionmin = 0;// position of the minimum distance in the
									// distance matrix.
				for (int j = 0; j < clusters; j++) {
					if (distance[p][j] < min) {
						min = distance[p][j];
						label[p][positionmin] = 0;
						positionmin = j;
					}
				}
				label[p][positionmin] = 1;

				// Count how many points are in each cluster
				quantitydatapoints[positionmin][0] = quantitydatapoints[positionmin][0] + 1;

			}

			// This restriction indicates that all the clusters must have at
			// least one datapoint
			for (int i = 0; i < clusters; i++) {
				if (quantitydatapoints[i][0] == 0) {
					h = nodes;
				}
			}

			// THIRD STEP: Calculate the
			// new centroid for each cluster of datapoints (datapoints fixed in
			// each cluster, centroid dynamic)

			// difference between the actual centroid and the new centroid (for
			// X and Y)
			double difference[][] = new double[clusters][2];

			// the new centroids after iterations
			double newcentroids[][] = new double[clusters][2];

			// Total accumulative difference between centroids and new centroids
			double accumdif = 0;

			// MEAN of each cluster, that is the NEW centroid
			for (int j = 0; j < clusters; j++) {
				double totaldistancex = 0;
				double totaldistancey = 0;
				for (int l = 0; l < nodes; l++) {
					if (label[l][j] == 1) {
						totaldistancex = totaldistancex + subspacebeta0[l][0];
						totaldistancey = totaldistancey + subspacebeta0[l][1];
					}
				}
				// calculate the mean through X and Y of all the points in the
				// same cluster
				newcentroids[j][0] = totaldistancex / quantitydatapoints[j][0];
				newcentroids[j][1] = totaldistancey / quantitydatapoints[j][0];

				difference[j][0] = Math.sqrt(Math.pow(newcentroids[j][0] - centroids[j][0], 2));
				difference[j][1] = Math.sqrt(Math.pow(newcentroids[j][1] - centroids[j][1], 2));
				accumdif = accumdif + difference[j][0] + difference[j][1];

				centroids[j][0] = newcentroids[j][0];
				centroids[j][1] = newcentroids[j][1];
			}

			if (accumdif == 0) {
				h = nodes;

			}
		}

		// Store in the finalcluster1 array the results of the KMEANS
		int finallabel[][] = label;

		int finalcluster1[][] = new int[nodes][2];

		for (int i = 0; i < nodes; i++) {
			finalcluster1[i][0] = i + 1;
			for (int j = 0; j < clusters; j++) {
				if (finallabel[i][j] == 1) {
					finalcluster1[i][1] = j + 1;
				}

			}

		}

		String finalclusters[][] = new String[nodes][3];
		for (int i = 0; i < nodes; i++) {

			// Fill the finalclusters array
			finalclusters[i][2] = String.valueOf(finalcluster1[i][1]);
			finalclusters[i][1] = nodelist[i];
			finalclusters[i][0] = String.valueOf(time);
		}

		return finalclusters;

	}

	// Obtain the Diagonal Matrix for the actual time
	private double[][] increaseDiagonal(double diagonal[][],
			double incidence[][], int nodeincrement, int incrementw) {

		double incdiagonal[][] = new double[incidence[0].length][incidence[0].length];

		// copy the initial diagonal matrix to the new one, will be empty cells
		// for the increments
		for (int i = 0; i < incidence[0].length - nodeincrement; i++) {
			incdiagonal[i][i] = diagonal[i][i];
		}

		// fill the increments of the diagonal
		for (int i = incidence.length - incrementw; i < incidence.length; i++) {
			for (int j = 0; j < incidence[0].length; j++) {
				if (incidence[i][j] == 1 || incidence[i][j] == -1) {
					incdiagonal[j][j] = incdiagonal[j][j] + 1;
				}
			}
		}

		return incdiagonal;
	}

	// Obtain the Laplacian Matrix for the actual time
	private double[][] increaseLaplacian(double[][] incidence,
			int edgeaccum, int nodeaccum, double[] incidencew) {

		// transform incidence matrix from DOUBLE format to MATRIX format

		Matrix r = new Matrix(new double[edgeaccum][nodeaccum]);
		r = Matrix.constructWithCopy(incidence);

		// transpose of r
		Matrix rt = r.transpose();

		// append to the r (or incidence) matrix all the values of W
		// (variations).
		for (int i = 0; i < incidencew.length; i++) {
			for (int j = 0; j < incidence[0].length; j++) {
				if (r.get(i, j) == 1) {
					if (incidencew[i] < 0) {
						r.set(i, j, Math.sqrt(incidencew[i] * (-1)));
					} else {
						r.set(i, j, Math.sqrt(incidencew[i]));
					}
				} else if (r.get(i, j) == -1) {
					if (incidencew[i] < 0) {
						r.set(i, j, -Math.sqrt(incidencew[i] * (-1)));
					} else {
						r.set(i, j, -Math.sqrt(incidencew[i]));
					}
				}
			}
		}

		// create the INCREMENT OF LAPLACIAN for the actual time instant.
		Matrix deltalapla = rt.times(r);
		double[][] laplacian = (matrix2array(deltalapla));
		return laplacian;
	}

	// INCREASE INCIDENCE MATRIX, FROM R TO R'
	private double[][] increaseIncidence(double[][] array,
			int deltanodes, int incrementw, ArrayList<String> nodesarraylist,
			ArrayList<String> fromnode, ArrayList<String> tonode) {

		double[][] incidence = new double[array.length + incrementw][array[0].length + deltanodes];

		// fill the increased incidence matrix with the actual incidence matrix
		// (will be additional and empty size for the new values)
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				incidence[i][j] = array[i][j];
			}
		}

		// "for" loop for each row, each EDGE variation
		for (int i = array.length; i < incidence.length; i++) {
			// "for" loop for each column, each node involved
			for (int j = 0; j < nodesarraylist.size(); j++) {
				if (fromnode.get(i - array.length).equals(nodesarraylist.get(j))) {
					incidence[i][j] = 1;
				}
			}
			for (int k = 0; k < nodesarraylist.size(); k++) {
				if (tonode.get(i - array.length).equals(nodesarraylist.get(k))) {
					incidence[i][k] = -1;
				}
			}
		}

		return incidence;
	}
	
	private ArrayList<String> removeDuplicate(ArrayList<String> timeNode) {
		HashSet<String> hs = new HashSet<String>();
		hs.addAll(timeNode);
		timeNode.clear();
		timeNode.addAll(hs);
		return timeNode;
	}

	private ArrayList<Integer> removeDuplicateInt(ArrayList<Integer> lista) {
		HashSet<Integer> hs = new HashSet<Integer>();
		hs.addAll(lista);
		lista.clear();
		lista.addAll(hs);
		return lista;
	}

	// getting the miniumum value
	private double getMinValue(double[][] array) {
		double minValue = array[0][0];
		for (int i = 1; i < array.length; i++) {
			if (array[i][0] < minValue) {
				minValue = array[i][0];
			}
		}
		return minValue;
	}

	public void createNodesView() {
		int time = 0;
		for(String[][] matrix : matrixNode) {
			time += 1;
			try {
				FileWriter writer = new FileWriter(App.DATA_PATH + "\\" + App.TABLE_NAME + "_nodes_t" + time + ".csv");
				
				writer.append("id");
				writer.append(";");
				writer.append("label");
				writer.append(";");
				writer.append("cluster");
				
				for (int row = 0; row < matrix.length; row++) {
					writer.append("\n");
					writer.append(matrix[row][1] + "");
					writer.append(";");
					writer.append(matrix[row][1] + "");
					writer.append(";");
					writer.append(matrix[row][2] + "");
				}
				
				App.logConsole(App.DATA_PATH + "\\" + App.TABLE_NAME + "_nodes_t" + time + ".csv" + " created!\n\n", App.SUCCESS);
				
				writer.flush();
				writer.close();
			} catch (IOException e) {
				App.logConsole("An exception has been encountered while trying to create "
						+ App.TABLE_NAME + "_nodes_t" + time + ".csv\n\n", App.WARNING);
				
				e.printStackTrace();
			}
		}
	}
	
	public void createEdgesView() {
		int time = 0;
		for(String[][] matrix : matrixEdge) {
			time += 1;
			try {
				FileWriter writer = new FileWriter(App.DATA_PATH + "\\" + App.TABLE_NAME + "_edges_t" + time + ".csv");
				
				writer.append("id");
				writer.append(";");
				writer.append("source");
				writer.append(";");
				writer.append("target");
				writer.append(";");
				writer.append("type");
				writer.append(";");
				writer.append("weight");
				
				for (int row = 0; row < matrix.length; row++) {
					int id = row + 1;
					writer.append("\n");
					writer.append(id + "");
					writer.append(";");
					writer.append(matrix[row][0] + "");
					writer.append(";");
					writer.append(matrix[row][1] + "");
					writer.append(";");
					writer.append("undirected");
					writer.append(";");
					writer.append("1");
				}
				
				App.logConsole(App.DATA_PATH + "\\" + App.TABLE_NAME + "_edges_t" + time + ".csv" + " created!\n\n", App.SUCCESS);

				writer.flush();
				writer.close();
			} catch (IOException e) {
				Text text = new Text("An exception has been encountered while trying to create "
						+ App.TABLE_NAME + "_edges_t" + time + ".csv\n\n");
				text.setFill(Color.web("#F6888B"));
				
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<String> getChanges() {
		return changes;
	}
	
	public ArrayList<String[][]> getMatrixNode() {
		return matrixNode;
	}
	
	public ArrayList<String[][]> getMatrixEdge() {
		return matrixEdge;
	}
	
	public ArrayList<String> getModularity() {
		return modularity;
	}
	
	public ArrayList<String> getModularityCluster() {
		return modularityCluster;
	}

	// method to transform a MATRIX to an ARRAY
	private double[][] matrix2array(Matrix matrix) {
		double arraybeta[][] = new double[matrix.getRowDimension()][matrix.getColumnDimension()];

		for (int row = 0; row < matrix.getRowDimension(); row++) {
			for (int column = 0; column < matrix.getColumnDimension(); column++) {
				arraybeta[row][column] = matrix.get(row, column);
			}
		}
		return arraybeta;
	}

}
