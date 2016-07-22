package org.treez.demo.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Skin;
import javafx.util.StringConverter;

public class DateTimePicker extends DatePicker {

	private ObjectProperty<LocalTime> timeValue = new SimpleObjectProperty<>();

	private ObjectProperty<ZonedDateTime> dateTimeValue;

	public DateTimePicker() {
		super();
		setValue(LocalDate.now());
		setTimeValue(LocalTime.now());
		setConverter(new StringConverter<LocalDate>() {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HH:mm:ssZ");

			@Override
			public String toString(LocalDate object) {
				return dateTimeValue.get().format(formatter);
			}

			@Override
			public LocalDate fromString(String string) {
				return LocalDate.parse(string, formatter);
			}
		});
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DateTimePickerSkin(this);
	}

	public LocalTime getTimeValue() {
		return timeValue.get();
	}

	void setTimeValue(LocalTime timeValue) {
		this.timeValue.set(timeValue);
	}

	public ObjectProperty<LocalTime> timeValueProperty() {
		return timeValue;
	}

	public ZonedDateTime getDateTimeValue() {
		return dateTimeValueProperty().get();
	}

	public void setDateTimeValue(ZonedDateTime dateTimeValue) {
		dateTimeValueProperty().set(dateTimeValue);
	}

	public ObjectProperty<ZonedDateTime> dateTimeValueProperty() {
		if (dateTimeValue == null) {
			dateTimeValue = new SimpleObjectProperty<>(
					ZonedDateTime.of(LocalDateTime.of(this.getValue(), timeValue.get()), ZoneId.systemDefault()));
			timeValue.addListener(t -> {
				dateTimeValue.set(
						ZonedDateTime.of(LocalDateTime.of(this.getValue(), timeValue.get()), ZoneId.systemDefault()));
			});

			valueProperty().addListener(t -> {
				dateTimeValue.set(
						ZonedDateTime.of(LocalDateTime.of(this.getValue(), timeValue.get()), ZoneId.systemDefault()));
			});
		}
		return dateTimeValue;
	}

}
