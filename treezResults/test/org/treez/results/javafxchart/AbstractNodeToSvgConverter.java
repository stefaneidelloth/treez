package org.treez.results.javafxchart;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Abstract base class for converting JavaFx Nodes to SVG code
 */
public abstract class AbstractNodeToSvgConverter<T extends Node> implements NodeConverter<T> {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(JavaFxNodeToSvgConverter.class);

	//#region ATTRIBUTES

	protected String indentation;

	protected SvgNodeProperties svgNodeProperties;

	//#end region

	//#region CONSTRUCTORS

	//#end region

	//#region METHODS

	/**
	 * Creates SVG code from the given JavaFx Node. The given initial comment is included as a prefix.
	 */
	@Override
	public abstract String extendCode(String initialComment, T node);

	/**
	 * Creates an svg string from the svg node properties (which have to be determined before calling this method). This
	 * method might be overridden by inheriting classes and be called from the method extendCode.
	 *
	 * @param svgNodeProperties
	 * @return
	 */
	protected String createSvgString() {

		String svgString = "";

		//create svg sub strings with svg node properties-----------------

		//comment string
		String commentString = createCommentString();

		//id sub string
		String idString = createIdString();

		//style sub string
		String styleString = createStyleString();

		//transformation sub string
		String transformString = createTransformString();

		//start sub string (included id, style and transform)
		String startString = createTagStartString(idString, styleString, transformString);

		//geometry string, including the end of the base tag
		String geometryString = createGeometryString();

		//add sub strings to svg string-----------------------------------
		svgString = svgString + commentString + startString + geometryString;

		return svgString;
	}

	/**
	 * If the given node has children, this method adds the svg text for the children and then closes the svg tag for
	 * the node.
	 *
	 * @param startNode
	 * @param initialSvgString
	 * @return
	 */
	protected String extendWithChildSvgCodeAndEndTag(String initialSvgString, Node startNode) {
		String svgString = initialSvgString;
		boolean isParent = startNode instanceof Parent;
		if (isParent) {
			NodeToSvgConverter nodeConverter = new NodeToSvgConverter();
			Parent parent = (Parent) startNode;
			List<Node> childNodes = parent.getChildrenUnmodifiable();
			for (Node node : childNodes) {
				nodeConverter.setIndentation(indentation);
				String childSvgString = nodeConverter.extendCode(initialSvgString, node);
				svgString = svgString + childSvgString;
			}

			boolean hasChildren = !childNodes.isEmpty();
			if (hasChildren) {

				//add g-end tag
				String groupEndString = "</g>\n";
				decreaseIndentation();
				svgString = svgString + indentation + groupEndString;

			}
		}
		return svgString;
	}

	/**
	 * Extracts svg properties directly from the given Node and applies them.
	 *
	 * @param nodeProperties
	 * @param node
	 */
	protected void addDataFromNode(Node node) {

		svgNodeProperties = new SvgNodeProperties();

		//comment
		String className = node.getClass().getName();
		svgNodeProperties.addComment(className);

		String comment = createCssClassString(node);
		svgNodeProperties.addComment(comment);

		//id
		String id = node.getId();
		svgNodeProperties.setId(id);

		//visibility
		if (node.isVisible()) {
			svgNodeProperties.setVisibility(SvgVisibility.VISIBLE);
		} else {
			svgNodeProperties.setVisibility(SvgVisibility.HIDDEN);
		}

		//opacity
		Double opacity = node.getOpacity();
		svgNodeProperties.setOpacity(opacity);

		Bounds bounds = node.getBoundsInParent();

		//x
		Double x = bounds.getMinX();
		svgNodeProperties.setX(x);

		//y
		Double y = bounds.getMinY();
		svgNodeProperties.setY(y);

		//x scale
		Double xScale = node.getScaleX();
		svgNodeProperties.setXScale(xScale);

		//y scale
		Double yScale = node.getScaleY();
		svgNodeProperties.setYScale(yScale);

		//rotation
		Double rotation = node.getRotate();
		svgNodeProperties.setRotation(rotation);

		//rotation axis
		Point3D rotationAxis = node.getRotationAxis();

		Double rotationAxisX = rotationAxis.getX();
		svgNodeProperties.setRotationAxisX(rotationAxisX);

		Double rotationAxisY = rotationAxis.getY();
		svgNodeProperties.setRotationAxisY(rotationAxisY);

	}

	/**
	 * Creates the SVG comment sub string that is placed before a svg node to tell something about its meaning. If no
	 * comment is available and empty string is returned
	 *
	 * @param svgNodeProperties
	 * @return
	 */
	protected String createCommentString() {
		String comment = svgNodeProperties.getComment();
		if (comment == null || comment.isEmpty()) {
			return "";
		} else {
			String commentString = indentation + "<!--" + comment + " -->\n";
			return commentString;
		}
	}

	/**
	 * Creates the SVG id sub string
	 *
	 * @return
	 */
	private String createIdString() {
		String id = svgNodeProperties.getId();
		boolean idExists = id != null && !id.isEmpty();
		String idString = "";
		if (idExists) {
			idString = " id=\"" + id + "\"";
		}
		return idString;
	}

	/**
	 * Creates the SVG style sub string
	 *
	 * @return
	 */
	private String createStyleString() {

		String styleString = " style=\"";

		styleString = createStyleContentString();

		//style end
		styleString = styleString + "\" ";

		//avoid empty style
		if (styleString.equals(" style=\"\" ")) {
			styleString = "";
		}

		return styleString;
	}

