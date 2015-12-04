package org.treez.results.javafxchart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.log4j.Logger;

import javafx.geometry.Bounds;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

/**
 * Converts a Region to SVG code
 */
public class RegionToSvgConverter extends AbstractNodeToSvgConverter<Region> {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(RegionToSvgConverter.class);

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	//#end region

	//#region METHODS

	/**
	 * Converts a Region to SVG code. The type hierarchy of Region is as follows:
	 *
	 * <pre>
	 *   *** Region (x)
	 *       **** Control
	 *            ***** Label (x)
	 *            ***** ...
	 *       **** ...
	 *
	 *  (The special case of Control should already have been handled.)
	 * </pre>
	 */

	@Override
	public String extendCode(String initialSvgString, Region region) {

		addDataFromRegion(region);
		String svgString = initialSvgString + createSvgString();
		svgString = extendWithChildSvgCodeAndEndTag(svgString, region);

		return svgString;
	}

	/**
	 * Extracts SVG properties from the given Region and applies them.
	 *
	 * @param nodeProperties
	 * @param node
	 */
	private void addDataFromRegion(Region region) {

		//x & y
		Bounds bounds = region.getBoundsInParent();

		Double translateX = region.getTranslateX();
		Double x = bounds.getMinX();
		svgNodeProperties.setX(x);

		Double translateY = region.getTranslateY();
		Double y = bounds.getMinY();
		svgNodeProperties.setY(y);

		/*
		Insets paddingInsets = region.getPadding();
		double leftPadding = paddingInsets.getLeft();
		double topPadding = paddingInsets.getTop();
		
		if (leftPadding != 0) {
		 System.out.println("" + leftPadding);
		 x += leftPadding;
		}
		*/

		//isDefinedByRect
		boolean isDefinedByRect = true;
		Shape shape = region.getShape();
		if (shape != null) {
			isDefinedByRect = false;
		}
		svgNodeProperties.setIsDefinedByRect(isDefinedByRect);

		//geometry
		if (isDefinedByRect) {
			//retrieve geometry directly from region

			//width
			String rectWidth = "" + region.getWidth();
			svgNodeProperties.setRectWidth(rectWidth);

			//height
			String rectHeight = "" + region.getHeight();
			svgNodeProperties.setRectHeight(rectHeight);
		} else {
			//retrieve geometry from shape
			svgNodeProperties = ShapeToSvgConverter.addDataFromShape(svgNodeProperties, shape);
		}

		//fill
		addFillDataFromRegion(region);

		//stroke
		addStrokeDataFromRegion(region);

	}

	private void addFillDataFromRegion(Region region) {
		//fill color
		Background backGround = region.getBackground();
		if (backGround != null) {
			String fillColor = backgroundToColorString(backGround);
			svgNodeProperties.setFill(fillColor);
		}

		//fill radius
		if (backGround != null) {
			List<Double> fillRadiuses = backgroundToFillRadiuses(backGround);
			svgNodeProperties.setFillRadius(fillRadiuses);
		}
	}

	private void addStrokeDataFromRegion(Region region) {
		//stroke colors
		Border border = region.getBorder();
		if (border != null) {
			List<String> strokeColors = borderToColorStrings(border);
			svgNodeProperties.setStroke(strokeColors);
		}

		//stroke radiuses
		if (border != null) {
			List<Double> strokeRadiuses = borderToStrokeRadii(border);
			svgNodeProperties.setStrokeRadius(strokeRadiuses);
		}

		//stroke widths
		if (border != null) {
			List<Double> strokeWidths = borderToStrokeWidths(border);
			svgNodeProperties.setStrokeWidth(strokeWidths);
		}

		//stroke opacities
		if (border != null) {
			List<Double> strokeOpacities = borderToStrokeOpacites(border);
			svgNodeProperties.setStrokeOpacities(strokeOpacities);
		}
	}

