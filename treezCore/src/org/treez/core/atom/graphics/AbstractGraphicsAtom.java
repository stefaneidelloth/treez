package org.treez.core.atom.graphics;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import org.apache.log4j.Logger;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.attribute.comboBox.lineStyle.LineStyleValue;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Attribute;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.functions.MouseClickFunction;

/**
 * Parent class for the atoms that are used for plotting with javafx-d3. It contains some helper methods that make it
 * easier to bind atom attributes to d3 properties.
 */
public abstract class AbstractGraphicsAtom extends AdjustableAtom implements MouseClickFunction {

	private static final Logger LOG = Logger.getLogger(AbstractGraphicsAtom.class);

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	public AbstractGraphicsAtom(String name) {
		super(name);
	}

	//#end region

	//#region METHODS

	/**
	 * Calculates an approximate text size using AWT. (The getBBox method for SVG elements does not seem to be
	 * reliable.)
	 *
	 * @param fontName
	 * @param fontSize
	 * @return
	 */
	public static double estimateTextHeight(String fontName, int fontSize) {
		String text = "Hello World";
		AffineTransform affinetransform = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(affinetransform, true, true);

		Font font = new Font(fontName, Font.PLAIN, fontSize);
		int textheight = (int) (font.getStringBounds(text, frc).getHeight());
		return textheight;
	}

	/**
	 * Binds the name of this atom to the id attribute of the given selection
	 *
	 * @param selection
	 */
	protected void bindNameToId(Selection selection) {
		selection.attr("id", name);
		addNameModificationListener((newName) -> selection.attr("id", newName));
	}

	/**
	 * Binds the given Attribute to the JavaScript attribute with the given data. A change of the attribute will change
	 * the JavaScript attribute. The original value is trimmed and spaces are removed, e.g. " 1 cm " => "1cm".
	 *
	 * @param wrappingAttribute
	 * @param selection
	 * @param selectionAttributeName
	 */
	public static void bindStringAttribute(
			Selection selection,
			String selectionAttributeName,
			Attribute<String> wrappingAttribute) {

		String consumerKey = selectionAttributeName + System.currentTimeMillis();
		addModificationConsumerAndRun(consumerKey, wrappingAttribute, () -> {
			String newValue = trim(wrappingAttribute.get());
			selection.attr(selectionAttributeName, newValue);
		});
	}

	public static void bindIntegerAttribute(
			Selection selection,
			String selectionAttributeName,
			Attribute<Integer> wrappingAttribute) {

		String consumerKey = selectionAttributeName + System.currentTimeMillis();
		addModificationConsumerAndRun(consumerKey, wrappingAttribute, () -> {
			Integer newValue = wrappingAttribute.get();
			selection.attr(selectionAttributeName, newValue);
		});
	}

	public static void bindText(Selection text, Attribute<String> textAttribute) {

		String consumerKey = "text" + +System.currentTimeMillis();
		addModificationConsumerAndRun(consumerKey, textAttribute, () -> {
			String newValue = textAttribute.get();
			text.text(newValue);
		});
	}

	public static void bindTransparency(Selection selection, Attribute<Double> transparency) {

		String consumerKey = "updateTransparency" + System.currentTimeMillis();
		transparency.addModificationConsumerAndRun(consumerKey, () -> {
			try {
				double transparencyValue = transparency.get();
				double opacity = 1 - transparencyValue;
				selection.attr("fill-opacity", "" + opacity);
			} catch (NumberFormatException exception) {

			}
		});
	}

	public static void bindTransparencyByStyle(Selection selection, Attribute<Double> transparency) {

		String consumerKey = "updateTransparency" + System.currentTimeMillis();
		transparency.addModificationConsumerAndRun(consumerKey, () -> {
			try {
				double transparencyValue = transparency.get();
				double opacity = 1 - transparencyValue;
				selection.style("fill-opacity", "" + opacity);
			} catch (NumberFormatException exception) {

			}
		});
	}

