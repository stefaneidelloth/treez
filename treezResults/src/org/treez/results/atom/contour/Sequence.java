package org.treez.results.atom.contour;

import org.treez.results.atom.contour.conrec.Point;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Sequence {

	//#region ATTRIBUTES

	public Point point = null;

	public Sequence nextSequence = null;

	public Sequence previousSequence = null;

	public Sequence headSequence = null;

	public Sequence tailSequence = null;

	public boolean isClosed = false;

	//#end region

	//#region CONSTRUCTORS

	public Sequence(Point point) {
		this.point = point;
	}

	public Sequence(Point point, Sequence nextSequence) {
		this.point = point;
		this.nextSequence = nextSequence;
	}

	//#end region

	//#end region

}
