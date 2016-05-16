package org.treez.results.atom.legend;

import java.util.ArrayList;
import java.util.List;

import org.treez.core.adaptable.Refreshable;
import org.treez.core.atom.attribute.AttributeRoot;
import org.treez.core.atom.attribute.EnumComboBox;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.atom.graphics.GraphicsAtom;
import org.treez.core.atom.graphics.GraphicsPropertiesPageFactory;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.behaviour.Drag;
import org.treez.javafxd3.d3.coords.Coords;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.core.Transform;
import org.treez.javafxd3.d3.functions.DragFunction;
import org.treez.results.atom.graph.Graph;

import javafx.geometry.BoundingBox;

@SuppressWarnings("checkstyle:visibilitymodifier")
public class Main implements GraphicsPropertiesPageFactory, DragFunction, Refreshable {

	//#region ATTRIBUTES

	private static final int MARGIN_AROUND_LEGEND_IN_PX = 20;

	private static final int HORIZONTAL_SPACING_IN_PX = 10;

	private static final int VERTICAL_SPACING_IN_PX = 5;

	public final Attribute<String> positionReference = new Wrap<>();

	public final Attribute<String> horizontalPosition = new Wrap<>();

	public final Attribute<String> verticalPosition = new Wrap<>();

	public final Attribute<Integer> manualHorizontalPosition = new Wrap<>();

	public final Attribute<Integer> manualVerticalPosition = new Wrap<>();

	public final Attribute<Integer> marginSize = new Wrap<>();

	public final Attribute<Integer> numberOfColumns = new Wrap<>();

	public final Attribute<Integer> keyLength = new Wrap<>();

	public final Attribute<Boolean> swapSymbol = new Wrap<>();

	public final Attribute<Boolean> hide = new Wrap<>();

	private D3 d3;

	private Legend legend;

	private Graph graph;

	private Selection legendSelection;

	private Selection contentSelection;

	private Selection rectSelection;

	private EnumComboBox<PositionReference> positionReferenceBox;

	private EnumComboBox<HorizontalPosition> horizontalPositionBox;

	private EnumComboBox<VerticalPosition> verticalPositionBox;

	//#end region

	//#region METHODS

	@Override
	public void createPage(AttributeRoot root, AbstractAtom parent) {

		Page mainPage = root.createPage("main");

		Section main = mainPage.createSection("main");

		positionReferenceBox = main.createEnumComboBox(positionReference, "Position reference",
				PositionReference.GRAPH);

		horizontalPositionBox = main.createEnumComboBox(horizontalPosition, "Horizontal position",
				HorizontalPosition.RIGHT);

		verticalPositionBox = main.createEnumComboBox(verticalPosition, "Vertical position", VerticalPosition.TOP);

		main.createIntegerVariableField(manualHorizontalPosition, this, 0) //
				.setLabel("Manual horizontal position");

		main.createIntegerVariableField(manualVerticalPosition, this, 0) //
				.setLabel("Manual vertical position");

		main.createIntegerVariableField(marginSize, this, HORIZONTAL_SPACING_IN_PX)//
				.setLabel("Margin size");

		main.createIntegerVariableField(numberOfColumns, this, 1)//
				.setLabel("Number of columns");

		final int defaultKeyLength = 30;
		main.createIntegerVariableField(keyLength, this, defaultKeyLength) //
				.setLabel("Key length");

		main.createCheckBox(swapSymbol, "Swap symbol");

		main.createCheckBox(hide, "hide");

	}

	@Override
	public Selection plotWithD3(D3 d3, Selection legendSelection, Selection rectSelection, GraphicsAtom parent) {

		this.d3 = d3;
		this.legendSelection = legendSelection;
		this.rectSelection = rectSelection;
		this.legend = (Legend) parent;
		this.graph = (Graph) legend.getParentAtom();

		Drag drag = d3.behavior().drag().onDrag(this);
		legendSelection.call(drag);

		replotLegendContentAndUpdateRect();

		listenForChanges(d3);

		return legendSelection;
	}

	private void replotLegendContentAndUpdateRect() {
		legendSelection //
				.select(".legend-content") //
				.remove();

		contentSelection = legendSelection //
				.append("g") //
				.classed("legend-content", true);

		createLegendEntries();
		setRectSize();
		setLegendPosition();
	}

	@Override
	public void refresh() {
		replotLegendContentAndUpdateRect();
	}

	private void setRectSize() {
		BoundingBox bounds = contentSelection.node().getBBox();
		Integer margin = marginSize.get();
		if (margin == null) {
			margin = 0;
		}
		int rectWidth = (int) (bounds.getWidth() + 2 * margin);
		int rectHight = (int) (bounds.getHeight() + 2 * margin);

		rectSelection.attr("width", rectWidth);
		rectSelection.attr("height", rectHight);
	}

