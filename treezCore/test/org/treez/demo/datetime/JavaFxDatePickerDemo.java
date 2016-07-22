package org.treez.demo.datetime;

import java.util.Locale;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JavaFxDatePickerDemo extends Application {

	private Stage stage;

	private DatePicker checkInDatePicker;

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		stage.setTitle("DatePickerSample ");
		initUI();
		stage.show();
	}

	private void initUI() {
		VBox vBox = new VBox();
		Scene s = new Scene(new ScrollPane(vBox), 600, 400);
		DateTimePicker d = new DateTimePicker();

		// Date only
		d.valueProperty().addListener(t -> System.out.println(t));

		// Time only
		d.timeValueProperty().addListener(t -> System.out.println(t));

		// DateAndTime
		d.dateTimeValueProperty().addListener(t -> System.out.println(t));

		vBox.getChildren().add(d);

		stage.setScene(s);
		stage.show();
	}
}
