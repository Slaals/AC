package tool;

import view.App;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class MatrixCreator extends MatrixTool {
	
	public MatrixCreator(App app) {
		super(app);
		
		init();
	}
	
	private void init() {
		ObservableList<String> options = Database.getTables();
		ComboBox<String> comboTableName = new ComboBox<String>(options);
		comboTableName.setPrefWidth(250);
		if(!options.isEmpty()) {
			comboTableName.getSelectionModel().select(options.get(0));
		}
		
		FlowPane tablePane = new FlowPane(Orientation.HORIZONTAL);
		tablePane.setHgap(10);
		
		tablePane.getChildren().add(new Label("Clone : "));
		tablePane.getChildren().add(comboTableName);
		
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
		btnPane.getChildren().add(new Label("Split : "));
		btnPane.getChildren().add(split);
		btnPane.getChildren().add(btnGenerate);
		btnPane.getChildren().add(btnCancel);
		
		comboTableName.setOnAction((event) -> {
			App.TABLE_NAME = options.get(comboTableName.getSelectionModel().getSelectedIndex());
			tableName.setText(App.TABLE_NAME + "_clone");
			
			int totalTime = Database.getTotalTime(App.TABLE_NAME);
			
			refreshTime();
			
			generateMatrixFromEdgesList(Database.getMatrix(), totalTime);
			
			for(int i = 1; i <= totalTime; i++) {
				generateTimeButtons();
			}
			
			matrix.setText(getMatrixStr(currentTime));
			showGraph();
		});
		
		btnGenerate.setOnAction((event) -> {
			Database.createTableGraph(tableName.getText());
			
			generateMatrixFromString();
			saveGraph(tableName.getText());
			
			app.refreshTable();
			
			close();
		});
		
		btnCancel.setOnAction((event) -> {
			close();
		});
		
		generateMatrixFromString();
		generateTimeButtons();
		
		matrixPane.getChildren().add(tablePane);
		matrixPane.getChildren().add(timePane);
		matrixPane.getChildren().add(matrix);
		matrixPane.getChildren().add(btnPane);
		
	}

}
