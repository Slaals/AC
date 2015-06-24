package tool;

import view.App;
import view.feature.Console;
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

public class MatrixEditor extends MatrixTool {
	
	public MatrixEditor(App app) {
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
		
		tablePane.getChildren().add(comboTableName);
		
		FlowPane btnPane = new FlowPane(Orientation.HORIZONTAL);
		btnPane.setHgap(15);
		TextField txtSplit = new TextField();
		txtSplit.setMaxWidth(40);
		Button btnSave = new Button("Save");
		Button btnCancel = new Button("Cancel");
		btnSave.setCursor(Cursor.HAND);
		btnCancel.setCursor(Cursor.HAND);
		
		btnPane.setAlignment(Pos.CENTER);
		btnPane.setPadding(new Insets(15));

		btnPane.getChildren().add(new Label("Split : "));
		btnPane.getChildren().add(split);
		btnPane.getChildren().add(btnSave);
		btnPane.getChildren().add(btnCancel);
		
		comboTableName.setOnAction((event) -> {
			App.TABLE_NAME = options.get(comboTableName.getSelectionModel().getSelectedIndex());
			int totalTime = Database.getTotalTime(App.TABLE_NAME);
			
			refreshTime();
			
			generateMatrixFromEdgesList(Database.getMatrix(), totalTime);
			
			for(int i = 1; i <= totalTime; i++) {
				generateTimeButtons();
			}
			
			matrix.setText(getMatrixStr(currentTime));
			showGraph();
		});
		
		btnSave.setOnAction((event) -> {
			String tableName = options.get(comboTableName.getSelectionModel().getSelectedIndex());
			
			generateMatrixFromString();
			
			if(tableName.isEmpty()) {
				Console.getConsole().logConsole("No table specified!", Console.WARNING);
			} else {
				Database.refreshTable(tableName);
				saveGraph(tableName);
			}
			
			close();
		});
		
		btnCancel.setOnAction((event) -> {
			close();
		});
		
		matrixPane.getChildren().add(tablePane);
		matrixPane.getChildren().add(timePane);
		matrixPane.getChildren().add(matrix);
		matrixPane.getChildren().add(btnPane);
		
	}
}
