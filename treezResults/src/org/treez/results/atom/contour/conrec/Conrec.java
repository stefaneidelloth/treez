package org.treez.results.atom.contour.conrec;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2016, Stefan Eidelloth
 *
 * Modified by Stefan Eidelloth on May 2016 to resolve some warnings and to include
 * features from Conrec.js
 *
 * See below for the respective copyright notices.
/*

/**
 * Copyright (c) 2010, Jason Davies.
 *
 * All rights reserved.  This code is based on Bradley White's Java version,
 * which is in turn based on Nicholas Yue's C++ version, which in turn is based
 * on Paul D. Bourke's original Fortran version.  See below for the respective
 * copyright notices.
 *
 * See http://local.wasp.uwa.edu.au/~pbourke/papers/conrec/ for the original
 * paper by Paul D. Bourke.
 *
 * The vector conversion code is based on http://apptree.net/conrec.htm by
 * Graham Cox.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Conrec.java
 *
 * Created on 5 August 2001, 15:03
 *
 *
 * Copyright (c) 1996-1997 Nicholas Yue
 *
 * This software is copyrighted by Nicholas Yue. This code is base on the work of
 * Paul D. Bourke CONREC.F routine
 *
 * The authors hereby grant permission to use, copy, and distribute this
 * software and its documentation for any purpose, provided that existing
 * copyright notices are retained in all copies and that this notice is included
 * verbatim in any distributions. Additionally, the authors grant permission to
 * modify this software and its documentation for any purpose, provided that
 * such modifications are not distributed without the explicit consent of the
 * authors and that existing copyright notices are retained in all copies. Some
 * of the algorithms implemented by this software are patented, observe all
 * applicable patent law.
 *
 * IN NO EVENT SHALL THE AUTHORS OR DISTRIBUTORS BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE, ITS DOCUMENTATION, OR ANY DERIVATIVES THEREOF,
 * EVEN IF THE AUTHORS HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE AUTHORS AND DISTRIBUTORS SPECIFICALLY DISCLAIM ANY WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, AND NON-INFRINGEMENT.  THIS SOFTWARE IS PROVIDED ON AN
 * "AS IS" BASIS, AND THE AUTHORS AND DISTRIBUTORS HAVE NO OBLIGATION TO PROVIDE
 * MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */

/**
 * Conrec: a straightforward method of contouring some surface represented a regular triangular mesh. Ported from the
 * C++ code by Nicholas Yue (see above copyright notice). See http://paulbourke.net/papers/conrec for full description
 * of code and original C++ source.
 *
 * @author Bradley White
 * @version 1.0
 */
@SuppressWarnings("checkstyle:magicnumber")
public class Conrec {

	//#region ATTRIBUTES

	private double[] h = new double[5];

	private int[] sh = new int[5];

	private double[] xh = new double[5];

	private double[] yh = new double[5];

	private ContourRenderer contourRenderer;

	private Map<Integer, ContourBlueprint> contourBlueprints = new HashMap<>();

	//#end region

	//#region CONSTRUCTORS

	public Conrec(ContourRenderer contourRenderer) {
		if (contourRenderer == null) {
			createDefaultContourRenderer();
		} else {
			this.contourRenderer = contourRenderer;
		}
	}

	//#end region

	//#region METHODS

