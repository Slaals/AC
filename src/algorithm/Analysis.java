package algorithm;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import object.Edge;



/////////////////////////
import org.jblas.DoubleMatrix;
import org.jblas.SimpleBlas;

public class Analysis {
	public static void main(String[] args) throws SQLException {

		// 16 clusters objetcs

		Clusters clusters1 = new Clusters();
		/*Clusters clusters2=new Clusters();
		Clusters clustersi=new Clusters();
		Clusters clustersii=new Clusters();
		Clusters clusters5=new Clusters();
		Clusters clusters6=new Clusters();
		Clusters clusters7=new Clusters();
		Clusters clusters8=new Clusters();
		Clusters clusters9=new Clusters();
		Clusters clusters10=new Clusters();
		Clusters clusters11=new Clusters();
		Clusters clusters12=new Clusters();

		
		//array of objects where will be stored the spatial neighborhood for increment of eigenvector calculations
        neighb[][] neighbArray = new neighb [60][3000];*/
		
		ArrayList<Edge> edgeslist = new ArrayList<Edge>();
		// List<edges> edgeslist = new List<edges>();
		try {
			// Step 1: Load the JDBC driver. jdbc:mysql://localhost:3306/travel
			Class.forName("com.mysql.jdbc.Driver");

			// Step 2: Connection to MYSQL database and extraction of the useful
			// columns (fromnode, tonode, weight)
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/graph1", "root", "root");
			Statement st = conn.createStatement();
			ResultSet srs = st.executeQuery("SELECT * FROM tempotwv2" + "" + "");
			while (srs.next()) {
				Edge edges = new Edge();
				edges.setFromnode(srs.getString("fromnode"));
				edges.setTonode(srs.getString("tonode"));
				edges.setFromtime(srs.getInt("fromtime"));
				edges.setTotime(srs.getInt("totime"));
				edges.setWeight(srs.getDouble("weight"));
				edgeslist.add(edges);
			}
			
			// Print the number of data points "n"
			// System.out.println(edgeslist.size());
			// System.out.println(edgeslist.get(1).getFromnode());
			// System.out.println(edgeslist.get(1).getFromnode());
			// System.out.println(edgeslist.get(3).getFromnode());

			// /////////////////////////////////////INSERT THE NUMBER OF
			// CLUSTERS///////////////////////////////////////////
			// /////////////////////////////////////INSERT THE NUMBER OF
			// CLUSTERS///////////////////////////////////////////
			// /////////////////////////////////////INSERT THE NUMBER OF
			// CLUSTERS///////////////////////////////////////////

			int edgesnumber0 = edgeslist.size();

			// Creation of the FULL edges label list, for the whole dataset
			String alledges[][] = new String[edgesnumber0][2];
			for (int z = 0; z < edgesnumber0; z++) {
				alledges[z][0] = (edgeslist.get(z).getFromnode());
				alledges[z][1] = (edgeslist.get(z).getTonode());
			}

			// Creation of the FULL timelist
			int timelist[][] = new int[edgesnumber0][2];
			int totaltime = 0;
			for (int z = 0; z < edgesnumber0; z++) {
				timelist[z][0] = (edgeslist.get(z).getFromtime());
				timelist[z][1] = (edgeslist.get(z).getTotime());
				if (totaltime < timelist[z][1]) {
					totaltime = timelist[z][1];
				}
			}

			// Creation of the FULL weight list
			double edgesweight[][] = new double[edgesnumber0][1];
			for (int z = 0; z < edgesnumber0; z++) {
				edgesweight[z][0] = (edgeslist.get(z).getWeight());
			}

			// Arraylist for nodes, this list will take all the nodes (including
			// duplicates) of the actual time
			ArrayList<String> timenodes = new ArrayList<String>();

			// Quantity of edges at instant of time
			int edgesnumber = 0;

			// ////////////////set the instant of to analyze and filter the
			// information from the main raw data

			List<String> fnode = new ArrayList<String>();
			List<String> tnode = new ArrayList<String>();
			List<Double> weight = new ArrayList<Double>();

			for (int time = 1; time < totaltime + 1; time++) {
				double density = 0;
				double ad = 0;
				double modularity = 0;

				fnode.clear();
				tnode.clear();
				weight.clear();
				edgesnumber = 0;
				timenodes.clear();

				// incidencew contains each modification to the original system.
				List<Double> incidencew = new ArrayList<Double>();

				// Filter the edges in the first time interval
				for (int i = 0; i < edgesnumber0; i++) {
					if (time <= timelist[i][1] && time >= timelist[i][0]) {
						fnode.add(alledges[i][0]);
						tnode.add(alledges[i][1]);
						weight.add(edgesweight[i][0]);
						incidencew.add(edgesweight[i][0]);
						timenodes.add(alledges[i][0]);
						timenodes.add(alledges[i][1]);
						edgesnumber = edgesnumber + 1;
					}
				}

				// Initialize the vector that will contains all the accumulative
				// nodes time by time
				String allnodes0[] = new String[edgesnumber0 * 2];
				clusters1.setAcumNodeList(allnodes0);

				// take out the duplicates with the arraylist format.
				ArrayList<String> nodesarraylist = new ArrayList<String>();
				nodesarraylist = noduplicate(timenodes);

				int nodes = 0;
				nodes = nodesarraylist.size();

				// ACCUMNODELIST WILL CONTAIN ALL THE NODES FROM THE BEGINNING
				// TO THE ACTUAL TIME
				for (int i = 0; i < nodesarraylist.size(); i++) {
					clusters1.accumnodelist[i] = (nodesarraylist.get(i));
				}

				clusters1.setNodeList(nodesarraylist
						.toArray(new String[nodesarraylist.size()]));

				String edges[][] = new String[edgesnumber][2];
				// Create a table of edges of current time instant
				for (int i = 0; i < edgesnumber; i++) {
					edges[i][0] = fnode.get(i);
					edges[i][1] = tnode.get(i);
				}

				clusters1.setEdgeList(edges);
				// store all the information for the graph at time=1
				clusters1.setNode(nodes);
				// clusters1.nodeList(nodelist);
				clusters1.setEdgeNumber(edgesnumber);
				clusters1.edgeweight.addAll(weight);
				clusters1.setEdgeAccum(edgesnumber);
				clusters1.setNodeAccum(nodes);

				// Incidencew has all the accumulated incidence variations
				clusters1.setIncidenceW(incidencew);

				// Create the INCIDENCE MATRIX
				double incidence[][] = new double[clusters1.edgenumber][clusters1.nodes];
				for (int i = 0; i < clusters1.edgenumber; i++) {
					for (int j = 0; j < clusters1.nodes; j++) {
						if (clusters1.edgelist[i][0].equals(clusters1.nodelist[j])) {
							incidence[i][j] = 1;
						}
					}
					for (int h = 0; h < clusters1.nodes; h++) {
						if (clusters1.edgelist[i][1].equals(clusters1.nodelist[h])) {
							incidence[i][h] = -1;
						}
					}
				}

				clusters1.setIncidenceMatrix(incidence);

				// //////////////////////////////////////////START THE SPECTRAL
				// CLUSTERING////////////////////////////////////////////
				// //////////////////////////////////////////START THE SPECTRAL
				// CLUSTERING////////////////////////////////////////////
				// //////////////////////////////////////////START THE SPECTRAL
				// CLUSTERING////////////////////////////////////////////

				// Creation of the weighted adjacency matrix
				double wadjacency[][] = new double[nodes][nodes];

				System.out.println("ACAAAA");
				System.out.println(clusters1.edgelist.length);

				// Fill the WADJACENCY MATRIX
				for (int n = 0; n < nodes; n++) {
					for (int e1 = 0; e1 < edgesnumber; e1++) {
						if (clusters1.nodelist[n].equals(clusters1.edgelist[e1][0])) {
							for (int m = 0; m < nodes; m++) {
								if (clusters1.nodelist[m].equals(clusters1.edgelist[e1][1])) {
									wadjacency[n][m] = clusters1.edgeweight.get(e1);
									wadjacency[m][n] = clusters1.edgeweight.get(e1);
								}
							}
						}
					}
				}

				// Creation of the DIAGONAL Matrix (weighted)
				double diagonal[][];
				diagonal = new double[nodes][nodes];
				for (int row = 0; row < wadjacency.length; row++) {
					for (int column = 0; column < wadjacency[row].length; column++) {
						diagonal[row][row] = (diagonal[row][row] + wadjacency[row][column]);
					}
				}

				// Creation of the DIAGONAL (DEGREE) Matrix not weighted
				double diagonal1[][];
				diagonal1 = new double[nodes][nodes];
				for (int row = 0; row < wadjacency.length; row++) {
					for (int column = 0; column < wadjacency[row].length; column++) {
						if (wadjacency[row][column] > 0) {
							diagonal1[row][row]++;
						}
					}
				}

				clusters1.Wadjacency(wadjacency);
				clusters1.setDiagonal(diagonal);

				// CALCULATE MODULARITY
				for (int c = 0; c < wadjacency.length; c++) {
					for (int h = 0; h < wadjacency.length; h++) {
						if (wadjacency[c][h] != 0) {
							modularity += ((double) (wadjacency[c][h] - ((double) (diagonal1[c][c] * diagonal1[h][h]) / (2 * edgesnumber))) / (2 * edgesnumber));
						}
					}
				}
				
				ad = (double) (edgesnumber * 2) / nodes;
				
				// Density time by time
				density = ((double) 2 * edgesnumber / (nodes * (nodes - 1)));
				
				System.out.println("time : " + time);
				System.out.println("average degree : " + ad);
				System.out.println("modularity : " + modularity);
				System.out.println("edges number : " + edgesnumber);
				System.out.println("nodes : " + nodes);
				System.out.println("density : " + density);
			}
		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	// Merge 2nd and 3rd eigenvectors to create the NEW subspace for KMEANS
	public static double[][] mergeEvectors(ArrayList<Double> evectorII,
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
	public static ArrayList<Double> deltaEvector(ArrayList<Double> evectorII,
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
		spatialneighb = noduplicateint(spatialneighb1);

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
	public static double[][] adaptLapla(double[][] laplacian, int nodeincrement) {
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
	public static double[][] adaptDiago(double[][] diagonal, int nodeincrement) {
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
	public static double deltaEvalue(int incrementw, double[][] eigenvector,
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

			deltaevalue = deltaevalue + incidencew[w] * (Math.pow(eigenvector[i][0] - eigenvector[j][0], 2) - 
					eigenvalue * (Math.pow(eigenvector[i][0], 2) + Math.pow(eigenvector[j][0], 2)));
		}

		return deltaevalue;
	}

	public static double[][] adaptevectorII(ArrayList<Double> evectorII, int nodeaccum) {

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

	public static double[][] adaptevector(double[][] evector, int nodeaccum) {

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
	public static double[][] deltalaplacian(double[][] lapla0, double[][] lapla1) {

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
	public static double[][] deltadiagonal(double[][] diago0, double[][] diago1) {

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
	public static DoubleMatrix[] generalizedEigenvectors(DoubleMatrix A, DoubleMatrix B) {
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
	public static String[][] kmeans(double subspacebeta0[][], int clusters, String nodelist[], int time) {

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
			// centroids[i][0]=(((max1-min1)/clusters)*(i+0.5));
			// centroids[i][1]=subspacebeta[i*(i+clusters)][1];

			// centroids[i][1]=((max2-((max2-min2)/2)));

			// int a = new Integer(subspacebeta.length/clusters).intValue();

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
					// distance[i][j]=Math.pow(Math.sqrt(Math.pow(subspace[i][0]-centroids[j][0],
					// 2)+Math.pow(subspace[i][1]-centroids[j][1], 2)),2);
					distance[i][j] = Math.sqrt(Math.pow(subspacebeta0[i][0] - centroids[j][0], 2) + Math.pow(subspacebeta0[i][1] - centroids[j][1], 2));
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

			// System.out.println("QUANTITY DATAPOINTS");
			// displayint(quantitydatapoints);
			// ///////////////////////////////////////THIRD STEP: Calculate the
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
	public static double[][] increaseDiagonal(double diagonal[][], double incidence[][], int nodeincrement, int incrementw) {

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
	public static double[][] increaseLaplacian(double[][] incidence, int edgeaccum, int nodeaccum, double[] incidencew) {

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
					r.set(i, j, Math.sqrt(incidencew[i]));
				} else if (r.get(i, j) == -1) {
					r.set(i, j, -Math.sqrt(incidencew[i]));
				}
			}
		}

		// create the INCREMENT OF LAPLACIAN for the actual time instant.
		Matrix deltalapla = rt.times(r);
		double[][] laplacian = (matrix2array(deltalapla));
		return laplacian;
	}

	// INCREASE INCIDENCE MATRIX, FROM R TO R'
	public static double[][] increaseIncidence(double[][] array,
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

	// Print ARRAY LIST
	public static void printarraylist(List<String> array) {
		Iterator<String> Iterator = array.iterator();
		while (Iterator.hasNext()) {
			String elemento = Iterator.next();
			System.out.print(elemento + " / ");
		}
	}

	// Print ARRAY LIST
	public static void printarraylistdouble(List<Double> array) {
		Iterator<Double> Iterator = array.iterator();
		while (Iterator.hasNext()) {
			Double elemento = Iterator.next();
			System.out.print(elemento + " / ");
		}
	}

	// eliminate duplicate elements from an arraylist
	public static ArrayList<String> noduplicate(ArrayList<String> lista) {

		HashSet<String> hs = new HashSet<String>();
		hs.addAll(lista);
		lista.clear();
		lista.addAll(hs);
		return lista;
	}

	public static ArrayList<Integer> noduplicateint(ArrayList<Integer> lista) {

		HashSet<Integer> hs = new HashSet<Integer>();
		hs.addAll(lista);
		lista.clear();
		lista.addAll(hs);
		return lista;
	}

	// getting the maximum value
	public static double getMaxValue(double[][] array) {
		double maxValue = array[0][0];
		for (int i = 1; i < array.length; i++) {
			if (array[i][0] > maxValue) {
				maxValue = array[i][0];

			}
		}
		return maxValue;
	}

	// getting the miniumum value
	public static double getMinValue(double[][] array) {
		double minValue = array[0][0];
		for (int i = 1; i < array.length; i++) {
			if (array[i][0] < minValue) {
				minValue = array[i][0];
			}
		}
		return minValue;
	}

	// method to display DOUBLE matrix
	public static void display(double matrix[][]) {
		for (int row = 0; row < matrix.length; row++) {
			for (int column = 0; column < matrix[row].length; column++) {
				System.out.print(matrix[row][column] + " ");
			}
			System.out.println();
		}
	}

	public static void displayvector(double matrix[]) {
		for (int row = 0; row < matrix.length; row++) {
			System.out.print(matrix[row] + " ");
			System.out.println();
		}
	}

	// method to display CHAR matrix [n][n]
	public static void displaystring(String matrix[][]) {
		for (int row = 0; row < matrix.length; row++) {
			for (int column = 0; column < matrix[row].length; column++) {
				System.out.print(matrix[row][column] + " ");
			}
			System.out.println();
		}
	}

	// method to display CHAR matrix [n][1]
	public static void displaystring1(String matrix[]) {
		for (int row = 0; row < matrix.length; row++) {
			System.out.print(matrix[row] + " ");
			System.out.println();
		}
	}

	// method to display MATRIX any matrix
	public static void displayint(int matrix[][]) {
		for (int row = 0; row < matrix.length; row++) {
			for (int column = 0; column < matrix[row].length; column++) {
				System.out.print(matrix[row][column] + " ");
			}
			System.out.println();
		}
	}

	// method to transform a MATRIX to an ARRAY
	public static double[][] matrix2array(Matrix matrix) {
		double arraybeta[][] = new double[matrix.getRowDimension()][matrix.getColumnDimension()];

		for (int row = 0; row < matrix.getRowDimension(); row++) {
			for (int column = 0; column < matrix.getColumnDimension(); column++) {
				arraybeta[row][column] = matrix.get(row, column);
			}
		}
		return arraybeta;
	}

	// Method to get the square root of a matrix A
	// Credits
	// http://www.openimaj.org/openimaj-core-libs/core-math/apidocs/org/openimaj/math/matrix/MatrixUtils.html
	// Compute the principle square root, X, of the matrix A such that A=X*X
	public static Matrix sqrt(Matrix matrix) {
		// A = V*D*V'
		final EigenvalueDecomposition evd = matrix.eig();
		final Matrix v = evd.getV();
		final Matrix d = evd.getD();

		// sqrt of cells of D and store in-place
		for (int r = 0; r < d.getRowDimension(); r++)
			for (int c = 0; c < d.getColumnDimension(); c++)
				d.set(r, c, Math.sqrt(d.get(r, c)));

		// Y = V*D/V
		// Y = V'.solve(V*D)'
		final Matrix a = v.inverse();
		final Matrix b = v.times(d).inverse();
		return a.solve(b).inverse();
	}

}
