package tool;

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
import core.App;
import core.Database;

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
		
		Label lblTime = new Label("Time : ");
		TextField txtTime = new TextField("1");
		txtTime.setMaxWidth(25);
		
		Button btnRefresh = new Button("Refresh");
		
		tablePane.getChildren().add(comboTableName);
		
		// At the moment only gen static graph
		/*tablePane.getChildren().add(lblTime);
		tablePane.getChildren().add(txtTime);
		tablePane.getChildren().add(btnRefresh);*/
		
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
		btnPane.getChildren().add(txtSplit);
		btnPane.getChildren().add(btnSave);
		btnPane.getChildren().add(btnCancel);
		
		comboTableName.setOnAction((event) -> {
			App.TABLE_NAME = options.get(comboTableName.getSelectionModel().getSelectedIndex());
			showMatrix(Database.getMatrixAtTime(txtTime.getText()));
		});
		
		btnSave.setOnAction((event) -> {
			String tableName = options.get(comboTableName.getSelectionModel().getSelectedIndex());
			
			if(tableName.isEmpty()) {
				App.logConsole("No table specified!", App.WARNING);
			} else {
				Database.refreshTable(tableName);
				generateGraph(tableName, txtSplit.getText());
			}
			
			close();
		});
		
		btnCancel.setOnAction((event) -> {
			close();
		});
		
		content.getChildren().add(tablePane);
		content.getChildren().add(matrix);
		content.getChildren().add(btnPane);
		
	}
}
