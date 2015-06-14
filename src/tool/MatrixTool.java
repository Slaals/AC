package tool;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import core.App;
import core.Database;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import object.Edge;

public class MatrixTool extends Stage {
	
	private static String DEFAULT_SPLIT = ";";
	
	private VBox graphPane;
	protected VBox matrixPane;
	protected TextArea matrix;
	protected FlowPane timePane;
	protected ComboBox<String> split;
	
	protected GraphViewer graph;
	
	protected int currentTime = 1;
	
	private ArrayList<ToggleButton> btnsTime;
	protected HashMap<Integer, Integer[][]> matrixTime;
	
	protected App app;

	public MatrixTool(App app) {
		super();
		
		BorderPane content = new BorderPane();
		
		this.app = app;
		btnsTime = new ArrayList<ToggleButton>();
		matrixTime = new HashMap<Integer, Integer[][]>();
		
		initStyle(StageStyle.UTILITY);
		
		init();
		
		content.setCenter(matrixPane);
		content.setRight(graphPane);
		
		Scene scene = new Scene(content);
		
		setTitle("Matrix Creator");
		setMinHeight(600);
		setMinWidth(1000);
		
		setResizable(true);
		
		initModality(Modality.APPLICATION_MODAL);
		
		setScene(scene);
		show();
	}
	
	public void showGraph() {
		Graph<String, String> graph = new SparseMultigraph<String, String>();
		
		Integer[][] matrix = matrixTime.get(currentTime);
		for(int vertex = 1; vertex <= matrix.length; vertex++) {
			graph.addVertex(vertex + "");
		}
		
		int edgeNumber = 1;
		
		for(int row = 0; row < matrix.length; row++) {
			for(int col = 0; col < matrix[row].length; col++) {
				if(matrix[row][col] != 0) {
					graph.addEdge(edgeNumber + "", (row + 1) + "", (col + 1) + "");
				}
				edgeNumber += 1;
			}
		}
		
		Layout<String, String> layout = new FRLayout<String, String>(graph);
		layout.setSize(new Dimension(400, 400));
		
		this.graph.refresh(layout);
	}
	
	/**
	 * Return the interval time
	 * @return
	 */
	private ArrayList<int[]> defineInterval() {
		ArrayList<int[]> interval = new ArrayList<int[]>();
		boolean endInterval = false;
		
		// For each time
		for(int currentTime = 1; currentTime <= matrixTime.size(); currentTime++) {
			Integer[][] matrix = matrixTime.get(currentTime);
			
			for(int i = currentTime; i < matrixTime.size(); i++) {
				if(matrixTime.containsKey(currentTime + 1)) {
					if(!Arrays.deepEquals(matrix, matrixTime.get(i + 1))) { // Found a difference, close the interval
						int[] intervalInt = new int[2];
						intervalInt[0] = currentTime;
						intervalInt[1] = i;
						
						interval.add(intervalInt);
						
						currentTime = i;
						
						endInterval = false;
						break;
					} else { // Every matrix is equal then end the interval
						endInterval = true;
					}
				}
			}
			
			// Last interval time
			if(endInterval || currentTime == matrixTime.size()) {
				int[] intervalInt = new int[2];
				intervalInt[0] = currentTime;
				intervalInt[1] = matrixTime.size();
				
				interval.add(intervalInt);
				
				break;
			}
		}
		
		return interval;
	}
	
	/**
	 * Feed the table of edge values
	 * @param tableName
	 * @param split char that determines how to split the matrix string
	 */
	protected void saveGraph(String tableName) {
		int nbValues = 0;
		int nbUpdates = 0;
		
		ArrayList<int[]> interval = defineInterval();
		Integer[][] oldMatrix = null;
		
		for(int[] intervalTime : interval) {
			Integer[][] matrix = matrixTime.get(intervalTime[0]);
			
			for(int row = 0; row < matrix.length; row++) {
				for(int col = 0; col < matrix.length; col++) {
					if(oldMatrix != null) {
						if(matrix[row][col] != oldMatrix[row][col]) { // Compare the last matrix and the current matrix
							if(matrix[row][col] > 0) { // If weight > 0 = new edge
								Database.feedTable(tableName, row + 1, col + 1, intervalTime[0], intervalTime[1]);
								nbValues++;
							} else { // Else the edge end at t = intervalTime[0] - 1
								Database.updateEdgeTime(tableName, row + 1, col + 1, intervalTime[0] - 1);
								nbUpdates++;
							}
						} else { // Values are equal
							if(matrix[row][col] > 0) { // Edge still exists; update totime t = intervalTime[1]
								Database.updateEdgeTime(tableName, row + 1, col + 1, intervalTime[1]);
								nbUpdates++;
							}
						}
					} else { // First loop; insert all edges
						if(matrix[row][col] > 0) { // First matrix, insert all edges
							Database.feedTable(tableName, row + 1, col + 1, intervalTime[0], intervalTime[1]);
							nbValues++;
						}
					}
				}
			}
			
			oldMatrix = matrix;
		}
		
		App.logConsole("\n" + nbValues + " values inserted! And ", App.INFO);
		App.logConsole(nbUpdates + " updated!\n\n", App.INFO);
	}
	
	/**
	 * Normalize the string by replacing any char by the default one ';'
	 * @param matrix
	 * @param split
	 * @return
	 */
	private String formatMatrix(String matrix) {
		String split = this.split.getSelectionModel().getSelectedItem();
		if(split.isEmpty()) {
			split = "\u0009";
			matrix = matrix.replace('\u0009', ';');
		} else if(split.charAt(0) != DEFAULT_SPLIT.charAt(0)) {
			matrix = matrix.replace(split.charAt(0), DEFAULT_SPLIT.charAt(0));
		} 
		
		return matrix;
	}
	
