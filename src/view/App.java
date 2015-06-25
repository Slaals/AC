package view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tool.Database;
import tool.GraphGenerator;
import view.feature.ClusterView;
import view.feature.Command;
import view.feature.Console;
import view.feature.MatrixCreator;
import view.feature.MatrixEditor;
import algorithm.Incremental;
import algorithm.Algorithm;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.text.Text;

public class App extends Application {
	
	public static String DATA_PATH = System.getProperty("user.dir");
	public static String TABLE_NAME;
	
	private ComboBox<String> comboTableName;
	
	private HashMap<String, Algorithm> algos = new HashMap<String, Algorithm>();
	private Algorithm currentAlgo;
	
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
	
	/**
	 * Add your algos here
	 */
	private void defineAlgo() {
		algos.put("Incremental", new Incremental());
	}
	
	@Override
	public void init() throws Exception {
		defineAlgo();
		
		mainPane = new BorderPane();
		
		GridPane form = new GridPane();
		
		Command btnPane = createButtons();
		MenuBar menuBar = createMenuBar();
		
		Label lblPath = new Label("Out");
		
		TextField txtPath = new TextField();
		txtPath.setEditable(false);
		txtPath.setText(DATA_PATH);
		txtPath.setPrefWidth(300);
		
		Button btnPath = new Button("...");
		
		Label lblTable = new Label("Table name");
		
		ObservableList<String> optionsTable = Database.getTables();
		comboTableName = new ComboBox<String>(optionsTable);
		comboTableName.setPrefWidth(300);
		if(!optionsTable.isEmpty()) {
			comboTableName.getSelectionModel().select(optionsTable.get(0));
			TABLE_NAME = optionsTable.get(0);
		}
		
		Label lblAlgo = new Label("Algorithm");
		
		ObservableList<String> optionsAlgo = FXCollections.observableArrayList();
		for(Map.Entry<String, Algorithm> entry : algos.entrySet()) {
			optionsAlgo.add(entry.getKey());
		}
		
		ComboBox<String> comboAlgo = new ComboBox<String>(optionsAlgo);
		comboAlgo.setPrefWidth(300);
		if(!optionsAlgo.isEmpty()) {
			comboAlgo.getSelectionModel().select(optionsAlgo.get(0));
			currentAlgo = algos.get(optionsAlgo.get(0));
		}
		
		Label lblNbCluster = new Label("Nb. Clusters");
		
		TextField txtNbCluster = new TextField(Incremental.nbCluster + "");
		txtNbCluster.setAlignment(Pos.CENTER);
		txtNbCluster.setPrefWidth(300);
		
		ScrollPane consoleView = new ScrollPane();
		consoleView.setPrefHeight(1000);
		consoleView.setHbarPolicy(ScrollBarPolicy.NEVER);
		consoleView.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		consoleView.setFitToHeight(true);
		consoleView.setFitToWidth(true);
		
		Button clearConsole = new Button("Clear");
		
		consoleView.setContent(Console.getConsole());

		form.add(lblTable, 0, 0);
		form.add(comboTableName, 1, 0);
		
		form.add(lblAlgo, 0, 1);
		form.add(comboAlgo, 1, 1);
		
		form.add(lblPath, 0, 2);
		form.add(txtPath, 1, 2);
		form.add(btnPath, 2, 2);
		
		form.add(lblNbCluster, 0, 3);
		form.add(txtNbCluster, 1, 3);
		
		form.add(consoleView, 0, 4);
		form.add(clearConsole, 0, 5);
		
		GridPane.setColumnSpan(consoleView, 3);
		
		Button start = new Button("Start");
		start.setCursor(Cursor.HAND);
		
		form.setHgap(15);
		form.setVgap(10);
		
		clearConsole.setOnAction((event) -> {
			Console.getConsole().getChildren().clear();
			Console.getConsole().layout();
		});

		comboTableName.setOnAction((event) -> {
			TABLE_NAME = comboTableName.getSelectionModel().getSelectedItem();
		});
		
		comboAlgo.setOnAction(event -> {
			currentAlgo = algos.get(comboAlgo.getSelectionModel().getSelectedItem());
		});
		
		txtNbCluster.setOnKeyReleased((event) -> {
			Pattern patt = Pattern.compile("(\\d)+");
			Matcher match = patt.matcher(txtNbCluster.getText());
			
			if(match.find()) {
				Incremental.nbCluster = Integer.parseInt(txtNbCluster.getText());
			}
		});
		
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Choose the path for your data");
		btnPath.setOnAction((event) -> {
			dirChooser.setInitialDirectory(new File(txtPath.getText()));
			File directory = dirChooser.showDialog(new Stage());
			
			if(directory != null) {
				txtPath.setText(directory.getAbsolutePath());
				DATA_PATH = txtPath.getText();
			}
		});
		
		start.setOnAction((event) -> {
			try {
				currentAlgo = currentAlgo.reset();
				
				Console.getConsole().logConsole(
						"The algorithm has been executed on the table '" + TABLE_NAME 
						+ "', please wait until it's finished...\n\n", Console.PLAIN);
				
				currentAlgo.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		mainPane.setTop(menuBar);
		mainPane.setCenter(form);
		mainPane.setRight(btnPane);
		mainPane.setBottom(start);
		
		BorderPane.setAlignment(start, Pos.CENTER);
		BorderPane.setMargin(start, new Insets(15, 0, 15, 0));
		BorderPane.setMargin(form, new Insets(15, 0, 0, 15));
		BorderPane.setMargin(btnPane, new Insets(15, 15, 0, 15));
	}
	
	private MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();
		
		Menu conf = new Menu("Conf.");
		
		MenuItem confDB = new MenuItem("Database");
		confDB.setOnAction((event) -> {
			new ConfDB(this);
		});
		
		conf.getItems().add(confDB);
		
		Menu tools = new Menu("Tool");
		
		MenuItem createMatrix = new MenuItem("Create graph");
		createMatrix.setOnAction((event) -> {
			new MatrixCreator(this);
			
			refreshTable();
		});
		
		MenuItem editMatrix = new MenuItem("Edit graph");
		editMatrix.setOnAction((event) -> {
			new MatrixEditor(this);
			
			refreshTable();
		});
		
		tools.getItems().add(createMatrix);
		tools.getItems().add(editMatrix);
		
		menuBar.getMenus().add(conf);
		menuBar.getMenus().add(tools);
		
		return menuBar;
	}
	
	/**
	 * Buttons pane to the right
	 * @return
	 */
	private Command createButtons() {
		Command btnPane = new Command(15);
		
		btnPane.addButton("Generate nodes list (.csv)", (event) -> {
			currentAlgo.createNodesView();
		});
		
		btnPane.addButton("Generate edges list (.csv)", (event) -> {
			currentAlgo.createEdgesView();
		});
		
		btnPane.addButton("Show inter frame", (event) -> {
			for(String str : currentAlgo.getChanges()) {
				Console.getConsole().logConsole(str, Console.INFO);
			}
		});
		
		btnPane.addButton("Show modularity", (event) -> {
			for(String str : currentAlgo.getModularity()) {
				Console.getConsole().logConsole(str, Console.INFO);
			}
		});
		
		btnPane.addButton("Show modulatiry per clusters", (event) -> {
			for(String str : currentAlgo.getModularityCluster()) {
				Console.getConsole().logConsole(str, Console.INFO);
			}
		});
		
		btnPane.addButton("Save console", (event) -> {
			try {
				FileWriter writer = new FileWriter(App.DATA_PATH + "\\console.txt");
				
				for(Node text : Console.getConsole().getChildren()) {
					writer.append(((Text)text).getText().replace("\n", System.lineSeparator()));
				}
				
				Console.getConsole().logConsole(App.DATA_PATH + "\\console.txt" + " created!\n\n", Console.SUCCESS);

				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		btnPane.addButton("Generate .gefx", (event) -> {
			GraphGenerator gg = new GraphGenerator(currentAlgo.getMatrixNode(), currentAlgo.getMatrixEdge());
			gg.renderGraph();
		});
		
		btnPane.addButton("Show graph", (event) -> {
			new ClusterView(currentAlgo.getMatrixEdge(), currentAlgo.getMatrixNode(), currentAlgo.getTotalTime());
		});
		
		return btnPane;
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	public void refreshTable() {
		comboTableName.setItems(Database.getTables());
	}
}
