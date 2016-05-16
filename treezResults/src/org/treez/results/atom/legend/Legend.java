package org.treez.results.atom.legend;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.treeview.TreeViewerRefreshable;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.results.Activator;
import org.treez.results.atom.graphicspage.Background;
import org.treez.results.atom.graphicspage.Border;
import org.treez.results.atom.graphicspage.GraphicsPropertiesPage;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Legend extends GraphicsPropertiesPage {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(Legend.class);

	//#region ATTRIBUTES

	public Main main;

	public Text text;

	public Background background;

	public Border border;

	private Selection legendGroupSelection;

	private Selection rectSelection;

	//#end region

	//#region CONSTRUCTORS

	public Legend(String name) {
		super(name);
	}

	//#end region

	//#region METHODS

	@Override
	protected void createPropertyPageFactories() {

		main = new Main();
		propertyPageFactories.add(main);

		text = new Text();
		propertyPageFactories.add(text);

		background = new Background();
		propertyPageFactories.add(background);

		border = new Border();
		propertyPageFactories.add(border);

	}

	@Override
	public Image provideImage() {
		return Activator.getImage("legend.png");
	}

	@Override
	protected List<Object> extendContextMenuActions(List<Object> actions, TreeViewerRefreshable treeViewer) {
		// no actions available right now
		return actions;
	}

	@Override
	public Selection plotWithD3(
			D3 d3,
			Selection graphSelection,
			Selection graphRectSelection,
			FocusChangingRefreshable refreshable) {
		Objects.requireNonNull(d3);
		this.treeViewRefreshable = refreshable;

		graphSelection //
				.select("#" + name) //
				.remove();

		legendGroupSelection = graphSelection //
				.append("g") //
				.attr("class", "legend");
		bindNameToId(legendGroupSelection);

		rectSelection = legendGroupSelection //
				.append("rect").onMouseClick(this);

		updatePlotWithD3(d3);

		return legendGroupSelection;
	}

	@Override
	public void updatePlotWithD3(D3 d3) {
		plotPageModels(d3);
	}

	private void plotPageModels(D3 d3) {
		for (GraphicsPropertiesPageFactory pageModel : propertyPageFactories) {
			legendGroupSelection = pageModel.plotWithD3(d3, legendGroupSelection, rectSelection, this);
		}
	}

	//#end region

}
