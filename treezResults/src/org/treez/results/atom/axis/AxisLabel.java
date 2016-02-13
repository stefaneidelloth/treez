package org.treez.results.atom.axis;

import java.util.function.Consumer;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.wrapper.Element;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.veuszpage.GraphicsPageModel;

import javafx.geometry.BoundingBox;

/**
 * Represents the label for an axis
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class AxisLabel implements GraphicsPageModel {

	//#region ATTRIBUTES

	/**
	 * Label font
	 */
	public final Attribute<String> font = new Wrap<>();

	/**
	 * Label size
	 */
	public final Attribute<String> size = new Wrap<>();

	/**
	 * Label color
	 */
	public final Attribute<String> color = new Wrap<>();

	/**
	 * Label text italic style
	 */
	public final Attribute<Boolean> italic = new Wrap<>();

	/**
	 * Label text bold style
	 */
	public final Attribute<Boolean> bold = new Wrap<>();

	/**
	 * Label text underline
	 */
	public final Attribute<Boolean> underline = new Wrap<>();

	/**
	 * Label at edge
	 */
	//public final Attribute<Boolean> atEdge = new Wrap<>();

	/**
	 * Label rotation
	 */
	public final Attribute<String> rotate = new Wrap<>();

	/**
	 * Label offset
	 */
	public final Attribute<String> labelOffset = new Wrap<>();

	/**
	 * Label position
	 */
	public final Attribute<String> position = new Wrap<>();

	/**
	 * Hide
	 */
	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page axisLabelPage = root.createPage("axisLabel", "   Axis label   ");

		Section axisLabel = axisLabelPage.createSection("axisLabel", "Axis label");

		axisLabel.createFont(font, "font");

		axisLabel.createTextField(size, "size", "22");

		axisLabel.createColorChooser(color, "color", "black");

		axisLabel.createCheckBox(italic, "italic");

		axisLabel.createCheckBox(bold, "bold");

		axisLabel.createCheckBox(underline, "underline");

		axisLabel.createCheckBox(hide, "hide");

		//CheckBox atEdgeCheck = axisLabel.createCheckBox(atEdge, "atEdge");
		//atEdgeCheck.setLabel("At edge");

		axisLabel.createComboBox(rotate, "rotate", "-180,-135,-90,-45,0,45,90,135,180", "0");

		TextField offsetField = axisLabel.createTextField(labelOffset, "labelOffset", "4");
		offsetField.setLabel("Label offset");

		axisLabel.createComboBox(position, "position", "at-minimum,centre,at-maximum", "centre");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection axisSelection, Selection rectSelection, GraphicsAtom parent) {

		Axis axis = (Axis) parent;
		boolean isHorizontal = axis.data.isHorizontal();

		Selection label = axisSelection//
				.append("g")
				.attr("id", "axis-label")
				.append("text");

		Graph graph = (Graph) axis.getParentAtom();
		org.treez.results.atom.page.Page page = (org.treez.results.atom.page.Page) graph.getParentAtom();

		Consumer<String> geometryConsumer = (data) -> {

			double offset = getPxLength(labelOffset);
			double tickOffset = getPxLength(axis.tickLabels.offset);

			double fontSize = getPxLength(size);

			double graphLeftMargin = getPxLength(graph.main.leftMargin);
			double graphTopMargin = getPxLength(graph.main.topMargin);
			double graphWidth = getPxLength(graph.main.width);
			double graphHeight = getPxLength(graph.main.height);

			double pageHeight = getPxLength(page.pageHeight);

			String positionString = position.get();
			if (positionString.equals("at-minimum")) {
				label.attr("text-anchor", "start");
			} else if (positionString.equals("centre")) {
				label.attr("text-anchor", "middle");
			} else {
				label.attr("text-anchor", "end");
			}

			String angleString = rotate.get();
			double rotation = 0;
			try {
				rotation = -Double.parseDouble(angleString);
			} catch (NumberFormatException exception) {}

			//initial transformation
			applyTransformation(label, 0, 0, rotation);

			//get actual text geometry and update transformation
			Element labelNode = label.node().getParentElement(); //label group
			BoundingBox boundingBox = labelNode.getBBox();

			double labelHeight = determineLabelHeight(boundingBox);

			double x = 0.0;
			double y = 0.0;
			if (isHorizontal) {
				Double tickLabelHeight = axis.tickLabels.getTickLabelHeight();

				if (positionString.equals("centre")) {
					x = graphWidth / 2;
				} else if (positionString.equals("at-maximum")) {
					x = graphWidth;
				}

				y = graphHeight + tickOffset + tickLabelHeight + offset + labelHeight;

			} else {
				Double tickLabelWidth = axis.tickLabels.getTickLabelWidth();
				final int extraVerticalRotation = -90;
				rotation += extraVerticalRotation;

				x = -(tickOffset + tickLabelWidth + offset + labelHeight);

				y = graphHeight;
				if (positionString.equals("centre")) {
					y = graphHeight / 2;
				} else if (positionString.equals("at-maximum")) {
					y = 0;
				}
			}

			applyTransformation(label, x, y, rotation);

		};

		position.addModificationConsumer("position", geometryConsumer);
		rotate.addModificationConsumer("position", geometryConsumer);
		labelOffset.addModificationConsumer("position", geometryConsumer);

		graph.main.leftMargin.addModificationConsumer("position", geometryConsumer);
		graph.main.topMargin.addModificationConsumer("position", geometryConsumer);
		graph.main.width.addModificationConsumer("position", geometryConsumer);
		graph.main.height.addModificationConsumer("position", geometryConsumer);
		page.pageHeight.addModificationConsumer("position", geometryConsumer);

		geometryConsumer.accept(null);

		Attribute<String> labelAttribute = axis.data.label;
		GraphicsAtom.bindText(label, labelAttribute);
		GraphicsAtom.bindStringAttribute(label, "font-family", font);
		GraphicsAtom.bindStringAttribute(label, "font-size", size);
		GraphicsAtom.bindStringAttribute(label, "fill", color);
		GraphicsAtom.bindFontItalicStyle(label, italic);
		GraphicsAtom.bindFontBoldStyle(label, bold);
		GraphicsAtom.bindFontUnderline(label, underline);
		GraphicsAtom.bindTransparencyToBooleanAttribute(label, hide);

		return axisSelection;
	}

	private double determineLabelHeight(BoundingBox boundingBox) {
		double svgLabelHeight = boundingBox.getHeight();

		String fontName = font.get();
		String fontSizeString = size.get();
		int fontSize = (int) Double.parseDouble(fontSizeString);
		double awtTextHeight = GraphicsAtom.estimateTextHeight(fontName, fontSize);

		double height = Math.max(svgLabelHeight, awtTextHeight);
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

}
