package org.treez.results.javafxchart;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class LineChartExample extends Application {

	private String indentation;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("Line Chart Sample");

		/*
		 *
		//defining the axes
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Number of Month");
		//creating the chart
		final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		
		lineChart.setTitle("Stock Monitoring, 2010");
		//defining a series
		XYChart.Series series = new XYChart.Series();
		series.setName("My portfolio");
		//populating the series with data
		series.getData().add(new XYChart.Data(1, 23));
		series.getData().add(new XYChart.Data(12, 44));
		lineChart.getData().add(series);
		
		Parent node = lineChart;

		*/

		StackPane node = new StackPane();

		node.setId("node");
		node.setStyle("-fx-background-color: white;");

		StackPane group = new StackPane();
		group.setId("Region");
		group.setId("group");
		group.setMaxWidth(300);
		group.setMaxHeight(300);
		node.getChildren().add(group);

		String style = "-fx-border-color: #FF0001 transparent #FF0003 transparent;" + "-fx-background-color:#00FF00;"
				+ "-fx-background-radius: 50px;" + "-fx-border-radius: 200px;";
		group.setStyle(style);

		List<Node> groupChildren = group.getChildren();

		Rectangle smallRectangle = new Rectangle(10, 10);
		smallRectangle.setId("smallrectangle");
		smallRectangle.setFill(Color.WHITE);

		groupChildren.add(smallRectangle);

		//Line line = new Line(0, 0, 50, 50);
		//groupChildren.add(line);

		Label label = new Label("hello");
		groupChildren.add(label);

		Scene scene = new Scene(node, 800, 600);
		stage.setScene(scene);
		stage.show();

		String svgString = JavaFxNodeToSvgConverter.nodeToSvg(node);

		System.out.println(svgString);

		//save svg string as svg file
		File file = new File("C:\\svgoutput.svg");

		try {
			FileUtils.writeStringToFile(file, svgString);
		} catch (IOException e) {

		}

	}

}
