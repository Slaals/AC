package algorithm;

public class Kmeans {
	
	private static boolean difference = false;
	
	private static double[][] CENTROIDS = null;
	
	public static void reset() {
		difference = false;
		CENTROIDS = null;
	}
	
	public static int[] exec(int nbClusters, double[][] points) {
		// Define the initial centroids
		if(CENTROIDS == null) {
			defineCentroids(nbClusters, points);
		} 
		
		// Find the nearest centroid for each point
		int[] pointCentroid = findNearestCentroid(points);
		
		do { // Redfine centroid and find the nearest centroid until each node isn't re-clustered
			difference = false;
			
			redefineCentroids(pointCentroid, points);

			pointCentroid = findNearestCentroid(points, pointCentroid);

		} while(difference);
		
		return pointCentroid;
	}
		
	/**
	 * @param centroids
	 * @param pointCentroid nearest centroid for each point
	 * @param points
	 * @return
	 */
	private static void redefineCentroids(int[] pointCentroid, double[][] points) {
		double totalDistanceX = 0;
		double totalDistanceY = 0;
		double quantityNode = 0;
		
		for(int centroid = 0; centroid < CENTROIDS.length; centroid++) {
			for(int point = 0; point < pointCentroid.length; point++) {
				if(Integer.valueOf(pointCentroid[point]) == centroid) {
					totalDistanceX += points[point][0];
					totalDistanceY += points[point][1];
					quantityNode += 1;
				}
			}
			
			if(quantityNode > 0) {
				CENTROIDS[centroid][0] = totalDistanceX / quantityNode;
				CENTROIDS[centroid][1] = totalDistanceY / quantityNode;
			} 
			
			totalDistanceX = 0;
			totalDistanceY = 0;
			quantityNode = 0;
		}
	}
	
	/**
	 * @param nbClusters
	 * @param points
	 * @return
	 */
	private static void defineCentroids(int nbClusters, double[][] points) {
		double randXCoord = 0;
		double randYCoord = 0;
		
		double[][] centroids = new double[nbClusters][2];
		
		double[][] minMaxXY = determineMinMaxLocations(points);
		
		for (int i = 0; i < nbClusters; i++) {
			randXCoord = Math.random() * (minMaxXY[0][1] - minMaxXY[0][0]); // rand * max - min
			randYCoord = Math.random() * (minMaxXY[1][1] - minMaxXY[1][0]);
			
			centroids[i][0] = randXCoord;
			centroids[i][1] = randYCoord;
		}
		
		CENTROIDS = centroids;
	}
	
	/**
	 * Find the nearest centroid and trigger the "difference", if it hasn't been triggered, it is the convergence
	 * @param centroids
	 * @param points
	 * @param previousPointCentroid
	 * @return
	 */
	private static int[] findNearestCentroid(double[][] points, int[] previousPointCentroid) {
		double distance = 0;
		double minDistance = 1;
		
		int[] pointCentroid = new int[points.length];
		
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < CENTROIDS.length; j++) {
				distance = Math.sqrt(Math.pow(points[i][0] - CENTROIDS[j][0], 2)
								+ Math.pow(points[i][1] - CENTROIDS[j][1], 2));
				
				if(distance < minDistance) {
					minDistance = distance;
					pointCentroid[i] = j;
				}
			}
			
			if(previousPointCentroid[i] != pointCentroid[i]) {
				difference = true;
			}

			minDistance = 1;
		}
		
		return pointCentroid;
	}
	
	/**
	 * @param centroids
	 * @param points
	 * @return
	 */
	private static int[] findNearestCentroid(double[][] points) {
		double distance = 0;
		double minDistance = 1;
		
		int[] pointCentroid = new int[points.length];
		
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < CENTROIDS.length; j++) {
				distance = Math.sqrt(Math.pow(points[i][0] - CENTROIDS[j][0], 2)
								+ Math.pow(points[i][1] - CENTROIDS[j][1], 2));
				
				if(distance < minDistance) {
					minDistance = distance;
					pointCentroid[i] = j;
				}
			}

			minDistance = 1;
		}
		
		return pointCentroid;
	}
	
	/**
	 * Determine de max coordinates to optimize the random interval
	 * @param points
	 * @return
	 */
	private static double[][] determineMinMaxLocations(double[][] points) {
		// locations[0][0] : min x
		// locations[0][1] : max x
		// locations[1][0] : min y
		// locations[1][1] : max y
		double[][] locations = new double[2][2];
		
		// init the location by first points location
		locations[0][0] = points[0][0];
		locations[0][1] = points[0][0];
		locations[1][0] = points[0][1];
		locations[1][1] = points[0][1];
		
		for(int i = 0; i < points.length; i++) {
			if(points[i][0] < locations[0][0]) {
				locations[0][0] = points[i][0];
			}
			
			if(points[i][0] > locations[0][1]) {
				locations[0][1] = points[i][0];
			}
			
			if(points[i][1] < locations[1][0]) {
				locations[1][0] = points[i][1];
			}
			
			if(points[i][1] > locations[1][1]) {
				locations[1][1] = points[i][1];
			}
		}
		
		return locations;
	}

}