	private void setLegendPosition() {
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
	}

	private void listenForChanges(D3 d3) {

		GraphicsAtom.bindDisplayToBooleanAttribute("hideGraph", legendSelection, hide);

		Consumer replotLegend = () -> {
			legend.updatePlotWithD3(d3);
		};

		positionReference.addModificationConsumer("replotGraph", () -> {
			convertManualPositions();
			replotLegend.consume();
		});
		horizontalPosition.addModificationConsumer("replotGraph", replotLegend);
		verticalPosition.addModificationConsumer("replotGraph", replotLegend);

		manualHorizontalPosition.addModificationConsumer("replotGraph", replotLegend);
		manualVerticalPosition.addModificationConsumer("replotGraph", replotLegend);
		marginSize.addModificationConsumer("replotGraph", replotLegend);
		numberOfColumns.addModificationConsumer("replotGraph", replotLegend);
		keyLength.addModificationConsumer("replotGraph", replotLegend);

		swapSymbol.addModificationConsumer("replotGraph", replotLegend);
	}

	private void createLegendEntries() {
		List<LegendContributor> legendContributors = getLegendContributors();
		List<Selection> legendEntrySelections = createLegendEntries(legendContributors);
		setLegendEntryPositions(legendEntrySelections);
	}

	private List<Selection> createLegendEntries(List<LegendContributor> legendContributors) {
		removeOldLegendEntries();
		List<Selection> legendEntrySelections = new ArrayList<>();
		for (LegendContributor contributor : legendContributors) {
			Selection legendEntrySelection = createLegendEntry(contributor);
			legendEntrySelections.add(legendEntrySelection);
		}
		return legendEntrySelections;
	}

	private void removeOldLegendEntries() {
		contentSelection //
				.selectAll(".legend-entry") //
				.remove();
	}

	private Selection createLegendEntry(LegendContributor contributor) {

		int symbolLengthInPx = keyLength.get();

		Selection entrySelection = contentSelection //
				.append("g") //
				.classed("legend-entry", true);

		Selection symbolSelection = contributor.createLegendSymbolGroup(d3, entrySelection, symbolLengthInPx, this);
		Selection labelSelection = createLegendLabel(entrySelection, contributor);

		boolean swap = swapSymbol.get();
		if (swap) {
			positionLabelBeforeSymbol(symbolSelection, labelSelection);
		} else {
			positionSymbolBeforeLabel(symbolSelection, labelSelection);
		}

		return entrySelection;
	}

	private Selection createLegendLabel(Selection entrySelection, LegendContributor contributor) {
		String legendText = contributor.getLegendText();
		Selection labelSelection = entrySelection //
				.append("text") //
				.attr("id", "text") //
				.classed("legend-text", true)
				.text(legendText);

		legend.text.formatText(labelSelection, this);

		return labelSelection;
	}

	private static void positionSymbolBeforeLabel(Selection symbolSelection, Selection labelSelection) {

		BoundingBox symbolBounds = symbolSelection.node().getBBox();
		BoundingBox labelBounds = labelSelection.node().getBBox();
		double symbolHeight = symbolBounds.getHeight();
		double symbolMinY = symbolBounds.getMinY();
		double symbolWidth = symbolBounds.getWidth();
		double symbolMinX = symbolBounds.getMinX();

		double labelHeight = labelBounds.getHeight();
		double labelMinY = labelBounds.getMinY();

		double maxHeight = Math.max(symbolHeight, labelHeight);

		double symbolX = 0 - symbolMinX;
		double symbolY = maxHeight / 2 - symbolHeight / 2 - symbolMinY;
		symbolSelection.attr("transform", "translate(" + symbolX + "," + symbolY + ")");

		double labelX = symbolWidth + HORIZONTAL_SPACING_IN_PX;
		double labelY = maxHeight / 2 - labelHeight / 2 - labelMinY;
		labelSelection.attr("transform", "translate(" + labelX + "," + labelY + ")");
	}

	private static void positionLabelBeforeSymbol(Selection symbolSelection, Selection labelSelection) {

		BoundingBox symbolBounds = symbolSelection.node().getBBox();
		BoundingBox labelBounds = labelSelection.node().getBBox();
		double symbolHeight = symbolBounds.getHeight();
		double symbolMinX = symbolBounds.getMinX();
		double symbolMinY = symbolBounds.getMinY();

		double labelHeight = labelBounds.getHeight();
		double labelMinY = labelBounds.getMinY();
		double labelWidth = labelBounds.getWidth();

		double maxHeight = Math.max(symbolHeight, labelHeight);

		double labelX = 0;
		double labelY = maxHeight / 2 - labelHeight / 2 - labelMinY;
		labelSelection.attr("transform", "translate(" + labelX + "," + labelY + ")");

		double symbolX = labelWidth + HORIZONTAL_SPACING_IN_PX - symbolMinX;
		double symbolY = maxHeight / 2 - symbolHeight / 2 - symbolMinY;
		symbolSelection.attr("transform", "translate(" + symbolX + "," + symbolY + ")");
	}