	@Override
	protected String createStyleContentString() {

		String styleContent = super.createStyleContentString();

		//fill
		String fill = svgNodeProperties.getFill();
		if (fill != null) {
			styleContent = styleContent + "fill:" + fill + ";";
		}

		//stroke
		List<String> strokes = svgNodeProperties.getStroke();
		if (strokes != null) {
			boolean hasOneStroke = strokes.size() == 1;
			if (hasOneStroke) {
				styleContent = styleContent + "stroke:" + strokes.get(0) + ";";
			} else {
				//the special case of multiple strokes is handled
				//in other methods (e.g. createRectGeometryString)
			}
		}

		//stroke width
		List<Double> strokeWidths = svgNodeProperties.getStrokeWidth();
		if (strokeWidths != null) {
			boolean hasOneStrokeWidth = strokeWidths.size() == 1;
			if (hasOneStrokeWidth) {
				Double strokeWidth = strokeWidths.get(0);
				if (!strokeWidth.equals(1)) {
					styleContent = styleContent + "stroke-width:" + strokeWidth + ";";
				}
			} else {
				//the special case of multiple strokes is handled
				//in other methods (e.g. createRectGeometryString)
			}
		}

		//stroke alignment
		SvgStrokeAlignment strokeAlignment = svgNodeProperties.getStrokeAlignment();
		if (strokeAlignment != null) {
			if (!strokeAlignment.equals(SvgStrokeAlignment.CENTER)) {
				styleContent = styleContent + "stroke-alignment:" + strokeAlignment + ";";
				String message = "The svg stroke-alignment (JavaFx: StrokeType) is set to '" + strokeAlignment + "'.\n"
						+ "This svg property is relativly new and might not yet be supported by your svg viewer.";
				sysLog.warn(message);
			}
		}

		//stroke line cap
		SvgStrokeLineCap strokeLineCap = svgNodeProperties.getStrokeLineCap();
		if (strokeLineCap != null) {
			if (!strokeLineCap.equals(SvgStrokeLineCap.SQUARE)) {
				styleContent = styleContent + "stroke-linecap:" + strokeLineCap + ";";
			}
		}

		//stroke dash array
		String strokeDashArray = svgNodeProperties.getStrokeDashArray();
		if (strokeDashArray != null) {
			if (!strokeDashArray.isEmpty()) {
				styleContent = styleContent + "stroke-dasharray:" + strokeDashArray + ";";
			}
		}
		return styleContent;
	}

	@Override
	protected String createTagStartString(String idString, String styleString, String transformString) {

		Objects.requireNonNull(svgNodeProperties, "svg node propeties must not be null.");

		boolean hasChildren = svgNodeProperties.hasChildren();

		boolean isDefinedByRect = svgNodeProperties.isDefinedByRect();

		String pathShape = svgNodeProperties.getPathShape();
		boolean hasPathShape = pathShape != null && !pathShape.isEmpty();

		String startString = "";
		if (hasChildren) {
			//add a group tag as prefix and include the id, style and transform into that group tag
			startString = startString + indentation + "<g" + idString + styleString + transformString + ">\n";
			increaseIndentation();

			//create "base tag" (the id, style and transform are not included here since they are already included in the group tag)
			if (isDefinedByRect) {
				//rects are drown as individual lines in a group
				//to be able to style the lines individually
				//this tag starts a group for the rect lines
				startString = startString + indentation + "<g>\n";
				increaseIndentation();
			} else {
				if (hasPathShape) {
					startString = startString + indentation + "<path";
				} else {
					//something went wrong: do not add corrupted path tag
					startString = startString + indentation;
				}
			}

		} else {
			//create individual tag and directly include id, style and transform
			if (isDefinedByRect) {
				//rects are drown as individual lines in a group
				//to be able to style the lines individually
				//this tag starts a group for the rect lines
				startString = startString + indentation + "<g" + idString + styleString + transformString + ">\n";
				increaseIndentation();
			} else {
				if (hasPathShape) {
					startString = startString + indentation + "<path" + idString + styleString + transformString;
				} else {
					//something went wrong: do not add corrupted path tag
					startString = startString + indentation;
				}
			}
		}

		return startString;
	}

