package view.feature;

import java.awt.geom.Point2D;
import java.util.HashMap;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class GraphView extends Group {
	private Relaxer relaxer;
	
	private double NODE_SIZE = 10;
	
	private HashMap<String, Integer> nodesCluster;
	
	String[] colors;
	
	public GraphView() {
		nodesCluster = new HashMap<String, Integer>();
		colors = new String[]{"#000000", "#FF0000", "#109F02", "#1020FF", "#505010", "#330099"};
	}
	
	public void addNodeCluster(String node, Integer cluster) {
		nodesCluster.put(node, cluster);
	}
	
	/**
	 * Create the graph
	 * It could be improved by updating only the part that needs to be updated
	 * This algorithm below re-create the whole graph
	 * @param layout
	 */
	public void refresh(Layout<String, String> layout) {
		getChildren().clear();
		
		if(relaxer != null) {
			relaxer.stop();
			relaxer = null;
		}
		
		// Apply the chosen layout
		if(layout instanceof IterativeContext) {
			layout.initialize();
			if(relaxer == null) {
				relaxer = new VisRunner((IterativeContext)layout);
				relaxer.prerelax();
				relaxer.relax();
			}
		}
		
		Graph<String, String> graph =  layout.getGraph();
		
		// Create edges : JavaFX lines
		for(String edge : graph.getEdges()) {
			Pair<String> endpoints = graph.getEndpoints(edge);
			
			Point2D pStart = layout.transform(endpoints.getFirst());
			Point2D pEnd = layout.transform(endpoints.getSecond());
			
			Line line = new Line();
			
			line.setStartX(pStart.getX());
			line.setStartY(pStart.getY());
			line.setEndX(pEnd.getX());
			line.setEndY(pEnd.getY());
			
			getChildren().add(line);
		}
		
		// Created vertices : JavaFX circles
		for(String vertex : graph.getVertices()) {
			Point2D p = layout.transform(vertex);
			
			Circle circle = new Circle();
			
			if(nodesCluster.containsKey(vertex)) {
				circle.setFill(Color.web(colors[nodesCluster.get(vertex)]));
			}
			
			circle.setCenterX(p.getX());
			circle.setCenterY(p.getY());
			circle.setRadius(NODE_SIZE);
			
			Text txt = new Text(vertex);
			txt.setTextOrigin(VPos.CENTER);
			txt.setFill(Color.WHITE);
			txt.setFont(Font.font("Arial", FontWeight.BOLD, 14));
			txt.setX(p.getX() - NODE_SIZE / 2);
			txt.setY(p.getY());
			
			getChildren().add(circle);
			getChildren().add(txt);
		}
	}
}
