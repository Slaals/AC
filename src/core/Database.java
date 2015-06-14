package core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import object.Edge;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Database {
	
	public static String address = "localhost";
	public static String port = "3306";
	public static String db = "graph1";
	public static String userName = "root";
	public static String passwd = "root";
	
	public static ObservableList<String> getTables() {
		ObservableList<String> options = FXCollections.observableArrayList();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			DatabaseMetaData md = getConnection().getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while(rs.next()) {
				options.add(rs.getString(3));
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return options;
	}
	
	public static void checkDatabase(String database) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			Statement st = getConnection().createStatement();
			
			String createDbIfExists = "CREATE DATABASE IF NOT EXISTS " + database;
			
			st.executeUpdate(createDbIfExists);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateTable(String tableName, String label, String fromNode, String toNode) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			Statement st = getConnection().createStatement();
			
			String updateValues = "UPDATE " + tableName + " SET fromnode=" +
					fromNode + ", tonode=" + toNode + " WHERE name=" + label + ";";
			
			st.executeUpdate(updateValues);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createTableGraph(String tableName) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			Statement st = getConnection().createStatement();
			
			String dropTable = "DROP TABLE IF EXISTS " + tableName + ";";
			
			st.executeUpdate(dropTable);
			
			String createTable = "CREATE TABLE " + tableName + " (" +
					"name int auto_increment primary key," +
					"fromnode int," +
					"tonode int," +
					"fromtime int default 1," +
					"totime int default 1,"+
					"weight int default 1);";
			
			st.executeUpdate(createTable);
			
			App.logConsole("Table " + tableName + " created! ", App.SUCCESS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int getTotalTime(String tableName) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			Statement st = getConnection().createStatement();
			ResultSet srs = st.executeQuery("SELECT MAX(totime) AS totime FROM " + App.TABLE_NAME);
			
			srs.next();
			return srs.getInt("totime");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static void updateEdgeTime(String tableName, int fromNode, int toNode, int time) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			Statement st = getConnection().createStatement();
			
			String updateTime = "UPDATE " + tableName + " SET totime=" + time + " WHERE fromNode=" + 
					fromNode + " AND toNode=" + toNode + ";";
			
			st.executeUpdate(updateTime);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void feedTable(String tableName, int fromNode, int toNode, int fromTime, int toTime) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			Statement st = getConnection().createStatement();
			
			String insertValues = "INSERT INTO " + tableName + "(fromnode, tonode, fromtime, totime) VALUES(" +
					fromNode + ", " + toNode + ", " + fromTime + ", " + toTime + ");";
			
			st.executeUpdate(insertValues);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void refreshTable(String tableName) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			Statement st = getConnection().createStatement();
			
			String deleteValues = "DELETE FROM " + tableName;
			
			st.executeUpdate(deleteValues);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static LinkedList<Edge> getMatrix() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			Statement st = getConnection().createStatement();
			ResultSet srs = st.executeQuery("SELECT * FROM " + App.TABLE_NAME + " ORDER BY fromnode ASC, tonode ASC");
			
			return generateEdgeMatrix(srs);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return new LinkedList<Edge>();
	}
	
	private static LinkedList<Edge> generateEdgeMatrix(ResultSet srs) throws SQLException {
		LinkedList<Edge> edgeList = new LinkedList<Edge>();
		
		while (srs.next()) {
			Edge edgeData = new Edge();
			edgeData.setFromnode(srs.getString("fromnode"));
			edgeData.setTonode(srs.getString("tonode"));
			edgeData.setFromtime(srs.getInt("fromtime"));
			edgeData.setTotime(srs.getInt("totime"));
			edgeData.setWeight(srs.getInt("weight"));
			
			edgeList.add(edgeData);
		}
		
		return edgeList;
	}
	
	private static Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + db, userName, passwd);
	}
}
