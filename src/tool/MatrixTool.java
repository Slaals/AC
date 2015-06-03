package tool;

import java.util.LinkedList;

import core.App;
import core.Database;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import object.Edge;

public class MatrixTool extends Stage {
	
	protected VBox content;
	
	protected TextArea matrix;
	
	protected App app;

	public MatrixTool(App app) {
		super();
		
		this.app = app;
		
		initStyle(StageStyle.UTILITY);
		
		content = new VBox();
		content.setPadding(new Insets(10));
		content.setSpacing(15);
		
		init();
		
		Scene scene = new Scene(content);
		
		setTitle("Matrix Creator");
		setMinHeight(500);
		setMinWidth(500);
		
		setResizable(false);
		
		initModality(Modality.APPLICATION_MODAL);
		
		setScene(scene);
		show();
	}
	
	/**
	 * Feed the table of edge values
	 * @param tableName
	 * @param split char that determines how to split the matrix string
	 */
	protected void generateGraph(String tableName, String split) {
		String matrix = "";
		String defaultSplit = ";";
		
		// Normalize the string by replacing any char by the default one ';'
		if(split.isEmpty()) {
			split = "\u0009";
			matrix = this.matrix.getText().replace('\u0009', ';');
		} else if(split.charAt(0) != defaultSplit.charAt(0)) {
			matrix = this.matrix.getText().replace(split.charAt(0), defaultSplit.charAt(0));
		}
		
		String[] rows = matrix.split("\n");
		
		int nbValues = 0;
		
		for(int row = 0; row < rows.length; row++) {
			String[] nodes = rows[row].split(defaultSplit);
			for(int node = 0; node < nodes.length; node++) {
				if(nodes[node].equals("1") || nodes[node].equals("1.0")) {
					// default time = 1, default weight = 1
					Database.feedTable(tableName, row + 1, node + 1);
					nbValues++;
				}
			}
		}
		
		App.logConsole(nbValues + " values inserted!\n\n", App.INFO);
	}
	
	/**
	 * It assumes that every node has a label which is sequential, node 2 is between 1 and 3...
	 * @param matrix
	 */
	protected void showMatrix(LinkedList<Edge> matrix) {
		int nbNodes = Integer.valueOf(matrix.getLast().getFromnode());
		String matrixStr = "";
		
		int lastToNode = 0;
		int lastFromNode = Integer.valueOf(matrix.getFirst().getFromnode());
		
		int toNode;
		int fromNode;
		
		for(Edge edge : matrix) {
			toNode = Integer.valueOf(edge.getTonode());
			fromNode = Integer.valueOf(edge.getFromnode());
			
			// Case where it switches to next row, fill the last row by 0 if it's necessary
			if(lastFromNode != fromNode) {
				if(lastToNode == nbNodes) {
					matrixStr += "\n";
				} else {
					matrixStr = fillZero(lastToNode + 1, nbNodes, matrixStr);
				}
				
				// New row
				lastToNode = 0;
			}
				
			// Put 0 for every cell between the lastToNode and the current node -1, -1 because the toNode is where
			// the weight will be
			for(int i = lastToNode; i < toNode - 1; i++) {
				matrixStr += "0;";
			}
			
			lastToNode = toNode;
			
			// Put weight
			matrixStr += edge.getWeight() +";";
			
			lastFromNode = fromNode;
			
		}
		
		matrixStr = fillZero(lastToNode + 1, nbNodes, matrixStr);
	
		this.matrix.setText(matrixStr);
	}
	
	private String fillZero(int begin, int end, String str) {
		for(int i = begin; i < end; i++) {
			str += "0;";
		}
		str += "0;\n";
		
		return str;
	}
	
	private void init() {
		matrix = new TextArea();
		matrix.setPrefHeight(800);
		matrix.setStyle("-fx-font-size: 14pt");
	}
}
