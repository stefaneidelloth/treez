package org.treez.data.table.nebula;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;

public class ColumnHeaderLayerStack extends AbstractLayerTransform {

	public ColumnHeaderLayerStack(IDataProvider dataProvider, BodyLayerStack bodyLayer) {
		DataLayer dataLayer = new DataLayer(dataProvider);
		ColumnHeaderLayer colHeaderLayer = new ColumnHeaderLayer(dataLayer, bodyLayer, bodyLayer.getSelectionLayer());
		setUnderlyingLayer(colHeaderLayer);
	}
}
