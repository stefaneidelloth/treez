package org.treez.demo.datetime;

import javafx.scene.Node;
import javafx.scene.control.skin.DatePickerSkin;

public class DateTimePickerSkin extends DatePickerSkin {

	private DateTimePicker datePicker;

	//private DatePickerContent ret;

	public DateTimePickerSkin(DateTimePicker datePicker) {
		super(datePicker);
		this.datePicker = datePicker;
	}

	@Override
	public Node getPopupContent() {
		/*
		if (ret == null) {
			ret = (DatePickerContent) super.getPopupContent();
		
			Slider hours = new Slider(
					0,
					23,
					(datePicker.getTimeValue() != null ? datePicker.getTimeValue().getMinute() : 0));
			Label hoursValue = new Label(
					"Hours: " + (datePicker.getTimeValue() != null ? datePicker.getTimeValue().getHour() : "") + " ");
		
			Slider minutes = new Slider(
					0,
					59,
					(datePicker.getTimeValue() != null ? datePicker.getTimeValue().getMinute() : 0));
			Label minutesValue = new Label(
					"Minutes: " + (datePicker.getTimeValue() != null ? datePicker.getTimeValue().getMinute() : "")
							+ " ");
		
			Slider seconds = new Slider(
					0,
					59,
					(datePicker.getTimeValue() != null ? datePicker.getTimeValue().getSecond() : 0));
			Label secondsValue = new Label(
					"Seconds: " + (datePicker.getTimeValue() != null ? datePicker.getTimeValue().getSecond() : "")
							+ " ");
		
			ret.getChildren().addAll(new HBox(hoursValue, hours), new HBox(minutesValue, minutes),
					new HBox(secondsValue, seconds));
		
			hours.valueProperty().addListener((observable, oldValue, newValue) -> {
				datePicker.setTimeValue(datePicker.getTimeValue().withHour(newValue.intValue()));
				hoursValue.setText("Hours: " + String.format("%02d", datePicker.getTimeValue().getHour()) + " ");
			});
		
			minutes.valueProperty().addListener((observable, oldValue, newValue) -> {
				datePicker.setTimeValue(datePicker.getTimeValue().withMinute(newValue.intValue()));
				minutesValue.setText("Minutes: " + String.format("%02d", datePicker.getTimeValue().getMinute()) + " ");
			});
		
			seconds.valueProperty().addListener((observable, oldValue, newValue) -> {
				datePicker.setTimeValue(datePicker.getTimeValue().withSecond(newValue.intValue()));
				secondsValue.setText("Seconds: " + String.format("%02d", datePicker.getTimeValue().getSecond()) + " ");
			});
		
		}
		return ret;
		*/
		return null;
	}

}
