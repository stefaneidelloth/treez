package org.treez.results.javafxchart;

import java.util.List;

import org.apache.log4j.Logger;
import org.treez.testutils.TestUtils;

import com.sun.javafx.css.SubCssMetaData;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Converts a JavaFx node to an SVG string
 */
public final class JavaFxNodeToSvgConverter {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(JavaFxNodeToSvgConverter.class);

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Private Constructor to prevent construction
	 */
	private JavaFxNodeToSvgConverter() {}

	//#end region

	//#region METHODS

	/**
	 * Converts a JavaFx Node to an SVG String
	 *
	 * @param node
	 * @return
	 */
	public static String nodeToSvg(Node node) {

		TestUtils.initializeLogging();

		String svgString = createSvgHeader();

		NodeToSvgConverter nodeConverter = new NodeToSvgConverter();
		String initialIndentation = "    ";
		nodeConverter.setIndentation(initialIndentation);

		svgString = nodeConverter.extendCode(svgString, node);

		String endString = "</svg>";
		svgString = svgString + endString;

		return svgString;

	}

	/**
	 * Creates the svg header
	 *
	 * @return
	 */
	private static String createSvgHeader() {

		String svgHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + "<svg\n"
				+ "    xmlns:svg=\"http://www.w3.org/2000/svg\"\n" + "    xmlns=\"http://www.w3.org/2000/svg\"\n"
				+ ">\n";
		return svgHeader;
	}

