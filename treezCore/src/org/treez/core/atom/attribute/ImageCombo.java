package org.treez.core.atom.attribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleTextAdapter;
import org.eclipse.swt.accessibility.AccessibleTextEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;

/**
 * The ImageCombo combines a text field and a table and creates a notification when an item is selected. *
 */
public final class ImageCombo extends Composite {

	//#region "ATTRIBUTES"

	private static final Color DEFAULT_BACKGROUND_COLOR = new Color(null, 255, 255, 255);

	private Text text;

	private Table table;

	private final int initalVisibleItemCount = 5;

	private int visibleItemCount = initalVisibleItemCount;

	private Shell popup;

	private Button arrow;

	private boolean hasFocus;

	private Listener listener;

	private Listener filter;

	private Color foreground;

	private Color background;

	private Font font;

	//#end region

	//#region "CONSTRUCTORS"

	public ImageCombo(Composite parent, int style) {
		super(parent, checkStyle(style));

		createListener();

		text = new Text(this, SWT.NONE);

		int[] textEvents = {
				SWT.KeyDown,
				SWT.KeyUp,
				SWT.Modify,
				SWT.MouseDown,
				SWT.MouseUp,
				SWT.Traverse,
				SWT.FocusIn };
		for (int i = 0; i < textEvents.length; i++) {
			text.addListener(textEvents[i], listener);
		}

		int checkedStyle = checkStyle(style);
		createArrow(checkedStyle);

		filter = new Listener() {

			@Override
			public void handleEvent(Event event) {
				Shell shell = ((Control) event.widget).getShell();
				if (shell == ImageCombo.this.getShell()) {
					handleFocus(SWT.FocusOut);
				}
			}
		};

		int[] comboEvents = { SWT.Dispose, SWT.Move, SWT.Resize };
		for (int i = 0; i < comboEvents.length; i++) {
			this.addListener(comboEvents[i], listener);
		}

		createPopup(-1);
		initAccessible();
		setBackground(DEFAULT_BACKGROUND_COLOR);
	}

	//#end region

	//#region METHODS

	private void createArrow(int checkedStyle) {
		int arrowStyle = SWT.ARROW | SWT.DOWN;
		if ((checkedStyle & SWT.FLAT) != 0) {
			arrowStyle |= SWT.FLAT;
		}
		arrow = new Button(this, arrowStyle);

		int[] arrowEvents = { SWT.Selection, SWT.FocusIn };
		for (int i = 0; i < arrowEvents.length; i++) {
			arrow.addListener(arrowEvents[i], listener);
		}
	}

