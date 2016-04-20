package org.treez.core.treeview;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.jface.viewers.IElementComparer;
import org.treez.core.adaptable.Adaptable;

/**
 * Compares two tree nodes by their names
 */
public class NodeComparer implements IElementComparer {

	//#region METHODS

	@Override
	public boolean equals(Object a, Object b) {
		if (a == b) {
			return true;
		}
		if (a instanceof Adaptable && b instanceof Adaptable) {
			String nameA = ((Adaptable) a).createTreeNodeAdaption().getName();
			String nameB = ((Adaptable) b).createTreeNodeAdaption().getName();

			return new EqualsBuilder().append(nameA, nameB).isEquals();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode(Object element) {
		String name = ((Adaptable) element).createTreeNodeAdaption().getName();
		final int initialOddNumber = 17;
		final int multiplier = 31;
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(initialOddNumber,
				multiplier);
		int hashCode = hashCodeBuilder.append(name).toHashCode();
		return hashCode;

	}

	//#end region

}