	/*

	/**
	 * Extracts default css values from the css meta data and applies them if the corresponding svg property has not
	 * been already set.
	 *
	 * @param nodeProperties
	 * @param node
	 * @return
	 */
	private static SvgNodeProperties addDataFromCssMetaData(SvgNodeProperties initialSvgNodeProperties, Node node) {

		SvgNodeProperties svgNodeProperties = initialSvgNodeProperties;

		List<CssMetaData<? extends Styleable, ?>> cssMetaDataList = node.getCssMetaData();

		for (CssMetaData<? extends Styleable, ?> cssMetaData : cssMetaDataList) {

			String property = cssMetaData.getProperty();
			switch (property) {
			case "visibility":
				//visibility
				if (svgNodeProperties.getVisibility() == null) {
					Boolean visible = (Boolean) cssMetaData.getInitialValue(null);
					if (visible != null) {
						svgNodeProperties.setVisibility(SvgVisibility.HIDDEN);
						sysLog.info("Set visibility from css:" + visible);
					}
				}
				break;
			case "-fx-shape":
				//path shape
				if (svgNodeProperties.getPathShape() == null) {
					String fxShape = (String) cssMetaData.getInitialValue(null);
					if (fxShape != null) {
						svgNodeProperties.setPathShape(fxShape);
						sysLog.info("Set fx-shape from css:" + fxShape);
					}
				}
				break;

			case "-fx-opacity":
				//opacity
				if (svgNodeProperties.getOpacity() == null) {
					Double opacity = (Double) cssMetaData.getInitialValue(null);
					if (opacity != null) {
						svgNodeProperties.setOpacity(opacity);
						sysLog.info("Set opacity from css:" + opacity);
					}
				}
				break;

			case "-fx-translate-x":
				//x
				if (svgNodeProperties.getX() == null) {
					Object xObj = cssMetaData.getInitialValue(null);
					if (xObj != null) {
						double x = (double) xObj;
						svgNodeProperties.setX(x);
						sysLog.info("Set x from css:" + x);
					}
				}
				break;

			case "-fx-translate-y":
				//y
				if (svgNodeProperties.getY() == null) {
					Object yObj = cssMetaData.getInitialValue(null);
					if (yObj != null) {
						Double y = (double) yObj;
						svgNodeProperties.setY(y);
						sysLog.info("Set y from css:" + y);
					}
				}
				break;
			case "-fx-scale-x":
				//xScale
				if (svgNodeProperties.getXScale() == null) {
					Object xScaleObj = cssMetaData.getInitialValue(null);
					if (xScaleObj != null) {
						double xScale = (double) xScaleObj;
						svgNodeProperties.setXScale(xScale);
						sysLog.info("Set xScale from css:" + xScale);
					}
				}
				break;
			case "-fx-scale-y":
				//yScale
				if (svgNodeProperties.getYScale() == null) {
					Object yScaleObj = cssMetaData.getInitialValue(null);
					if (yScaleObj != null) {
						double yScale = (double) yScaleObj;
						svgNodeProperties.setXScale(yScale);
						sysLog.info("Set yScale from css:" + yScale);
					}
				}
				break;
			case "-fx-rotate":
				//rotate
				if (svgNodeProperties.getRotation() == null) {
					Object rotationObj = cssMetaData.getInitialValue(null);
					if (rotationObj != null) {
						double rotation = (double) rotationObj;
						svgNodeProperties.setXScale(rotation);
						sysLog.info("Set rotation from css:" + rotation);
					}
				}
				break;

			case "-fx-fill":
				//fill color
				if (svgNodeProperties.getFill() == null) {
					try {
						Color fillColor = (Color) cssMetaData.getInitialValue(null);
						if (fillColor != null) {
							String fill = paintToColorString(fillColor);
							svgNodeProperties.setFill(fill);
							sysLog.info("Set fill from css:" + fill);
						}
					} catch (NullPointerException exception) {}
				}
				break;

			case "-fx-region-background":
				//background properties (fill color)
				//if (svgNodeProperties.getFill() == null) {
				sysLog.info("region background css data ##############");
				List<CssMetaData<? extends Styleable, ?>> subCssMetaDataList = cssMetaData.getSubProperties();
				for (CssMetaData<? extends Styleable, ?> subCssMetaData : subCssMetaDataList) {
					SubCssMetaData<?> subData = (SubCssMetaData<?>) subCssMetaData;
					sysLog.info(subData);
					String subProperty = subData.getProperty();
					switch (subProperty) {
					case "-fx-background-color":
						//fill color
						try {
							Color fillColor = (Color) subData.getStyleableProperty(node).getValue();
							//if (fillColor != null) {
							String fill = paintToColorString(fillColor);
							svgNodeProperties.setFill(fill);
							sysLog.info("Set fill from css:" + fill);
							//}
						} catch (NullPointerException exception) {}
						break;
					default:
						//property is not yet implemented
					}
				}
				//}
				break;
			case "-fx-region-border":
				//border properties (stroke color)
				//if (svgNodeProperties.getFill() == null) {
				subCssMetaDataList = cssMetaData.getSubProperties();
				sysLog.info("region border css data ##############");
				for (CssMetaData<? extends Styleable, ?> subCssMetaData : subCssMetaDataList) {
					SubCssMetaData<?> subData = (SubCssMetaData<?>) subCssMetaData;
					sysLog.info(subData);
					String subProperty = subData.getProperty();
					switch (subProperty) {
					case "-fx-border-color":
						//stroke color
						try {
							Region region = (Region) node;
							StyleableProperty<?> borderProperty = subData.getStyleableProperty(region);
							Object strokeColor = borderProperty.getValue();
							//if (fillColor != null) {
							String stroke = paintToColorString((Color) strokeColor);
							svgNodeProperties.setStroke(stroke);
							sysLog.info("Set stroke from css:" + stroke);
							//}
						} catch (NullPointerException exception) {}
						break;
					case "-fx-border-width":
						//stroke width
						try {
							StyleableProperty<?> cssProperty = subData.getStyleableProperty(node);
							if (cssProperty != null) {
								Double strokeWidth = (Double) cssProperty.getValue();
								if (strokeWidth != null) {

									svgNodeProperties.setStrokeWidth(strokeWidth);
									sysLog.info("Set strokeWidth from css:" + strokeWidth);
								}
							}
						} catch (NullPointerException exception) {}
						break;
					default:
						//property is not yet implemented
					}
				}
				//}
				break;
			case "-fx-background-color":
				//fill color
				if (svgNodeProperties.getFill() == null) {
					try {
						Color fillColor = (Color) cssMetaData.getStyleableProperty(null).getValue();
						if (fillColor != null) {
							String fill = paintToColorString(fillColor);
							svgNodeProperties.setFill(fill);
							sysLog.info("Set fill from css:" + fill);
						}
					} catch (NullPointerException exception) {}
				}
				break;
			case "-fx-background-radius":
				//background radius
				if (svgNodeProperties.getFillRadius() == null) {
					Double rectRadius = (Double) cssMetaData.getInitialValue(null);
					if (rectRadius != null) {
						svgNodeProperties.setFillRadius(rectRadius);
						sysLog.info("Set rect radius from css:" + rectRadius);
					}
				}
				break;
			case "-fx-border-color":
				//stroke color
				if (svgNodeProperties.getStroke() == null) {
					try {
						Color strokeColor = (Color) cssMetaData.getInitialValue(null);
						if (strokeColor != null) {
							String stroke = paintToColorString(strokeColor);
							svgNodeProperties.setStroke(stroke);
							sysLog.info("Set stroke from css:" + stroke);
						}
					} catch (NullPointerException exception) {}
				}
				break;
			case "-fx-padding":
				//padding is not supported by svg
				//TODO: check if needs to be included in coordinates
				break;
			case "-fx-stroke":
				//stroke color
				if (svgNodeProperties.getStroke() == null) {
					try {
						Color strokeColor = (Color) cssMetaData.getInitialValue(null);
						if (strokeColor != null) {
							String stroke = paintToColorString(strokeColor);
							svgNodeProperties.setStroke(stroke);
							sysLog.info("Set stroke from css:" + stroke);
						}
					} catch (NullPointerException exception) {}
				}
				break;

			case "-fx-stroke-width":
				//stroke width
				if (svgNodeProperties.getStrokeWidth() == null) {
					Double strokeWidth = (Double) cssMetaData.getInitialValue(null);
					if (strokeWidth != null) {
						svgNodeProperties.setStrokeWidth(strokeWidth);
						sysLog.info("Set stroke width from css:" + strokeWidth);
					}
				}
				break;
			case "-fx-font-size":
				if (svgNodeProperties.getFontSize() == null) {
					String fontSize = (String) cssMetaData.getInitialValue(null);
					if (fontSize != null) {
						svgNodeProperties.setFontSize(fontSize);
						sysLog.info("Set font size from css:" + fontSize);
					}
				}

				break;
			default:
				//property is not yet implemented

				//also see https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html

				//stroke others

				//-fx-stroke-dash-offset

				//-fx-stroke-line-join
				//-fx-stroke-type
				//-fx-stroke-miter-limit

				//futher properties

				//visibility
				//-fx-smooth
				//-fx-font, initalValue: Font[name=System Regular, family=System, style=Regular, size=12.0],
				//-fx-font-family, initalValue: System
				//-fx-font-size, initalValue: 12.0
				//-fx-font-style, initalValue: REGULAR
				//-fx-font-weight, initalValue: NORMAL
				//-fx-underline, converter: BooleanConverter, initalValue: false, inherits: false, subProperties: []}
				//-fx-strikethrough, converter: BooleanConverter, initalValue: false, inherits: false, subProperties: []}
				//-fx-text-alignment, initalValue: LEFT
				//-fx-text-origin, initalValue: BASELINE
				//-fx-font-smoothing-type, initalValue: GRAY
				//-fx-line-spacing, converter: SizeConverter, initalValue: 0, inherits: false, subProperties: []}

				//-fx-padding,
				//-fx-region-background,
				//-fx-region-border,
				//-fx-opaque-insets,
				//-fx-scale-shape,
				//-fx-position-shape,
				//-fx-snap-to-pixel,
				//-fx-min-width,
				//-fx-pref-width,
				//-fx-max-width,
				//-fx-min-height,
				//-fx-pref-height,
				//-fx-max-height,
				//-fx-alignment,

			}

		}

		return svgNodeProperties;
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

	@SuppressWarnings("checkstyle:magicnumber")
	private static String colorToRGBCode(Color color) {

		int red = (int) (color.getRed() * 255);
		int green = (int) (color.getGreen() * 255);
		int blue = (int) (color.getBlue() * 255);

		return String.format("#%02X%02X%02X", red, green, blue);
	}

	//#end region

	//#region ACCESSORS

	//#end region

}