	public static void bindTransparencyToBooleanAttribute(
			Selection selection,
			Attribute<Boolean> hide,
			Attribute<Double> transparency) {

		String consumerKey = "hideFill" + +System.currentTimeMillis();
		hide.addModificationConsumerAndRun(consumerKey, () -> {
			try {
				boolean doHide = hide.get();
				if (doHide) {
					selection.attr("fill-opacity", "0");
				} else {
					double transparencyValue = transparency.get();
					double opacity = 1 - transparencyValue;
					selection.attr("fill-opacity", "" + opacity);
				}
			} catch (NumberFormatException exception) {}
		});
	}

	public static void bindTransparencyToBooleanAttributeByStyle(
			Selection selection,
			Attribute<Boolean> hide,
			Attribute<Double> transparency) {

		String consumerKey = "hideFillByStyle" + +System.currentTimeMillis();
		hide.addModificationConsumerAndRun(consumerKey, () -> {
			try {
				boolean doHide = hide.get();
				if (doHide) {
					selection.style("fill-opacity", "0");
				} else {
					double transparencyValue = transparency.get();
					double opacity = 1 - transparencyValue;
					selection.style("fill-opacity", "" + opacity);
				}
			} catch (NumberFormatException exception) {}
		});
	}

	public static void bindTransparencyToBooleanAttribute(Selection selection, Attribute<Boolean> hide) {
		String consumerKey = "hideFill" + +System.currentTimeMillis();
		hide.addModificationConsumerAndRun(consumerKey, () -> {
			boolean doHide = hide.get();
			if (doHide) {
				selection.attr("fill-opacity", "0");
			} else {
				selection.attr("fill-opacity", "1");
			}
		});

	}

	/**
	 * public void bindStringAttribute(Selection selection, String selectionAttributeName, Attribute <String>
	 * wrappingAttribute) { //set initial value selection.attr(selectionAttributeName, trim(wrappingAttribute.get()));
	 * //create one way binding addModificationConsumer(wrappingAttribute, (newValue) -> selection
	 * .attr(selectionAttributeName, trim(newValue))); } /** If the state of the Boolean attribute is true, the dislpay
	 * of the selection will be set to 'none', meaning it is not visible. If the State is false, the display will be set
	 * to 'inline', meaning it is visible.
	 *
	 * @param wrappingAttribute
	 * @param selection
	 */
	public static
			void
			bindDisplayToBooleanAttribute(String key, Selection selection, Attribute<Boolean> wrappingAttribute) {

		addModificationConsumerAndRun(key, wrappingAttribute, () -> {
			Boolean state = wrappingAttribute.get();
			if (state) {
				selection.attr("display", "none");
			} else {
				selection.attr("display", "inline");
			}
		});

	}

	/**
	 * @param leftMargin
	 * @param topMargin
	 */
	public static void bindTranslationAttribute(
			String key,
			Selection selection,
			Attribute<String> leftMargin,
			Attribute<String> topMargin) {

		updateTranslation(selection, leftMargin, topMargin);

		addModificationConsumerStatic(key, leftMargin, () -> {
			updateTranslation(selection, leftMargin, topMargin);
		});

		addModificationConsumerStatic(key, topMargin, () -> {
			updateTranslation(selection, leftMargin, topMargin);
		});

	}

	private static
			void
			updateTranslation(Selection selection, Attribute<String> leftMargin, Attribute<String> topMargin) {

		try {
			String xString = leftMargin.get();
			String yString = topMargin.get();
			Double x = Length.toPx(xString);
			Double y = Length.toPx(yString);
			String transformString = "translate(" + x + "," + y + ")";
			selection.attr("transform", transformString);
		} catch (IllegalArgumentException exception) {

		}
	}

	public static void bindStringStyle(Selection selection, String styleName, Attribute<String> style) {
		String consumerKey = "updateStyle" + +System.currentTimeMillis();
		style.addModificationConsumerAndRun(consumerKey, () -> {
			selection.style(styleName, style.get());
		});
	}

	public static void bindDoubleStyle(Selection selection, String styleName, Attribute<Double> style) {
		String consumerKey = "updateStyle" + +System.currentTimeMillis();
		style.addModificationConsumerAndRun(consumerKey, () -> {
			selection.style(styleName, style.get());
		});
	}

