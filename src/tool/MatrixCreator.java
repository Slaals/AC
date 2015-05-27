package tool;

import core.App;
import core.Database;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MatrixCreator extends Stage {
	
	private int nbNodes;
	private TextField[][][] inputs;
	
	private App app;
	
	private GridPane content;

	public MatrixCreator(String nbNodes, App app) {
		super();
		
		this.nbNodes = Integer.valueOf(nbNodes);
		this.app = app;
		
		inputs = new TextField[this.nbNodes][this.nbNodes][1];
		
		initStyle(StageStyle.UTILITY);
		
		content = new GridPane();
		content.setAlignment(Pos.CENTER);
		content.setHgap(15);
		content.setVgap(15);
		
		init();
		
		Scene scene = new Scene(content);
		
		setTitle("Matrix Creator");
		setMinHeight(280);
		setMinWidth(500);
		
		setResizable(false);
		
		initModality(Modality.APPLICATION_MODAL);
		
		setScene(scene);
		show();
	}
	
	private void labelize() {
		for(int i = 1; i <= nbNodes; i++) {
			Label lbl = new Label(String.valueOf(i));
			content.add(new Label(String.valueOf(i)), 0, i);
			content.add(lbl, i, 0);
			
			GridPane.setHalignment(lbl, HPos.CENTER);
		}
	}
	
	private void generateGraph(String tableName) {
		Database.createTableGraph(tableName);
		
		for(int row = 1; row <= nbNodes; row++) {
			for(int col = row; col <= nbNodes; col++) {
				int celVal = Integer.valueOf(inputs[row - 1][col - 1][0].getText());
				if(celVal != 0) {
					Database.feedTable(tableName, row, col);
				}
			}
		}
	}
	
	private void init() {
		labelize();
		for(int row = 1; row <= nbNodes; row++) {
			createRow(row);
		}
		
		TextField tableName = new TextField("Tablename");
		tableName.setAlignment(Pos.CENTER);
		
		FlowPane btnPane = new FlowPane(Orientation.HORIZONTAL);
		btnPane.setHgap(15);
		Button btnGenerate = new Button("Generate");
		Button btnCancel = new Button("Cancel");
		btnGenerate.setCursor(Cursor.HAND);
		btnCancel.setCursor(Cursor.HAND);
		
		btnPane.setAlignment(Pos.CENTER);
		btnPane.setPadding(new Insets(15));
		
		btnPane.getChildren().add(tableName);
		btnPane.getChildren().add(btnGenerate);
		btnPane.getChildren().add(btnCancel);
		
		btnGenerate.setOnAction((event) -> {
			generateGraph(tableName.getText());
			
			app.refreshTable();
			
			close();
		});
		
		btnCancel.setOnAction((event) -> {
			close();
		});
		
		content.add(btnPane, 0, nbNodes + 1);
		
		GridPane.setColumnSpan(btnPane, nbNodes + 1);
	}
	
	private void createRow(int row) {
		for(int col = row; col <= nbNodes; col++) {
			TextField txtField = new TextField("0");
			txtField.setPrefWidth(25);
			txtField.setMaxWidth(25);
			
			inputs[row - 1][col - 1][0] = txtField;
			
			content.add(txtField, row, col);
		}
	}
}
