package org.treez.results.atom.axis;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.scales.Scale;
import org.treez.results.Activator;
import org.treez.results.atom.axis.scale.OrdinalScaleBuilder;
import org.treez.results.atom.axis.scale.QuantitativeScaleBuilder;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPage;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Axis extends GraphicsPropertiesPage {

	//#region ATTRIBUTES

	public Data data;

	public AxisLine axisLine;

	public MajorTicks majorTicks;

	public MinorTicks minorTicks;

	public TickLabels tickLabels;

	public AxisLabel axisLabel;

	private Selection axisSelection;

	private QuantitativeScaleBuilder quantitativeScaleBuilder;

	private OrdinalScaleBuilder ordinalScaleBuilder;

	private D3 d3;

	//#end region

	//#region CONSTRUCTORS

	public Axis(String name) {
		super(name);
		quantitativeScaleBuilder = new QuantitativeScaleBuilder(this);
		ordinalScaleBuilder = new OrdinalScaleBuilder();
	}

	public Axis(String name, Direction direction) {
		this(name);
		data.direction.set(direction.toString());
	}

	//#end region

	//#region METHODS

	@Override
	public Image provideImage() {
		return Activator.getImage("axis.png");
	}

	@Override
	protected void createPropertyPageFactories() {

		data = new Data();
		propertyPageFactories.add(data);

		axisLine = new AxisLine();
		propertyPageFactories.add(axisLine);

		majorTicks = new MajorTicks();
		propertyPageFactories.add(majorTicks);

		minorTicks = new MinorTicks();
		propertyPageFactories.add(minorTicks);

		tickLabels = new TickLabels();
		propertyPageFactories.add(tickLabels);

		axisLabel = new AxisLabel();
		propertyPageFactories.add(axisLabel);

	}

	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {
		//no actions available right now
		return actions;
	}

	@Override
	public Selection plotWithD3(
			D3 d3,
			Selection graphSelection,
			Selection graphRectSelection,
			FocusChangingRefreshable refreshable) {

		Objects.requireNonNull(d3);
		this.d3 = d3;
		this.treeViewRefreshable = refreshable;
		removeOldAxisGroupIfAlreadyExists(graphSelection);
		createNewAxisGroup(graphSelection);
		updatePlotWithD3(d3);
		return graphSelection;
	}

	private void removeOldAxisGroupIfAlreadyExists(Selection graphSelection) {
		graphSelection //
				.select("#" + name) //
				.remove(); //
	}

	private void createNewAxisGroup(Selection graphSelection) {
		axisSelection = graphSelection //
				.append("g") //
				.attr("class", "axis") //
				.onMouseClick(this);
		bindNameToId(axisSelection);
	}

	@Override
	public void updatePlotWithD3(D3 d3) {
		plotPageModels(d3);
	}

	public void update() {
		if (d3 != null) {
			updatePlotWithD3(d3);
		}
	}

	private void plotPageModels(D3 d3) {
		for (GraphicsPropertiesPageFactory pageModel : propertyPageFactories) {
			axisSelection = pageModel.plotWithD3(d3, axisSelection, null, this);
		}
	}

	//#end region

	//#region ACCESSORS

	public Scale<?> getScale() {

		AxisMode axisMode = data.getAxisMode();
		switch (axisMode) {
		case QUANTITATIVE:
			return quantitativeScaleBuilder.getScale();
		case ORDINAL:
			return ordinalScaleBuilder.getScale();
		//case TIME:
		//	throw new IllegalStateException("not yet implemented");
		default:
			throw new IllegalStateException("not yet implemented");
		}
	}

	public Boolean isQuantitative() {
		boolean hasQuantitativeScale = this.data.isQuantitative();
		return hasQuantitativeScale;
	}

	public QuantitativeScaleBuilder getQuantitativeScaleBuilder() {
		return quantitativeScaleBuilder;
	}

	public void includeDataForAutoScale(Collection<Double> dataForAutoScale) {
		quantitativeScaleBuilder.includeDataForAutScale(dataForAutoScale);
	}

	public boolean isOrdinal() {
		boolean isOrdinal = this.data.isOrdinal();
		return isOrdinal;
	}

	public OrdinalScaleBuilder getOrdinalScaleBuilder() {
		return ordinalScaleBuilder;
	}

	public void addOrdinalValue(String ordinalValue) {
		ordinalScaleBuilder.addValue(ordinalValue);
	}

	public boolean isHorizontal() {
		String direction = data.direction.get();
		return direction.equals(Direction.HORIZONTAL.toString());
	}

	//#end region

}