	public static void bindLineStyle(Selection selection, Attribute<String> style) {

		String consumerKey = "updateLineStyle" + +System.currentTimeMillis();
		style.addModificationConsumerAndRun(consumerKey, () -> {
			String lineStyleString = style.get();
			LineStyleValue lineStyle = LineStyleValue.fromString(lineStyleString);
			String dashArray = lineStyle.getDashArray();
			selection.style("stroke-dasharray", dashArray);
		});
	}

	public static void bindLineTransparency(Selection selection, Attribute<Double> transparency) {

		String consumerKey = "updateLineTransparency" + +System.currentTimeMillis();
		transparency.addModificationConsumerAndRun(consumerKey, () -> {
			try {
				double lineTransparency = transparency.get();
				double opacity = 1 - lineTransparency;
				selection.style("stroke-opacity", "" + opacity);
			} catch (NumberFormatException exception) {

			}
		});
	}

	/**
	 * @param selection
	 * @param hide
	 * @param transparency
	 */
	public static void bindLineTransparencyToBooleanAttribute(
			Selection selection,
			Attribute<Boolean> hide,
			Attribute<Double> transparency) {

		String consumerKey = "hideLine" + +System.currentTimeMillis();
		hide.addModificationConsumerAndRun(consumerKey, () -> {
			try {
				boolean doHide = hide.get();
				if (doHide) {
					selection.style("stroke-opacity", "0");
				} else {
					double lineTransparency = transparency.get();
					double opacity = 1 - lineTransparency;
					selection.style("stroke-opacity", "" + opacity);
				}
			} catch (NumberFormatException exception) {

			}
		});

	}

	/**
	 * @param selection
	 * @param rotate
	 * @param isHorizontal
	 */
	public static void bindTransformRotate(Selection selection, Attribute<String> rotate, boolean isHorizontal) {

		String consumerKey = "transformRotate" + +System.currentTimeMillis();
		rotate.addModificationConsumerAndRun(consumerKey, () -> {
			String angleString = rotate.get();
			double rotation = 0;
			try {
				rotation = Double.parseDouble(angleString);
			} catch (NumberFormatException exception) {}
			if (!isHorizontal) {
				final int extraVerticalRotation = 90;
				rotation += extraVerticalRotation;
			}
			String transform = "rotate(" + rotation + ")";
			selection.attr("transform", transform);
		});

	}

	/**
	 * @param text
	 * @param italic
	 */
	public static void bindFontItalicStyle(Selection text, Attribute<Boolean> italic) {

		String consumerKey = "italic" + +System.currentTimeMillis();
		italic.addModificationConsumerAndRun(consumerKey, () -> {
			boolean isActive = italic.get();
			if (isActive) {
				text.attr("font-style", "italic");
			} else {
				text.attr("font-style", "normal");
			}
		});
	}

	/**
	 * @param text
	 * @param bold
	 */
	public static void bindFontBoldStyle(Selection text, Attribute<Boolean> bold) {

		String consumerKey = "bold" + +System.currentTimeMillis();
		bold.addModificationConsumerAndRun(consumerKey, () -> {
			boolean isActive = bold.get();
			if (isActive) {
				text.attr("font-weight", "bold");
			} else {
				text.attr("font-weight", "normal");
			}
		});
	}

	/**
	 * @param text
	 * @param underline
	 */
	public static void bindFontUnderline(Selection text, Attribute<Boolean> underline) {

		String consumerKey = "underline" + +System.currentTimeMillis();
		underline.addModificationConsumerAndRun(consumerKey, () -> {
			boolean isActive = underline.get();
			if (isActive) {
				text.attr("text-decoration", "underline");
			} else {
				text.attr("text-decoration", "none");
			}
		});

	}

	/**
	 * Applies toString() to the given object, trims the result and removes spaces
	 *
	 * @param value
	 * @return
	 */
	protected static String trim(Object value) {
		String result = value.toString().trim().replace(" ", "");
		return result;
	}

	/**
	 * Handles JavaScript mouse click
	 */
	@Override
	public void handleMouseClick(Object context) {
		LOG.debug("Setting focus");
		setFocus(this);
	}

	//#end region

}
