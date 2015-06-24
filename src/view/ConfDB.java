package view;

import tool.Database;
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

public class ConfDB extends Stage {

	private GridPane content;
	
	private App app;

	public ConfDB(App app) {
		super();
		this.app = app;
		initStyle(StageStyle.UTILITY);
		
		content = new GridPane();
		content.setAlignment(Pos.CENTER);
		content.setHgap(15);
		content.setVgap(15);
		
		init();
		
		Scene scene = new Scene(content);
		
		setTitle("Conf. DB");
		setMinHeight(280);
		setMinWidth(500);
		
		setResizable(false);
		
		initModality(Modality.APPLICATION_MODAL);
		
		setScene(scene);
		show();
	}
	
	private void init() {
		Label lblAddr = new Label("Address");
		Label lblPort = new Label("Port");
		Label lblDb = new Label("Database");
		Label lblUsern = new Label("Username");
		Label lblPasswd = new Label("Password");
		
		TextField txtAddr = new TextField(Database.address);
		TextField txtPort = new TextField(Database.port);
		TextField txtDb = new TextField(Database.db);
		TextField txtUsern = new TextField(Database.userName);
		TextField txtPasswd = new TextField();
		
		if(Database.passwd == null) {
			txtPasswd.setText("");
		} else {
			txtPasswd.setText(Database.passwd);
		}
		
		FlowPane btnPane = new FlowPane(Orientation.HORIZONTAL);
		btnPane.setHgap(15);
		Button btnSave = new Button("Save");
		Button btnCancel = new Button("Cancel");
		btnSave.setCursor(Cursor.HAND);
		btnCancel.setCursor(Cursor.HAND);
		
		btnSave.setOnAction((event) -> {
			Database.address = txtAddr.getText();
			Database.port = txtPort.getText();
			Database.db = txtDb.getText();
			Database.userName = txtUsern.getText();
			if(txtPasswd.getText().isEmpty()) {
				Database.passwd = null;
			}
			
			Database.checkDatabase(Database.db);
			
			app.refreshTable();
			
			close();
		});
		
		btnCancel.setOnAction((event) -> {
			close();
		});
		
		btnPane.setAlignment(Pos.CENTER);
		
		btnPane.getChildren().add(btnSave);
		btnPane.getChildren().add(btnCancel);
		
		content.add(lblAddr, 0, 0);
		content.add(txtAddr, 1, 0);
		content.add(lblPort, 0, 1);
		content.add(txtPort, 1, 1);
		content.add(lblDb, 0, 2);
		content.add(txtDb, 1, 2);
		content.add(lblUsern, 0, 3);
		content.add(txtUsern, 1, 3);
		content.add(lblPasswd, 0, 4);
		content.add(txtPasswd, 1, 4);
		
		content.add(btnPane, 0, 5);
		GridPane.setColumnSpan(btnPane, 2);
	}
}
