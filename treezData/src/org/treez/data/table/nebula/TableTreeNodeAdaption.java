package org.treez.data.table.nebula;

import org.treez.core.atom.base.AtomTreeNodeAdaption;

public class TableTreeNodeAdaption extends AtomTreeNodeAdaption {

	//#region CONSTRUCTORS

	public TableTreeNodeAdaption(Table correspondingAtomAdaptable) {
		super(correspondingAtomAdaptable);
	}

	//#end region

	//#region METHODS

	@Override
	public void preExpand() {
		Table table = (Table) atomAdaptable;
		table.reload();
	}

	//#end region

}