	/**
	 * Create matrix from a list of edges
	 * @param matrixEdge
	 */
	protected void generateMatrixFromEdgesList(LinkedList<Edge> matrixEdge, int totalTime) {
		int nbNodes = Integer.valueOf(matrixEdge.getLast().getFromnode());

		int toNode;
		int fromNode;
		int toTime;
		int fromTime;
		int time;
		
		for(int i = 0; i < totalTime; i++) {
			Integer[][] matrix = new Integer[nbNodes][nbNodes];
			
			for(int row = 0; row < matrix.length; row++) {
				for(int col = 0; col < matrix.length; col++) {
					matrix[row][col] = 0;
				}
			}
			
			matrixTime.put(matrixTime.size() + 1, matrix);
		}

		for(Edge edge : matrixEdge) {
			toNode = Integer.valueOf(edge.getTonode());
			fromNode = Integer.valueOf(edge.getFromnode());
			toTime = edge.getTotime();
			fromTime = edge.getFromtime();
			
			time = fromTime;
			
			while(time <= toTime) {
				Integer[][] matrix = matrixTime.get(time);
				matrix[fromNode - 1][toNode - 1] = edge.getWeight();
				matrixTime.put(time, matrix);
				time += 1;
			}
		}
	}
	
	/**
	 * Transform Interger[][] to a strandard matrix String
	 * @param time
	 * @return
	 */
	protected String getMatrixStr(int time) {
		String strMatrix = "";

		if(matrixTime.get(time) != null) {
			Integer[][] matrix = matrixTime.get(time);
			for(int row = 0; row < matrix.length; row++) {
				for(int col = 0; col < matrix[row].length; col++) {
					strMatrix += matrix[row][col] + ";";
				}
				strMatrix += "\n";
			}
		}
			
		return strMatrix;
	}
	
	/**
	 * Refresh the buttons and update the textarea
	 */
	private void switchTimeButtons() {
		for(ToggleButton btn : btnsTime) {
			btn.setSelected(false);
		}
		btnsTime.get(currentTime - 1).setSelected(true);
	}
	
	protected void refreshTime() {
		timePane.getChildren().removeAll(btnsTime);
		btnsTime.clear();
		matrixTime.clear();
		currentTime = 1;
	}
	
	/**
	 * @param split
	 */
	protected void generateMatrixFromString() {
		String strMatrix = formatMatrix(this.matrix.getText());
			
		String[] rows = strMatrix.split("\n");
		
		Integer[][] matrix;
		
		if(!strMatrix.isEmpty()) {
			matrix = matrixTime.get(currentTime);
			
			if(matrix == null) {
				matrix = new Integer[rows.length][rows.length];
			}
				
			for(int row = 0; row < rows.length; row++) {
				String[] edges = rows[row].split(DEFAULT_SPLIT);
				for(int col = 0; col < edges.length; col++) {
					matrix[row][col] = Integer.valueOf(edges[col]);
				}
			}
			matrixTime.put(currentTime, matrix);
		} else {
			matrixTime.put(currentTime, null);
		}
	}
	
	protected void generateTimeButtons() {
		currentTime = btnsTime.size() + 1;
		
		ToggleButton btnTime = new ToggleButton(currentTime + "");
		
		btnTime.setOnAction(event -> {
			generateMatrixFromString();
			currentTime = Integer.valueOf(btnTime.getText());
			matrix.setText(getMatrixStr(currentTime));
			showGraph();
			switchTimeButtons();
		});
		btnsTime.add(btnTime);
		
		switchTimeButtons();
		
		timePane.getChildren().add(btnTime);
	}
	
	private void init() {
		matrixPane = new VBox();
		matrixPane.setPadding(new Insets(10));
		matrixPane.setSpacing(15);
		
		matrix = new TextArea("");
		matrix.setPrefHeight(800);
		matrix.setStyle("-fx-font-size: 14pt");
		
		timePane = new FlowPane();
		timePane.setHgap(10);
		
		Button btnAddTime = new Button("+");
		btnAddTime.setOnAction(event -> {
			generateMatrixFromString();
			generateTimeButtons();
		});
		timePane.getChildren().add(btnAddTime);
		
		ObservableList<String> splitOptions = FXCollections.observableArrayList();
		
		splitOptions.add("");
		splitOptions.add(";");
		splitOptions.add(",");
		
		split = new ComboBox<String>(splitOptions);
		split.getSelectionModel().select(0);
		split.setPrefWidth(40);

		Label lblTime = new Label(" Time : ");
		timePane.getChildren().add(lblTime);
		
		graphPane = new VBox();
		graphPane.setSpacing(20);
		graphPane.setMinSize(450, 600);
		graphPane.setMaxSize(450, 600);
		graphPane.setStyle("-fx-background-color: #D1D6D2;"
				+ "-fx-padding: 15px");
		Button btnRefresh = new Button("Refresh");
		btnRefresh.setMaxWidth(Double.MAX_VALUE);
		
		graph = new GraphViewer();
		
		graphPane.getChildren().add(btnRefresh);
		graphPane.getChildren().add(new Separator(Orientation.HORIZONTAL));
		graphPane.getChildren().add(graph);
		
		btnRefresh.setOnAction(event -> {
			generateMatrixFromString();
			showGraph();
		});
		
		BorderPane.setAlignment(graphPane, Pos.CENTER);
		BorderPane.setMargin(graphPane, new Insets(25));
	}
}
