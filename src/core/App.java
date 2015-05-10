package core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import algorithm.Dynamic;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.text.Text;

public class App extends Application {
	
	public static String address = "localhost";
	public static String port = "3306";
	public static String db = "graph1";
	public static String userName = "root";
	public static String passwd = "root";
	
	public static int WARNING = 0;
	public static int INFO = 1;
	public static int PLAIN = 2;
	public static int SUCCESS = 3;
	
	public static String DATA_PATH = System.getProperty("user.dir");
	public static String TABLE_NAME;
	
	public static TextFlow CONSOLE;
	
	private CheckBox actModularity;
	private CheckBox actModularityByCluster;
	private Button btnGenNodes;
	private Button btnGenEdges;
	private Button btnShowChanges;
	private Button btnShowModularity;
	private Button btnShowModularityByCluster;
	private Button btnShowGraph;
	
	private ComboBox<String> comboTableName;
	
	private Dynamic dynamic;
	
	private BorderPane mainPane;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scn = new Scene(mainPane);
		
		primaryStage.setMaxHeight(600);
		primaryStage.setMaxWidth(700);
		
		primaryStage.setTitle("Simu");
		primaryStage.setResizable(false);
		primaryStage.setScene(scn);
		primaryStage.show();
	}
	
	@Override
	public void stop() {
		System.exit(0);
		Platform.exit();
	}
	
	@Override
	public void init() throws Exception {
		dynamic = new Dynamic();
		
		mainPane = new BorderPane();
		
		GridPane form = new GridPane();
		
		VBox btnsPane = new VBox(15);
		
		Label lblPath = new Label("Out");
		
		TextField txtPath = new TextField();
		txtPath.setEditable(false);
		txtPath.setText(DATA_PATH);
		txtPath.setPrefWidth(300);
		
		Button btnPath = new Button("...");
		
		Label lblTable = new Label("Table name");
		
		ObservableList<String> options = getTables();
		comboTableName = new ComboBox<String>(options);
		comboTableName.setPrefWidth(300);
		if(!options.isEmpty()) {
			comboTableName.getSelectionModel().select(options.get(0));
			TABLE_NAME = options.get(0);
		}
		
		Label lblNbCluster = new Label("Nb. Clusters");
		
		TextField txtNbCluster = new TextField(Dynamic.nbCluster + "");
		txtNbCluster.setAlignment(Pos.CENTER);
		txtNbCluster.setPrefWidth(300);
		
		ScrollPane consoleView = new ScrollPane();
		consoleView.setPrefHeight(1000);
		consoleView.setHbarPolicy(ScrollBarPolicy.NEVER);
		consoleView.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		consoleView.setFitToHeight(true);
		consoleView.setFitToWidth(true);
		
		Button clearConsole = new Button("Clear");
		
		CONSOLE = new TextFlow();
		CONSOLE.setStyle("-fx-background-color: #272823;"
				+ "-fx-padding: 6px;");
		
		consoleView.setContent(CONSOLE);

		form.add(lblTable, 0, 0);
		form.add(comboTableName, 1, 0);
		
		form.add(lblPath, 0, 1);
		form.add(txtPath, 1, 1);
		form.add(btnPath, 2, 1);
		
		form.add(lblNbCluster, 0, 2);
		form.add(txtNbCluster, 1, 2);
		
		form.add(consoleView, 0, 3);
		form.add(clearConsole, 0, 4);
		
		GridPane.setColumnSpan(consoleView, 3);
		
		Button start = new Button("Start");
		start.setCursor(Cursor.HAND);
		
		form.setHgap(15);
		form.setVgap(10);
		
		btnGenNodes = new Button("Generate nodes list (.csv)");
		btnGenEdges = new Button("Generate edges list (.csv)");
		btnShowChanges = new Button("Show inter frame");
		
		GridPane btnsModuPane = new GridPane();
		actModularity = new CheckBox();
		actModularityByCluster = new CheckBox();
		actModularity.setCursor(Cursor.HAND);
		actModularityByCluster.setCursor(Cursor.HAND);
		actModularity.setStyle("-fx-color: white");
		actModularityByCluster.setStyle("-fx-color: white");
		
		btnShowModularity = new Button("Show modulatiry");
		btnShowModularityByCluster = new Button("Show modularity / cluster");
		
		Button btnSaveConsole = new Button("Save console");
		
		btnsModuPane.setVgap(15);
		
		btnsModuPane.add(actModularity, 0, 0);
		btnsModuPane.add(btnShowModularity, 1, 0);
		btnsModuPane.add(actModularityByCluster, 0, 1);
		btnsModuPane.add(btnShowModularityByCluster, 1, 1);
		
		String neverUsed = "-fx-color: #B8BDB9;";
		String alreadyUsed = "-fx-color: #B3D9B4;"
				+ "-fx-focus-color: transparent;"
				+ "-fx-background-insets: -1.4, 0, 1, 2;";
		
		btnShowGraph = new Button("Generate graphs");
		
		btnsPane.getChildren().add(btnGenNodes);
		btnsPane.getChildren().add(btnGenEdges);
		btnsPane.getChildren().add(btnShowChanges);
		btnsPane.getChildren().add(btnsModuPane);
		btnsPane.getChildren().add(btnShowGraph);
		btnsPane.getChildren().add(btnSaveConsole);
		
		btnGenNodes.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		btnGenEdges.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		btnShowChanges.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		btnShowModularity.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		btnShowModularityByCluster.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		btnSaveConsole.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		btnShowGraph.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		btnGenNodes.setCursor(Cursor.HAND);
		btnGenEdges.setCursor(Cursor.HAND);
		btnShowChanges.setCursor(Cursor.HAND);
		btnShowModularity.setCursor(Cursor.HAND);
		btnShowModularityByCluster.setCursor(Cursor.HAND);
		btnSaveConsole.setCursor(Cursor.HAND);
		btnShowGraph.setCursor(Cursor.HAND);
		
		btnsPane.setStyle(neverUsed
				+ "-fx-background-color: #D1D6D2;"
				+ "-fx-padding: 15px");
		
		clearConsole.setOnAction((event) -> {
			CONSOLE.getChildren().clear();
			CONSOLE.layout();
		});
		
		btnSaveConsole.setOnAction((event) -> {
			try {
				FileWriter writer = new FileWriter(App.DATA_PATH + "\\console.txt");
				
				for(Node text : CONSOLE.getChildren()) {
					writer.append(((Text)text).getText().replace("\n", System.lineSeparator()));
				}
				
				logConsole(App.DATA_PATH + "\\console.txt" + " created!\n\n", SUCCESS);

				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		comboTableName.setOnAction((event) -> {
			TABLE_NAME = comboTableName.getSelectionModel().getSelectedItem();
		});
		
		btnGenNodes.setOnAction((event) -> {
			btnGenNodes.setStyle(alreadyUsed);
			dynamic.createNodesView();
			
			consoleView.layout();
		});
		
		btnGenEdges.setOnAction((event) -> {
			btnGenEdges.setStyle(alreadyUsed);
			dynamic.createEdgesView();
			
			consoleView.layout();
		});
		
		btnShowChanges.setOnAction((event) -> {
			btnShowChanges.setStyle(alreadyUsed);
			for(String str : dynamic.getChanges()) {
				logConsole(str, INFO);
			}
		});
		
		btnShowModularity.setOnAction((event) -> {
			btnShowModularity.setStyle(alreadyUsed);
			for(String str : dynamic.getModularity()) {
				logConsole(str, INFO);
			}
		});
		
		btnShowModularityByCluster.setOnAction((event) -> {
			btnShowModularityByCluster.setStyle(alreadyUsed);
			for(String str : dynamic.getModularityCluster()) {
				logConsole(str, INFO);
			}
		});
		
		actModularity.setOnAction((event) -> {
			if(actModularity.isSelected()) {
				Dynamic.printmodularity = 1;
			} else {
				Dynamic.printmodularity = 0;
				Dynamic.printclustermodularity = 0;
				actModularityByCluster.setSelected(false);
			}
		});
		
		actModularityByCluster.setOnAction((event) -> {
			if(actModularityByCluster.isSelected()) {
				Dynamic.printmodularity = 1;
				Dynamic.printclustermodularity = 1;
				actModularity.setSelected(true);
			} else {
				Dynamic.printclustermodularity = 0;
			}
		});
		
		btnShowGraph.setOnAction(event -> {
			//Init a project
	       /*ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
	        pc.newProject();
	        
	        ArrayList<File> files = new ArrayList<File>();
	        
	        Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					for(int time = 0; time < dynamic.getMatrixNode().size(); time++) {
						renderGraph(dynamic.getMatrixNode().get(time), dynamic.getMatrixEdge().get(time), pc, time + 1, files);
					}
					
					mergeFiles(files);
				}
			});
	        
	        t.start();*/
			GraphGenerator gg = new GraphGenerator(dynamic.getMatrixNode(), dynamic.getMatrixEdge());
			gg.renderGraph();
		});
		
		txtNbCluster.setOnKeyReleased((event) -> {
			Pattern patt = Pattern.compile("(\\d)+");
			Matcher match = patt.matcher(txtNbCluster.getText());
			
			if(match.find()) {
				Dynamic.nbCluster = Integer.parseInt(txtNbCluster.getText());
			}
		});
		
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Choose the path for your data");
		dirChooser.setInitialDirectory(new File(txtPath.getText()));
		btnPath.setOnAction((event) -> {
			File directory = dirChooser.showDialog(new Stage());
			
			if(directory != null) {
				txtPath.setText(directory.getAbsolutePath());
				DATA_PATH = txtPath.getText();
			}
		});
		
		start.setOnAction((event) -> {
			resetBtns(neverUsed);
			try {
				dynamic = new Dynamic();
				
				logConsole("The algorithm has been executed on the table '" + TABLE_NAME + "', please wait until it's finished...\n\n", PLAIN);

				dynamic.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		MenuBar menuBar = new MenuBar();
		
		Menu conf = new Menu("Conf.");
		
		MenuItem confDB = new MenuItem("Database");
		confDB.setOnAction((event) -> {
			new ConfDB(this);
		});
		
		conf.getItems().add(confDB);
		
		menuBar.getMenus().add(conf);
		
		mainPane.setTop(menuBar);
		mainPane.setCenter(form);
		mainPane.setRight(btnsPane);
		mainPane.setBottom(start);
		
		BorderPane.setAlignment(start, Pos.CENTER);
		BorderPane.setMargin(start, new Insets(15, 0, 15, 0));
		BorderPane.setMargin(form, new Insets(15, 0, 0, 15));
		BorderPane.setMargin(btnsPane, new Insets(15, 15, 0, 15));
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	private void resetBtns(String style) {
		btnGenNodes.setStyle(style);
		btnGenEdges.setStyle(style);
		btnShowChanges.setStyle(style);
		btnShowModularity.setStyle(style);
		btnShowModularityByCluster.setStyle(style);
	}
	
	public static void logConsole(String str, int type) {
		final Text txt;
		switch(type) {
			case 0:
				txt = new Text(str);
				txt.setFill(Color.web("#F6888B"));
				break;
			case 1:
				txt = new Text(str);
				txt.setFill(javafx.scene.paint.Color.web("#A1DAF5"));
				break;
			case 2:
				txt = new Text(str);
				txt.setFill(javafx.scene.paint.Color.web("#FFFFFF"));
				break;
			case 3:
				txt = new Text(str);
				txt.setFill(javafx.scene.paint.Color.web("#B3D9B4"));
				break;
			default:
				txt = new Text("");
				break;
		}
		
		Platform.runLater(() -> {
			CONSOLE.getChildren().add(txt);
			CONSOLE.layout();
		});
	}

	private ObservableList<String> getTables() {
		ObservableList<String> options = FXCollections.observableArrayList();
		// Step 1: Load the JDBC driver. jdbc:mysql://localhost:3306/travel
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			// Step 2: Connection to MYSQL database and extraction of the useful
			// columns (fromnode, tonode, weight)
			Connection conn = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + db, userName, passwd);
			DatabaseMetaData md = conn.getMetaData();
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
	
	public void refreshTable() {
		comboTableName.setItems(getTables());
	}
}
