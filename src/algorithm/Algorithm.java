package algorithm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import view.App;
import view.feature.Console;

public abstract class Algorithm extends Thread {
	
	public static int nbCluster = 3;
	
	protected ArrayList<String[][]> matrixNode;
	protected ArrayList<String[][]> matrixEdge;
	
	protected ArrayList<String> changes;
	protected ArrayList<String> modularity;
	protected ArrayList<String> modularityCluster;
	
	protected long chrono;
	protected long totalChrono;
	
	protected Clusters[] clusters;
	
	protected int totalTime;
	
	public abstract Algorithm reset();
	
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
				
				Console.getConsole().logConsole(App.DATA_PATH + "\\" + App.TABLE_NAME + "_nodes_t" + time + ".csv" + " created!\n\n", Console.SUCCESS);
				
				writer.flush();
				writer.close();
			} catch (IOException e) {
				Console.getConsole().logConsole("An exception has been encountered while trying to create "
						+ App.TABLE_NAME + "_nodes_t" + time + ".csv\n\n", Console.WARNING);
				
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
				
				Console.getConsole().logConsole(App.DATA_PATH + "\\" + App.TABLE_NAME + "_edges_t" + time + ".csv" + " created!\n\n", Console.SUCCESS);

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
	
	public ArrayList<String> getModularity() {
		return modularity;
	}
	
	public ArrayList<String> getModularityCluster() {
		return modularityCluster;
	}
	
	public ArrayList<String[][]> getMatrixNode() {
		return matrixNode;
	}
	
	public ArrayList<String[][]> getMatrixEdge() {
		return matrixEdge;
	}
	
	public int getTotalTime() {
		return totalTime;
	}
}
