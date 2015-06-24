package view.feature;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedList;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClusterView extends Stage {
	
	private FlowPane timePane;
	
	private ArrayList<ToggleButton> btnsTime;
	
	private ArrayList<String[][]> edgeList;
	private ArrayList<String[][]> nodeList;
	private int totalTime;
	
	private LinkedList<GraphView> graphList;
	private StackPane graphPane;
	
	private int currentTime;
	
	public ClusterView(ArrayList<String[][]> edgeList, ArrayList<String[][]> nodeList, int totalTime) {
		super();
		
		this.edgeList = edgeList;
		this.nodeList = nodeList;
		this.totalTime = totalTime;
		
		VBox content = new VBox(10);
		content.setPadding(new Insets(10));
		
		btnsTime = new ArrayList<ToggleButton>();
		graphList = new LinkedList<GraphView>();
		
		initStyle(StageStyle.UTILITY);
		
		init();
		
		content.getChildren().add(timePane);
		content.getChildren().add(new Separator());
		content.getChildren().add(graphPane);
		
		Scene scene = new Scene(content);
		
		setTitle("Clustering");
		setMinHeight(600);
		setMinWidth(1000);
		
		setResizable(false);
		
		initModality(Modality.APPLICATION_MODAL);
		
		setScene(scene);
		show();
	}
	
	private void init() {
		graphPane = new StackPane();
		graphPane.setStyle("-fx-background-color: #D1D6D2;");
		graphPane.setMinSize(990, 500);
		
		for(int i = 0; i < totalTime; i++) {
			graphList.add(new GraphView());
		}
		
		generateGraph();
		
		timePane = new FlowPane();
		timePane.setHgap(10);
		
		Label lblTime = new Label(" Time : ");
		timePane.getChildren().add(lblTime);
		
		for(int time = 0; time < totalTime; time++) {
			generateTimeButtons();
		}
		
		switchTimeButtons();
	}
	
	/**
	 * Refresh the buttons and update the textarea
	 */
	private void switchTimeButtons() {
		for(ToggleButton btn : btnsTime) {
			btn.setSelected(false);
		}
		btnsTime.get(currentTime - 1).setSelected(true);
		
		graphPane.getChildren().clear();
		graphPane.getChildren().add(graphList.get(currentTime - 1));
	}
	
	/**
	 * Create time buttons
	 */
	private void generateTimeButtons() {
		currentTime = btnsTime.size() + 1;
		
		ToggleButton btnTime = new ToggleButton(currentTime + "");
		
		btnTime.setOnAction(event -> {
			currentTime = Integer.valueOf(btnTime.getText());
			
			switchTimeButtons();
		});
		btnsTime.add(btnTime);
		
		timePane.getChildren().add(btnTime);
	}
	
	/**
	 * Show the graph to the right
	 */
	private void generateGraph() {
		Graph<String, String> graph;
		
		Layout<String, String> layout;
		
		GraphView graphView;
		
		for(int time = 0; time < nodeList.size(); time++) {
			String[][] nodes = nodeList.get(time);
			String[][] edges = edgeList.get(time);
			
			graphView = graphList.get(time);
			
			graph = new SparseMultigraph<String, String>();
			
			layout = new FRLayout<String, String>(graph);
			
			for(int row = 0; row < nodes.length; row++) {
				String nodeLabel = nodes[row][1];
				String cluster = nodes[row][2];
				
				graph.addVertex(nodeLabel);
				
				graphList.get(time).addNodeCluster(nodeLabel, Integer.valueOf(cluster));
				graphList.get(time).refresh(layout);
			}
			
			for(int row = 0; row < edges.length; row++) {
				String fromNode = edges[row][0];
				String toNode = edges[row][1];
				
				graph.addEdge((row + 1) + "", fromNode, toNode);
			}
			
			layout.setSize(new Dimension(990, 490));
			
			graphView.refresh(layout);
		}
	}
}
