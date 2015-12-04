package org.treez.results.javafxchart;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

/**
 * Represents the "properties of a svg node" that corresponds to a JavaFx node
 */
public class SvgNodeProperties {

	//#region ATTRIBUTES

	//#region GENERAL

	/**
	 * A comment that will be placed above the svg node in order to tell something about its meaning.
	 */
	private String comment;

	/**
	 * An id that identifies the node. If the node has children, this id will be put in a g- tag. If it does not have
	 * children, the id will be put in the path-tag or rect-tag directly. Empty ids will not be included.
	 */
	private String id;

	/**
	 * This is true if the svg node is a rect. The rect can have rounded edges). If this is true, the svg node
	 * represents a JavaFx Region without an extra -fx-shape. If this attribute is false, the node might have any
	 * geometry (including rectangle). It then represents a JavaFx Shape or a Region with -fx-shape.
	 */
	private Boolean isDefinedByRect = false;

	/**
	 * This is true if the node has child nodes
	 */
	private Boolean hasChildren = false;

	/**
	 * If this is true the node represents a JavaFx Group
	 */
	private Boolean isGroup = false;

	//#end region

	//#region TRANSPARENCY

	/**
	 * The svg visibility of the node
	 */
	private SvgVisibility visibility;

	/**
	 * The opacity of the node: 1...0, where a value of 1 means that the node is fully visible and a value of 0 means
	 * that it is totally transparent. (Note: The "stroke-opacity" is not used here and half-transparent borders have to
	 * be specified by extra nodes.)
	 */
	private Double opacity;

	/**
	 * The opacities of the strokes
	 */
	private List<Double> strokeOpacity;

	//#end region

	//#region GEOMETRY AND TRANSFORMATIONS

	//#region GENERAL

	/**
	 * The x coordinate of the node in respect to its parent node, will be used in the style of the node with translate
	 */
	private Double x;

	/**
	 * The y coordinate of the node in respect to its parent node, will be used in the style of the node with
	 * translate(x,y)
	 */
	private Double y;

	/**
	 * The x scale value, will be used in the style of the node with scale(xScale,yScale)
	 */
	private Double xScale;

	/**
	 * The y scale value, will be used in the style of the node with scale(xScale,yScale)
	 */
	private Double yScale;

	/**
	 * The rotation value, will be used in the tranform of the node with rotate(rotation,0,0), measured in degrees.
	 */
	private Double rotation;

	/**
	 * The x coordinate of the rotation axis
	 */
	private Double rotationAxisX;

	/**
	 * The y coordinate of the rotation axis
	 */
	private Double rotationAxisY;

	//#end region

	//#region SHAPE

	/**
	 * Represents the svg-property "d" of a path: describes the geometry. This is used if the corresponding JavaFx node
	 * represents a Shape or if the geometry is given by the JavaFx css property "-fx-shape"
	 */
	private String pathShape;

	//#end region

	//#region RECT

	/**
	 * This is the width for a special case: the node is defined with by a rect. In the other cases the width will be
	 * determined by the pathShape.
	 */
	private String rectWidth;

	/**
	 * This is the height for a special case: the node is defined by a rect. In the other cases the height will be
	 * determined by the pathShape.
	 */
	private String rectHeight;

	//#end region

	//#end region

	//#region STYLE

	//#region FILL

	private String fillColor;

	/**
	 * The fill radiuses
	 */
	private List<Double> fillRadius;

	//#end region

	//#region STROKE

	//see http://www.w3.org/TR/SVG/painting.html#StrokeProperties

	/**
	 * The colors of line segments (for example a rectangle might have different strokes for each side of a rectangle in
	 * JavaFx)
	 */
	private List<String> stroke;

	/**
	 * The width of lines
	 */
	private List<Double> strokeWidth;

	/**
	 * Defines how the end of lines looks like
	 */
	private SvgStrokeLineCap strokeLineCap;

