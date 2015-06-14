package tool;

import core.App;
import core.Database;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class MatrixCreator extends MatrixTool {
	
	public MatrixCreator(App app) {
		super(app);
		
		init();
	}
	
	private void init() {
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
		
		matrixPane.getChildren().add(timePane);
		matrixPane.getChildren().add(matrix);
		matrixPane.getChildren().add(btnPane);
		
	}

}