	private void setLegendEntryPositions(List<Selection> legendEntrySelections) {
		int numberOfLegendEntries = legendEntrySelections.size();
		int numOfColumns = numberOfColumns.get();
		int numberOfEntriesPerColumn = numberOfLegendEntries / numOfColumns;
		if (numberOfEntriesPerColumn == 0) {
			numberOfEntriesPerColumn = 1;
		}

		double[] columnWidths = getColumnWidths(legendEntrySelections, numOfColumns, numberOfEntriesPerColumn);
		double rowHeight = getMaxEntryHeight(legendEntrySelections);
		int margin = marginSize.get();

		int columnIndex = 0;
		int rowIndexInColumn = 0;
		int x = margin;
		int y0 = margin;

		for (Selection legendEntry : legendEntrySelections) {

			BoundingBox entryBounds = legendEntry.node().getBBox();
			double entryHeight = entryBounds.getHeight();
			double entryMinY = entryBounds.getMinY();
			double y = y0 + rowHeight / 2 - entryHeight / 2 - entryMinY;

			legendEntry.attr("transform", "translate(" + x + "," + y + ")");
			rowIndexInColumn++;
			if (rowIndexInColumn < numberOfEntriesPerColumn) {
				y0 += rowHeight + VERTICAL_SPACING_IN_PX;
			} else {
				x += columnWidths[columnIndex] + HORIZONTAL_SPACING_IN_PX;
				y0 = margin;
				columnIndex++;
				rowIndexInColumn = 0;
			}
		}
	}

	private static double[] getColumnWidths(
			List<Selection> entrySelections,
			int numberOfColumns,
			int numberOfEntriesPerColumn) {

		double[] columnWidths = new double[numberOfColumns];

		int columnIndex = 0;
		int rowIndex = 0;
		columnWidths[columnIndex] = 0;
		for (Selection entrySelection : entrySelections) {

			if (rowIndex >= numberOfEntriesPerColumn) {
				rowIndex = 0;
				columnIndex++;
				columnWidths[columnIndex] = 0;
			}

			BoundingBox bounds = entrySelection.node().getBBox();
			double width = bounds.getWidth();
			if (width > columnWidths[columnIndex]) {
				columnWidths[columnIndex] = width;
			}
			rowIndex++;
		}

		return columnWidths;
	}

	private static double getMaxEntryHeight(List<Selection> legendEntrySelections) {
		double maxHeight = 0;
		for (Selection legendEntry : legendEntrySelections) {
			BoundingBox entryBounds = legendEntry.node().getBBox();
			double height = entryBounds.getHeight();
			if (height > maxHeight) {
				maxHeight = height;
			}
		}
		return maxHeight;
	}

	private List<LegendContributor> getLegendContributors() {
		List<LegendContributor> legendContributors = new ArrayList<>();
		List<AbstractAtom> graphChildren = graph.getChildAtoms();
		for (AbstractAtom graphChild : graphChildren) {
			boolean isLegendContributorProvider = graphChild instanceof LegendContributorProvider;
			if (isLegendContributorProvider) {
				LegendContributorProvider provider = (LegendContributorProvider) graphChild;
				provider.addLegendContributors(legendContributors);
			}
		}
		return legendContributors;
	}

	private void convertManualPositions() {
		int x = manualHorizontalPosition.get();
		int y = manualVerticalPosition.get();

		Double leftGraphMargin = Length.toPx(graph.data.leftMargin.get());
		Double topGraphMargin = Length.toPx(graph.data.topMargin.get());

		boolean referencesPage = positionReferenceBox.getValueAsEnum().isPage();
		if (referencesPage) {
			int pageX = x + leftGraphMargin.intValue();
			manualHorizontalPosition.set(pageX);

			int pageY = y + topGraphMargin.intValue();
			manualVerticalPosition.set(pageY);
		} else {
			int graphX = x - leftGraphMargin.intValue();
			manualHorizontalPosition.set(graphX);

			int pageY = y - topGraphMargin.intValue();
			manualVerticalPosition.set(pageY);
		}
	}

	private void applyManualHorizontalPosition(PositionReference positionReference) {
		int x = manualHorizontalPosition.get();
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
		int rectWidth = Length.toPx(rectSelection.attr("width")).intValue();
		int x = xRightBorder / 2 - rectWidth / 2;
		return x;
	}

