package org.treez.results.atom.tornado;

import java.util.List;
import java.util.Objects;

import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.Activator;
import org.treez.results.atom.graph.Graph;
import org.treez.results.atom.graphicsPage.GraphicsPropertiesPage;
import org.treez.results.atom.legend.LegendContributor;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Tornado extends GraphicsPropertiesPage implements LegendContributor {

	//#region ATTRIBUTES

	public Data data;

	public Fill fill;

	public Line line;

	public Labels labels;

	private Selection tornadoSelection;

	//#end region

	//#region CONSTRUCTORS

	public Tornado(String name) {
		super(name);
	}

	//#end region

	//#region METHODS

	@Override
	protected void createPropertyPageFactories() {

		data = new Data();
		propertyPageFactories.add(data);

		fill = new Fill();
		propertyPageFactories.add(fill);

		line = new Line();
		propertyPageFactories.add(line);

		labels = new Labels();
		propertyPageFactories.add(labels);

	}

	@Override
	public Image provideImage() {
		return Activator.getImage("tornado.png");
	}

	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {
		// no actions available right now
		return actions;
	}

	@Override
	public void execute(FocusChangingRefreshable refreshable) {
		treeViewRefreshable = refreshable;
	}

	@Override
	public Selection plotWithD3(
			D3 d3,
			Selection graphOrBarSeriesSelection,
			Selection graphRectSelection,
			FocusChangingRefreshable refreshable) {
		Objects.requireNonNull(d3);
		this.treeViewRefreshable = refreshable;

		//remove old bar group if it already exists
		graphOrBarSeriesSelection //
				.select("#" + name) //
				.remove();

		//create new axis group
		tornadoSelection = graphOrBarSeriesSelection //
				.insert("g", ".axis") //
				.attr("class", "tornado") //
				.onClick(this);
		bindNameToId(tornadoSelection);

		updatePlotWithD3(d3);

		return tornadoSelection;
	}

	@Override
	public void updatePlotWithD3(D3 d3) {
		plotPageModels(d3);
	}

	private void plotPageModels(D3 d3) {
		for (GraphicsPropertiesPageFactory pageModel : propertyPageFactories) {
			tornadoSelection = pageModel.plotWithD3(d3, tornadoSelection, null, this);
		}
	}

	@Override
	public void addLegendContributors(List<LegendContributor> legendContributors) {
		if (providesLegendEntry()) {
			legendContributors.add(this);
		}
	}

	@Override
	public boolean providesLegendEntry() {
		return !getLegendText().isEmpty();
	}

	@Override
	public String getLegendText() {
		return data.leftLegendText.get();
	}

	@Override
	public Selection createLegendSymbolGroup(
			D3 d3,
			Selection parentSelection,
			int symbolLengthInPx,
			Refreshable refreshable) {
		Selection symbolSelection = parentSelection //
				.append("rect") //
				.classed("tornado-legend-entry-symbol", true);

		this.fill.formatLegendSymbol(symbolSelection, symbolLengthInPx);
		this.line.formatLegendSymbolLine(symbolSelection, refreshable);

		return symbolSelection;
	}

	public Graph getGraph() {
		AbstractAtom<?> grandParent = getParentAtom();
		Graph graph;
		boolean isGraph = Graph.class.isAssignableFrom(grandParent.getClass());
		if (isGraph) {
			graph = (Graph) grandParent;
		} else {
			AbstractAtom<?> greatGrandParent = grandParent.getParentAtom();
			graph = (Graph) greatGrandParent;
		}
		return graph;
	}

	//#end region

}
