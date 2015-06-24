package view.feature;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Console extends TextFlow {
	
	private static Console instance = null;
	
	public static int WARNING = 0;
	public static int INFO = 1;
	public static int PLAIN = 2;
	public static int SUCCESS = 3;
	
	private Console() {
		setStyle("-fx-background-color: #272823;"
				+ "-fx-padding: 6px;");
	}
	
	public static Console getConsole() {
		if(instance == null) {
			instance = new Console();
		} 
		
		return instance;
	}
	
	public void logConsole(String str, int type) {
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
			getChildren().add(txt);
			layout();
		});
	}
}
