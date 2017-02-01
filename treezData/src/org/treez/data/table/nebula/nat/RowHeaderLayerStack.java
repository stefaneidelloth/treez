package org.treez.data.table.nebula.nat;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;

public class RowHeaderLayerStack extends AbstractLayerTransform {

	public RowHeaderLayerStack(IDataProvider dataProvider, BodyLayerStack bodyLayer) {

		final int defaultColumnWidth = 50;
		final int defaultRowHeight = 20;
		DataLayer dataLayer = new DataLayer(dataProvider, defaultColumnWidth, defaultRowHeight);
		RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(dataLayer, bodyLayer, bodyLayer.getSelectionLayer());
		setUnderlyingLayer(rowHeaderLayer);

	}
}
