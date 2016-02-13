package org.treez.results.atom.axis;

import java.util.function.Consumer;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.wrapper.Element;
import org.treez.results.atom.veuszpage.GraphicsPageModel;

import javafx.geometry.BoundingBox;

/**
 * Represents the tick labels
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class TickLabels implements GraphicsPageModel {

	//#region ATTRIBUTES

	/**
	 * Font
	 */
	public final Attribute<String> font = new Wrap<>();

	/**
	 * Size
	 */
	public final Attribute<String> size = new Wrap<>();

	/**
	 * Color
	 */
	public final Attribute<String> color = new Wrap<>();

	/**
	 * Format
	 */
	public final Attribute<String> format = new Wrap<>();

	/**
	 * Italic style
	 */
	public final Attribute<Boolean> italic = new Wrap<>();

	/**
	 * Bold style
	 */
	public final Attribute<Boolean> bold = new Wrap<>();

	/**
	 * Underline style
	 */
	public final Attribute<Boolean> underline = new Wrap<>();

	/**
	 * Rotation
	 */
	public final Attribute<String> rotate = new Wrap<>();

	/**
	 * Offset
	 */
	public final Attribute<String> offset = new Wrap<>();

	/**
	 * Hide
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	private Double tickLabelHeight = null;

	private Double tickLabelWidth = null;

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page axisLabelPage = root.createPage("tickLabels", "   Tick labels   ");

		Section tickLabels = axisLabelPage.createSection("tickLabels", "Tick labels");

		tickLabels.createFont(font, "font");

		tickLabels.createTextField(size, "size", "20");

		tickLabels.createColorChooser(color, "color", "black");

		tickLabels.createTextField(format, "format", "");

		tickLabels.createCheckBox(italic, "italic");

		tickLabels.createCheckBox(bold, "bold");

		tickLabels.createCheckBox(underline, "underline");

		tickLabels.createCheckBox(hide, "hide");

		tickLabels.createComboBox(rotate, "rotate", "-180,-135,-90,-45,0,45,90,135,180", "0");

		tickLabels.createTextField(offset, "offset", "4");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection axisSelection, Selection rectSelection, GraphicsAtom parent) {

		Axis axis = (Axis) parent;
		boolean isHorizontal = axis.data.isHorizontal();

		Selection tickLabels = axisSelection //
				.selectAll(".primary")
				.selectAll(".tick")
				.selectAll("text");

		//remove default shift
		if (isHorizontal) {
			tickLabels.attr("dy", "0");
		}

		Consumer<String> geometryConsumer = (data) -> {

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
				Element tickNode = firstNode.getParentElement();
				BoundingBox boundingBox = tickNode.getBBox();
				tickLabelWidth = boundingBox.getWidth();
				double deltaX = -boundingBox.getMaxX();
				x += deltaX - tickOffset;
			}

			applyTransformation(tickLabels, x, y, rotation);

		};

		rotate.addModificationConsumer("position", geometryConsumer);
		offset.addModificationConsumer("position", geometryConsumer);
		size.addModificationConsumer("position", geometryConsumer);

		geometryConsumer.accept(null);

		GraphicsAtom.bindStringAttribute(tickLabels, "font-family", font);
		GraphicsAtom.bindStringAttribute(tickLabels, "font-size", size);
		GraphicsAtom.bindStringAttribute(tickLabels, "fill", color);
		GraphicsAtom.bindFontItalicStyle(tickLabels, italic);
		GraphicsAtom.bindFontBoldStyle(tickLabels, bold);
		GraphicsAtom.bindFontUnderline(tickLabels, underline);
		GraphicsAtom.bindTransparencyToBooleanAttribute(tickLabels, hide);

		return axisSelection;
	}

	private double determineTickLabelHeight(Selection tickLabels) {
		Element firstNode = tickLabels.node();
		BoundingBox boundingBox = firstNode.getBBox();
		double svgTickLabelHeight = boundingBox.getHeight();
		String fontName = font.get();
		String fontSizeString = size.get();
		int fontSize = (int) Double.parseDouble(fontSizeString);
		double awtTextHeight = GraphicsAtom.estimateTextHeight(fontName, fontSize);

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

	@Override
	public String createVeuszText(AbstractAtom parent) {
		return "";
	}

	//#end region

	//#region ACCESSORS

	/**
	 * @return
	 */
	public Double getTickLabelHeight() {
		return tickLabelHeight;
	}

	/**
	 * @return
	 */
	public Double getTickLabelWidth() {
		return tickLabelWidth;
	}

	//#end region

}
