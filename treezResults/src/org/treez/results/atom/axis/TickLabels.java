package org.treez.results.atom.axis;

import org.treez.core.atom.attribute.attributeContainer.AttributeRoot;
import org.treez.core.atom.attribute.attributeContainer.Page;
import org.treez.core.atom.attribute.attributeContainer.section.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.wrapper.Element;

import javafx.geometry.BoundingBox;

/**
 * Represents the tick labels
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class TickLabels implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> font = new Wrap<>();

	public final Attribute<String> size = new Wrap<>();

	public final Attribute<String> color = new Wrap<>();

	public final Attribute<String> format = new Wrap<>();

	public final Attribute<Boolean> italic = new Wrap<>();

	public final Attribute<Boolean> bold = new Wrap<>();

	public final Attribute<Boolean> underline = new Wrap<>();

	public final Attribute<String> rotate = new Wrap<>();

	public final Attribute<String> offset = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	private Double tickLabelHeight = 0.0;

	private Double tickLabelWidth = 0.0;

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page axisLabelPage = root.createPage("tickLabels", "   Tick labels   ");

		Section tickLabels = axisLabelPage.createSection("tickLabels", "Tick labels");

		tickLabels.createFont(font, this);

		tickLabels.createTextField(size, this, "20");

		tickLabels.createColorChooser(color, this, "black");

		tickLabels.createTextField(format, this, "");

		tickLabels.createCheckBox(italic, this);

		tickLabels.createCheckBox(bold, this);

		tickLabels.createCheckBox(underline, this);

		tickLabels.createCheckBox(hide, this);

		tickLabels.createComboBox(rotate, this, "-180,-135,-90,-45,0,45,90,135,180", "0");

		tickLabels.createTextField(offset, this, "4");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection axisSelection, Selection rectSelection, AbstractGraphicsAtom parent) {

		//Hint: The major ticks already have been created with the axis (see Data).
		//Here only the properties of the tick labels need to be applied.

		//get tick labels
		Selection tickLabels = axisSelection //
				.selectAll(".primary").selectAll(".tick").selectAll("text");

		//remove default shift
		Axis axis = (Axis) parent;
		boolean isHorizontal = axis.data.isHorizontal();
		if (isHorizontal) {
			tickLabels.attr("dy", "0");
		}

		//update label geometry
		Consumer geometryConsumer = () -> {
			updateLabelGeometry(tickLabels, isHorizontal);
		};
		rotate.addModificationConsumer("position", geometryConsumer);
		offset.addModificationConsumer("position", geometryConsumer);
		size.addModificationConsumer("position", geometryConsumer);

		geometryConsumer.consume();

		//bind attributes
		AbstractGraphicsAtom.bindStringAttribute(tickLabels, "font-family", font);
		AbstractGraphicsAtom.bindStringAttribute(tickLabels, "font-size", size);
		AbstractGraphicsAtom.bindStringAttribute(tickLabels, "fill", color);
		AbstractGraphicsAtom.bindFontItalicStyle(tickLabels, italic);
		AbstractGraphicsAtom.bindFontBoldStyle(tickLabels, bold);
		AbstractGraphicsAtom.bindFontUnderline(tickLabels, underline);
		AbstractGraphicsAtom.bindTransparencyToBooleanAttribute(tickLabels, hide);

		format.addModificationConsumer("replotAxis", () -> axis.updatePlotWithD3(d3));

		return axisSelection;
	}

	private void updateLabelGeometry(Selection tickLabels, boolean isHorizontal) {
		double tickOffset = getPxLength(offset);

		String angleString = rotate.get();
		double rotation = 0;
		try {
			rotation = -Double.parseDouble(angleString);
		} catch (NumberFormatException exception) {}

		//initial transform
		applyTransformation(tickLabels, 0, 0, rotation);

		//get actual text geometry and update transformation
		double x = 0;
		double y = 0;

		if (isHorizontal) {
			tickLabelHeight = determineTickLabelHeight(tickLabels);
			y += tickLabelHeight + tickOffset;
		} else {

			Element firstNode = tickLabels.node();
			if (firstNode != null) {
				Element tickNode = firstNode.getParentElement();
				BoundingBox boundingBox = tickNode.getBBox();
				tickLabelWidth = boundingBox.getWidth();
				double deltaX = -boundingBox.getMaxX();
				x += deltaX - tickOffset;
			}

		}

		applyTransformation(tickLabels, x, y, rotation);
	}

	private double determineTickLabelHeight(Selection tickLabels) {
		Element firstNode = tickLabels.node();
		if (firstNode == null) {
			return 0.0;
		}
		BoundingBox boundingBox = firstNode.getBBox();
		double svgTickLabelHeight = boundingBox.getHeight();
		String fontName = font.get();
		String fontSizeString = size.get();
		int fontSize = (int) Double.parseDouble(fontSizeString);
		double awtTextHeight = AbstractGraphicsAtom.estimateTextHeight(fontName, fontSize);

		double height = Math.max(svgTickLabelHeight, awtTextHeight);
		return height;
	}

	private static void applyTransformation(Selection tickLabels, double x, double y, double rotation) {
		String transformString = "translate(" + x + "," + y + "),rotate(" + rotation + ")";
		tickLabels.attr("transform", transformString);
	}

	private static Double getPxLength(Attribute<String> attribute) {
		String stringValue = attribute.get();
		Double doubleValue = Length.toPx(stringValue);
		return doubleValue;
	}

	//#end region

	//#region ACCESSORS

	public Double getTickLabelHeight() {
		return tickLabelHeight;
	}

	public Double getTickLabelWidth() {
		return tickLabelWidth;
	}

	//#end region

}