	/**
	 * Creates the content/value for the style string. This method might be overridden by inheriting classes.
	 *
	 * @return
	 */
	protected String createStyleContentString() {

		String styleContent = "";

		//visibility
		SvgVisibility visibility = svgNodeProperties.getVisibility();
		if (visibility != null) {
			if (visibility != SvgVisibility.VISIBLE) {
				styleContent = styleContent + "visibility:" + visibility.toString() + ";";
			}
		}

		//opacity
		Double opacity = svgNodeProperties.getOpacity();
		if (opacity != null) {
			if (!opacity.equals(1.0)) {
				styleContent = styleContent + "opacity:" + opacity + ";";
			}
		}

		return styleContent;
	}

	/**
	 * Creates the SVG transform sub string
	 *
	 * @return
	 */
	private String createTransformString() {
		Double x = svgNodeProperties.getX();
		Double y = svgNodeProperties.getY();
		Double xScale = svgNodeProperties.getXScale();
		Double yScale = svgNodeProperties.getYScale();

		String transformString = "";
		boolean hasTranslation = x != 0 || y != 0;
		boolean hasScale = xScale != 1 || yScale != 1;
		if (hasTranslation || hasScale) {

			transformString = " transform=\"";
			if (hasTranslation) {
				transformString = transformString + "translate(" + x + "," + y + ") ";
			}
			if (hasScale) {
				transformString = transformString + "scale(" + xScale + "," + yScale + ")";
			}
			transformString = transformString + "\" ";
		}
		return transformString;
	}

	/**
	 * Creates the SVG start sub string. The arguments have to be passed because if the node contains children, a group
	 * tag will be put as prefix and the id, style and transform information will be put in that group tag. If the node
	 * does not contain children, the id, style and transform data will be put directly in the SVG node tag. This
	 * contract has to be fulfilled by the inheriting classes.
	 *
	 * @param svgNodeProperties
	 * @param idString
	 * @param styleString
	 * @param transformString
	 * @return
	 */
	@SuppressWarnings("unused")
	protected String createTagStartString(String idString, String styleString, String transformString) {
		return indentation;
	}

	/**
	 * Creates the geometry string, including the end of the (base) tag. (This does not include the end tag of a maybe
	 * existing parent group.) See the inheriting classes for alternative implementations. This base implementation only
	 * includes a warning and should be overridden.
	 *
	 * @param svgNodeProperties
	 * @return
	 */
	protected String createGeometryString() {
		//Something went wrong because geometry data is missing: include a SVG comment with a warning
		String warningString = "<!-- warning: empty node geometry -->\n";
		return warningString;
	}

	/**
	 * Tries to convert a given Paint to a hex color string. If the color can not be extracted a black default color is
	 * used.
	 *
	 * @param paint
	 * @return
	 */
	protected static String paintToColorString(Paint paint) {
		String colorString = "transparent";
		boolean isColor = paint instanceof Color;
		if (isColor) {
			Color color = (Color) paint;
			colorString = colorToRGBCode(color);
		}
		return colorString;
	}

	/**
	 * Extracts a color string from the given background
	 *
	 * @param backGround
	 * @return
	 */
	protected static String backgroundToColorString(Background backGround) {
		Objects.requireNonNull(backGround, "Background must not be null");
		List<BackgroundFill> fills = backGround.getFills();
		BackgroundFill backgroundFill = fills.get(0);
		Paint fill = backgroundFill.getFill();
		String fillColor = paintToColorString(fill);
		return fillColor;
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private static String colorToRGBCode(Color color) {

		int red = (int) (color.getRed() * 255);
		int green = (int) (color.getGreen() * 255);
		int blue = (int) (color.getBlue() * 255);

		return String.format("#%02X%02X%02X", red, green, blue);
	}

	/**
	 * Creates a string that includes all css classes of the node
	 *
	 * @param node
	 * @return
	 */
	private static String createCssClassString(Node node) {
		List<String> styleClasses = node.getStyleClass();
		String cssClassString = String.join(" ", styleClasses);
		return cssClassString;
	}

	/**
	 * Converts a list of double values to a single stroke dash array string.
	 *
	 * @param strokeDashArrayList
	 * @return
	 */
	protected static String doubleArrayToStrokeDashArrayString(List<Double> strokeDashArrayList) {
		String fullArrayString = strokeDashArrayList.toString(); //get array as comma separated string in brackets
		String strokeDashArray = fullArrayString.substring(1, fullArrayString.length() - 1); //remove square brackets
		return strokeDashArray;
	}

	//#region INDENTATION

	/**
	 * Increases the indentation
	 *
	 * @param indentation
	 */
	protected void increaseIndentation() {
		indentation = indentation + "    ";
	}

	/**
	 * Decreases the indentation
	 *
	 * @param indentation
	 */
	protected void decreaseIndentation() {
		final int tablength = 4;
		int endIndex = indentation.length() - tablength;
		indentation = indentation.substring(0, endIndex);
	}

	//#end region

	//#end region

	//#region ACCESSORS

	@Override
	public String getIndentation() {
		return indentation;
	}

	@Override
	public void setIndentation(String indentation) {
		this.indentation = indentation;
	}

	/**
	 * Sets the svg node properties
	 *
	 * @param svgNodeProperties
	 */
	public void setSvgNodeProperties(SvgNodeProperties svgNodeProperties) {
		this.svgNodeProperties = svgNodeProperties;
	}

	//#end region

}