	private void createListener() {
		listener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (popup == event.widget) {
					popupEvent(event);
					return;
				}
				if (text == event.widget) {
					textEvent(event);
					return;
				}
				if (table == event.widget) {
					listEvent(event);
					return;
				}
				if (arrow == event.widget) {
					arrowEvent(event);
					return;
				}
				if (ImageCombo.this == event.widget) {
					comboEvent(event);
					return;
				}
				if (getShell() == event.widget) {
					handleFocus(SWT.FocusOut);
				}
			}
		};
	}

	static int checkStyle(int style) {
		int directionOptions = SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;
		int mask = SWT.BORDER | SWT.READ_ONLY | SWT.FLAT | directionOptions;
		return style & mask;
	}

	/**
	 * Adds the argument to the end of the receiver's list.
	 */
	public void add(String string, Image image) {
		checkWidget();
		if (string == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		TableItem newItem = new TableItem(this.table, SWT.NONE);
		newItem.setText(string);
		if (image != null) {
			newItem.setImage(image);
		}
	}

	/**
	 * Adds the argument to the receiver's list at the given zero-based index.
	 */
	public void add(String string, Image image, int index) {
		checkWidget();
		if (string == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		TableItem newItem = new TableItem(this.table, SWT.NONE, index);
		if (image != null) {
			newItem.setImage(image);
		}
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when the receiver's selection changes
	 */
	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection, typedListener);
		addListener(SWT.DefaultSelection, typedListener);
	}

	private void arrowEvent(Event event) {

		if (event.type == SWT.FocusIn) {
			handleFocus(SWT.FocusIn);
		} else if (event.type == SWT.Selection) {
			dropDown(!isDropped());
		}

	}

	private void comboEvent(Event event) {
		switch (event.type) {
		case SWT.Dispose:
			if (popup != null && !popup.isDisposed()) {
				table.removeListener(SWT.Dispose, listener);
				popup.dispose();
			}
			Shell shell = getShell();
			shell.removeListener(SWT.Deactivate, listener);
			Display display = getDisplay();
			display.removeFilter(SWT.FocusIn, filter);
			popup = null;
			text = null;
			table = null;
			arrow = null;
			break;
		case SWT.Move:
			dropDown(false);
			break;
		case SWT.Resize:
			internalLayout(false);
			break;
		default:

		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();
		int width = 0;
		int height = 0;
		String[] items = getStringsFromTable();
		int textWidth = 0;
		GC gc = new GC(text);
		int spacer = gc.stringExtent(" ").x; //$NON-NLS-1$
		for (int i = 0; i < items.length; i++) {
			textWidth = Math.max(gc.stringExtent(items[i]).x, textWidth);
		}
		gc.dispose();
		Point textSize = text.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
		Point arrowSize = arrow.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
		Point listSize = table.computeSize(wHint, SWT.DEFAULT, changed);
		int borderWidth = getBorderWidth();

		height = Math.max(hHint, Math.max(textSize.y, arrowSize.y) + 2 * borderWidth);
		width = Math.max(wHint, Math.max(textWidth + 2 * spacer + arrowSize.x + 2 * borderWidth, listSize.x));
		return new Point(width, height);
	}

	private void createPopup(int selectionIndex) {

		//create shell and list
		popup = new Shell(getShell(), SWT.NO_TRIM | SWT.ON_TOP);

		createTable();

		int[] popupEvents = { SWT.Close, SWT.Paint, SWT.Deactivate };
		for (int i = 0; i < popupEvents.length; i++) {
			popup.addListener(popupEvents[i], listener);
		}
		int[] listEvents = {
				SWT.MouseUp,
				SWT.Selection,
				SWT.Traverse,
				SWT.KeyDown,
				SWT.KeyUp,
				SWT.FocusIn,
				SWT.Dispose };
		for (int i = 0; i < listEvents.length; i++) {
			table.addListener(listEvents[i], listener);
		}

		if (selectionIndex != -1) {
			table.setSelection(selectionIndex);
		}
	}

	private void createTable() {
		int style = getStyle();
		int listStyle = SWT.SINGLE | SWT.V_SCROLL;
		if ((style & SWT.FLAT) != 0) {
			listStyle |= SWT.FLAT;
		}
		if ((style & SWT.RIGHT_TO_LEFT) != 0) {
			listStyle |= SWT.RIGHT_TO_LEFT;
		}
		if ((style & SWT.LEFT_TO_RIGHT) != 0) {
			listStyle |= SWT.LEFT_TO_RIGHT;
		}

		//create a table instead of a list.
		table = new Table(popup, listStyle);
		if (font != null) {
			table.setFont(font);
		}
		if (foreground != null) {
			table.setForeground(foreground);
		}
		if (background != null) {
			table.setBackground(background);
		}
	}

	/**
	 * Deselects the item at the given zero-based index. If the item at the index was already deselected, it remains
	 * deselected. Indices that are out of range are ignored.
	 */
	public void deselect(int index) {
		checkWidget();
		table.deselect(index);
	}

	private void dropDown(boolean drop) {
		if (drop == isDropped()) {
			return;
		}
		if (!drop) {
			popup.setVisible(false);
			if (!isDisposed() && arrow.isFocusControl()) {
				text.setFocus();
			}
			return;
		}

		if (getShell() != popup.getParent()) {
			table.getItems();
			int selectionIndex = table.getSelectionIndex();
			table.removeListener(SWT.Dispose, listener);
			popup.dispose();
			popup = null;
			table = null;
			createPopup(selectionIndex);
		}

		Point size = getSize();
		int itemCount = determineItemCount();

		int itemHeight = table.getItemHeight() * itemCount;
		Point listSize = table.computeSize(SWT.DEFAULT, itemHeight, false);
		table.setBounds(1, 1, Math.max(size.x - 2, listSize.x), listSize.y);

		int index = table.getSelectionIndex();
		if (index != -1) {
			table.setTopIndex(index);
		}

		setPoupupSize();
		popup.setVisible(true);
		table.setFocus();
	}

	private void setPoupupSize() {
		Display display = getDisplay();
		Rectangle listRect = table.getBounds();
		Rectangle parentRect = display.map(getParent(), null, getBounds());
		Point comboSize = getSize();
		Rectangle displayRect = getMonitor().getClientArea();
		int width = Math.max(comboSize.x, listRect.width + 2);
		int height = listRect.height + 2;
		int x = parentRect.x;
		int y = parentRect.y + comboSize.y;
		if (y + height > displayRect.y + displayRect.height) {
			y = parentRect.y - height;
		}
		popup.setBounds(x, y, width, height);
	}

	private int determineItemCount() {
		int itemCount = table.getItemCount();

		if (itemCount == 0) {
			itemCount = visibleItemCount;
		} else {
			itemCount = Math.min(visibleItemCount, itemCount);
		}
		return itemCount;
	}

	/*
	 * Return the Label immediately preceding the receiver in the z-order, or
	 * null if none.
	 */
	private Label getAssociatedLabel() {
		Control[] siblings = getParent().getChildren();
		for (int i = 0; i < siblings.length; i++) {
			if (siblings[i] == ImageCombo.this) {
				if (i > 0 && siblings[i - 1] instanceof Label) {
					return (Label) siblings[i - 1];
				}
			}
		}
		return null;
	}

	@Override
	public Control[] getChildren() {
		checkWidget();
		return new Control[0];
	}

	/**
	 * Gets the editable state.
	 */
	public boolean getEditable() {
		checkWidget();
		return text.getEditable();
	}

	/**
	 * Returns the item at the given, zero-based index. Throws an exception if the index is out of range.
	 */
	public TableItem getItem(int index) {
		checkWidget();
		return this.table.getItem(index);
	}

	/**
	 * Returns the number of items
	 */
	public int getItemCount() {
		checkWidget();
		return table.getItemCount();
	}

	/**
	 * Returns the height of the area which would be used to display <em>one</em> of the items
	 */
	public int getItemHeight() {
		checkWidget();
		return table.getItemHeight();
	}

	/**
	 * Returns an array of <code>String</code>s which are the items in the list.
	 */
	public TableItem[] getItems() {
		checkWidget();
		return table.getItems();
	}

	private static char getMnemonic(String string) {
		int index = 0;
		int length = string.length();
		do {
			while ((index < length) && (string.charAt(index) != '&')) {
				index++;
			}
			if (++index >= length) {
				return '\0';
			}
			if (string.charAt(index) != '&') {
				return string.charAt(index);
			}
			index++;
		} while (index < length);
		return '\0';
	}

	String[] getStringsFromTable() {
		String[] items = new String[this.table.getItems().length];
		for (int i = 0, n = items.length; i < n; i++) {
			items[i] = this.table.getItem(i).getText();
		}
		return items;
	}

	/**
	 * Returns a <code>Point</code> whose x coordinate is the start of the selection in the receiver's text field, and
	 * whose y coordinate is the end of the selection. The returned values are zero-relative. An "empty" selection as
	 * indicated by the the x and y coordinates having the same filePath.
	 */
	public Point getSelection() {
		checkWidget();
		return text.getSelection();
	}

	/**
	 * Returns the zero-relative index of the item which is currently selected, or -1 if no item is selected.
	 */
	public int getSelectionIndex() {
		checkWidget();
		return table.getSelectionIndex();
	}

	@Override
	public int getStyle() {
		int style = super.getStyle();
		style &= ~SWT.READ_ONLY;
		if (!text.getEditable()) {
			style |= SWT.READ_ONLY;
		}
		return style;
	}

	/**
	 * Returns a string containing a copy of the contents.
	 */
	public String getText() {
		checkWidget();
		return text.getText();
	}

	void handleFocus(int type) {
		if (isDisposed()) {
			return;
		}
		doHandleFocus(type);
	}

	private void doHandleFocus(int type) {
		switch (type) {
		case SWT.FocusIn: {
			if (hasFocus) {
				return;
			}
			handleFocusIn();
			break;
		}
		case SWT.FocusOut: {
			if (!hasFocus) {
				return;
			}
			Control focusControl = getDisplay().getFocusControl();
			if (focusControl == arrow || focusControl == table || focusControl == text) {
				return;
			}
			handleFocusOut();
			break;
		}
		default:
		}
	}

	private void handleFocusOut() {
		hasFocus = false;
		Shell shell = getShell();
		shell.removeListener(SWT.Deactivate, listener);
		Display display = getDisplay();
		display.removeFilter(SWT.FocusIn, filter);
		Event e = new Event();
		notifyListeners(SWT.FocusOut, e);
	}

	private void handleFocusIn() {
		if (getEditable()) {
			text.selectAll();
		}
		hasFocus = true;
		Shell shell = getShell();
		shell.removeListener(SWT.Deactivate, listener);
		shell.addListener(SWT.Deactivate, listener);
		Display display = getDisplay();
		display.removeFilter(SWT.FocusIn, filter);
		display.addFilter(SWT.FocusIn, filter);
		Event e = new Event();
		notifyListeners(SWT.FocusIn, e);
	}

	private void initAccessible() {
		AccessibleAdapter accessibleAdapter = createAccessibleAdapter();
		getAccessible().addAccessibleListener(accessibleAdapter);
		text.getAccessible().addAccessibleListener(accessibleAdapter);
		table.getAccessible().addAccessibleListener(accessibleAdapter);

		arrow.getAccessible().addAccessibleListener(createAccessibleListener());

		getAccessible().addAccessibleTextListener(new AccessibleTextAdapter() {

			@Override
			public void getCaretOffset(AccessibleTextEvent e) {
				e.offset = text.getCaretPosition();
			}
		});

		getAccessible().addAccessibleControlListener(new ImageComboAccessibleControlAdapter(this));

		text.getAccessible().addAccessibleControlListener(createControlListener());

		arrow.getAccessible().addAccessibleControlListener(createArrowListener());
	}

	private AccessibleControlAdapter createArrowListener() {
		return new AccessibleControlAdapter() {

			@Override
			public void getDefaultAction(AccessibleControlEvent e) {
				String result = SWT.getMessage("SWT_Open");
				if (isDropped()) {
					result = SWT.getMessage("SWT_Close");
				}
				e.result = result;
			}
		};
	}

	private AccessibleControlAdapter createControlListener() {
		return new AccessibleControlAdapter() {

			@Override
			public void getRole(AccessibleControlEvent e) {
				int detail = ACC.ROLE_LABEL;
				if (text.getEditable()) {
					detail = ACC.ROLE_TEXT;
				}
				e.detail = detail;
			}
		};
	}

	private AccessibleAdapter createAccessibleListener() {
		return new AccessibleAdapter() {

			@Override
			public void getName(AccessibleEvent e) {
				String result = SWT.getMessage("SWT_Open");
				if (isDropped()) {
					result = SWT.getMessage("SWT_Close");
				}
				e.result = result;
			}

			@Override
			public void getKeyboardShortcut(AccessibleEvent e) {
				e.result = "Alt+Down Arrow"; //$NON-NLS-1$
			}

			@Override
			public void getHelp(AccessibleEvent e) {
				e.result = getToolTipText();
			}
		};
	}

	private AccessibleAdapter createAccessibleAdapter() {
		AccessibleAdapter accessibleAdapter = new AccessibleAdapter() {

			@Override
			public void getName(AccessibleEvent e) {
				String name = null;
				Label label = getAssociatedLabel();
				if (label != null) {
					name = stripMnemonic(label.getText());
				}
				e.result = name;
			}

			@Override
			public void getKeyboardShortcut(AccessibleEvent e) {
				String shortcut = getKeyboardShortCut();
				e.result = shortcut;
			}

			@Override
			public void getHelp(AccessibleEvent e) {
				e.result = getToolTipText();
			}
		};
		return accessibleAdapter;
	}

	private String getKeyboardShortCut() {
		String shortcut = null;
		Label label = getAssociatedLabel();
		if (label != null) {
			String currentText = label.getText();
			if (currentText != null) {
				char mnemonic = getMnemonic(currentText);
				if (mnemonic != '\0') {
					shortcut = "Alt+" + mnemonic; //$NON-NLS-1$
				}
			}
		}
		return shortcut;
	}

	boolean isDropped() {
		return popup.getVisible();
	}

	@Override
	public boolean isFocusControl() {
		checkWidget();
		if (text.isFocusControl() || arrow.isFocusControl() || table.isFocusControl() || popup.isFocusControl()) {
			return true;
		}
		return super.isFocusControl();
	}

	void internalLayout(boolean changed) {
		if (isDropped()) {
			dropDown(false);
		}
		Rectangle rect = getClientArea();
		int width = rect.width;
		int height = rect.height;
		Point arrowSize = arrow.computeSize(SWT.DEFAULT, height, changed);
		text.setBounds(0, 0, width - arrowSize.x, height);
		arrow.setBounds(width - arrowSize.x, 0, arrowSize.x, arrowSize.y);
	}

	@SuppressWarnings({ "checkstyle:javancss", "checkstyle:cyclomaticcomplexity" })
	void listEvent(Event event) {
		switch (event.type) {
		case SWT.Dispose:
			if (getShell() != popup.getParent()) {
				table.getItems();
				int selectionIndex = table.getSelectionIndex();
				popup = null;
				table = null;
				createPopup(selectionIndex);
			}
			break;
		case SWT.FocusIn: {
			handleFocus(SWT.FocusIn);
			break;
		}
		case SWT.MouseUp: {
			if (event.button != 1) {
				return;
			}
			dropDown(false);
			break;
		}
		case SWT.Selection: {
			int index = table.getSelectionIndex();
			if (index == -1) {
				return;
			}
			handleSelection(event, index);
			break;
		}
		case SWT.Traverse: {
			handleListTraverse(event);
			break;
		}
		case SWT.KeyUp: {
			handleKeyUp(event);
			break;
		}
		case SWT.KeyDown: {
			handleListKeyDown(event);
			//At this point the widget may have been disposed.
			//If so, do not continue.
			if (isDisposed()) {
				break;
			}
			notifyListListeners(event);
			break;

		}
		default:
		}
	}

	private void notifyListListeners(Event event) {
		Event e = new Event();
		e.time = event.time;
		e.character = event.character;
		e.keyCode = event.keyCode;
		e.stateMask = event.stateMask;
		notifyListeners(SWT.KeyDown, e);
	}

	private void handleListKeyDown(Event event) {
		if (event.character == SWT.ESC) {
			//Escape key cancels popup list
			dropDown(false);
		}
		if ((event.stateMask & SWT.ALT) != 0 && (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN)) {
			dropDown(false);
		}
		if (event.character == SWT.CR) {
			handleCrTextKeyEvent(event);
		}
	}

	private void handleListTraverse(Event event) {
		switch (event.detail) {
		case SWT.TRAVERSE_RETURN:
		case SWT.TRAVERSE_ESCAPE:
		case SWT.TRAVERSE_ARROW_PREVIOUS:
		case SWT.TRAVERSE_ARROW_NEXT:
			event.doit = false;
			break;
		default:
		}
		Event e = new Event();
		e.time = event.time;
		e.detail = event.detail;
		e.doit = event.doit;
		e.character = event.character;
		e.keyCode = event.keyCode;
		notifyListeners(SWT.Traverse, e);
		event.doit = e.doit;
		event.detail = e.detail;
	}

	private void handleSelection(Event event, int index) {
		text.setText(table.getItem(index).getText());
		text.selectAll();
		table.setSelection(index);
		Event e = new Event();
		e.time = event.time;
		e.stateMask = event.stateMask;
		e.doit = event.doit;
		notifyListeners(SWT.Selection, e);
		event.doit = e.doit;
	}

	void popupEvent(Event event) {
		switch (event.type) {
		case SWT.Paint:
			//draw black rectangle around list
			Rectangle listRect = table.getBounds();
			Color black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
			event.gc.setForeground(black);
			event.gc.drawRectangle(0, 0, listRect.width + 1, listRect.height + 1);
			break;
		case SWT.Close:
			event.doit = false;
			dropDown(false);
			break;
		case SWT.Deactivate:
			dropDown(false);
			break;
		default:
		}
	}

	@Override
	public void redraw() {
		super.redraw();
		text.redraw();
		arrow.redraw();
		if (popup.isVisible()) {
			table.redraw();
		}
	}

	@Override
	public void redraw(int x, int y, int width, int height, boolean all) {
		super.redraw(x, y, width, height, true);
	}

	/**
	 * Removes the item from the receiver's list at the given zero-relative index.
	 */
	public void remove(int index) {
		checkWidget();
		table.remove(index);
	}

	/**
	 * Selects the item at the given zero-relative index in the receiver's list. If the item at the index was already
	 * selected, it remains selected. Indices that are out of range are ignored.
	 */
	public void select(int index) {
		checkWidget();
		if (index == -1) {
			table.deselectAll();
			text.setText(""); //$NON-NLS-1$
			return;
		}
		if (0 <= index && index < table.getItemCount()) {
			if (index != getSelectionIndex()) {
				text.setText(table.getItem(index).getText());
				text.selectAll();
				table.select(index);
				table.showSelection();
			}
		}
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		background = color;
		if (text != null) {
			text.setBackground(color);
		}
		if (table != null) {
			table.setBackground(color);
		}
		if (arrow != null) {
			arrow.setBackground(color);
		}
	}

	/**
	 * Sets the editable state.
	 */
	public void setEditable(boolean editable) {
		checkWidget();
		text.setEditable(editable);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (popup != null) {
			popup.setVisible(false);
		}
		if (text != null) {
			text.setEnabled(enabled);
		}
		if (arrow != null) {
			arrow.setEnabled(enabled);
		}
	}

	@Override
	public boolean setFocus() {
		checkWidget();
		return text.setFocus();
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		this.font = font;
		text.setFont(font);
		table.setFont(font);
		internalLayout(true);
	}

	@Override
	public void setForeground(Color color) {
		super.setForeground(color);
		foreground = color;
		if (text != null) {
			text.setForeground(color);
		}
		if (table != null) {
			table.setForeground(color);
		}
		if (arrow != null) {
			arrow.setForeground(color);
		}
	}

	/**
	 * Sets the layout which is associated with the receiver to be the argument which may be null.
	 * <p>
	 * Note : No Layout can be set on this Control because it already manages the size and position of its children.
	 * </p>
	 */
	@Override
	public void setLayout(Layout layout) {
		checkWidget();
		return;
	}

	@Override
	public void setToolTipText(String string) {
		checkWidget();
		super.setToolTipText(string);
		arrow.setToolTipText(string);
		text.setToolTipText(string);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!visible) {
			popup.setVisible(false);
		}
	}

	private static String stripMnemonic(String string) {
		int index = 0;
		int length = string.length();
		do {
			while ((index < length) && (string.charAt(index) != '&')) {
				index++;
			}
			if (++index >= length) {
				return string;
			}
			if (string.charAt(index) != '&') {
				return string.substring(0, index - 1) + string.substring(index, length);
			}
			index++;
		} while (index < length);
		return string;
	}

	@SuppressWarnings({ "checkstyle:javancss", "checkstyle:cyclomaticcomplexity" })
	void textEvent(Event event) {
		switch (event.type) {
		case SWT.FocusIn: {
			handleFocus(SWT.FocusIn);
			break;
		}
		case SWT.KeyDown: {
			if (event.character == SWT.CR) {
				handleCrTextKeyEvent(event);
			}
			//At this point the widget may have been disposed.
			//If so, do not continue.
			if (isDisposed()) {
				break;
			}

			if (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN) {
				event.doit = false;
				if ((event.stateMask & SWT.ALT) != 0) {
					handleMouseDown();
					break;
				}

				handleArrowKeyUpOrDown(event);

				//At this point the widget may have been disposed.
				//If so, do not continue.
				if (isDisposed()) {
					break;
				}
			}

			//Further work : Need to add support for incremental search in
			//pop up list as characters typed in text widget

			notifyListListeners(event);
			break;
		}
		case SWT.KeyUp: {
			handleKeyUp(event);
			break;
		}
		case SWT.Modify: {
			handleModify(event);
			break;
		}
		case SWT.MouseDown: {
			if (event.button != 1) {
				return;
			}
			if (text.getEditable()) {
				return;
			}
			handleMouseDown();
			break;
		}
		case SWT.MouseUp: {
			if (event.button != 1) {
				return;
			}
			if (text.getEditable()) {
				return;
			}
			text.selectAll();
			break;
		}
		case SWT.Traverse: {
			handleTraverse(event);
			break;

		}
		default:
		}
	}

	private void handleArrowKeyUpOrDown(Event event) {
		int oldIndex = getSelectionIndex();
		if (event.keyCode == SWT.ARROW_UP) {
			select(Math.max(oldIndex - 1, 0));
		} else {
			select(Math.min(oldIndex + 1, getItemCount() - 1));
		}
		if (oldIndex != getSelectionIndex()) {
			Event e = new Event();
			e.time = event.time;
			e.stateMask = event.stateMask;
			notifyListeners(SWT.Selection, e);
		}
	}

	private void handleTraverse(Event event) {
		switch (event.detail) {
		case SWT.TRAVERSE_RETURN:
		case SWT.TRAVERSE_ARROW_PREVIOUS:
		case SWT.TRAVERSE_ARROW_NEXT:
			//The enter causes default selection and
			//the arrow keys are used to manipulate the list contents so
			//do not use them for traversal.
			event.doit = false;
			break;
		default:
		}

		Event e = new Event();
		e.time = event.time;
		e.detail = event.detail;
		e.doit = event.doit;
		e.character = event.character;
		e.keyCode = event.keyCode;
		notifyListeners(SWT.Traverse, e);
		event.doit = e.doit;
		event.detail = e.detail;
	}

	private void handleMouseDown() {
		boolean dropped = isDropped();
		text.selectAll();
		if (!dropped) {
			setFocus();
		}
		dropDown(!dropped);
	}

	private void handleModify(Event event) {
		table.deselectAll();
		Event e = new Event();
		e.time = event.time;
		notifyListeners(SWT.Modify, e);
	}

	private void handleKeyUp(Event event) {
		Event e = new Event();
		e.time = event.time;
		e.character = event.character;
		e.keyCode = event.keyCode;
		e.stateMask = event.stateMask;
		notifyListeners(SWT.KeyUp, e);
	}

	private void handleCrTextKeyEvent(Event event) {
		dropDown(false);
		Event e = new Event();
		e.time = event.time;
		e.stateMask = event.stateMask;
		notifyListeners(SWT.DefaultSelection, e);
	}

	//#end region
}