	/**
	 * Defines how the join for two line segments looks like (e.g. rounded edge)
	 */
	private SvgStrokeLineJoin strokeLineJoin;

	/**
	 * Defines if the stroke is drawn inside or outside of a shape or centered on the edge
	 */
	private SvgStrokeAlignment strokeAlignment;

	/**
	 * The miter limit, see http://www.w3.org/TR/SVG/painting.html#StrokeProperties Must be >=1.
	 */
	private Double strokeMiterLimit;

	/**
	 * Defines the line dash distances: a list of comma separated numbers as String.
	 */
	private String strokeDashArray;

	/**
	 * Distance into the dash pattern to start the dash.
	 */
	private Double strokeDashOffset;

	/**
	 * The radiuses of the strokes
	 */
	private List<Double> strokeRadius;

	//#end region

	//#region FONT

	private String fontSize = null;

	//#end region

	//#end region

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 */
	public SvgNodeProperties() {}

	//#end region

	//#region ACCESSORS

	/**
	 * @return
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @param comment
	 */
	public void addComment(String comment) {
		if (!comment.isEmpty()) {
			if (this.comment == null || this.comment.isEmpty()) {
				this.comment = comment;
			} else {
				this.comment = this.comment + " | " + comment;
			}
		}
	}

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return
	 */
	public Boolean isDefinedByRect() {
		return isDefinedByRect;
	}

	/**
	 * @param isDefinedByRect
	 */
	public void setIsDefinedByRect(Boolean isDefinedByRect) {
		this.isDefinedByRect = isDefinedByRect;
	}

	/**
	 * Get isGroup
	 *
	 * @return the isGroup
	 */
	public Boolean isGroup() {
		return isGroup;
	}

	/**
	 * Set isGroup
	 *
	 * @param isGroup
	 *            the isGroup to set
	 */
	public void setIsGroup(Boolean isGroup) {
		this.isGroup = isGroup;
	}

	/**
	 * @return
	 */
	public Boolean hasChildren() {
		if (hasChildren != null) {
			return hasChildren;
		} else {
			String message = "The hasChildren attribute has not been set";
			throw new IllegalStateException(message);
		}
	}

