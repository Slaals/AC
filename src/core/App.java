package core;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerFactory;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.partition.api.Partition;
import org.gephi.partition.api.PartitionController;
import org.gephi.partition.plugin.NodeColorTransformer;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.ProcessingTarget;
import org.gephi.preview.api.RenderTarget;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;

import processing.core.PApplet;
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

import java.awt.Color;

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
	
	public static String DATA_PATH = System.getProperty("user.dir");
	public static String TABLE_NAME;
	
	public static TextFlow CONSOLE;
	
	private static CheckBox actModularity;
	private static CheckBox actModularityByCluster;
	private static Button btnGenNodes;
	private static Button btnGenEdges;
	private static Button btnShowChanges;
	private static Button btnShowModularity;
	private static Button btnShowModularityByCluster;
	private static Button btnShowGraph;
	
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
		ComboBox<String> comboTableName = new ComboBox<String>(options);
		comboTableName.setPrefWidth(300);
		comboTableName.getSelectionModel().select(options.get(0));
		TABLE_NAME = options.get(0);
		
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
		
		btnShowGraph = new Button("Show Graph");
		
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
		
		btnGenNodes.setDisable(true);
		btnGenEdges.setDisable(true);
		btnShowChanges.setDisable(true);
		btnShowModularity.setDisable(true);
		btnShowModularityByCluster.setDisable(true);
		btnShowGraph.setDisable(true);
		
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
				
				Text text = new Text(App.DATA_PATH + "\\console.txt" + " created!\n\n");
				text.setFill(javafx.scene.paint.Color.web("#B3D9B4"));
				App.CONSOLE.getChildren().add(text);
				
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
				Text txt = new Text(str);
				txt.setFill(javafx.scene.paint.Color.web("#A1DAF5"));
				
				CONSOLE.getChildren().add(txt);
				CONSOLE.layout();
			}
		});
		
		btnShowModularity.setOnAction((event) -> {
			btnShowModularity.setStyle(alreadyUsed);
			for(String str : dynamic.getModularity()) {
				Text txt = new Text(str);
				txt.setFill(javafx.scene.paint.Color.web("#A1DAF5"));
				
				CONSOLE.getChildren().add(txt);
				CONSOLE.layout();
			}
		});
		
		btnShowModularityByCluster.setOnAction((event) -> {
			btnShowModularityByCluster.setStyle(alreadyUsed);
			for(String str : dynamic.getModularityCluster()) {
				Text txt = new Text(str);
				txt.setFill(javafx.scene.paint.Color.web("#A1DAF5"));
				
				CONSOLE.getChildren().add(txt);
				CONSOLE.layout();
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
			renderGraph();
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
				
				Text textLaunch = new Text("The algorithm has been executed on the table '" + TABLE_NAME + "', please wait until it's finished...\n\n");
				textLaunch.setFill(javafx.scene.paint.Color.web("#FFFFFF"));
				CONSOLE.getChildren().add(textLaunch);
				
				consoleView.layout();
				
				notifyBtns(false);

				dynamic.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		MenuBar menuBar = new MenuBar();
		
		Menu conf = new Menu("Conf.");
		
		MenuItem confDB = new MenuItem("Database");
		confDB.setOnAction((event) -> {
			new ConfDB();
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
	
	public static void notifyBtns(boolean ready) {
		btnGenNodes.setDisable(!ready);
		btnGenEdges.setDisable(!ready);
		btnShowChanges.setDisable(!ready);
		btnShowGraph.setDisable(!ready);
		
		if(ready) {
			if(actModularity.isSelected()) {
				btnShowModularity.setDisable(false);
			} else {
				btnShowModularity.setDisable(true);
			}
			
			if(actModularityByCluster.isSelected()) {
				btnShowModularityByCluster.setDisable(false);
			} else {
				btnShowModularityByCluster.setDisable(true);
			}
		}
	}
	
	private void renderGraph() {
		//t=1
		String[][] nodes;
		String[][] edges;
		
		for(int clusterId = 0; clusterId < dynamic.getMatrixNode().size(); clusterId++) {
			HashMap<String, org.gephi.graph.api.Node> nodesKey = new HashMap<String, org.gephi.graph.api.Node>();
			
			nodes = dynamic.getMatrixNode().get(clusterId);
			edges = dynamic.getMatrixEdge().get(clusterId);
			
			//Init a project
	        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
	        pc.newProject();
	        Workspace workspace = pc.getCurrentWorkspace();
	        
	        //Get a graph model
	        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
	        AttributeModel attModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
	        PartitionController partController = Lookup.getDefault().lookup(PartitionController.class);
	        
	        ForceAtlasLayout layout = new ForceAtlasLayout(null);
	        layout.setGraphModel(graphModel);
	        layout.resetPropertiesValues();
	        layout.setRepulsionStrength(800.0);
	        
	        Container container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();
	        UndirectedGraph graph = graphModel.getUndirectedGraph();
	        
	        for(String[] node : nodes) {
	        	org.gephi.graph.api.Node n = graphModel.factory().newNode(node[1]);
	        	nodesKey.put(node[1], n);
	        	graph.addNode(n);
	        }
	        
	        for(int i = 0; i < edges.length; i++) {
	        	graph.addEdge(graphModel.factory().newEdge(nodesKey.get(edges[i][0]), nodesKey.get(edges[i][1]), 1f, false));
	        }
	        
	        Modularity modu = new Modularity();
	        modu.execute(graphModel, attModel);
	        
	        AttributeColumn modColumn = attModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);
	        Partition<?> p = partController.buildPartition(modColumn, graph);
	        
	        NodeColorTransformer nodeColorTr = new NodeColorTransformer();
	        nodeColorTr.randomizeColors(p);
	        partController.transform(p, nodeColorTr);

	        //Append container to graph structure
	        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
	        importController.process(container, new DefaultProcessor(), workspace);

	        layout.initAlgo();
	        for(int i = 0; i < 100 && layout.canAlgo(); i++) {
	        	layout.goAlgo();
	        }
		}
		
		//Preview configuration
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel();
        previewModel.getProperties().putValue(PreviewProperty.NODE_BORDER_WIDTH, 0.1f);
        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.FALSE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_RADIUS, 2f);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, 0.5f);
        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);
        previewController.refreshPreview();
		
		//New Processing target, get the PApplet
        ProcessingTarget target = (ProcessingTarget) previewController.getRenderTarget(RenderTarget.PROCESSING_TARGET);
        PApplet applet = target.getApplet();
        applet.init();

        //Refresh the preview and reset the zoom
        previewController.render(target);
        target.refresh();
        target.resetZoom();

        //Add the applet to a JFrame and display
        JFrame frame = new JFrame("Graph Preview");
        frame.setLayout(new BorderLayout());
        
        frame.add(applet, BorderLayout.CENTER);
        
        frame.pack();
        frame.setVisible(true);
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
}
