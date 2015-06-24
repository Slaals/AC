package view.feature;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class Command extends VBox {
	
	public Command(int gap) {
		super(gap);
		
		setStyle("-fx-color: #B8BDB9;"
				+ "-fx-background-color: #D1D6D2;"
				+ "-fx-padding: 15px");
	}
	
	private Button createButton(String name, EventHandler<ActionEvent> event) {
		Button btn = new Button(name);
		btn.setCursor(Cursor.HAND);
		btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		btn.setOnAction(event);
		
		return btn;
	}
	
	public void addButton(String name, EventHandler<ActionEvent> event) {
		getChildren().add(createButton(name, event));
	}
	
}
