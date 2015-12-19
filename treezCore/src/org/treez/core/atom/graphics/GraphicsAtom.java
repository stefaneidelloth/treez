package org.treez.core.atom.graphics;

import org.treez.core.atom.adjustable.AdjustableAtom;
import org.treez.core.atom.graphics.length.Length;
import org.treez.core.attribute.Attribute;
import org.treez.javafxd3.d3.core.Selection;
import org.treez.javafxd3.d3.functions.MouseClickFunction;

/**
 *
 */
public class GraphicsAtom extends AdjustableAtom implements MouseClickFunction {

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param name
	 */
	public GraphicsAtom(String name) {
		super(name);

	}

	//#end region

	//#region METHODS

	/**
	 * Binds the given Attribute to the JavaScript attribute with the given
	 * data. A change of the attribute will change the JavaScript attribute. The
	 * original value is trimmed and spaces are removed, e.g. " 1 cm " => "1cm".
	 *
	 * @param wrappingAttribute
	 * @param selection
	 * @param selectionAttributeName
	 */
	public void bindStringAttribute(Selection selection,
			String selectionAttributeName,
			Attribute<String> wrappingAttribute) {

		//set initial value
		selection.attr(selectionAttributeName, trim(wrappingAttribute.get()));

		//create one way binding
		addModificationConsumer(wrappingAttribute, (newValue) -> selection
				.attr(selectionAttributeName, trim(newValue)));
	}

	/**
	 * public void bindStringAttribute(Selection selection, String
	 * selectionAttributeName, Attribute <String> wrappingAttribute) { //set
	 * initial value selection.attr(selectionAttributeName,
	 * trim(wrappingAttribute.get())); //create one way binding
	 * addModificationConsumer(wrappingAttribute, (newValue) -> selection
	 * .attr(selectionAttributeName, trim(newValue))); } /** If the state of the
	 * Boolean attribute is true, the dislpay of the selection will be set to
	 * 'none', meaning it is not visible. If the State is false, the display
	 * will be set to 'inline', meaning it is visible.
	 *
	 * @param wrappingAttribute
	 * @param selection
	 */
	public void bindDisplayToBooleanAttribute(Selection selection,
			Attribute<Boolean> wrappingAttribute) {

		//set initial value
		Boolean state = wrappingAttribute.get();
		if (state) {
			selection.attr("display", "none");
		} else {
			selection.attr("display", "inline");
		}

		//create one way binding
		addModificationConsumer(wrappingAttribute, (newValue) -> {
			if (newValue) {
				selection.attr("display", "none");
			} else {
				selection.attr("display", "inline");
			}
		});

	}

	/**
	 * @param leftMargin
	 * @param topMargin
	 */
	public void bindTranslationAttribute(Selection selection,
			Attribute<String> leftMargin, Attribute<String> topMargin) {

		updateTranslation(selection, leftMargin, topMargin);

		addModificationConsumer(leftMargin, (newValue) -> {
			updateTranslation(selection, leftMargin, topMargin);
		});

		addModificationConsumer(topMargin, (newValue) -> {
			updateTranslation(selection, leftMargin, topMargin);
		});

	}

	private static void updateTranslation(Selection selection,
			Attribute<String> leftMargin, Attribute<String> topMargin) {

		try {
			String xString = leftMargin.get();
			String yString = topMargin.get();
			Double x = Length.toPx(xString);
			Double y = Length.toPx(yString);
			String transformString = "translate(" + x + "," + y + ")";
			selection.attr("transform", transformString);
		} catch (IllegalArgumentException exception) {

		}
	}

	/**
	 * Applies toString() to the given object, trims the result and removes
	 * spaces
	 *
	 * @param value
	 * @return
	 */
	protected String trim(Object value) {
		String result = value.toString().trim().replace(" ", "");
		return result;
	}

	/**
	 * Handles JavaScript mouse click on Page rect
	 */
	@Override
	public void handleMouseClick(Object context) {
		setFocus(this);
	}

	//#end region

}