	private void createDefaultContourRenderer() {

		/**
		 * drawContour - interface for implementing the user supplied method to render the contours. Draws a line
		 * between the start and end coordinates.
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
		this.contourRenderer = new ContourRenderer() {

			@Override
			public void drawContourSegment(
					double startX,
					double startY,
					double endX,
					double endY,
					double contourLevel,
					int contourIndex) {

				boolean builderExists = contourBlueprints.containsKey(contourIndex);

				ContourBlueprint contourBuilder;
				if (!builderExists) {
					contourBuilder = new ContourBlueprint(contourLevel);
					contourBlueprints.put(contourIndex, contourBuilder);
				} else {
					contourBuilder = contourBlueprints.get(contourIndex);
				}

				Point startPoint = new Point(startX, startY);
				Point endPoint = new Point(endX, endY);
				contourBuilder.addSequence(startPoint, endPoint);
			}
		};

	}

	public List<Contour> getContours() {
		List<Contour> contourList = new ArrayList<>();

		for (int contourIndex : contourBlueprints.keySet()) {
			ContourBlueprint contourBlueprint = contourBlueprints.get(contourIndex);
			Sequence sequence = contourBlueprint.getSequence();
			while (sequence != null) {
				Contour contour = convertSequenceToContour(contourIndex, contourBlueprint, sequence);
				contourList.add(contour);
				sequence = sequence.nextSequence;
			}
		}
		sortContoursByContourIndex(contourList);
		return contourList;

	}

	private static
			Contour
			convertSequenceToContour(int contourIndex, ContourBlueprint contourBlueprint, Sequence sequence) {
		double contourLevel = contourBlueprint.getLevel();
		Contour contour = new Contour(contourLevel, contourIndex);
		Sequence headSequence = sequence.headSequence;
		while (headSequence != null && headSequence.point != null) {
			contour.add(headSequence.point);
			headSequence = headSequence.nextSequence;
		}
		return contour;
	}

	private static void sortContoursByContourIndex(List<Contour> contourList) {
		contourList.sort(new Comparator<Contour>() {

			@Override
			public int compare(Contour firstPolygon, Contour secondPolygon) {
				return firstPolygon.getIndex() - secondPolygon.getIndex();
			}
		});
	}

	/**
	 * contour is a contouring subroutine for rectangular spaced data It emits calls to a line drawing subroutine
	 * supplied by the user which draws a contour map corresponding to real*4data on a randomly spaced rectangular grid.
	 * The coordinates emitted are in the same units given in the x() and y() arrays. Any number of contour levels may
	 * be specified but they must be in order of increasing value.
	 *
	 * @param dataToContour
	 *            - matrix of data to contour
	 * @param ilb,
	 *            iub, jlb, jub - index bounds of data matrix The following two, one dimensional arrays (x and y)
	 *            contain the horizontal and vertical coordinates of each sample points.
	 * @param x
	 *            - data matrix column coordinates
	 * @param y
	 *            - data matrix row coordinates
	 * @param numberOfContourLevels
	 *            - number of contour levels
	 * @param contourLevelsInIncreasingOrder
	 *            - contour levels in increasing order.
	 */
	@SuppressWarnings({ "javadoc", "checkstyle:parameternumber" })
	public void contour(
			double[][] dataToContour,
			int iLowerBound,
			int iUpperBound,
			int jLowerBound,
			int jUpperBound,
			double[] x,
			double[] y,
			int numberOfContourLevels,
			double[] contourLevelsInIncreasingOrder) {

		// The indexing of im and jm should be noted as it has to start from zero
		// unlike the fortran counter part
		int[] im = { 0, 1, 1, 0 };
		int[] jm = { 0, 0, 1, 1 };

		// Note that castab is arranged differently from the FORTRAN code because
		// Fortran and C/C++ arrays are transposed of each other, in this case
		// it is more tricky as castab is in 3 dimension
		int[][][] castab = {
				{ { 0, 0, 8 }, { 0, 2, 5 }, { 7, 6, 9 } },
				{ { 0, 3, 4 }, { 1, 3, 1 }, { 4, 3, 0 } },
				{ { 9, 6, 7 }, { 5, 2, 0 }, { 8, 0, 0 } } };

		for (int j = (jUpperBound - 1); j >= jLowerBound; j--) {

			for (int i = iLowerBound; i <= iUpperBound - 1; i++) {
				processMatrixElement(dataToContour, x, y, numberOfContourLevels, contourLevelsInIncreasingOrder, im, jm,
						castab, j, i);
			}
		}

	}

	@SuppressWarnings({
			"checkstyle:parameternumber",
			"checkstyle:javancss",
			"checkstyle:cyclomaticcomplexity",
			"checkstyle:executablestatementcount" })
	private void processMatrixElement(
			double[][] dataToContour,
			double[] x,
			double[] y,
			int numberOfContourLevels,
			double[] contourLevelsInIncreasingOrder,
			int[] im,
			int[] jm,
			int[][][] castab,
			int j,
			int i) {

		double temp1;
		double temp2;
		double dmin;
		double dmax;
		temp1 = Math.min(dataToContour[i][j], dataToContour[i][j + 1]);
		temp2 = Math.min(dataToContour[i + 1][j], dataToContour[i + 1][j + 1]);
		dmin = Math.min(temp1, temp2);
		temp1 = Math.max(dataToContour[i][j], dataToContour[i][j + 1]);
		temp2 = Math.max(dataToContour[i + 1][j], dataToContour[i + 1][j + 1]);
		dmax = Math.max(temp1, temp2);

		if (dmax >= contourLevelsInIncreasingOrder[0]
				&& dmin <= contourLevelsInIncreasingOrder[numberOfContourLevels - 1]) {
			for (int contourIndex = 0; contourIndex < numberOfContourLevels; contourIndex++) {

				if (contourLevelsInIncreasingOrder[contourIndex] >= dmin
						&& contourLevelsInIncreasingOrder[contourIndex] <= dmax) {

					for (int m = 4; m >= 0; m--) {
						if (m > 0) {
							// The indexing of im and jm should be noted as it has to
							// start from zero
							h[m] = dataToContour[i + im[m - 1]][j + jm[m - 1]]
									- contourLevelsInIncreasingOrder[contourIndex];
							xh[m] = x[i + im[m - 1]];
							yh[m] = y[j + jm[m - 1]];
						} else {
							h[0] = 0.25 * (h[1] + h[2] + h[3] + h[4]);
							xh[0] = 0.5 * (x[i] + x[i + 1]);
							yh[0] = 0.5 * (y[j] + y[j + 1]);
						}
						if (h[m] > 0.0) {
							sh[m] = 1;
						} else if (h[m] < 0.0) {
							sh[m] = -1;
						} else {
							sh[m] = 0;
						}
					}

					//
					// Note: at this stage the relative heights of the corners and the
					// centre are in the h array, and the corresponding coordinates are
					// in the xh and yh arrays. The centre of the box is indexed by 0
					// and the 4 corners by 1 to 4 as shown below.
					// Each triangle is then indexed by the parameter m, and the 3
					// vertices of each triangle are indexed by parameters m1,m2,and
					// m3.
					// It is assumed that the centre of the box is always vertex 2
					// though this isimportant only when all 3 vertices lie exactly on
					// the same contour level, in which case only the side of the box
					// is drawn.
					//
					//
					//      vertex 4 +-------------------+ vertex 3
					//               | \               / |
					//               |   \    m-3    /   |
					//               |     \       /     |
					//               |       \   /       |
					//               |  m=2    X   m=2   |       the centre is vertex 0
					//               |       /   \       |
					//               |     /       \     |
					//               |   /    m=1    \   |
					//               | /               \ |
					//      vertex 1 +-------------------+ vertex 2
					//
					//
					//
					//               Scan each triangle in the box
					//
					for (int m = 1; m <= 4; m++) {

						int m1;
						int m2;
						int m3;

						m1 = m;
						m2 = 0;
						if (m != 4) {
							m3 = m + 1;
						} else {
							m3 = 1;
						}

						int caseValue = castab[sh[m1] + 1][sh[m2] + 1][sh[m3] + 1];
						if (caseValue != 0) {
							calculateAndDrawSegment(contourLevelsInIncreasingOrder, contourIndex, m1, m2, m3,
									caseValue);

						}
					}
				}
			}
		}
	}

	@SuppressWarnings({
			"checkstyle:javancss",
			"checkstyle:cyclomaticcomplexity",
			"checkstyle:executablestatementcount" })
	private void calculateAndDrawSegment(
			double[] contourLevelsInIncreasingOrder,
			int contourIndex,
			int m1,
			int m2,
			int m3,
			int caseValue) {

		double x1 = 0.0;
		double x2 = 0.0;
		double y1 = 0.0;
		double y2 = 0.0;

		switch (caseValue) {
		case 1: // Line between vertices 1 and 2
			x1 = xh[m1];
			y1 = yh[m1];
			x2 = xh[m2];
			y2 = yh[m2];
			break;
		case 2: // Line between vertices 2 and 3
			x1 = xh[m2];
			y1 = yh[m2];
			x2 = xh[m3];
			y2 = yh[m3];
			break;
		case 3: // Line between vertices 3 and 1
			x1 = xh[m3];
			y1 = yh[m3];
			x2 = xh[m1];
			y2 = yh[m1];
			break;
		case 4: // Line between vertex 1 and side 2-3
			x1 = xh[m1];
			y1 = yh[m1];
			x2 = xsect(m2, m3);
			y2 = ysect(m2, m3);
			break;
		case 5: // Line between vertex 2 and side 3-1
			x1 = xh[m2];
			y1 = yh[m2];
			x2 = xsect(m3, m1);
			y2 = ysect(m3, m1);
			break;
		case 6: //  Line between vertex 3 and side 1-2
			x1 = xh[m3];
			y1 = yh[m3];
			x2 = xsect(m1, m2);
			y2 = ysect(m1, m2);
			break;
		case 7: // Line between sides 1-2 and 2-3
			x1 = xsect(m1, m2);
			y1 = ysect(m1, m2);
			x2 = xsect(m2, m3);
			y2 = ysect(m2, m3);
			break;
		case 8: // Line between sides 2-3 and 3-1
			x1 = xsect(m2, m3);
			y1 = ysect(m2, m3);
			x2 = xsect(m3, m1);
			y2 = ysect(m3, m1);
			break;
		case 9: // Line between sides 3-1 and 1-2
			x1 = xsect(m3, m1);
			y1 = ysect(m3, m1);
			x2 = xsect(m1, m2);
			y2 = ysect(m1, m2);
			break;
		default:
			break;
		}
		contourRenderer.drawContourSegment(x1, y1, x2, y2, contourLevelsInIncreasingOrder[contourIndex], contourIndex);
	}

	public void drawContourSegment(
			double startX,
			double startY,
			double endX,
			double endY,
			double contourLevel,
			int contourIndex) {
		contourRenderer.drawContourSegment(startX, startY, endX, endY, contourLevel, contourIndex);
	}

	private double xsect(int p1, int p2) {
		return (h[p2] * xh[p1] - h[p1] * xh[p2]) / (h[p2] - h[p1]);
	}

	private double ysect(int p1, int p2) {
		return (h[p2] * yh[p1] - h[p1] * yh[p2]) / (h[p2] - h[p1]);
	}

	//#end region

}
