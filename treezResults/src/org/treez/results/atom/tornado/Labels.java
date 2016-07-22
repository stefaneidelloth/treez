package org.treez.results.atom.tornado;

import org.apache.log4j.Logger;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.functions.AttributeStringDatumFunction;
import org.treez.javafxd3.d3.functions.AxisScaleInversedValueDatumFunction;
import org.treez.javafxd3.d3.functions.AxisScaleKeyDatumFunction;
import org.treez.javafxd3.d3.functions.AxisScaleValueDatumFunction;
import org.treez.javafxd3.d3.scales.Scale;
import org.treez.results.atom.graph.Graph;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Labels implements GraphicsPropertiesPageFactory {

	Logger LOG = Logger.getLogger(Labels.class);

	//#region ATTRIBUTES

	public final Attribute<String> labelMode = new Wrap<>();

	public final Attribute<String> font = new Wrap<>();

	public final Attribute<Integer> size = new Wrap<>();

	public final Attribute<String> color = new Wrap<>();

	public final Attribute<Boolean> italic = new Wrap<>();

	public final Attribute<Boolean> bold = new Wrap<>();

	public final Attribute<Boolean> underline = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page labelsPage = root.createPage("labels");

		Section labels = labelsPage.createSection("labels");

		labels.createEnumComboBox(labelMode, this, LabelMode.ABSOLUTE).setLabel("Label mode");

		labels.createFont(font, this);

		final int defaultFontSize = 14;
		labels.createIntegerVariableField(size, this, defaultFontSize) //
				.setLabel("Size");

		labels.createColorChooser(color, this, "black");

		labels.createCheckBox(italic, this);

		labels.createCheckBox(bold, this);

		labels.createCheckBox(underline, this);

		labels.createCheckBox(hide, this);

	}

	@Override
	public Selection plotWithD3(
			D3 d3,
			Selection tornadoSelection,
			Selection rectSelection,
			AbstractGraphicsAtom parent) {

		String parentName = parent.getName();

		Selection leftSelection = tornadoSelection //
				.select(".bar-rects-left"); //

		Selection rightSelection = tornadoSelection //
				.select(".bar-rects-right");

		Tornado tornado = (Tornado) parent;
		Graph graph = tornado.getGraph();
		double graphHeight = Length.toPx(graph.data.height.get());
		double graphWidth = Length.toPx(graph.data.width.get());

		org.treez.results.atom.axis.Axis inputAxis = tornado.data.getInputAxis();
		boolean inputAxisIsOrdinal = inputAxis.isOrdinal();
		boolean inputAxisIsHorizontal = inputAxis.isHorizontal();
		Scale<?> inputScale = tornado.data.getInputScale();

		org.treez.results.atom.axis.Axis outputAxis = tornado.data.getOutputAxis();
		boolean outputAxisIsOrdinal = outputAxis.isOrdinal();
		boolean outputAxisIsHorizontal = outputAxis.isHorizontal();
		Scale<?> outputScale = tornado.data.getOutputScale();

		String leftDataString = tornado.data.getLeftBarDataString();
		String rightDataString = tornado.data.getRightBarDataString();

		if (outputAxisIsHorizontal) {

			leftSelection.selectAll("text") //
					.data(leftDataString) //
					.enter() //
					.append("text")
					.attr("x", new AxisScaleValueDatumFunction(outputScale))
					.attr("y", new AxisScaleKeyDatumFunction(inputScale))
					.style("fill", "black")
					.text(new AttributeStringDatumFunction("input"));

			rightSelection.selectAll("text") //
					.data(rightDataString) //
					.enter() //
					.append("text")
					.attr("x", new AxisScaleValueDatumFunction(outputScale))
					.attr("y", new AxisScaleKeyDatumFunction(inputScale))
					.style("fill", "black")
					.text(new AttributeStringDatumFunction("input"));

		} else {

			leftSelection.selectAll("text") //
					.data(leftDataString) //
					.enter() //
					.append("text")
					.attr("x", new AxisScaleKeyDatumFunction(inputScale))
					.attr("y", new AxisScaleInversedValueDatumFunction(outputScale, graphHeight))
					.style("fill", "black")
					.text(new AttributeStringDatumFunction("input"));

			rightSelection.selectAll("text") //
					.data(rightDataString) //
					.enter() //
					.append("text")
					.attr("x", new AxisScaleKeyDatumFunction(inputScale))
					.attr("y", new AxisScaleInversedValueDatumFunction(outputScale, graphHeight))
					.style("fill", "black")
					.text(new AttributeStringDatumFunction("input"));
		}

		Selection textSelection = tornadoSelection.selectAll("g").selectAll("text");

		formatText(textSelection);

		return tornadoSelection;
	}

	public Selection formatText(Selection textSelection) {
		AbstractGraphicsAtom.bindStringAttribute(textSelection, "font-family", font);
		AbstractGraphicsAtom.bindIntegerAttribute(textSelection, "font-size", size);
		AbstractGraphicsAtom.bindStringStyle(textSelection, "fill", color);
		AbstractGraphicsAtom.bindFontItalicStyle(textSelection, italic);
		AbstractGraphicsAtom.bindFontBoldStyle(textSelection, bold);
		AbstractGraphicsAtom.bindFontUnderline(textSelection, underline);
		AbstractGraphicsAtom.bindTransparencyToBooleanAttribute(textSelection, hide);

		/*
		Consumer refreshLegendLayout = () -> main.refresh();
		font.addModificationConsumer("font", refreshLegendLayout);
		size.addModificationConsumer("font", refreshLegendLayout);
		color.addModificationConsumer("font", refreshLegendLayout);
		italic.addModificationConsumer("font", refreshLegendLayout);
		bold.addModificationConsumer("font", refreshLegendLayout);
		underline.addModificationConsumer("font", refreshLegendLayout);
		 */

		return textSelection;
	}

	//#end region

}
