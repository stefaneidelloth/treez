package org.treez.data.table;

import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 * Use a JTable as a renderer for row numbers of a given main table. This table must be added to the row header of the
 * scroll pane that contains the main table.
 */
public class RowNumberTable extends JTable implements ChangeListener, PropertyChangeListener {

	//#region ATTRIBUTES

	private static final long serialVersionUID = -9039089014126726175L;

	private JTable main;

	//#end region

	//#region CONSTRUCTORS

	public RowNumberTable(JTable table) {
		main = table;
		main.addPropertyChangeListener(this);
		main.getModel().addTableModelListener(this);

		setFocusable(false);
		setAutoCreateColumnsFromModel(false);
		setSelectionModel(main.getSelectionModel());

		TableColumn column = new TableColumn();
		column.setHeaderValue(" ");
		addColumn(column);
		column.setCellRenderer(new RowNumberRenderer());

		final int width = 50;
		getColumnModel().getColumn(0).setPreferredWidth(width);
		setPreferredScrollableViewportSize(getPreferredSize());
	}

	//#end region

	//#region METHODS

	@Override
	public void addNotify() {
		super.addNotify();

		Component c = getParent();

		//  Keep scrolling of the row table in sync with the main table.

		if (c instanceof JViewport) {
			JViewport viewport = (JViewport) c;
			viewport.addChangeListener(this);
		}
	}

	/*
	 *  Delegate method to main table
	 */
	@Override
	public int getRowCount() {
		return main.getRowCount();
	}

	@Override
	public int getRowHeight(int row) {
		int rowHeight = main.getRowHeight(row);

		if (rowHeight != super.getRowHeight(row)) {
			super.setRowHeight(row, rowHeight);
		}

		return rowHeight;
	}

	/*
	 *  No model is being used for this table so just use the row number
	 *  as the value of the cell.
	 */
	@Override
	public Object getValueAt(int row, int column) {
		return Integer.toString(row + 1);
	}

	/*
	 *  Don't edit data in the main TableModel by mistake
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	/*
	 *  Do nothing since the table ignores the model
	 */
	@Override
	public void setValueAt(Object value, int row, int column) {}

	//
	//  Implement the ChangeListener
	//
	@Override
	public void stateChanged(ChangeEvent e) {
		//  Keep the scrolling of the row table in sync with main table

		JViewport viewport = (JViewport) e.getSource();
		JScrollPane scrollPane = (JScrollPane) viewport.getParent();
		scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
	}

	//
	//  Implement the PropertyChangeListener
	//
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		//  Keep the row table in sync with the main table

		if ("selectionModel".equals(e.getPropertyName())) {
			setSelectionModel(main.getSelectionModel());
		}

		if ("rowHeight".equals(e.getPropertyName())) {
			repaint();
		}

		if ("model".equals(e.getPropertyName())) {
			main.getModel().addTableModelListener(this);
			revalidate();
		}
	}

	//
	//  Implement the TableModelListener
	//
	@Override
	public void tableChanged(TableModelEvent e) {
		revalidate();
	}

	//#end region

	//#region INTERNAL CLASSES

	/**
	 * Attempt to mimic the table header renderer
	 */
	private static class RowNumberRenderer extends DefaultTableCellRenderer {

		//#region ATTRIBUTES

		private static final long serialVersionUID = 1396266162559922803L;

		//#end region

		//#region CONSTRUCTORS

		RowNumberRenderer() {
			setHorizontalAlignment(SwingConstants.CENTER);
		}

		//#end region

		//#region METHODS

		@Override
		public Component getTableCellRendererComponent(
				JTable table,
				Object value,
				boolean isSelected,
				boolean hasFocus,
				int row,
				int column) {
			if (table != null) {
				JTableHeader header = table.getTableHeader();

				if (header != null) {
					setForeground(header.getForeground());
					setBackground(header.getBackground());
					setFont(header.getFont());
				}
			}

			if (isSelected) {
				setFont(getFont().deriveFont(Font.BOLD));
			}

			String text = "";
			if (value != null) {
				text = value.toString();
			}

			setText(text);
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));

			return this;
		}

		//#end region
	}

	//#end region
}