	private int getHorizontalPositionForRightAlignment(PositionReference positionReference) {
		int xRightBorder = getRightBorderX(positionReference);
		int rectWidth = Length.toPx(rectSelection.attr("width")).intValue();
		int x = xRightBorder - MARGIN_AROUND_LEGEND_IN_PX - rectWidth;
		return x;
	}

	private int getRightBorderX(PositionReference positionReference) {
		int xRightBorder;
		boolean isPageReference = positionReference.isPage();
		if (isPageReference) {
			org.treez.results.atom.page.Page page = (org.treez.results.atom.page.Page) graph.getParentAtom();
			xRightBorder = Length.toPx(page.width.get()).intValue();
		} else {
			xRightBorder = Length.toPx(graph.data.width.get()).intValue();
		}
		return xRightBorder;
	}

	private void setXPosition(PositionReference positionReference, int x) {
		Transform oldTransform = d3.transform(legendSelection.attr("transform"));
		Double oldY = oldTransform.translate().get(1, Double.class);

		boolean isPageReference = positionReference.isPage();
		if (isPageReference) {
			Double graphMargin = Length.toPx(graph.data.leftMargin.get());
			int pageX = x - graphMargin.intValue();
			legendSelection.attr("transform", "translate(" + pageX + "," + oldY + ")");
		} else {
			legendSelection.attr("transform", "translate(" + x + "," + oldY + ")");
		}
	}

	private void applyManualVerticalPosition(PositionReference positionReference) {
		int y = manualVerticalPosition.get();
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
		int rectHeight = Length.toPx(rectSelection.attr("height")).intValue();
		int y = yBottomBorder - MARGIN_AROUND_LEGEND_IN_PX - rectHeight;
		return y;
	}

	private int getVerticalPositionForCentreAlignment(PositionReference positionReference) {
		int yBottomBorder = getBottomBorderY(positionReference);
		int rectHeight = Length.toPx(rectSelection.attr("height")).intValue();
		int y = yBottomBorder / 2 - rectHeight / 2;
		return y;
	}

	private int getBottomBorderY(PositionReference positionReference) {
		int yBottomBorder;
		boolean isPageReference = positionReference.isPage();
		if (isPageReference) {
			org.treez.results.atom.page.Page page = (org.treez.results.atom.page.Page) graph.getParentAtom();
			yBottomBorder = Length.toPx(page.height.get()).intValue();
		} else {
			yBottomBorder = Length.toPx(graph.data.height.get()).intValue();
		}
		return yBottomBorder;
	}

	private void setYPosition(PositionReference positionReference, int y) {

		Transform oldTransform = d3.transform(legendSelection.attr("transform"));
		Double oldX = oldTransform.translate().get(0, Double.class);

		boolean isPageReference = positionReference.isPage();
		if (isPageReference) {
			Double graphMargin = Length.toPx(graph.data.topMargin.get());
			int pageY = y - graphMargin.intValue();
			legendSelection.attr("transform", "translate(" + oldX + "," + pageY + ")");
		} else {
			legendSelection.attr("transform", "translate(" + oldX + "," + y + ")");
		}
	}

	@Override
	public synchronized void handleDrag(final Object context, final Object d, final int index) {

		Transform oldTransform = d3.transform(legendSelection.attr("transform"));
		Double oldX = oldTransform.translate().get(0, Double.class);
		Double oldY = oldTransform.translate().get(1, Double.class);

		Coords delta = d3.eventAsDCoords();
		Double dX = delta.x();
		Double dY = delta.y();

		Double x = oldX + dX;
		Double y = oldY + dY;

		if (!x.equals(oldX)) {
			setNewManualXPosition(x);
		}

		if (!y.equals(oldY)) {
			setNewManualYPosition(y);
		}

		legendSelection //
				.attr("transform", "translate(" + x + "," + y + ")");

	}

	private void setNewManualXPosition(Double x) {
		horizontalPosition.set("manual");
		Integer intValue = x.intValue();

		boolean isPageReference = positionReferenceBox.getValueAsEnum().isPage();
		if (isPageReference) {
			Double leftGraphMargin = Length.toPx(graph.data.leftMargin.get());
			intValue += leftGraphMargin.intValue();
		}

		manualHorizontalPosition.set(intValue);
	}

	private void setNewManualYPosition(Double y) {
		verticalPosition.set("manual");
		Integer intValue = y.intValue();

		boolean isPageReference = positionReferenceBox.getValueAsEnum().isPage();
		if (isPageReference) {
			Double topGraphMargin = Length.toPx(graph.data.topMargin.get());
			intValue += topGraphMargin.intValue();
		}

		manualVerticalPosition.set(intValue);
	}

	@Override
	public void handleDragStart(final Object context, final Object d, final int index) {
		//not used here
	}

	@Override
	public void handleDragEnd(final Object context, final Object d, final int index) {
		//not used here
	}

	//#end region

}
