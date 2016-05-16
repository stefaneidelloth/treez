package org.treez.core.color;

import java.util.ArrayList;
import java.util.List;

public enum ColorValue {

	//#region VALUES

	WHITE("white", "#ffffff"),

	BLACK("black", "#000000"),

	RED("red", "#ff0000"),

	GREEN("green", "#008000"),

	BLUE("blue", "#0000ff"),

	CYAN("cyan", "#00ffff"),

	MAGENTA("magenta", "#ff00ff"),

	YELLOW("yellow", "#ffff00"),

	GREY("grey", "#808080"),

	DARKRED("darkred", "#8b0000"),

	DARKGREEN("darkgreen", "#006400"),

	DARKBLUE("darkblue", "#00008b"),

	DARKCYAN("darkcyan", "#008b8b"),

	DARKMAGENTA("darkmagenta", "#8b008b");

	//#end region

	//#region ATTRIBUTES

	private String colorName;

	private String hexCode;

	//#end region

	//#region CONSTRUCTORS

	ColorValue(String colorName, String hexCode) {
		this.colorName = colorName;
		this.hexCode = hexCode;
	}

	//#end region

	//#region ACCESSORS

	@Override
	public String toString() {
		return colorName;
	}

	public String getHexCode() {
		return hexCode;
	}

	public static String getHexCode(String colorText) {
		ColorValue[] allValues = ColorValue.values();
		for (ColorValue color : allValues) {
			boolean isWantedColor = color.toString().equals(colorText);
			if (isWantedColor) {
				return color.getHexCode();
			}
		}
		throw new IllegalArgumentException(
				"The color text '" + colorText + "' is not know. ");
	}

	/**
	 * Returns a set of all colors as strings
	 *
	 * @return
	 */
	public static List<String> getAllStringValues() {
		List<String> allStringValues = new ArrayList<>();

		ColorValue[] allValues = ColorValue.values();
		for (ColorValue color : allValues) {
			allStringValues.add(color.toString());
		}
		return allStringValues;
	}

	/**
	 * Returns a set of all hex codes as strings
	 *
	 * @return
	 */
	public static List<String> getAllHexCodes() {
		List<String> allHexCodes = new ArrayList<>();

		ColorValue[] allValues = ColorValue.values();
		for (ColorValue colorValue : allValues) {
			allHexCodes.add(colorValue.getHexCode());
		}
		return allHexCodes;
	}

	//#end region
}
