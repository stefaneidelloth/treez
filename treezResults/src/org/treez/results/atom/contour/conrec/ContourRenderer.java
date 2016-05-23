package org.treez.results.atom.contour.conrec;

/**
 * A class implements the Render interface and drawContour method to draw contours.
 *
 * @author Bradley White
 * @version 1.0
 */
public interface ContourRenderer {

	/**
	 * drawContour - interface for implementing the user supplied method to render the contours. Draws a line between
	 * the start and end coordinates.
	 *
	 * @param startX
	 *            - start coordinate for X
	 * @param startY
	 *            - start coordinate for Y
	 * @param endX
	 *            - end coordinate for X
	 * @param endY
	 *            - end coordinate for Y
	 * @param contourLevel
	 *            - Contour level for line.
	 */
	void drawContourSegment(double startX, double startY, double endX, double endY, double contourLevel, int contourIndex);

}
