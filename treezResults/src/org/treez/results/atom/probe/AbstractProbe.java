package org.treez.results.atom.probe;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.treez.core.adaptable.FocusChangingRefreshable;
import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.data.column.ColumnBlueprint;
import org.treez.core.utils.Utils;
import org.treez.data.column.Columns;
import org.treez.data.table.Table;

/**
 * Represents the root atom for all models
 */
public abstract class AbstractProbe extends AdjustableAtom implements Probe {

	private static final Logger LOG = Logger.getLogger(AbstractProbe.class);

	//#region CONSTRUCTORS

	public AbstractProbe(String name) {
		super(name);
		setRunnable();
	}

	//#end region

	//#region METHODS

	/**
	 * Executes the probe
	 */
	@Override
	public void execute(FocusChangingRefreshable refreshable) {
		Objects.requireNonNull(refreshable);
		afterCreateControlAdaptionHook();
		runNonUiJob("AbstractProbe: execute", (monitor) -> runProbe(refreshable, monitor));
	}

	/**
	 * If relative model paths are used in the model of the probe, the root for those paths is updated with this method.
	 * This method has to be called after the tree has been established, so that the model path atoms are able to access
	 * their parent.
	 */
	protected abstract void updateRelativePathRoots();

	/**
	 * Runs the Probe to create a table with collected probe data
	 */
	@Override
	public Table runProbe(FocusChangingRefreshable refreshable, IProgressMonitor monitor) {

		String identifier = this.getClass().getSimpleName() + " '" + getName() + "'";

		LOG.info("Running " + identifier + "...");

		//control flag
		boolean continueProbe = false;

		//(re)create table
		Table table = null;
		try {
			table = reCreateTable();
			continueProbe = true;
		} catch (IllegalStateException exception) {
			String exceptionMessage = exception.getMessage();
			String message = "Could not create the probe table. The probe has been canceled.\n" + exceptionMessage;
			Utils.showMessage(message);
		}

		if (continueProbe) {
			//this.runUiJobNonBlocking(() -> refreshable.refresh());

			//collect probe data
			collectProbeDataAndFillTable(table);
			this.runUiJobNonBlocking(() -> {
				if (refreshable != null) {
					refreshable.refresh();
				}
			});
		}

		LOG.info("Finished " + identifier + ".");

		return table;
	}

	/**
	 * Deletes the old probe table and creates a new one
	 *
	 * @return
	 */
	protected Table reCreateTable() throws IllegalStateException {

		boolean tableExists = hasChildTable();
		Table table;
		if (tableExists) {
			//delete old table
			table = getFirstChildTable();
			children.remove(table);
		}

		//create new table
		String probeTableName = getName() + "Table";
		table = new Table(probeTableName);
		addChild(table);

		//create columns
		createTableColumns(table);

		return table;
	}

	/**
	 * Creates columns for the given table
	 *
	 * @param table
	 */
	protected abstract void createTableColumns(Table table) throws IllegalStateException;

	/**
	 * Collects the probe data and fills the probe table
	 */
	protected abstract void collectProbeDataAndFillTable(Table table);

	protected static void createColumns(Table table, List<ColumnBlueprint> columnBlueprints) {
		Columns columns = table.createColumns("columns");
		for (ColumnBlueprint columnBlueprint : columnBlueprints) {
			columns.createColumn(columnBlueprint);
		}
	}

	/**
	 * Returns true if this atom has a child of type Table
	 *
	 * @param wantedClass
	 */
	private boolean hasChildTable() {
		for (AbstractAtom child : children) {
			Class<?> currentClass = child.getClass();
			boolean isTable = currentClass.equals(Table.class);
			if (isTable) {
				return true;
			}
		}
		return false;

	}

	/**
	 * Returns true if this atom has a child of type Table. Throws an exception if no child table could be found
	 *
	 * @param wantedClass
	 */
	private Table getFirstChildTable() {
		for (AbstractAtom child : children) {
			Class<?> currentClass = child.getClass();
			boolean isTable = currentClass.equals(Table.class);
			if (isTable) {
				return (Table) child;
			}
		}
		String message = "The probe " + getName() + " has no probe table. You need to create it before accessing it.";
		throw new IllegalStateException(message);

	}

	/**
	 * Provides an image to represent this atom
	 */
	@Override
	public Image provideImage() {
		Image baseImage = provideBaseImage();
		Image sweepProbeImage = org.treez.core.Activator.getOverlayImageStatic(baseImage, "probe.png");
		return sweepProbeImage;
	}

	protected abstract Image provideBaseImage();

	//#end region

}
