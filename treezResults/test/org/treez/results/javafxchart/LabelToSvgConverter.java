package org.treez.results.javafxchart;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Converts a Label to SVG code
 */
public class LabelToSvgConverter extends AbstractNodeToSvgConverter<Label> {

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	//#end region

	//#region METHODS

	/**
	 * Converts a Label to SVG code (without SVG header and end tags)
	 *
	 * @return
	 */
	@Override
	public String extendCode(String initialSvgString, Label label) {

		//comment
		String commentString = createComment(label);

		//label image
		String imageSvgString = createImageSvgStringFromLabel(label);

		//text
		String text = label.getText();

		//background color
		String backgroundFill = determineBackgroundFill(label);
		boolean hasBackground = backgroundFill != null;

		//x & y
		Bounds bounds = label.getBoundsInParent();
		Double x = bounds.getMinX();
		boolean hasImage = !imageSvgString.isEmpty();
		if (hasImage) {
			Node image = label.getGraphic();
			Double xOffset = image.getBoundsInParent().getMaxX();
			x = x + xOffset;
		}
		Double y = bounds.getHeight();

		//font
		Font font = label.getFont();
		String fontFamily = font.getFamily();
		Double fontSize = font.getSize();

		//font color
		Paint textFill = label.getTextFill();
		String fill = paintToColorString(textFill);

		//text anchor
		SvgTextAnchor textAnchor = determineTextAnchor(label);

		//comment
		String svgString = commentString;

		//<rect> start
		boolean wrapInRect = hasImage || hasBackground;
		if (wrapInRect) {
			svgString = includeRectStartTag(svgString, imageSvgString, backgroundFill, hasBackground, bounds);
		}

		//<text> start
		svgString = includeTextStartTag(svgString, x, y, fontFamily, fontSize, fill, textAnchor);

		//<text> content
		svgString = svgString + text;

		//<text> end
		svgString = svgString + "</text>\n\n";

		//<rect> end
		if (wrapInRect) {
			decreaseIndentation();
			svgString = includeRectEndTag(svgString);
		}

		return svgString;

	}

	private String includeTextStartTag(
			String initialSvgString,
			Double x,
			Double y,
			String fontFamily,
			Double fontSize,
			String fill,
			SvgTextAnchor textAnchor) {
		//@formatter:off
		String svgString = initialSvgString + indentation + "<text "
				+ "x=\""+ x + "\" "
				+ "y=\""+ y + "\" "
				+ "font-family=\""+ fontFamily + "\" "
				+ "font-size=\""+ fontSize + "\" ";
		//@formatter:on

		if (fill != null) {
			svgString = svgString + "fill=\"" + fill + "\"";
		}

		if (!textAnchor.equals(SvgTextAnchor.LEFT)) {
			svgString = svgString + "text-anchor=\"" + textAnchor + "\"";
		}

		svgString = svgString + ">";
		return svgString;
	}

	private static String determineBackgroundFill(Label label) {
		String backgroundFill = null;
		Background background = label.getBackground();
		if (background != null) {
			backgroundFill = backgroundToColorString(background);
		}
		return backgroundFill;
	}

	private static SvgTextAnchor determineTextAnchor(Label label) {
		TextAlignment textAlignment = label.getTextAlignment();
		SvgTextAnchor textAnchor;
		switch (textAlignment) {
		case LEFT:
			textAnchor = SvgTextAnchor.LEFT;
			break;
		case CENTER:
			textAnchor = SvgTextAnchor.MIDDLE;
			break;
		case RIGHT:
			textAnchor = SvgTextAnchor.END;
			break;
		case JUSTIFY:
			textAnchor = SvgTextAnchor.LEFT;
			break;
		default:
			String message = "The text alignment '" + textAlignment + "' is not known.";
			throw new IllegalStateException(message);
		}
		return textAnchor;
	}

	private String includeRectStartTag(
			String initialSvgString,
			String imageSvgString,
			String backgroundFill,
			boolean hasBackground,
			Bounds bounds) {

		String svgString = initialSvgString + indentation + "<g>\n";
		increaseIndentation();
		Double width = bounds.getWidth();
		Double height = bounds.getHeight();
		String rectString = "<rect width=\"" + width + "\" height=\"" + height + "\"";
		if (hasBackground) {
			rectString = rectString + " fill=\"" + backgroundFill + "\"";
		}
		rectString = rectString + "/>\n\n";
		svgString = svgString + indentation + rectString;

		svgString = svgString + imageSvgString;
		return svgString;
	}

	private String includeRectEndTag(String initialSvgString) {
		String svgString = initialSvgString + indentation + "</g>\n\n";
		return svgString;
	}

	private String createComment(Label label) {
		String className = label.getClass().getName();
		String commentString = indentation + "<!-- " + className + "-->\n";
		return commentString;
	}

	private String createImageSvgStringFromLabel(Label label) {
		String imageSvgString = "";
		Node image = label.getGraphic();
		if (image != null) {

			NodeToSvgConverter nodeConverter = new NodeToSvgConverter();
			nodeConverter.setIndentation(indentation);
			nodeConverter.increaseIndentation();
			imageSvgString = nodeConverter.extendCode("", image);

		}
		return imageSvgString;
	}

	//#end region

}
