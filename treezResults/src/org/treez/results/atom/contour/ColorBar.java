package org.treez.results.atom.contour;

import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.EnumComboBox;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.AbstractGraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.core.Transform;
import org.treez.javafxd3.plotly.Plotly;
import org.treez.javafxd3.plotly.data.contour.colorbar.LenMode;
import org.treez.javafxd3.plotly.data.contour.colorbar.TickPosition;
import org.treez.javafxd3.plotly.data.contour.colorbar.TitleSide;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.legend.HorizontalPosition;
import org.treez.results.atom.legend.PositionReference;
import org.treez.results.atom.legend.VerticalPosition;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class ColorBar implements GraphicsPropertiesPageFactory {

	//#region ATTRIBUTES

	private static final int MARGIN_AROUND_LEGEND_IN_PX = 20;

	public final Attribute<String> positionReference = new Wrap<>();

	public final Attribute<String> horizontalPosition = new Wrap<>();

	public final Attribute<String> verticalPosition = new Wrap<>();

	public final Attribute<Integer> manualHorizontalPosition = new Wrap<>();

	public final Attribute<Integer> manualVerticalPosition = new Wrap<>();

	public final Attribute<Integer> width = new Wrap<>();

	public final Attribute<Integer> height = new Wrap<>();

	public final Attribute<String> backgroundColor = new Wrap<>();

	public final Attribute<Double> backgroundTransparency = new Wrap<>();

	public final Attribute<Boolean> backgroundHide = new Wrap<>();

	public final Attribute<String> borderColor = new Wrap<>();

	public final Attribute<Double> borderWidth = new Wrap<>();

	public final Attribute<Double> borderTransparency = new Wrap<>();

	public final Attribute<Boolean> borderHide = new Wrap<>();

	public final Attribute<String> title = new Wrap<>();

	public final Attribute<String> titleSide = new Wrap<>();

	public final Attribute<String> titleFontColor = new Wrap<>();

	public final Attribute<String> titleFontFamily = new Wrap<>();

	public final Attribute<Integer> titleFontSize = new Wrap<>();

	public final Attribute<String> tickFontColor = new Wrap<>();

	public final Attribute<String> tickFontFamily = new Wrap<>();

	public final Attribute<Integer> tickFontSize = new Wrap<>();

	public final Attribute<String> tickPosition = new Wrap<>();

	public final Attribute<String> tickColor = new Wrap<>();

	public final Attribute<Integer> tickWidth = new Wrap<>();

	public final Attribute<Integer> tickLength = new Wrap<>();

	public final Attribute<Integer> tickAngle = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	private EnumComboBox<PositionReference> positionReferenceBox;

	private EnumComboBox<HorizontalPosition> horizontalPositionBox;

	private EnumComboBox<VerticalPosition> verticalPositionBox;

	private Selection colorBarSelection;

	private Selection rectSelection;

	private D3 d3;

	private Graph graph;

	private Consumer updatePosition;

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom<?> parent) {

		Page page = root.createPage("colorBar", "   ColorBar   ");

		Section geometrySection = page.createSection("Geometry");

		positionReferenceBox = geometrySection.createEnumComboBox(positionReference, this, PositionReference.GRAPH);
		positionReferenceBox.setLabel("Position reference");

		horizontalPositionBox = geometrySection.createEnumComboBox(horizontalPosition, this, HorizontalPosition.RIGHT);
		horizontalPositionBox.setLabel("Horizontal position");

		verticalPositionBox = geometrySection.createEnumComboBox(verticalPosition, this, VerticalPosition.TOP);
		verticalPositionBox.setLabel("Vertical position");

		geometrySection.createIntegerVariableField(manualHorizontalPosition, this, 0) //
				.setLabel("Manual horizontal position");

		geometrySection.createIntegerVariableField(manualVerticalPosition, this, 0) //
				.setLabel("Manual vertical position");

		final int defaultBarWidth = 20;
		geometrySection.createIntegerVariableField(width, this, defaultBarWidth);

		final int defaultBarHeight = 200;
		geometrySection.createIntegerVariableField(height, this, defaultBarHeight);

		geometrySection.createCheckBox(hide, this);

		Section backgroundSection = page.createSection("background");

		backgroundSection.createColorChooser(backgroundColor, this, "white").setLabel("Color");
		backgroundSection.createDoubleVariableField(backgroundTransparency, this, 0.0).setLabel("Transparency");
		backgroundSection.createCheckBox(backgroundHide, this);

		Section borderSection = page.createSection("border");

		borderSection.createColorChooser(borderColor, this, "black").setLabel("Color");
		borderSection.createDoubleVariableField(borderWidth, this, 1.0);
		borderSection.createDoubleVariableField(borderTransparency, this, 0.0).setLabel("Transparency");
		borderSection.createCheckBox(borderHide, this);

		Section titleSection = page.createSection("title");

		titleSection.createTextField(title, this);

		titleSection.createEnumComboBox(titleSide, this, TitleSide.TOP).setLabel("Title side");

		titleSection.createColorChooser(titleFontColor, this, "black").setLabel("Color");
		titleSection.createFont(titleFontFamily, this).setLabel("Font");
		final int defaultFontSize = 12;
		titleSection.createIntegerVariableField(titleFontSize, this, defaultFontSize);

		Section tickFontSection = page.createSection("tickfont");
		tickFontSection.setLabel("Tick font");

		tickFontSection.createColorChooser(tickFontColor, this, "black").setLabel("Color");
		tickFontSection.createFont(tickFontFamily, this).setLabel("Font");
		tickFontSection.createIntegerVariableField(tickFontSize, this, defaultFontSize);

		Section tickSection = page.createSection("ticks");

		tickSection.createEnumComboBox(tickPosition, this, TickPosition.NONE).setLabel("Position");
		tickSection.createColorChooser(tickColor, this, "black").setLabel("Color");
		tickSection.createIntegerVariableField(tickWidth, this, 1);
		final int defaultTickLength = 5;
		tickSection.createIntegerVariableField(tickLength, this, defaultTickLength);
		tickSection.createIntegerVariableField(tickAngle, this, 0);

	}

	@Override
	public Selection plotWithD3(
			D3 d3,
			Selection contourSelection,
			Selection rectSelection,
			AbstractGraphicsAtom parent) {
		//not used here
		return contourSelection;
	}

	public static Selection getColorBarSelection(Selection contourSelection) {
		return contourSelection.select(".colorbar");
	}

	public org.treez.javafxd3.plotly.data.contour.colorbar.ColorBar createColorBar(
			Plotly plotly,
			Consumer updateConsumer) {

		org.treez.javafxd3.plotly.data.contour.colorbar.ColorBar colorBar = createColorBar(plotly);
		setSize(colorBar, updateConsumer);
		setTitleAttributes(plotly, updateConsumer, colorBar);
		setTickAttributes(plotly, updateConsumer, colorBar);

		return colorBar;
	}

	private static org.treez.javafxd3.plotly.data.contour.colorbar.ColorBar createColorBar(Plotly plotly) {
		org.treez.javafxd3.plotly.data.contour.colorbar.ColorBar colorBar = plotly.createColorBar();

		colorBar.setThicknessMode(LenMode.PIXELS);
		colorBar.setLenMode(LenMode.PIXELS);
		final double positionInsidePlot = 0.5; //this is an initial dummy position that has to be inside the plot
		colorBar.setX(positionInsidePlot);
		colorBar.setY(positionInsidePlot);
		return colorBar;
	}

	private void setSize(org.treez.javafxd3.plotly.data.contour.colorbar.ColorBar colorBar, Consumer updateConsumer) {
		colorBar.setThickness(width.get());
		colorBar.setLen(height.get());

		width.addModificationConsumer("colorBarWidth", updateConsumer);
		height.addModificationConsumer("colorBarHeight", updateConsumer);
	}

	private void setTitleAttributes(
			Plotly plotly,
			Consumer updateConsumer,
			org.treez.javafxd3.plotly.data.contour.colorbar.ColorBar colorBar) {

		colorBar.setTitle(title.get());
		colorBar.setTitleSide(titleSide.get());

		org.treez.javafxd3.plotly.data.Font titleFont = createTitleFont(plotly, updateConsumer);
		colorBar.setTitleFont(titleFont);

		title.addModificationConsumer("colorBarTitle", updateConsumer);
		titleSide.addModificationConsumer("colorBarTitleSide", updateConsumer);
	}

	private void setTickAttributes(
			Plotly plotly,
			Consumer updateConsumer,
			org.treez.javafxd3.plotly.data.contour.colorbar.ColorBar colorBar) {

		org.treez.javafxd3.plotly.data.Font tickFont = createTickFont(plotly, updateConsumer);
		colorBar.setTickFont(tickFont);

		colorBar.setTicks(tickPosition.get());
		colorBar.setTickColor(tickColor.get());
		colorBar.setTickWidth(tickWidth.get());
		colorBar.setTickLen(tickLength.get());
		colorBar.setTickAngle(tickAngle.get());

		tickPosition.addModificationConsumer("tickPosition", updateConsumer);
		tickColor.addModificationConsumer("tickColor", updateConsumer);
		tickWidth.addModificationConsumer("tickWidth", updateConsumer);
		tickLength.addModificationConsumer("tickLength", updateConsumer);
		tickAngle.addModificationConsumer("tickAngle", updateConsumer);

	}

	public void bindAdditionalColorBarAttributes(Selection colorBarSelection, D3 d3, Graph graph) {
		this.graph = graph;
		setPosition(colorBarSelection, d3, graph);

		Selection backgroundSelection = colorBarSelection.select(".cbbg");

		AbstractGraphicsAtom.bindStringStyle(backgroundSelection, "fill", backgroundColor);
		AbstractGraphicsAtom.bindTransparencyByStyle(backgroundSelection, backgroundTransparency);
		AbstractGraphicsAtom.bindTransparencyToBooleanAttributeByStyle(backgroundSelection, backgroundHide,
				backgroundTransparency);

		AbstractGraphicsAtom.bindStringStyle(backgroundSelection, "stroke", borderColor);
		AbstractGraphicsAtom.bindDoubleStyle(backgroundSelection, "stroke-width", borderWidth);
		AbstractGraphicsAtom.bindLineTransparency(backgroundSelection, borderTransparency);
		AbstractGraphicsAtom.bindLineTransparencyToBooleanAttribute(backgroundSelection, borderHide,
				borderTransparency);

	}

	private org.treez.javafxd3.plotly.data.Font createTitleFont(Plotly plotly, Consumer updateConsumer) {

		org.treez.javafxd3.plotly.data.Font font = plotly.createFont();
		font.setColor(titleFontColor.get());
		font.setFamily(titleFontFamily.get());
		font.setSize(titleFontSize.get());

		titleFontColor.addModificationConsumer("titleFontColor", updateConsumer);
		titleFontFamily.addModificationConsumer("titleFontFamily", updateConsumer);
		titleFontSize.addModificationConsumer("titleFontSize", updateConsumer);

		return font;
	}

	private org.treez.javafxd3.plotly.data.Font createTickFont(Plotly plotly, Consumer updateConsumer) {

		org.treez.javafxd3.plotly.data.Font font = plotly.createFont();
		font.setColor(tickFontColor.get());
		font.setFamily(tickFontFamily.get());
		font.setSize(tickFontSize.get());

		tickFontColor.addModificationConsumer("tickFontColor", updateConsumer);
		tickFontFamily.addModificationConsumer("tickFontFamily", updateConsumer);
		tickFontSize.addModificationConsumer("tickFontSize", updateConsumer);

		return font;
	}

	public void setPosition(Selection colorBarSelection, D3 d3, Graph graph) {
		this.d3 = d3;
		this.graph = graph;
		this.colorBarSelection = colorBarSelection;
		this.rectSelection = colorBarSelection.select(".cbbg");
		updatePosition = () -> setPosition(colorBarSelection, d3, graph);

		PositionReference positionReferenceEnum = positionReferenceBox.getValueAsEnum();
		HorizontalPosition horizontalPositionEnum = horizontalPositionBox.getValueAsEnum();
		VerticalPosition verticalPositionEnum = verticalPositionBox.getValueAsEnum();

		boolean isManualHorizontalPosition = horizontalPositionEnum.isManual();
		if (isManualHorizontalPosition) {
			applyManualHorizontalPosition(positionReferenceEnum);
		} else {
			applyAutomaticHorizontalPosition(horizontalPositionEnum, positionReferenceEnum);
		}

		boolean isManualVerticalPosition = verticalPositionEnum.isManual();
		if (isManualVerticalPosition) {
			applyManualVerticalPosition(positionReferenceEnum);
		} else {
			applyAutomaticVerticalPosition(verticalPositionEnum, positionReferenceEnum);
		}

		positionReferenceBox.addModificationConsumer("positionReference", updatePosition);
		horizontalPositionBox.addModificationConsumer("horizontalPosition", updatePosition);
		verticalPositionBox.addModificationConsumer("verticalPosition", updatePosition);

	}

	private void applyManualHorizontalPosition(PositionReference positionReference) {
		int x = manualHorizontalPosition.get();
		manualHorizontalPosition.addModificationConsumer("manualHorizontalPosition", updatePosition);

		setXPosition(positionReference, x);
	}

	private void applyAutomaticHorizontalPosition(
			HorizontalPosition horizontalPosition,
			PositionReference positionReference) {
		int x;
		switch (horizontalPosition) {
		case CENTRE:
			x = getHorizontalPositionForCentreAlignment(positionReference);
			break;
		case LEFT:
			x = MARGIN_AROUND_LEGEND_IN_PX;
			break;
		case MANUAL:
			throw new IllegalStateException("This method must not be called in manual mode");
		case RIGHT:
			x = getHorizontalPositionForRightAlignment(positionReference);
			break;
		default:
			String message = "The position '" + horizontalPosition + "' is not known.";
			throw new IllegalStateException(message);
		}
		setXPosition(positionReference, x);
	}

	private int getHorizontalPositionForCentreAlignment(PositionReference positionReference) {

		int xRightBorder = getRightBorderX(positionReference);
		int rectWidth = (int) rectSelection.node().getBBox().getWidth();
		int x = xRightBorder / 2 - rectWidth / 2;
		return x;
	}

	private int getHorizontalPositionForRightAlignment(PositionReference positionReference) {
		int xRightBorder = getRightBorderX(positionReference);
		int rectWidth = (int) rectSelection.node().getBBox().getWidth();
		int x = xRightBorder - MARGIN_AROUND_LEGEND_IN_PX - rectWidth;
		return x;
	}

	private int getRightBorderX(PositionReference positionReference) {
		int xRightBorder;
		boolean isPageReference = positionReference.isPage();
		if (isPageReference) {
			org.treez.results.atom.page.Page page = (org.treez.results.atom.page.Page) graph.getParentAtom();
			xRightBorder = Length.toPx(page.width.get()).intValue();
			page.width.addModificationConsumer("colorBarHorizontalPosition", updatePosition);
		} else {
			xRightBorder = Length.toPx(graph.data.width.get()).intValue();
			graph.data.width.addModificationConsumer("colorBarHorizontalPosition", updatePosition);
		}

		return xRightBorder;
	}

	private void setXPosition(PositionReference positionReference, int x) {

		double graphWidth = Length.toPx(graph.data.width.get());
		graph.data.width.addModificationConsumer("colorBarHorizontalPosition", updatePosition);

		double x0 = -graphWidth / 2;

		int correctedX = (int) x0 + x;

		String transformString = colorBarSelection.attr("transform");
		boolean transformExists = transformString != null;
		Double oldY = 0.0;
		if (transformExists) {
			Transform oldTransform = d3.transform(transformString);
			oldY = oldTransform.translate().get(1, Double.class);
		}

		boolean isPageReference = positionReference.isPage();
		if (isPageReference) {
			Double graphMargin = Length.toPx(graph.data.leftMargin.get());
			graph.data.leftMargin.addModificationConsumer("colorBarHorizontalPosition", updatePosition);

			int pageX = correctedX - graphMargin.intValue();
			colorBarSelection.attr("transform", "translate(" + pageX + "," + oldY + ")");
		} else {
			colorBarSelection.attr("transform", "translate(" + correctedX + "," + oldY + ")");
		}
	}

	private void applyManualVerticalPosition(PositionReference positionReference) {
		int y = manualVerticalPosition.get();
		manualVerticalPosition.addModificationConsumer("manualVerticalPosition", updatePosition);

		setYPosition(positionReference, y);
	}

	private void applyAutomaticVerticalPosition(
			VerticalPosition verticalPosition,
			PositionReference positionReference) {
		int y;
		switch (verticalPosition) {
		case BOTTOM:
			y = getVerticalPositionForBottomAlignment(positionReference);
			break;
		case CENTRE:
			y = getVerticalPositionForCentreAlignment(positionReference);
			break;
		case MANUAL:
			throw new IllegalStateException("This method must not be called in manual mode");
		case TOP:
			y = MARGIN_AROUND_LEGEND_IN_PX;
			break;
		default:
			String message = "The position '" + verticalPosition + "' is not known.";
			throw new IllegalStateException(message);
		}
		setYPosition(positionReference, y);
	}

	private int getVerticalPositionForBottomAlignment(PositionReference positionReference) {
		int yBottomBorder = getBottomBorderY(positionReference);
		int rectHeight = (int) rectSelection.node().getBBox().getHeight();
		int y = yBottomBorder - MARGIN_AROUND_LEGEND_IN_PX - rectHeight;
		return y;
	}

	private int getVerticalPositionForCentreAlignment(PositionReference positionReference) {
		int yBottomBorder = getBottomBorderY(positionReference);
		int rectHeight = (int) rectSelection.node().getBBox().getHeight();
		int y = yBottomBorder / 2 - rectHeight / 2;
		return y;
	}

	private int getBottomBorderY(PositionReference positionReference) {
		int yBottomBorder;
		boolean isPageReference = positionReference.isPage();
		if (isPageReference) {
			org.treez.results.atom.page.Page page = (org.treez.results.atom.page.Page) graph.getParentAtom();
			yBottomBorder = Length.toPx(page.height.get()).intValue();
			page.height.addModificationConsumer("colorBarVerticalPosition", updatePosition);

		} else {
			yBottomBorder = Length.toPx(graph.data.height.get()).intValue();
			graph.data.height.addModificationConsumer("colorBarVerticalPosition", updatePosition);
		}
		return yBottomBorder;
	}

	@SuppressWarnings("magicnumber")
	private void setYPosition(PositionReference positionReference, int y) {

		double graphHeight = Length.toPx(graph.data.height.get());
		graph.data.height.addModificationConsumer("colorBarVerticalPosition", updatePosition);

		double y0 = 0.5 * this.height.get() - 0.5 * graphHeight;
		int correctedY = (int) y0 + y;

		String transformString = colorBarSelection.attr("transform");
		boolean transformExists = transformString != null;
		Double oldX = 0.0;
		if (transformExists) {
			Transform oldTransform = d3.transform(transformString);
			oldX = oldTransform.translate().get(0, Double.class);
		}

		boolean isPageReference = positionReference.isPage();
		if (isPageReference) {
			Double graphMargin = Length.toPx(graph.data.topMargin.get());
			graph.data.topMargin.addModificationConsumer("colorBarVerticalPosition", updatePosition);

			int pageY = correctedY - graphMargin.intValue();
			colorBarSelection.attr("transform", "translate(" + oldX + "," + pageY + ")");
		} else {
			colorBarSelection.attr("transform", "translate(" + oldX + "," + correctedY + ")");
		}
	}

	//#end region

}
