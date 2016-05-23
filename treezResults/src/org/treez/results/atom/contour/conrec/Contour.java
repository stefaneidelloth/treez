package org.treez.results.atom.contour.conrec;

import java.util.ArrayList;

public class Contour extends ArrayList<Point> {

	private double level;

	private int index;

	//#region ATTRIBUTES

	private static final long serialVersionUID = 7363735007576579828L;

	//#end region

	//#region CONSTRUCTORS

	public Contour(double level, int contourIndex) {
		this.level = level;
		this.index = contourIndex;
	}

	//#end region

	//#region ACCESSORS

	public double getLevel() {
		return level;
	}

	public int getIndex() {
		return index;
	}

	//#end region

}