	/**
	 * @param hasChildren
	 */
	public void setHasChildren(Boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	/**
	 * @return
	 */
	public SvgVisibility getVisibility() {
		return visibility;
	}

	/**
	 * @param visibility
	 */
	public void setVisibility(SvgVisibility visibility) {
		this.visibility = visibility;
	}

	/**
	 * @return
	 */
	public Double getOpacity() {
		return opacity;
	}

	/**
	 * @param opacity
	 */
	public void setOpacity(Double opacity) {
		this.opacity = opacity;
	}

	/**
	 * @return
	 */
	public Double getX() {
		return x;
	}

	/**
	 * @param x
	 */
	public void setX(Double x) {
		this.x = x;
	}

	/**
	 * @return
	 */
	public Double getY() {
		return y;
	}

	/**
	 * @param y
	 */
	public void setY(Double y) {
		this.y = y;
	}

	/**
	 * @return
	 */
	public Double getXScale() {
		return xScale;
	}

	/**
	 * @param xScale
	 */
	public void setXScale(Double xScale) {
		this.xScale = xScale;
	}

	/**
	 * @return
	 */
	public Double getYScale() {
		return yScale;
	}

	/**
	 * @param yScale
	 */
	public void setYScale(Double yScale) {
		this.yScale = yScale;
	}

	/**
	 * @return
	 */
	public Double getRotation() {
		return rotation;
	}

	/**
	 * @param rotation
	 */
	public void setRotation(Double rotation) {
		this.rotation = rotation;
	}

	/**
	 * Get rotationAxisX
	 *
	 * @return the rotationAxisX
	 */
	public Double getRotationAxisX() {
		return rotationAxisX;
	}

	/**
	 * Set rotationAxisX
	 *
	 * @param rotationAxisX
	 *            the rotationAxisX to set
	 */
	public void setRotationAxisX(Double rotationAxisX) {
		this.rotationAxisX = rotationAxisX;
	}

	/**
	 * Get rotationAxisY
	 *
	 * @return the rotationAxisY
	 */
	public Double getRotationAxisY() {
		return rotationAxisY;
	}

	/**
	 * Set rotationAxisY
	 *
	 * @param rotationAxisY
	 *            the rotationAxisY to set
	 */
	public void setRotationAxisY(Double rotationAxisY) {
		this.rotationAxisY = rotationAxisY;
	}

	/**
	 * @return
	 */
	public String getPathShape() {
		return pathShape;
	}

	/**
	 * @param pathShape
	 */
	public void setPathShape(String pathShape) {
		this.pathShape = pathShape;
	}

	/**
	 * @return
	 */
	public String getRectWidth() {
		return rectWidth;
	}

	/**
	 * @param rectWidth
	 */
	public void setRectWidth(String rectWidth) {
		this.rectWidth = rectWidth;
	}

	/**
	 * @return
	 */
	public String getRectHeight() {
		return rectHeight;
	}

	/**
	 * @param rectHeight
	 */
	public void setRectHeight(String rectHeight) {
		this.rectHeight = rectHeight;
	}

	/**
	 * @return
	 */
	public String getFill() {
		return fillColor;
	}

	/**
	 * @param fillColor
	 */
	public void setFill(String fillColor) {
		this.fillColor = fillColor;
	}

	/**
	 * Get fillRadius
	 *
	 * @return the fillRadius
	 */
	public List<Double> getFillRadius() {
		return fillRadius;
	}

	/**
	 * Set fillRadius
	 *
	 * @param fillRadius
	 *            the fillRadius to set
	 */
	public void setFillRadius(List<Double> fillRadius) {
		this.fillRadius = fillRadius;
	}

	/**
	 * Set fillRadius
	 *
	 * @param fillRadius
	 *            the fillRadius to set
	 */
	public void setFillRadius(Double fillRadius) {
		this.fillRadius = new ArrayList<Double>();
		this.fillRadius.add(fillRadius);
	}

	/**
	 * @return
	 */
	public List<String> getStroke() {
		return stroke;
	}

	/**
	 * @param stroke
	 */
	public void setStroke(List<String> stroke) {
		this.stroke = stroke;
	}

	/**
	 * Creates a list of strokes that contains the given stroke as single entry and uses it as strokes list
	 *
	 * @param stroke
	 */
	public void setStroke(String stroke) {
		this.stroke = new ArrayList<>();
		this.stroke.add(stroke);
	}

	/**
	 * @return
	 */
	public List<Double> getStrokeWidth() {
		return strokeWidth;
	}

	/**
	 * @param strokeWidth
	 */
	public void setStrokeWidth(List<Double> strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	/**
	 * Sets the stroke width width a single value
	 *
	 * @param strokeWidth
	 */
	public void setStrokeWidth(Double strokeWidth) {
		this.strokeWidth = new ArrayList<>();
		this.strokeWidth.add(strokeWidth);

	}

	/**
	 * @return
	 */
	public List<Double> getStrokeRadius() {
		return strokeWidth;
	}

	/**
	 * @param strokeRadius
	 */
	public void setStrokeRadius(List<Double> strokeRadius) {
		this.strokeRadius = strokeRadius;
	}

	/**
	 * @return
	 */
	public SvgStrokeLineCap getStrokeLineCap() {
		return strokeLineCap;
	}

	/**
	 * @param strokeLineCap
	 */
	public void setStrokeLineCap(SvgStrokeLineCap strokeLineCap) {
		this.strokeLineCap = strokeLineCap;
	}

	/**
	 * Sets the stroke line cap with a JavaFx stroke line cap
	 */
	public void setStrokeLineCap(StrokeLineCap javaFxStrokeLineCap) {
		switch (javaFxStrokeLineCap) {
		case BUTT:
			setStrokeLineCap(SvgStrokeLineCap.BUTT);
			break;
		case ROUND:
			setStrokeLineCap(SvgStrokeLineCap.ROUND);
			break;
		case SQUARE:
			setStrokeLineCap(SvgStrokeLineCap.SQUARE);
			break;
		default:
			String message = "The line cap " + javaFxStrokeLineCap + " is not known.";
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * @return
	 */
	public SvgStrokeLineJoin getStrokeLineJoin() {
		return strokeLineJoin;
	}

	/**
	 * @param strokeLineJoin
	 */
	public void setStrokeLineJoin(SvgStrokeLineJoin strokeLineJoin) {
		this.strokeLineJoin = strokeLineJoin;
	}

	/**
	 * Sets the stroke line join with a JavaFx stroke line join
	 */
	public void setStrokeLineJoin(StrokeLineJoin javaFxStrokeLineJoin) {
		switch (javaFxStrokeLineJoin) {
		case MITER:
			setStrokeLineJoin(SvgStrokeLineJoin.MITER);
			break;
		case ROUND:
			setStrokeLineJoin(SvgStrokeLineJoin.ROUND);
			break;
		case BEVEL:
			setStrokeLineJoin(SvgStrokeLineJoin.BEVEL);
			break;
		default:
			String message = "The line join " + javaFxStrokeLineJoin + " is not known.";
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * @return
	 */
	public SvgStrokeAlignment getStrokeAlignment() {
		return strokeAlignment;
	}

	/**
	 * @param strokeAlignment
	 */
	public void setStrokeAlignment(SvgStrokeAlignment strokeAlignment) {
		this.strokeAlignment = strokeAlignment;
	}

	/**
	 * Sets the stroke alignment with a JavaFx stroke type
	 */
	public void setStrokeAlignment(StrokeType javaFxStrokeType) {
		switch (javaFxStrokeType) {
		case CENTERED:
			setStrokeAlignment(SvgStrokeAlignment.CENTER);
			break;
		case INSIDE:
			setStrokeAlignment(SvgStrokeAlignment.INNER);
			break;
		case OUTSIDE:
			setStrokeAlignment(SvgStrokeAlignment.OUTER);
			break;
		default:
			String message = "The stroke type " + javaFxStrokeType + " is not known.";
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * @return
	 */
	public Double getStrokeMiterLimit() {
		return strokeMiterLimit;
	}

	/**
	 * @param strokeMiterLimit
	 */
	public void setStrokeMiterLimit(Double strokeMiterLimit) {
		this.strokeMiterLimit = strokeMiterLimit;
	}

	/**
	 * @return
	 */
	public String getStrokeDashArray() {
		return strokeDashArray;
	}

	/**
	 * @param strokeDashArray
	 */
	public void setStrokeDashArray(String strokeDashArray) {
		this.strokeDashArray = strokeDashArray;
	}

	/**
	 * @return
	 */
	public Double getStrokeDashOffset() {
		return strokeDashOffset;
	}

	/**
	 * @param strokeDashOffset
	 */
	public void setStrokeDashOffset(Double strokeDashOffset) {
		this.strokeDashOffset = strokeDashOffset;
	}

	/**
	 * Get strokeOpacities
	 *
	 * @return the strokeOpacities
	 */
	public List<Double> getStrokeOpacities() {
		return strokeOpacity;
	}

	/**
	 * Set strokeOpacities
	 *
	 * @param strokeOpacities
	 *            the strokeOpacities to set
	 */
	public void setStrokeOpacities(List<Double> strokeOpacities) {
		this.strokeOpacity = strokeOpacities;
	}

	/**
	 * @return
	 */
	public String getFontSize() {
		return fontSize;
	}

	/**
	 * @param fontSize
	 */
	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	//#end region

}
