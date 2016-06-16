package org.treez.data.table;

import java.util.List;

import org.apache.log4j.Logger;
import org.treez.core.adaptable.CodeContainer;
import org.treez.core.atom.base.AtomCodeAdaption;
import org.treez.core.data.row.Row;

public class TableCodeAdaption extends AtomCodeAdaption {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(TableCodeAdaption.class);

	//#end region

	//#region CONSTRUCTORS

	public TableCodeAdaption(Table atom) {
		super(atom);
	}

	//#end region

	//#region METHODS

	/**
	 * Adds code for the data in the table
	 */
	@Override
	protected CodeContainer postProcessAllChildrenCodeContainer(CodeContainer allChildrenCodeContainer) {
		CodeContainer extendedContainer = allChildrenCodeContainer;

		Table table = (Table) this.getAdaptable();
		List<Row> rows = table.getRows();

		for (Row row : rows) {
			String rowLine = "\t\t" + PARENT_VARIABLE_NAME + "." + row.toString();
			extendedContainer.extendBulk(rowLine);
		}

		return extendedContainer;

	}

	//#end region

}
