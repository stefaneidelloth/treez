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
import org.treez.results.atom.graphicspage.GraphicsPropertiesPageFactory;

import javafx.geometry.BoundingBox;

/**
 * Represents the label for an axis
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class AxisLabel implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	public final Attribute<String> font = new Wrap<>();

	public final Attribute<String> size = new Wrap<>();

	public final Attribute<String> color = new Wrap<>();

	public final Attribute<Boolean> italic = new Wrap<>();

	public final Attribute<Boolean> bold = new Wrap<>();

	public final Attribute<Boolean> underline = new Wrap<>();

	//public final Attribute<Boolean> atEdge = new Wrap<>();

	public final Attribute<String> rotate = new Wrap<>();

	public final Attribute<String> labelOffset = new Wrap<>();

	public final Attribute<String> position = new Wrap<>();

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

		//remove label group if it already exists
		axisSelection //
				.select("#axis-label")
				.remove();

		//create new label
		Selection label = axisSelection//
				.append("g")
				.attr("id", "axis-label")
				.append("text");

		Consumer<String> geometryConsumer = (data) -> {
			Graph graph = (Graph) axis.getParentAtom();
			updateLabelGeometry(axis, label, graph);
		};

		position.addModificationConsumer("position", geometryConsumer);
		rotate.addModificationConsumer("position", geometryConsumer);
		labelOffset.addModificationConsumer("position", geometryConsumer);

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

	private void updateLabelGeometry(Axis axis, Selection label, Graph graph) {

		String positionString = position.get();
		setTextAnchor(label, positionString);

		double rotation = getRotation();

		//initial transformation
		applyTransformation(label, 0, 0, rotation);

		//get actual text geometry and update transformation
		Element labelNode = label.node().getParentElement(); //label group
		BoundingBox boundingBox = labelNode.getBBox();
		double labelHeight = determineLabelHeight(boundingBox);

		boolean isHorizontal = axis.data.isHorizontal();
		if (isHorizontal) {
			applyTransformationForHorizontalOrientation(graph, axis, label, positionString, rotation, labelHeight);
		} else {
			applyTransformationForVerticalOrientation(graph, axis, label, positionString, rotation, labelHeight);
		}
	}

	private static void setTextAnchor(Selection label, String positionString) {
		if (positionString.equals("at-minimum")) {
			label.attr("text-anchor", "start");
		} else if (positionString.equals("centre")) {
			label.attr("text-anchor", "middle");
		} else {
			label.attr("text-anchor", "end");
		}
	}

	private double getRotation() {
		String angleString = rotate.get();
		double rotation = 0;
		try {
			rotation = -Double.parseDouble(angleString);
		} catch (NumberFormatException exception) {}
		return rotation;
	}

	private void applyTransformationForVerticalOrientation(
			Graph graph,
			Axis axis,
			Selection label,
			String positionString,
			double rotation,
			double labelHeight) {

		double offset = getPxLength(labelOffset);
		double tickOffset = getPxLength(axis.tickLabels.offset);
		double graphHeight = getPxLength(graph.data.height);

		Double tickLabelWidth = axis.tickLabels.getTickLabelWidth();
		final int extraVerticalRotation = -90;
		double verticalRotation = rotation + extraVerticalRotation;

		double x = -(tickOffset + tickLabelWidth + offset + labelHeight);
		double y = graphHeight;
		if (positionString.equals("centre")) {
			y = graphHeight / 2;
		} else if (positionString.equals("at-maximum")) {
			y = 0;
		}
		applyTransformation(label, x, y, verticalRotation);
	}

	private void applyTransformationForHorizontalOrientation(
			Graph graph,
			Axis axis,
			Selection label,
			String positionString,
			double rotation,
			double labelHeight) {

		double offset = getPxLength(labelOffset);
		double tickOffset = getPxLength(axis.tickLabels.offset);

		double graphWidth = getPxLength(graph.data.width);
		double graphHeight = getPxLength(graph.data.height);

		Double tickLabelHeight = axis.tickLabels.getTickLabelHeight();

		double x = 0.0;
		if (positionString.equals("centre")) {
			x = graphWidth / 2;
		} else if (positionString.equals("at-maximum")) {
			x = graphWidth;
		}

		double y = graphHeight + tickOffset + tickLabelHeight + offset + labelHeight;
		applyTransformation(label, x, y, rotation);
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

	//#end region

}
