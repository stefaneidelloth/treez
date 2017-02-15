package org.treez.data.table.nebula.nat.pagination;

import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.renderers.ICompositeRendererFactory;
import org.eclipse.swt.widgets.Composite;

public class PaginationComponentFactory implements ICompositeRendererFactory {

	//#region METHODS

	@Override
	public Composite createComposite(Composite parent, int style, PageableController controller) {
		return new PaginationComponentRenderer(parent, style, controller);
	}

	//#end region

}