	/**
	 * Creates the geometry string, including the end of the (base) tag. (This does not include the end tag of a maybe
	 * existing parent group.)
	 *
	 * @param svgNodeProperties
	 * @return
	 */
	@Override
	protected String createGeometryString() {

		boolean isDefinedByRect = svgNodeProperties.isDefinedByRect();

		String pathShape = svgNodeProperties.getPathShape();
		boolean hasPathShape = pathShape != null && !pathShape.isEmpty();

		if (isDefinedByRect) {
			//create rect geometry string
			String rectGeometryString = createSvgRectString(svgNodeProperties);
			return rectGeometryString;
		} else {
			if (hasPathShape) {
				//create path geometry string
				String shapeGeometryString = createPathGeometryString(pathShape);
				return shapeGeometryString;
			} else {
				//something went wrong: include a svg comment with a warning
				//the start string also checks for this issue and does not include a
				//start tag. Therefore, the start of the comment tag is included here without issues.
				String warningString = "<!-- warning: empty path shape -->\n";
				return warningString;
			}
		}
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private String createSvgRectString(SvgNodeProperties svgNodeProperties) {
		String width = svgNodeProperties.getRectWidth();
		String height = svgNodeProperties.getRectHeight();

		List<Double> fillRadius = svgNodeProperties.getFillRadius();
		boolean hasFillRadius = fillRadius != null && !fillRadius.isEmpty();
		boolean hasIndividualFillRadiuses = fillRadius != null && fillRadius.size() > 1;

		List<String> stroke = svgNodeProperties.getStroke();
		boolean hasIndividualStrokes = stroke != null && stroke.size() > 1;

		List<Double> strokeWidth = svgNodeProperties.getStrokeWidth();
		boolean hasIndividualStrokeWidth = strokeWidth != null && strokeWidth.size() > 1;

		List<Double> strokeOpacity = svgNodeProperties.getStrokeOpacities();
		boolean hasIndividualStrokeOpacities = strokeOpacity != null && strokeOpacity.size() > 1;

		List<Double> strokeRadius = svgNodeProperties.getStrokeRadius();
		boolean hasStrokeRadius = strokeRadius != null && !strokeRadius.isEmpty();
		boolean hasIndividualStrokeRadiuses = strokeRadius != null && strokeRadius.size() > 1;

		boolean useIndividualLines = hasIndividualStrokes || hasIndividualStrokeWidth || hasIndividualStrokeOpacities
				|| hasIndividualStrokeRadiuses;

		String rectGeometryString = "";

		if (hasIndividualFillRadiuses) {
			//not yet implemented
			String warnString = "Individual fill radiuses are not yet implemented.";
			sysLog.warn(warnString);
			rectGeometryString += indentation + "<!--" + warnString + "-->\n";
		}

		if (hasIndividualStrokeRadiuses) {
			//not yet implemented
			String warnString = "Individual stroke radiuses are not yet implemented.";
			sysLog.warn(warnString);
			rectGeometryString += indentation + "<!--" + warnString + "-->\n";
		}

		if (useIndividualLines) {

			if (hasStrokeRadius) {
				//not yet implemented
				String warnString = "The stroke radius is not yet implemented for individual stroke styles.";
				sysLog.warn(warnString);
				rectGeometryString += indentation + "<!--" + warnString + "-->\n";
			}

			if (stroke == null) {
				stroke = Arrays.asList(null, null, null, null);
			}
			if (strokeWidth == null) {
				strokeWidth = Arrays.asList(null, null, null, null);
			}
			if (strokeOpacity == null) {
				strokeWidth = Arrays.asList(null, null, null, null);
			}

			//rect for showing fill
			String rectString = "<rect width=\"" + width + "\" height=\"" + height + "\"/>\n";
			rectGeometryString += indentation + rectString;

			//individual border lines to apply individual border styles
			String topStroke = stroke.get(0);
			Double topStrokeWidth = strokeWidth.get(0);
			Double topOpacity = strokeOpacity.get(0);
			String topLine = createSvgLineString("top", "0", height, width, height, topStroke, topStrokeWidth,
					topOpacity);
			rectGeometryString += indentation + topLine;

			String rightStroke = stroke.get(1);
			Double rightStrokeWidth = strokeWidth.get(1);
			Double rightOpacity = strokeOpacity.get(1);
			String rightLine = createSvgLineString("right", width, height, width, "0", rightStroke, rightStrokeWidth,
					rightOpacity);
			rectGeometryString += indentation + rightLine;

			String bottomStroke = stroke.get(2);
			Double bottomStrokeWidth = strokeWidth.get(2);
			Double bottomOpacity = strokeOpacity.get(2);
			String bottomLine = createSvgLineString("bottom", width, "0", "0", "0", bottomStroke, bottomStrokeWidth,
					bottomOpacity);
			rectGeometryString += indentation + bottomLine;

			String leftStroke = stroke.get(3);
			Double leftStrokeWidth = strokeWidth.get(3);
			Double leftOpacity = strokeOpacity.get(3);
			String leftLine = createSvgLineString("left", "0", "0", "0", height, leftStroke, leftStrokeWidth,
					leftOpacity);
			rectGeometryString += indentation + leftLine;

		} else {

			String fillRadiusString = "";
			if (hasFillRadius) {
				Double r = fillRadius.get(0);
				fillRadiusString = " rx=\"" + r + "\" ry=\"" + r + "\"";
			}

			String rectString = "<rect width=\"" + width + "\" height=\"" + height + "\"" + fillRadiusString + "/>\n";
			rectGeometryString += indentation + rectString;

			if (hasStrokeRadius) {
				//add extra rect with transparent fill to show the border
				Double r = strokeRadius.get(0);
				String strokeRadiusString = "fill=\"transparent\" rx=\"" + r + "\" ry=\"" + r + "\"";

				String extraRectString = "<rect width=\"" + width + "\" height=\"" + height + "\"" + strokeRadiusString
						+ "/>\n";
				rectGeometryString += indentation + extraRectString;
			}
		}

		//if (radius != null) {
		//	rectGeometryString = rectGeometryString + "rx=\"" + radius + "\" " + "ry=\"" + radius + "\"";
		//}

		decreaseIndentation();
		rectGeometryString = rectGeometryString + indentation + "</g>\n\n";
		return rectGeometryString;
	}

	private static String createPathGeometryString(String pathShape) {
		String shapeGeometryString = " d=\"" + pathShape + "\"/>\n\n";
		return shapeGeometryString;
	}

	private static String createSvgLineString(
			String id,
			String x1,
			String y1,
			String x2,
			String y2,
			String stroke,
			Double strokeWidth,
			Double opacity) {

		String styleString = "style =\"";

		if (stroke != null) {
			styleString += "stroke:" + stroke + ";";
		}

		if (strokeWidth != null) {
			styleString += "stroke-width:" + strokeWidth + ";";
		}

		if (opacity != null) {
			styleString += "opacity:" + opacity + ";";
		}

		styleString += "\"";

		//avoid empty styles
		if (styleString.equals("style=\"\"")) {
			styleString = "";
		}

		String lineSvgString = "<line id=\"" + id + "\" x1=\"" + x1 + "\" y1=\"" + y1 + "\"  x2=\"" + x2 + "\" y2=\""
				+ y2 + "\" " + styleString + "/>\n";
		return lineSvgString;
	}

	/**
	 * Extracts the background radiuses
	 *
	 * @param backGround
	 * @return
	 */
	private static List<Double> backgroundToFillRadiuses(Background backGround) {
		Objects.requireNonNull(backGround, "Background must not be null");
		List<BackgroundFill> fills = backGround.getFills();
		BackgroundFill backgroundFill = fills.get(0);
		CornerRadii cornerRadii = backgroundFill.getRadii();
		List<Double> radii = new ArrayList<>();

		Double firstRadius = cornerRadii.getTopLeftHorizontalRadius();
		radii.add(firstRadius);

		Double secondRadius = cornerRadii.getTopRightHorizontalRadius();
		radii.add(secondRadius);

		Double thirdRadius = cornerRadii.getBottomRightHorizontalRadius();
		radii.add(thirdRadius);

		Double fourthRadius = cornerRadii.getBottomLeftHorizontalRadius();
		radii.add(fourthRadius);

		//check for equal radii and condense them in a single number if
		//they are equal
		Set<Double> radiiSet = new HashSet<Double>(radii);
		boolean hasEqualRadii = radiiSet.size() == 1;
		if (hasEqualRadii) {
			Double radius = radii.get(0);
			radii.clear();
			radii.add(radius);
		}

		return radii;
	}

	/**
	 * Extracts color strings from the given border.
	 *
	 * @param border
	 * @return
	 */
	private static List<String> borderToColorStrings(Border border) {
		Objects.requireNonNull(border, "Border must not be null.");

		List<String> strokeColors = new ArrayList<>();
		List<BorderStroke> strokes = border.getStrokes();
		for (BorderStroke borderStroke : strokes) {
			Paint topStroke = borderStroke.getTopStroke();
			String topStrokeColor = paintToColorString(topStroke);
			strokeColors.add(topStrokeColor);

			Paint rightStroke = borderStroke.getRightStroke();
			String rightStrokeColor = paintToColorString(rightStroke);
			strokeColors.add(rightStrokeColor);

			Paint bottomStroke = borderStroke.getBottomStroke();
			String bottomStrokeColor = paintToColorString(bottomStroke);
			strokeColors.add(bottomStrokeColor);

			Paint leftStroke = borderStroke.getLeftStroke();
			String leftStrokeColor = paintToColorString(leftStroke);
			strokeColors.add(leftStrokeColor);
		}

		//check for stroke colors and condense them in a single color if
		//they are equal
		Set<String> colorSet = new HashSet<String>(strokeColors);
		boolean hasEqualRadii = colorSet.size() == 1;
		if (hasEqualRadii) {
			String color = strokeColors.get(0);
			strokeColors.clear();
			strokeColors.add(color);
		}

		return strokeColors;

	}

	/**
	 * Extracts radii from the given border.
	 *
	 * @param border
	 * @return
	 */
	private static List<Double> borderToStrokeRadii(Border border) {
		Objects.requireNonNull(border, "Border must not be null.");

		List<Double> strokeRadii = Arrays.asList(null, null, null, null);

		//TODO: not yet implemented

		return strokeRadii;

	}

	/**
	 * Extracts stroke widths from the given border.
	 *
	 * @param border
	 * @return
	 */
	private static List<Double> borderToStrokeWidths(Border border) {
		Objects.requireNonNull(border, "Border must not be null.");

		List<Double> strokeWidths = Arrays.asList(null, null, null, null);

		//TODO: not yet implemented

		return strokeWidths;

	}

	/**
	 * Extracts the opacities from the given border.
	 *
	 * @param border
	 * @return
	 */
	private static List<Double> borderToStrokeOpacites(Border border) {
		Objects.requireNonNull(border, "Border must not be null.");

		List<Double> strokeOpacities = new ArrayList<>();
		List<BorderStroke> strokes = border.getStrokes();
		for (BorderStroke borderStroke : strokes) {
			Paint topStroke = borderStroke.getTopStroke();
			double topOpacity = paintToOpacity(topStroke);
			strokeOpacities.add(topOpacity);

			Paint rightStroke = borderStroke.getRightStroke();
			double rightOpacity = paintToOpacity(rightStroke);
			strokeOpacities.add(rightOpacity);

			Paint bottomStroke = borderStroke.getBottomStroke();
			double bottomOpacity = paintToOpacity(bottomStroke);
			strokeOpacities.add(bottomOpacity);

			Paint leftStroke = borderStroke.getLeftStroke();
			double leftOpacity = paintToOpacity(leftStroke);
			strokeOpacities.add(leftOpacity);
		}
		return strokeOpacities;

	}

	private static double paintToOpacity(Paint topStroke) {
		Color topStrokeColor = (Color) topStroke;
		double topOpacity = topStrokeColor.getOpacity();
		return topOpacity;
	}

	//#end region

	//#region ACCESSORS

	//#end region

}
