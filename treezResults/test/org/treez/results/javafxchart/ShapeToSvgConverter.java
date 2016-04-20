package org.treez.results.javafxchart;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

/**
 * Converts a Shape to SVG code
 */
public class ShapeToSvgConverter extends AbstractNodeToSvgConverter<Shape> {

	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(ShapeToSvgConverter.class);

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	//#end region

	//#region METHODS

	@Override
	public String extendCode(String initialSvgString, Shape shape) {
		addDataFromNode(shape);
		addDataFromShape(shape);
		String svgString = initialSvgString + createSvgString();
		svgString = extendWithChildSvgCodeAndEndTag(svgString, shape);
		return svgString;
	}

	private void addDataFromShape(Shape shape) {
		svgNodeProperties = addDataFromShape(svgNodeProperties, shape);
	}

	/**
	 * Extracts SVG properties from the given Shape and applies them.
	 */
	public static SvgNodeProperties addDataFromShape(SvgNodeProperties properties, Shape shape) {

		//hasChildren: a shape has no children
		properties.setHasChildren(false);

		//isDefinedByRect: a shape is not defined by a rect
		properties.setIsDefinedByRect(false);

		//x and y
		Double xTranslate = shape.getTranslateX();
		properties.setX(xTranslate);

		Double yTranslate = shape.getTranslateY();
		properties.setY(yTranslate);

		//path shape
		String svgPathShape = shapeToSvg(shape);
		properties.setPathShape(svgPathShape);

		//fill
		Paint fill = shape.getFill();
		String fillColor = paintToColorString(fill);
		properties.setFill(fillColor);

		//stroke
		addStrokeDataFromShape(properties, shape);

		return properties;

	}

	private static void addStrokeDataFromShape(SvgNodeProperties properties, Shape shape) {

		//color
		Paint stroke = shape.getStroke();
		String strokeColor = paintToColorString(stroke);
		properties.setStroke(strokeColor);

		//width
		Double strokeWidth = shape.getStrokeWidth();
		properties.setStrokeWidth(strokeWidth);

		//line cap
		StrokeLineCap strokeLineCap = shape.getStrokeLineCap();
		properties.setStrokeLineCap(strokeLineCap);

		//dash array
		List<Double> strokeDashArrayList = shape.getStrokeDashArray();
		String strokeDashArray = doubleArrayToStrokeDashArrayString(strokeDashArrayList);
		properties.setStrokeDashArray(strokeDashArray);

		//dash offset
		Double strokeDashOffset = shape.getStrokeDashOffset();
		properties.setStrokeDashOffset(strokeDashOffset);

		//line join
		StrokeLineJoin strokeLineJoin = shape.getStrokeLineJoin();
		properties.setStrokeLineJoin(strokeLineJoin);

		//miter limit
		Double strokeMiterLimit = shape.getStrokeMiterLimit();
		properties.setStrokeMiterLimit(strokeMiterLimit);

		//alignment
		StrokeType strokeType = shape.getStrokeType();
		properties.setStrokeAlignment(strokeType);
	}

	@Override
	protected String createStyleContentString() {

		String styleContent = super.createStyleContentString();
		styleContent = addFillStyle(styleContent);
		styleContent = addStrokeStyle(styleContent);

		return styleContent;
	}

	private String addFillStyle(String initialStyleContent) {
		String styleContent = initialStyleContent;
		String fill = svgNodeProperties.getFill();
		if (fill != null) {
			styleContent = styleContent + "fill:" + fill + ";";
		}
		return styleContent;
	}

	//#region STROKE STYLE

	private String addStrokeStyle(String initialStyleContent) {

		String styleContent = initialStyleContent;
		styleContent = addStrokeColor(styleContent);
		styleContent = addStrokeWidth(styleContent);
		styleContent = addStrokeAlignment(styleContent);
		styleContent = addStrokeLineCap(styleContent);
		styleContent = addStrokeDashArray(styleContent);

		return styleContent;
	}

