package org.treez.results.atom.legend;

import org.treez.core.adaptable.Refreshable;
import org.treez.javafxd3.d3.D3;
import org.treez.javafxd3.d3.core.Selection;

public interface LegendContributor extends LegendContributorProvider {

	/**
	 * Returns true if a legend entry is actually contributed
	 */
	boolean providesLegendEntry();

	/**
	 * Provides the label text that will be shown in the legend
	 */
	String getLegendText();

	/**
	 * Creates the symbol that will be shown in the legend
	 */
	Selection createLegendSymbolGroup(D3 d3, Selection parentSelection, int symbolLengthInPx, Refreshable refreshable);

}
