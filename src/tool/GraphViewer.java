package tool;

import java.awt.Dimension;
import java.awt.geom.Point2D;

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

public class GraphViewer extends Group {
	private Relaxer relaxer;
	private Layout<String, String> layout;
	private double NODE_SIZE = 10;
	
	public void refresh(Layout<String, String> layout) {
		getChildren().clear();
		
		this.layout = layout;
		this.layout.setSize(new Dimension(400, 400)); 
		
		if(relaxer != null) {
			relaxer.stop();
			relaxer = null;
		}
		
		if(layout instanceof IterativeContext) {
			layout.initialize();
			if(relaxer == null) {
				relaxer = new VisRunner((IterativeContext)layout);
				relaxer.prerelax();
				relaxer.relax();
			}
		}
		
		Graph<String, String> graph =  layout.getGraph();
		
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
		
		for(String vertex : graph.getVertices()) {
			Point2D p = layout.transform(vertex);

			Circle circle = new Circle();
			
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