	private String addStrokeColor(String initialStyleContent) {
		String styleContent = initialStyleContent;
		//stroke color
		List<String> strokes = svgNodeProperties.getStroke();
		if (strokes != null) {
			boolean hasOneStroke = strokes.size() == 1;
			if (hasOneStroke) {
				styleContent = styleContent + "stroke:" + strokes.get(0) + ";";
			} else {
				LOG.warn("Could not determine stroke.");
			}
		}
		return styleContent;
	}

	private String addStrokeWidth(String initialStyleContent) {
		String styleContent = initialStyleContent;
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
				LOG.warn("Could not determine stroke width.");
			}
		}
		return styleContent;
	}

	private String addStrokeAlignment(String initialStyleContent) {
		String styleContent = initialStyleContent;
		//stroke alignment
		SvgStrokeAlignment strokeAlignment = svgNodeProperties.getStrokeAlignment();
		if (strokeAlignment != null) {
			if (!strokeAlignment.equals(SvgStrokeAlignment.CENTER)) {
				styleContent = styleContent + "stroke-alignment:" + strokeAlignment + ";";
				String message = "The svg stroke-alignment (JavaFx: StrokeType) is set to '" + strokeAlignment + "'.\n"
						+ "This svg property is relativly new and might not yet be supported by your svg viewer.";
				LOG.warn(message);
			}
		}
		return styleContent;
	}

	private String addStrokeLineCap(String initialStyleContent) {
		String styleContent = initialStyleContent;
		//stroke line cap
		SvgStrokeLineCap strokeLineCap = svgNodeProperties.getStrokeLineCap();
		if (strokeLineCap != null) {
			if (!strokeLineCap.equals(SvgStrokeLineCap.SQUARE)) {
				styleContent = styleContent + "stroke-linecap:" + strokeLineCap + ";";
			}
		}
		return styleContent;
	}

	private String addStrokeDashArray(String initialStyleContent) {
		String styleContent = initialStyleContent;
		//stroke dash array
		String strokeDashArray = svgNodeProperties.getStrokeDashArray();
		if (strokeDashArray != null) {
			if (!strokeDashArray.isEmpty()) {
				styleContent = styleContent + "stroke-dasharray:" + strokeDashArray + ";";
			}
		}
		return styleContent;
	}

	//#end region

	@Override
	protected String createTagStartString(String idString, String styleString, String transformString) {

		Objects.requireNonNull(svgNodeProperties, "svg node propeties must not be null.");

		String pathShape = svgNodeProperties.getPathShape();
		boolean hasPathShape = pathShape != null && !pathShape.isEmpty();

		String startString;
		if (hasPathShape) {
			startString = indentation + "<path" + idString + styleString + transformString;
		} else {
			//something went wrong: do not add (corrupted) path tag
			startString = indentation;
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

		String pathShape = svgNodeProperties.getPathShape();
		boolean hasPathShape = pathShape != null && !pathShape.isEmpty();

		if (hasPathShape) {
			//create path geometry string
			String shapeGeometryString = createPathGeometryString(pathShape);
			return shapeGeometryString;
		} else {
			//something went wrong: include SVG comment with a warning
			//the start string also checks for this issue and does not include a
			//start tag. Therefore, the start of the comment tag is included here without issues.
			String warningString = "<!-- warning: empty path shape -->\n";
			return warningString;
		}

	}

	private static String createPathGeometryString(String pathShape) {
		String shapeGeometryString = " d=\"" + pathShape + "\"/>\n\n";
		return shapeGeometryString;
	}

	/**
	 * Converts a basic shape to an SVG string
	 *
	 * @param shape
	 * @return
	 */
	private static String shapeToSvg(Shape shape) {
		String svgString = ShapeConverter.shapeToSvgString(shape);
		return svgString;
	}

	//#end region

}
