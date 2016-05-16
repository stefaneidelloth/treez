package org.treez.core.color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * Some of the color arrays used here originate from D3.js and some from ColorBrewer
 *
 * <pre>
 * www.ColorBrewer.org by Cynthia A. Brewer, Geography, Pennsylvania State University.
 *
 *  http://personal.psu.edu/cab38/ColorBrewer/ColorBrewer_updates.html
 *
 * Apache-Style Software License for ColorBrewer software and ColorBrewer Color Schemes
 * Copyright (c) 2002 Cynthia Brewer, Mark Harrower, and The Pennsylvania State University.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * </pre>
 *
 * <pre>
 * https://github.com/mbostock/d3/wiki/Ordinal-Scales by Mike Bostock
 *
 *
 * Copyright (c) 2010-2016, Michael Bostock
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * The name Michael Bostock may not be used to endorse or promote products
 *   derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL MICHAEL BOSTOCK BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * </pre>
 */
/**
 *
 */
@SuppressWarnings({"checkstyle:visibilitymodifier", "checkstyle:magicnumber",
		"checkstyle:javancss", "checkstyle:executablestatementcount"})
public final class ColorBrewer {

	//#region ATTRIBUTES

	public static Map<String, Map<Integer, String[]>> colorFamilies;

	public static Map<Integer, String[]> Category;

	public static Map<Integer, String[]> Veusz;

	public static Map<Integer, String[]> YlGn;

	public static Map<Integer, String[]> YlGnBu;

	public static Map<Integer, String[]> GnBu;

	public static Map<Integer, String[]> BuGn;

	public static Map<Integer, String[]> PuBuGn;

	public static Map<Integer, String[]> PuBu;

	public static Map<Integer, String[]> BuPu;

	public static Map<Integer, String[]> RdPu;

	public static Map<Integer, String[]> PuRd;

	public static Map<Integer, String[]> OrRd;

	public static Map<Integer, String[]> YlOrRd;

	public static Map<Integer, String[]> YlOrBr;

	public static Map<Integer, String[]> Purples;

	public static Map<Integer, String[]> Blues;

	public static Map<Integer, String[]> Greens;

	public static Map<Integer, String[]> Oranges;

	public static Map<Integer, String[]> Reds;

	public static Map<Integer, String[]> Greys;

	public static Map<Integer, String[]> PuOr;

	public static Map<Integer, String[]> BrBG;

	public static Map<Integer, String[]> PRGn;

	public static Map<Integer, String[]> PiYG;

	public static Map<Integer, String[]> RdBu;

	public static Map<Integer, String[]> RdGy;

	public static Map<Integer, String[]> RdYlBu;

	public static Map<Integer, String[]> Spectral;

	public static Map<Integer, String[]> RdYlGn;

	public static Map<Integer, String[]> Accent;

	public static Map<Integer, String[]> Dark2;

	public static Map<Integer, String[]> Paired;

	public static Map<Integer, String[]> Pastel1;

	public static Map<Integer, String[]> Pastel2;

	public static Map<Integer, String[]> Set1;

	public static Map<Integer, String[]> Set2;

	public static Map<Integer, String[]> Set3;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction
	 */
	private ColorBrewer() {
	}

	/**
	 * Static constructor
	 */

	static {

		Category = new LinkedHashMap<>();
		Category.put(3, new String[]{"#1f77b4", "#2ca02c", "#d62728"});

		Category.put(10,
				new String[]{"#1f77b4", "#ff7f0e", "#2ca02c", "#d62728",
						"#9467bd", "#8c564b", "#e377c2", "#7f7f7f", "#bcbd22",
						"#17becf"});

		Category.put(20,
				new String[]{"#1f77b4", "#aec7e8", "#ff7f0e", "#ffbb78",
						"#2ca02c", "#98df8a", "#d62728", "#ff9896", "#9467bd",
						"#c5b0d5", "#8c564b", "#c49c94", "#e377c2", "#f7b6d2",
						"#7f7f7f", "#c7c7c7", "#bcbd22", "#dbdb8d", "#17becf",
						"#9edae5"});

		Veusz = new LinkedHashMap<>();
		Veusz.put(13,
				new String[]{"#000000", "#ff0000", "#008000", "#0000ff",
						"#00ffff", "#ff00ff", "#ffff00", "#808080", "#8b0000",
						"#006400", "#00008b", "#008b8b", "#8b008b"});

		YlGn = new LinkedHashMap<>();
		YlGn.put(3, new String[]{"#f7fcb9", "#addd8e", "#31a354"});
		YlGn.put(4, new String[]{"#ffffcc", "#c2e699", "#78c679", "#238443"});
		YlGn.put(5, new String[]{"#ffffcc", "#c2e699", "#78c679", "#31a354",
				"#006837"});
		YlGn.put(6, new String[]{"#ffffcc", "#d9f0a3", "#addd8e", "#78c679",
				"#31a354", "#006837"});
		YlGn.put(7, new String[]{"#ffffcc", "#d9f0a3", "#addd8e", "#78c679",
				"#41ab5d", "#238443", "#005a32"});
		YlGn.put(8, new String[]{"#ffffe5", "#f7fcb9", "#d9f0a3", "#addd8e",
				"#78c679", "#41ab5d", "#238443", "#005a32"});
		YlGn.put(9, new String[]{"#ffffe5", "#f7fcb9", "#d9f0a3", "#addd8e",
				"#78c679", "#41ab5d", "#238443", "#006837", "#004529"});

		YlGnBu = new LinkedHashMap<>();
		YlGnBu.put(3, new String[]{"#edf8b1", "#7fcdbb", "#2c7fb8"});
		YlGnBu.put(4, new String[]{"#ffffcc", "#a1dab4", "#41b6c4", "#225ea8"});
		YlGnBu.put(5, new String[]{"#ffffcc", "#a1dab4", "#41b6c4", "#2c7fb8",
				"#253494"});
		YlGnBu.put(6, new String[]{"#ffffcc", "#c7e9b4", "#7fcdbb", "#41b6c4",
				"#2c7fb8", "#253494"});
		YlGnBu.put(7, new String[]{"#ffffcc", "#c7e9b4", "#7fcdbb", "#41b6c4",
				"#1d91c0", "#225ea8", "#0c2c84"});
		YlGnBu.put(8, new String[]{"#ffffd9", "#edf8b1", "#c7e9b4", "#7fcdbb",
				"#41b6c4", "#1d91c0", "#225ea8", "#0c2c84"});
		YlGnBu.put(9, new String[]{"#ffffd9", "#edf8b1", "#c7e9b4", "#7fcdbb",
				"#41b6c4", "#1d91c0", "#225ea8", "#253494", "#081d58"});

		GnBu = new LinkedHashMap<>();
		GnBu.put(3, new String[]{"#e0f3db", "#a8ddb5", "#43a2ca"});
		GnBu.put(4, new String[]{"#f0f9e8", "#bae4bc", "#7bccc4", "#2b8cbe"});
		GnBu.put(5, new String[]{"#f0f9e8", "#bae4bc", "#7bccc4", "#43a2ca",
				"#0868ac"});
		GnBu.put(6, new String[]{"#f0f9e8", "#ccebc5", "#a8ddb5", "#7bccc4",
				"#43a2ca", "#0868ac"});
		GnBu.put(7, new String[]{"#f0f9e8", "#ccebc5", "#a8ddb5", "#7bccc4",
				"#4eb3d3", "#2b8cbe", "#08589e"});
		GnBu.put(8, new String[]{"#f7fcf0", "#e0f3db", "#ccebc5", "#a8ddb5",
				"#7bccc4", "#4eb3d3", "#2b8cbe", "#08589e"});
		GnBu.put(9, new String[]{"#f7fcf0", "#e0f3db", "#ccebc5", "#a8ddb5",
				"#7bccc4", "#4eb3d3", "#2b8cbe", "#0868ac", "#084081"});

		BuGn = new LinkedHashMap<>();
		BuGn.put(3, new String[]{"#e5f5f9", "#99d8c9", "#2ca25f"});
		BuGn.put(4, new String[]{"#edf8fb", "#b2e2e2", "#66c2a4", "#238b45"});
		BuGn.put(5, new String[]{"#edf8fb", "#b2e2e2", "#66c2a4", "#2ca25f",
				"#006d2c"});
		BuGn.put(6, new String[]{"#edf8fb", "#ccece6", "#99d8c9", "#66c2a4",
				"#2ca25f", "#006d2c"});
		BuGn.put(7, new String[]{"#edf8fb", "#ccece6", "#99d8c9", "#66c2a4",
				"#41ae76", "#238b45", "#005824"});
		BuGn.put(8, new String[]{"#f7fcfd", "#e5f5f9", "#ccece6", "#99d8c9",
				"#66c2a4", "#41ae76", "#238b45", "#005824"});
		BuGn.put(9, new String[]{"#f7fcfd", "#e5f5f9", "#ccece6", "#99d8c9",
				"#66c2a4", "#41ae76", "#238b45", "#006d2c", "#00441b"});

		PuBuGn = new LinkedHashMap<>();
		PuBuGn.put(3, new String[]{"#ece2f0", "#a6bddb", "#1c9099"});
		PuBuGn.put(4, new String[]{"#f6eff7", "#bdc9e1", "#67a9cf", "#02818a"});
		PuBuGn.put(5, new String[]{"#f6eff7", "#bdc9e1", "#67a9cf", "#1c9099",
				"#016c59"});
		PuBuGn.put(6, new String[]{"#f6eff7", "#d0d1e6", "#a6bddb", "#67a9cf",
				"#1c9099", "#016c59"});
		PuBuGn.put(7, new String[]{"#f6eff7", "#d0d1e6", "#a6bddb", "#67a9cf",
				"#3690c0", "#02818a", "#016450"});
		PuBuGn.put(8, new String[]{"#fff7fb", "#ece2f0", "#d0d1e6", "#a6bddb",
				"#67a9cf", "#3690c0", "#02818a", "#016450"});
		PuBuGn.put(9, new String[]{"#fff7fb", "#ece2f0", "#d0d1e6", "#a6bddb",
				"#67a9cf", "#3690c0", "#02818a", "#016c59", "#014636"});

		PuBu = new LinkedHashMap<>();
		PuBu.put(3, new String[]{"#ece7f2", "#a6bddb", "#2b8cbe"});
		PuBu.put(4, new String[]{"#f1eef6", "#bdc9e1", "#74a9cf", "#0570b0"});
		PuBu.put(5, new String[]{"#f1eef6", "#bdc9e1", "#74a9cf", "#2b8cbe",
				"#045a8d"});
		PuBu.put(6, new String[]{"#f1eef6", "#d0d1e6", "#a6bddb", "#74a9cf",
				"#2b8cbe", "#045a8d"});
		PuBu.put(7, new String[]{"#f1eef6", "#d0d1e6", "#a6bddb", "#74a9cf",
				"#3690c0", "#0570b0", "#034e7b"});
		PuBu.put(8, new String[]{"#fff7fb", "#ece7f2", "#d0d1e6", "#a6bddb",
				"#74a9cf", "#3690c0", "#0570b0", "#034e7b"});
		PuBu.put(9, new String[]{"#fff7fb", "#ece7f2", "#d0d1e6", "#a6bddb",
				"#74a9cf", "#3690c0", "#0570b0", "#045a8d", "#023858"});

		BuPu = new LinkedHashMap<>();
		BuPu.put(3, new String[]{"#e0ecf4", "#9ebcda", "#8856a7"});
		BuPu.put(4, new String[]{"#edf8fb", "#b3cde3", "#8c96c6", "#88419d"});
		BuPu.put(5, new String[]{"#edf8fb", "#b3cde3", "#8c96c6", "#8856a7",
				"#810f7c"});
		BuPu.put(6, new String[]{"#edf8fb", "#bfd3e6", "#9ebcda", "#8c96c6",
				"#8856a7", "#810f7c"});
		BuPu.put(7, new String[]{"#edf8fb", "#bfd3e6", "#9ebcda", "#8c96c6",
				"#8c6bb1", "#88419d", "#6e016b"});
		BuPu.put(8, new String[]{"#f7fcfd", "#e0ecf4", "#bfd3e6", "#9ebcda",
				"#8c96c6", "#8c6bb1", "#88419d", "#6e016b"});
		BuPu.put(9, new String[]{"#f7fcfd", "#e0ecf4", "#bfd3e6", "#9ebcda",
				"#8c96c6", "#8c6bb1", "#88419d", "#810f7c", "#4d004b"});

		RdPu = new LinkedHashMap<>();
		RdPu.put(3, new String[]{"#fde0dd", "#fa9fb5", "#c51b8a"});
		RdPu.put(4, new String[]{"#feebe2", "#fbb4b9", "#f768a1", "#ae017e"});
		RdPu.put(5, new String[]{"#feebe2", "#fbb4b9", "#f768a1", "#c51b8a",
				"#7a0177"});
		RdPu.put(6, new String[]{"#feebe2", "#fcc5c0", "#fa9fb5", "#f768a1",
				"#c51b8a", "#7a0177"});
		RdPu.put(7, new String[]{"#feebe2", "#fcc5c0", "#fa9fb5", "#f768a1",
				"#dd3497", "#ae017e", "#7a0177"});
		RdPu.put(8, new String[]{"#fff7f3", "#fde0dd", "#fcc5c0", "#fa9fb5",
				"#f768a1", "#dd3497", "#ae017e", "#7a0177"});
		RdPu.put(9, new String[]{"#fff7f3", "#fde0dd", "#fcc5c0", "#fa9fb5",
				"#f768a1", "#dd3497", "#ae017e", "#7a0177", "#49006a"});

		PuRd = new LinkedHashMap<>();
		PuRd.put(3, new String[]{"#e7e1ef", "#c994c7", "#dd1c77"});
		PuRd.put(4, new String[]{"#f1eef6", "#d7b5d8", "#df65b0", "#ce1256"});
		PuRd.put(5, new String[]{"#f1eef6", "#d7b5d8", "#df65b0", "#dd1c77",
				"#980043"});
		PuRd.put(6, new String[]{"#f1eef6", "#d4b9da", "#c994c7", "#df65b0",
				"#dd1c77", "#980043"});
		PuRd.put(7, new String[]{"#f1eef6", "#d4b9da", "#c994c7", "#df65b0",
				"#e7298a", "#ce1256", "#91003f"});
		PuRd.put(8, new String[]{"#f7f4f9", "#e7e1ef", "#d4b9da", "#c994c7",
				"#df65b0", "#e7298a", "#ce1256", "#91003f"});
		PuRd.put(9, new String[]{"#f7f4f9", "#e7e1ef", "#d4b9da", "#c994c7",
				"#df65b0", "#e7298a", "#ce1256", "#980043", "#67001f"});

		OrRd = new LinkedHashMap<>();
		OrRd.put(3, new String[]{"#fee8c8", "#fdbb84", "#e34a33"});
		OrRd.put(4, new String[]{"#fef0d9", "#fdcc8a", "#fc8d59", "#d7301f"});
		OrRd.put(5, new String[]{"#fef0d9", "#fdcc8a", "#fc8d59", "#e34a33",
				"#b30000"});
		OrRd.put(6, new String[]{"#fef0d9", "#fdd49e", "#fdbb84", "#fc8d59",
				"#e34a33", "#b30000"});
		OrRd.put(7, new String[]{"#fef0d9", "#fdd49e", "#fdbb84", "#fc8d59",
				"#ef6548", "#d7301f", "#990000"});
		OrRd.put(8, new String[]{"#fff7ec", "#fee8c8", "#fdd49e", "#fdbb84",
				"#fc8d59", "#ef6548", "#d7301f", "#990000"});
		OrRd.put(9, new String[]{"#fff7ec", "#fee8c8", "#fdd49e", "#fdbb84",
				"#fc8d59", "#ef6548", "#d7301f", "#b30000", "#7f0000"});

		YlOrRd = new LinkedHashMap<>();
		YlOrRd.put(3, new String[]{"#ffeda0", "#feb24c", "#f03b20"});
		YlOrRd.put(4, new String[]{"#ffffb2", "#fecc5c", "#fd8d3c", "#e31a1c"});
		YlOrRd.put(5, new String[]{"#ffffb2", "#fecc5c", "#fd8d3c", "#f03b20",
				"#bd0026"});
		YlOrRd.put(6, new String[]{"#ffffb2", "#fed976", "#feb24c", "#fd8d3c",
				"#f03b20", "#bd0026"});
		YlOrRd.put(7, new String[]{"#ffffb2", "#fed976", "#feb24c", "#fd8d3c",
				"#fc4e2a", "#e31a1c", "#b10026"});
		YlOrRd.put(8, new String[]{"#ffffcc", "#ffeda0", "#fed976", "#feb24c",
				"#fd8d3c", "#fc4e2a", "#e31a1c", "#b10026"});
		YlOrRd.put(9, new String[]{"#ffffcc", "#ffeda0", "#fed976", "#feb24c",
				"#fd8d3c", "#fc4e2a", "#e31a1c", "#bd0026", "#800026"});

		YlOrBr = new LinkedHashMap<>();
		YlOrBr.put(3, new String[]{"#fff7bc", "#fec44f", "#d95f0e"});
		YlOrBr.put(4, new String[]{"#ffffd4", "#fed98e", "#fe9929", "#cc4c02"});
		YlOrBr.put(5, new String[]{"#ffffd4", "#fed98e", "#fe9929", "#d95f0e",
				"#993404"});
		YlOrBr.put(6, new String[]{"#ffffd4", "#fee391", "#fec44f", "#fe9929",
				"#d95f0e", "#993404"});
		YlOrBr.put(7, new String[]{"#ffffd4", "#fee391", "#fec44f", "#fe9929",
				"#ec7014", "#cc4c02", "#8c2d04"});
		YlOrBr.put(8, new String[]{"#ffffe5", "#fff7bc", "#fee391", "#fec44f",
				"#fe9929", "#ec7014", "#cc4c02", "#8c2d04"});
		YlOrBr.put(9, new String[]{"#ffffe5", "#fff7bc", "#fee391", "#fec44f",
				"#fe9929", "#ec7014", "#cc4c02", "#993404", "#662506"});

		Purples = new LinkedHashMap<>();
		Purples.put(3, new String[]{"#efedf5", "#bcbddc", "#756bb1"});
		Purples.put(4,
				new String[]{"#f2f0f7", "#cbc9e2", "#9e9ac8", "#6a51a3"});
		Purples.put(5, new String[]{"#f2f0f7", "#cbc9e2", "#9e9ac8", "#756bb1",
				"#54278f"});
		Purples.put(6, new String[]{"#f2f0f7", "#dadaeb", "#bcbddc", "#9e9ac8",
				"#756bb1", "#54278f"});
		Purples.put(7, new String[]{"#f2f0f7", "#dadaeb", "#bcbddc", "#9e9ac8",
				"#807dba", "#6a51a3", "#4a1486"});
		Purples.put(8, new String[]{"#fcfbfd", "#efedf5", "#dadaeb", "#bcbddc",
				"#9e9ac8", "#807dba", "#6a51a3", "#4a1486"});
		Purples.put(9, new String[]{"#fcfbfd", "#efedf5", "#dadaeb", "#bcbddc",
				"#9e9ac8", "#807dba", "#6a51a3", "#54278f", "#3f007d"});

		Blues = new LinkedHashMap<>();
		Blues.put(3, new String[]{"#deebf7", "#9ecae1", "#3182bd"});
		Blues.put(4, new String[]{"#eff3ff", "#bdd7e7", "#6baed6", "#2171b5"});
		Blues.put(5, new String[]{"#eff3ff", "#bdd7e7", "#6baed6", "#3182bd",
				"#08519c"});
		Blues.put(6, new String[]{"#eff3ff", "#c6dbef", "#9ecae1", "#6baed6",
				"#3182bd", "#08519c"});
		Blues.put(7, new String[]{"#eff3ff", "#c6dbef", "#9ecae1", "#6baed6",
				"#4292c6", "#2171b5", "#084594"});
		Blues.put(8, new String[]{"#f7fbff", "#deebf7", "#c6dbef", "#9ecae1",
				"#6baed6", "#4292c6", "#2171b5", "#084594"});
		Blues.put(9, new String[]{"#f7fbff", "#deebf7", "#c6dbef", "#9ecae1",
				"#6baed6", "#4292c6", "#2171b5", "#08519c", "#08306b"});

		Greens = new LinkedHashMap<>();
		Greens.put(3, new String[]{"#e5f5e0", "#a1d99b", "#31a354"});
		Greens.put(4, new String[]{"#edf8e9", "#bae4b3", "#74c476", "#238b45"});
		Greens.put(5, new String[]{"#edf8e9", "#bae4b3", "#74c476", "#31a354",
				"#006d2c"});
		Greens.put(6, new String[]{"#edf8e9", "#c7e9c0", "#a1d99b", "#74c476",
				"#31a354", "#006d2c"});
		Greens.put(7, new String[]{"#edf8e9", "#c7e9c0", "#a1d99b", "#74c476",
				"#41ab5d", "#238b45", "#005a32"});
		Greens.put(8, new String[]{"#f7fcf5", "#e5f5e0", "#c7e9c0", "#a1d99b",
				"#74c476", "#41ab5d", "#238b45", "#005a32"});
		Greens.put(9, new String[]{"#f7fcf5", "#e5f5e0", "#c7e9c0", "#a1d99b",
				"#74c476", "#41ab5d", "#238b45", "#006d2c", "#00441b"});

		Oranges = new LinkedHashMap<>();
		Oranges.put(3, new String[]{"#fee6ce", "#fdae6b", "#e6550d"});
		Oranges.put(4,
				new String[]{"#feedde", "#fdbe85", "#fd8d3c", "#d94701"});
		Oranges.put(5, new String[]{"#feedde", "#fdbe85", "#fd8d3c", "#e6550d",
				"#a63603"});
		Oranges.put(6, new String[]{"#feedde", "#fdd0a2", "#fdae6b", "#fd8d3c",
				"#e6550d", "#a63603"});
		Oranges.put(7, new String[]{"#feedde", "#fdd0a2", "#fdae6b", "#fd8d3c",
				"#f16913", "#d94801", "#8c2d04"});
		Oranges.put(8, new String[]{"#fff5eb", "#fee6ce", "#fdd0a2", "#fdae6b",
				"#fd8d3c", "#f16913", "#d94801", "#8c2d04"});
		Oranges.put(9, new String[]{"#fff5eb", "#fee6ce", "#fdd0a2", "#fdae6b",
				"#fd8d3c", "#f16913", "#d94801", "#a63603", "#7f2704"});

		Reds = new LinkedHashMap<>();
		Reds.put(3, new String[]{"#fee0d2", "#fc9272", "#de2d26"});
		Reds.put(4, new String[]{"#fee5d9", "#fcae91", "#fb6a4a", "#cb181d"});
		Reds.put(5, new String[]{"#fee5d9", "#fcae91", "#fb6a4a", "#de2d26",
				"#a50f15"});
		Reds.put(6, new String[]{"#fee5d9", "#fcbba1", "#fc9272", "#fb6a4a",
				"#de2d26", "#a50f15"});
		Reds.put(7, new String[]{"#fee5d9", "#fcbba1", "#fc9272", "#fb6a4a",
				"#ef3b2c", "#cb181d", "#99000d"});
		Reds.put(8, new String[]{"#fff5f0", "#fee0d2", "#fcbba1", "#fc9272",
				"#fb6a4a", "#ef3b2c", "#cb181d", "#99000d"});
		Reds.put(9, new String[]{"#fff5f0", "#fee0d2", "#fcbba1", "#fc9272",
				"#fb6a4a", "#ef3b2c", "#cb181d", "#a50f15", "#67000d"});

		Greys = new LinkedHashMap<>();
		Greys.put(3, new String[]{"#f0f0f0", "#bdbdbd", "#636363"});
		Greys.put(4, new String[]{"#f7f7f7", "#cccccc", "#969696", "#525252"});
		Greys.put(5, new String[]{"#f7f7f7", "#cccccc", "#969696", "#636363",
				"#252525"});
		Greys.put(6, new String[]{"#f7f7f7", "#d9d9d9", "#bdbdbd", "#969696",
				"#636363", "#252525"});
		Greys.put(7, new String[]{"#f7f7f7", "#d9d9d9", "#bdbdbd", "#969696",
				"#737373", "#525252", "#252525"});
		Greys.put(8, new String[]{"#ffffff", "#f0f0f0", "#d9d9d9", "#bdbdbd",
				"#969696", "#737373", "#525252", "#252525"});
		Greys.put(9, new String[]{"#ffffff", "#f0f0f0", "#d9d9d9", "#bdbdbd",
				"#969696", "#737373", "#525252", "#252525", "#000000"});

		PuOr = new LinkedHashMap<>();
		PuOr.put(3, new String[]{"#f1a340", "#f7f7f7", "#998ec3"});
		PuOr.put(4, new String[]{"#e66101", "#fdb863", "#b2abd2", "#5e3c99"});
		PuOr.put(5, new String[]{"#e66101", "#fdb863", "#f7f7f7", "#b2abd2",
				"#5e3c99"});
		PuOr.put(6, new String[]{"#b35806", "#f1a340", "#fee0b6", "#d8daeb",
				"#998ec3", "#542788"});
		PuOr.put(7, new String[]{"#b35806", "#f1a340", "#fee0b6", "#f7f7f7",
				"#d8daeb", "#998ec3", "#542788"});
		PuOr.put(8, new String[]{"#b35806", "#e08214", "#fdb863", "#fee0b6",
				"#d8daeb", "#b2abd2", "#8073ac", "#542788"});
		PuOr.put(9, new String[]{"#b35806", "#e08214", "#fdb863", "#fee0b6",
				"#f7f7f7", "#d8daeb", "#b2abd2", "#8073ac", "#542788"});
		PuOr.put(10,
				new String[]{"#7f3b08", "#b35806", "#e08214", "#fdb863",
						"#fee0b6", "#d8daeb", "#b2abd2", "#8073ac", "#542788",
						"#2d004b"});
		PuOr.put(11,
				new String[]{"#7f3b08", "#b35806", "#e08214", "#fdb863",
						"#fee0b6", "#f7f7f7", "#d8daeb", "#b2abd2", "#8073ac",
						"#542788", "#2d004b"});

		BrBG = new LinkedHashMap<>();
		BrBG.put(3, new String[]{"#d8b365", "#f5f5f5", "#5ab4ac"});
		BrBG.put(4, new String[]{"#a6611a", "#dfc27d", "#80cdc1", "#018571"});
		BrBG.put(5, new String[]{"#a6611a", "#dfc27d", "#f5f5f5", "#80cdc1",
				"#018571"});
		BrBG.put(6, new String[]{"#8c510a", "#d8b365", "#f6e8c3", "#c7eae5",
				"#5ab4ac", "#01665e"});
		BrBG.put(7, new String[]{"#8c510a", "#d8b365", "#f6e8c3", "#f5f5f5",
				"#c7eae5", "#5ab4ac", "#01665e"});
		BrBG.put(8, new String[]{"#8c510a", "#bf812d", "#dfc27d", "#f6e8c3",
				"#c7eae5", "#80cdc1", "#35978f", "#01665e"});
		BrBG.put(9, new String[]{"#8c510a", "#bf812d", "#dfc27d", "#f6e8c3",
				"#f5f5f5", "#c7eae5", "#80cdc1", "#35978f", "#01665e"});
		BrBG.put(10,
				new String[]{"#543005", "#8c510a", "#bf812d", "#dfc27d",
						"#f6e8c3", "#c7eae5", "#80cdc1", "#35978f", "#01665e",
						"#003c30"});
		BrBG.put(11,
				new String[]{"#543005", "#8c510a", "#bf812d", "#dfc27d",
						"#f6e8c3", "#f5f5f5", "#c7eae5", "#80cdc1", "#35978f",
						"#01665e", "#003c30"});

		PRGn = new LinkedHashMap<>();
		PRGn.put(3, new String[]{"#af8dc3", "#f7f7f7", "#7fbf7b"});
		PRGn.put(4, new String[]{"#7b3294", "#c2a5cf", "#a6dba0", "#008837"});
		PRGn.put(5, new String[]{"#7b3294", "#c2a5cf", "#f7f7f7", "#a6dba0",
				"#008837"});
		PRGn.put(6, new String[]{"#762a83", "#af8dc3", "#e7d4e8", "#d9f0d3",
				"#7fbf7b", "#1b7837"});
		PRGn.put(7, new String[]{"#762a83", "#af8dc3", "#e7d4e8", "#f7f7f7",
				"#d9f0d3", "#7fbf7b", "#1b7837"});
		PRGn.put(8, new String[]{"#762a83", "#9970ab", "#c2a5cf", "#e7d4e8",
				"#d9f0d3", "#a6dba0", "#5aae61", "#1b7837"});
		PRGn.put(9, new String[]{"#762a83", "#9970ab", "#c2a5cf", "#e7d4e8",
				"#f7f7f7", "#d9f0d3", "#a6dba0", "#5aae61", "#1b7837"});
		PRGn.put(10,
				new String[]{"#40004b", "#762a83", "#9970ab", "#c2a5cf",
						"#e7d4e8", "#d9f0d3", "#a6dba0", "#5aae61", "#1b7837",
						"#00441b"});
		PRGn.put(11,
				new String[]{"#40004b", "#762a83", "#9970ab", "#c2a5cf",
						"#e7d4e8", "#f7f7f7", "#d9f0d3", "#a6dba0", "#5aae61",
						"#1b7837", "#00441b"});

		PiYG = new LinkedHashMap<>();
		PiYG.put(3, new String[]{"#e9a3c9", "#f7f7f7", "#a1d76a"});
		PiYG.put(4, new String[]{"#d01c8b", "#f1b6da", "#b8e186", "#4dac26"});
		PiYG.put(5, new String[]{"#d01c8b", "#f1b6da", "#f7f7f7", "#b8e186",
				"#4dac26"});
		PiYG.put(6, new String[]{"#c51b7d", "#e9a3c9", "#fde0ef", "#e6f5d0",
				"#a1d76a", "#4d9221"});
		PiYG.put(7, new String[]{"#c51b7d", "#e9a3c9", "#fde0ef", "#f7f7f7",
				"#e6f5d0", "#a1d76a", "#4d9221"});
		PiYG.put(8, new String[]{"#c51b7d", "#de77ae", "#f1b6da", "#fde0ef",
				"#e6f5d0", "#b8e186", "#7fbc41", "#4d9221"});
		PiYG.put(9, new String[]{"#c51b7d", "#de77ae", "#f1b6da", "#fde0ef",
				"#f7f7f7", "#e6f5d0", "#b8e186", "#7fbc41", "#4d9221"});
		PiYG.put(10,
				new String[]{"#8e0152", "#c51b7d", "#de77ae", "#f1b6da",
						"#fde0ef", "#e6f5d0", "#b8e186", "#7fbc41", "#4d9221",
						"#276419"});
		PiYG.put(11,
				new String[]{"#8e0152", "#c51b7d", "#de77ae", "#f1b6da",
						"#fde0ef", "#f7f7f7", "#e6f5d0", "#b8e186", "#7fbc41",
						"#4d9221", "#276419"});

		RdBu = new LinkedHashMap<>();
		RdBu.put(3, new String[]{"#ef8a62", "#f7f7f7", "#67a9cf"});
		RdBu.put(4, new String[]{"#ca0020", "#f4a582", "#92c5de", "#0571b0"});
		RdBu.put(5, new String[]{"#ca0020", "#f4a582", "#f7f7f7", "#92c5de",
				"#0571b0"});
		RdBu.put(6, new String[]{"#b2182b", "#ef8a62", "#fddbc7", "#d1e5f0",
				"#67a9cf", "#2166ac"});
		RdBu.put(7, new String[]{"#b2182b", "#ef8a62", "#fddbc7", "#f7f7f7",
				"#d1e5f0", "#67a9cf", "#2166ac"});
		RdBu.put(8, new String[]{"#b2182b", "#d6604d", "#f4a582", "#fddbc7",
				"#d1e5f0", "#92c5de", "#4393c3", "#2166ac"});
		RdBu.put(9, new String[]{"#b2182b", "#d6604d", "#f4a582", "#fddbc7",
				"#f7f7f7", "#d1e5f0", "#92c5de", "#4393c3", "#2166ac"});
		RdBu.put(10,
				new String[]{"#67001f", "#b2182b", "#d6604d", "#f4a582",
						"#fddbc7", "#d1e5f0", "#92c5de", "#4393c3", "#2166ac",
						"#053061"});
		RdBu.put(11,
				new String[]{"#67001f", "#b2182b", "#d6604d", "#f4a582",
						"#fddbc7", "#f7f7f7", "#d1e5f0", "#92c5de", "#4393c3",
						"#2166ac", "#053061"});

		RdGy = new LinkedHashMap<>();
		RdGy.put(3, new String[]{"#ef8a62", "#ffffff", "#999999"});
		RdGy.put(4, new String[]{"#ca0020", "#f4a582", "#bababa", "#404040"});
		RdGy.put(5, new String[]{"#ca0020", "#f4a582", "#ffffff", "#bababa",
				"#404040"});
		RdGy.put(6, new String[]{"#b2182b", "#ef8a62", "#fddbc7", "#e0e0e0",
				"#999999", "#4d4d4d"});
		RdGy.put(7, new String[]{"#b2182b", "#ef8a62", "#fddbc7", "#ffffff",
				"#e0e0e0", "#999999", "#4d4d4d"});
		RdGy.put(8, new String[]{"#b2182b", "#d6604d", "#f4a582", "#fddbc7",
				"#e0e0e0", "#bababa", "#878787", "#4d4d4d"});
		RdGy.put(9, new String[]{"#b2182b", "#d6604d", "#f4a582", "#fddbc7",
				"#ffffff", "#e0e0e0", "#bababa", "#878787", "#4d4d4d"});
		RdGy.put(10,
				new String[]{"#67001f", "#b2182b", "#d6604d", "#f4a582",
						"#fddbc7", "#e0e0e0", "#bababa", "#878787", "#4d4d4d",
						"#1a1a1a"});
		RdGy.put(11,
				new String[]{"#67001f", "#b2182b", "#d6604d", "#f4a582",
						"#fddbc7", "#ffffff", "#e0e0e0", "#bababa", "#878787",
						"#4d4d4d", "#1a1a1a"});

		RdYlBu = new LinkedHashMap<>();
		RdYlBu.put(3, new String[]{"#fc8d59", "#ffffbf", "#91bfdb"});
		RdYlBu.put(4, new String[]{"#d7191c", "#fdae61", "#abd9e9", "#2c7bb6"});
		RdYlBu.put(5, new String[]{"#d7191c", "#fdae61", "#ffffbf", "#abd9e9",
				"#2c7bb6"});
		RdYlBu.put(6, new String[]{"#d73027", "#fc8d59", "#fee090", "#e0f3f8",
				"#91bfdb", "#4575b4"});
		RdYlBu.put(7, new String[]{"#d73027", "#fc8d59", "#fee090", "#ffffbf",
				"#e0f3f8", "#91bfdb", "#4575b4"});
		RdYlBu.put(8, new String[]{"#d73027", "#f46d43", "#fdae61", "#fee090",
				"#e0f3f8", "#abd9e9", "#74add1", "#4575b4"});
		RdYlBu.put(9, new String[]{"#d73027", "#f46d43", "#fdae61", "#fee090",
				"#ffffbf", "#e0f3f8", "#abd9e9", "#74add1", "#4575b4"});
		RdYlBu.put(10,
				new String[]{"#a50026", "#d73027", "#f46d43", "#fdae61",
						"#fee090", "#e0f3f8", "#abd9e9", "#74add1", "#4575b4",
						"#313695"});
		RdYlBu.put(11,
				new String[]{"#a50026", "#d73027", "#f46d43", "#fdae61",
						"#fee090", "#ffffbf", "#e0f3f8", "#abd9e9", "#74add1",
						"#4575b4", "#313695"});

		Spectral = new LinkedHashMap<>();
		Spectral.put(3, new String[]{"#fc8d59", "#ffffbf", "#99d594"});
		Spectral.put(4,
				new String[]{"#d7191c", "#fdae61", "#abdda4", "#2b83ba"});
		Spectral.put(5, new String[]{"#d7191c", "#fdae61", "#ffffbf", "#abdda4",
				"#2b83ba"});
		Spectral.put(6, new String[]{"#d53e4f", "#fc8d59", "#fee08b", "#e6f598",
				"#99d594", "#3288bd"});
		Spectral.put(7, new String[]{"#d53e4f", "#fc8d59", "#fee08b", "#ffffbf",
				"#e6f598", "#99d594", "#3288bd"});
		Spectral.put(8, new String[]{"#d53e4f", "#f46d43", "#fdae61", "#fee08b",
				"#e6f598", "#abdda4", "#66c2a5", "#3288bd"});
		Spectral.put(9, new String[]{"#d53e4f", "#f46d43", "#fdae61", "#fee08b",
				"#ffffbf", "#e6f598", "#abdda4", "#66c2a5", "#3288bd"});
		Spectral.put(10,
				new String[]{"#9e0142", "#d53e4f", "#f46d43", "#fdae61",
						"#fee08b", "#e6f598", "#abdda4", "#66c2a5", "#3288bd",
						"#5e4fa2"});
		Spectral.put(11,
				new String[]{"#9e0142", "#d53e4f", "#f46d43", "#fdae61",
						"#fee08b", "#ffffbf", "#e6f598", "#abdda4", "#66c2a5",
						"#3288bd", "#5e4fa2"});

		RdYlGn = new LinkedHashMap<>();
		RdYlGn.put(3, new String[]{"#fc8d59", "#ffffbf", "#91cf60"});
		RdYlGn.put(4, new String[]{"#d7191c", "#fdae61", "#a6d96a", "#1a9641"});
		RdYlGn.put(5, new String[]{"#d7191c", "#fdae61", "#ffffbf", "#a6d96a",
				"#1a9641"});
		RdYlGn.put(6, new String[]{"#d73027", "#fc8d59", "#fee08b", "#d9ef8b",
				"#91cf60", "#1a9850"});
		RdYlGn.put(7, new String[]{"#d73027", "#fc8d59", "#fee08b", "#ffffbf",
				"#d9ef8b", "#91cf60", "#1a9850"});
		RdYlGn.put(8, new String[]{"#d73027", "#f46d43", "#fdae61", "#fee08b",
				"#d9ef8b", "#a6d96a", "#66bd63", "#1a9850"});
		RdYlGn.put(9, new String[]{"#d73027", "#f46d43", "#fdae61", "#fee08b",
				"#ffffbf", "#d9ef8b", "#a6d96a", "#66bd63", "#1a9850"});
		RdYlGn.put(10,
				new String[]{"#a50026", "#d73027", "#f46d43", "#fdae61",
						"#fee08b", "#d9ef8b", "#a6d96a", "#66bd63", "#1a9850",
						"#006837"});
		RdYlGn.put(11,
				new String[]{"#a50026", "#d73027", "#f46d43", "#fdae61",
						"#fee08b", "#ffffbf", "#d9ef8b", "#a6d96a", "#66bd63",
						"#1a9850", "#006837"});

		Accent = new LinkedHashMap<>();
		Accent.put(3, new String[]{"#7fc97f", "#beaed4", "#fdc086"});
		Accent.put(4, new String[]{"#7fc97f", "#beaed4", "#fdc086", "#ffff99"});
		Accent.put(5, new String[]{"#7fc97f", "#beaed4", "#fdc086", "#ffff99",
				"#386cb0"});
		Accent.put(6, new String[]{"#7fc97f", "#beaed4", "#fdc086", "#ffff99",
				"#386cb0", "#f0027f"});
		Accent.put(7, new String[]{"#7fc97f", "#beaed4", "#fdc086", "#ffff99",
				"#386cb0", "#f0027f", "#bf5b17"});
		Accent.put(8, new String[]{"#7fc97f", "#beaed4", "#fdc086", "#ffff99",
				"#386cb0", "#f0027f", "#bf5b17", "#666666"});

		Dark2 = new LinkedHashMap<>();
		Dark2.put(3, new String[]{"#1b9e77", "#d95f02", "#7570b3"});
		Dark2.put(4, new String[]{"#1b9e77", "#d95f02", "#7570b3", "#e7298a"});
		Dark2.put(5, new String[]{"#1b9e77", "#d95f02", "#7570b3", "#e7298a",
				"#66a61e"});
		Dark2.put(6, new String[]{"#1b9e77", "#d95f02", "#7570b3", "#e7298a",
				"#66a61e", "#e6ab02"});
		Dark2.put(7, new String[]{"#1b9e77", "#d95f02", "#7570b3", "#e7298a",
				"#66a61e", "#e6ab02", "#a6761d"});
		Dark2.put(8, new String[]{"#1b9e77", "#d95f02", "#7570b3", "#e7298a",
				"#66a61e", "#e6ab02", "#a6761d", "#666666"});

		Paired = new LinkedHashMap<>();
		Paired.put(3, new String[]{"#a6cee3", "#1f78b4", "#b2df8a"});
		Paired.put(4, new String[]{"#a6cee3", "#1f78b4", "#b2df8a", "#33a02c"});
		Paired.put(5, new String[]{"#a6cee3", "#1f78b4", "#b2df8a", "#33a02c",
				"#fb9a99"});
		Paired.put(6, new String[]{"#a6cee3", "#1f78b4", "#b2df8a", "#33a02c",
				"#fb9a99", "#e31a1c"});
		Paired.put(7, new String[]{"#a6cee3", "#1f78b4", "#b2df8a", "#33a02c",
				"#fb9a99", "#e31a1c", "#fdbf6f"});
		Paired.put(8, new String[]{"#a6cee3", "#1f78b4", "#b2df8a", "#33a02c",
				"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00"});
		Paired.put(9, new String[]{"#a6cee3", "#1f78b4", "#b2df8a", "#33a02c",
				"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6"});
		Paired.put(10,
				new String[]{"#a6cee3", "#1f78b4", "#b2df8a", "#33a02c",
						"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6",
						"#6a3d9a"});
		Paired.put(11,
				new String[]{"#a6cee3", "#1f78b4", "#b2df8a", "#33a02c",
						"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6",
						"#6a3d9a", "#ffff99"});
		Paired.put(12,
				new String[]{"#a6cee3", "#1f78b4", "#b2df8a", "#33a02c",
						"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6",
						"#6a3d9a", "#ffff99", "#b15928"});

		Pastel1 = new LinkedHashMap<>();
		Pastel1.put(3, new String[]{"#fbb4ae", "#b3cde3", "#ccebc5"});
		Pastel1.put(4,
				new String[]{"#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4"});
		Pastel1.put(5, new String[]{"#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4",
				"#fed9a6"});
		Pastel1.put(6, new String[]{"#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4",
				"#fed9a6", "#ffffcc"});
		Pastel1.put(7, new String[]{"#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4",
				"#fed9a6", "#ffffcc", "#e5d8bd"});
		Pastel1.put(8, new String[]{"#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4",
				"#fed9a6", "#ffffcc", "#e5d8bd", "#fddaec"});
		Pastel1.put(9, new String[]{"#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4",
				"#fed9a6", "#ffffcc", "#e5d8bd", "#fddaec", "#f2f2f2"});

		Pastel2 = new LinkedHashMap<>();
		Pastel2.put(3, new String[]{"#b3e2cd", "#fdcdac", "#cbd5e8"});
		Pastel2.put(4,
				new String[]{"#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4"});
		Pastel2.put(5, new String[]{"#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4",
				"#e6f5c9"});
		Pastel2.put(6, new String[]{"#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4",
				"#e6f5c9", "#fff2ae"});
		Pastel2.put(7, new String[]{"#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4",
				"#e6f5c9", "#fff2ae", "#f1e2cc"});
		Pastel2.put(8, new String[]{"#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4",
				"#e6f5c9", "#fff2ae", "#f1e2cc", "#cccccc"});

		Set1 = new LinkedHashMap<>();
		Set1.put(3, new String[]{"#e41a1c", "#377eb8", "#4daf4a"});
		Set1.put(4, new String[]{"#e41a1c", "#377eb8", "#4daf4a", "#984ea3"});
		Set1.put(5, new String[]{"#e41a1c", "#377eb8", "#4daf4a", "#984ea3",
				"#ff7f00"});
		Set1.put(6, new String[]{"#e41a1c", "#377eb8", "#4daf4a", "#984ea3",
				"#ff7f00", "#ffff33"});
		Set1.put(7, new String[]{"#e41a1c", "#377eb8", "#4daf4a", "#984ea3",
				"#ff7f00", "#ffff33", "#a65628"});
		Set1.put(8, new String[]{"#e41a1c", "#377eb8", "#4daf4a", "#984ea3",
				"#ff7f00", "#ffff33", "#a65628", "#f781bf"});
		Set1.put(9, new String[]{"#e41a1c", "#377eb8", "#4daf4a", "#984ea3",
				"#ff7f00", "#ffff33", "#a65628", "#f781bf", "#999999"});

		Set2 = new LinkedHashMap<>();
		Set2.put(3, new String[]{"#66c2a5", "#fc8d62", "#8da0cb"});
		Set2.put(4, new String[]{"#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3"});
		Set2.put(5, new String[]{"#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3",
				"#a6d854"});
		Set2.put(6, new String[]{"#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3",
				"#a6d854", "#ffd92f"});
		Set2.put(7, new String[]{"#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3",
				"#a6d854", "#ffd92f", "#e5c494"});
		Set2.put(8, new String[]{"#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3",
				"#a6d854", "#ffd92f", "#e5c494", "#b3b3b3"});

		Set3 = new LinkedHashMap<>();
		Set3.put(3, new String[]{"#8dd3c7", "#ffffb3", "#bebada"});
		Set3.put(4, new String[]{"#8dd3c7", "#ffffb3", "#bebada", "#fb8072"});
		Set3.put(5, new String[]{"#8dd3c7", "#ffffb3", "#bebada", "#fb8072",
				"#80b1d3"});
		Set3.put(6, new String[]{"#8dd3c7", "#ffffb3", "#bebada", "#fb8072",
				"#80b1d3", "#fdb462"});
		Set3.put(7, new String[]{"#8dd3c7", "#ffffb3", "#bebada", "#fb8072",
				"#80b1d3", "#fdb462", "#b3de69"});
		Set3.put(8, new String[]{"#8dd3c7", "#ffffb3", "#bebada", "#fb8072",
				"#80b1d3", "#fdb462", "#b3de69", "#fccde5"});
		Set3.put(9, new String[]{"#8dd3c7", "#ffffb3", "#bebada", "#fb8072",
				"#80b1d3", "#fdb462", "#b3de69", "#fccde5", "#d9d9d9"});
		Set3.put(10,
				new String[]{"#8dd3c7", "#ffffb3", "#bebada", "#fb8072",
						"#80b1d3", "#fdb462", "#b3de69", "#fccde5", "#d9d9d9",
						"#bc80bd"});
		Set3.put(11,
				new String[]{"#8dd3c7", "#ffffb3", "#bebada", "#fb8072",
						"#80b1d3", "#fdb462", "#b3de69", "#fccde5", "#d9d9d9",
						"#bc80bd", "#ccebc5"});
		Set3.put(12,
				new String[]{"#8dd3c7", "#ffffb3", "#bebada", "#fb8072",
						"#80b1d3", "#fdb462", "#b3de69", "#fccde5", "#d9d9d9",
						"#bc80bd", "#ccebc5", "#ffed6f"});

		colorFamilies = new LinkedHashMap<>();
		colorFamilies.put("Category", Category);
		colorFamilies.put("Category", Category);
		colorFamilies.put("Veusz", Veusz);
		colorFamilies.put("YlGn", YlGn);
		colorFamilies.put("YlGnBu", YlGnBu);
		colorFamilies.put("GnBu", GnBu);
		colorFamilies.put("BuGn", BuGn);
		colorFamilies.put("PuBuGn", PuBuGn);
		colorFamilies.put("PuBu", PuBu);
		colorFamilies.put("BuPu", BuPu);
		colorFamilies.put("RdPu", RdPu);
		colorFamilies.put("PuRd", PuRd);
		colorFamilies.put("OrRd", OrRd);
		colorFamilies.put("YlOrRd", YlOrRd);
		colorFamilies.put("YlOrBr", YlOrBr);
		colorFamilies.put("Purples", Purples);
		colorFamilies.put("Blues", Blues);
		colorFamilies.put("Greens", Greens);
		colorFamilies.put("Oranges", Oranges);
		colorFamilies.put("Reds", Reds);
		colorFamilies.put("Greys", Greys);
		colorFamilies.put("PuOr", PuOr);
		colorFamilies.put("BrBG", BrBG);
		colorFamilies.put("PRGn", PRGn);
		colorFamilies.put("PiYG", PiYG);
		colorFamilies.put("RdBu", RdBu);
		colorFamilies.put("RdGy", RdGy);
		colorFamilies.put("RdYlBu", RdYlBu);
		colorFamilies.put("Spectral", Spectral);
		colorFamilies.put("RdYlGn", RdYlGn);
		colorFamilies.put("Accent", Accent);
		colorFamilies.put("Dark2", Dark2);
		colorFamilies.put("Paired", Paired);
		colorFamilies.put("Pastel1", Pastel1);
		colorFamilies.put("Pastel2", Pastel2);
		colorFamilies.put("Set1", Set1);
		colorFamilies.put("Set2", Set2);
		colorFamilies.put("Set3", Set3);

	}

	//#end region

	//#region METHODS

	public static List<String> getColorFamilyKeys() {
		List<String> keys = new ArrayList<>(colorFamilies.keySet());
		keys.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}

		});
		return keys;
	}

	/**
	 * Creates map from color family name to an image that represents the last
	 * color array of the color family.
	 */
	public static Map<String, Image> getLastColorFamilyImages() {

		Map<String, Image> colorImages = new LinkedHashMap<>();

		List<String> keys = getColorFamilyKeys();
		for (String key : keys) {
			Map<Integer, String[]> colorFamily = colorFamilies.get(key);
			Set<Integer> arrayKeys = colorFamily.keySet();
			Integer lastKey = (Integer) arrayKeys.toArray()[arrayKeys.size()
					- 1];
			String[] lastColorArray = colorFamily.get(lastKey);
			Image colorImage = createColorImage(lastColorArray);
			colorImages.put(key, colorImage);
		}
		return colorImages;
	}

	public static Image createColorImage(String[] colors) {

		final double prefferedImageWidth = 100;
		final int imageHeight = 20;

		int numberOfColors = colors.length;
		int colorWidth = (int) (prefferedImageWidth / numberOfColors);
		int imageWidth = colorWidth * numberOfColors;

		Display display = Display.getDefault();
		Image image = new Image(display, imageWidth, imageHeight);
		GC gc = new GC(image);

		int x = 0;
		for (String colorCode : colors) {

			java.awt.Color awtColor = java.awt.Color.decode(colorCode);
			Color swtColor = new Color(display, awtColor.getRed(),
					awtColor.getGreen(), awtColor.getBlue());
			gc.setBackground(swtColor);
			gc.fillRectangle(x, 0, colorWidth, imageHeight);
			x += colorWidth;
		}
		gc.dispose();
		return image;

	}

	public static void drawColorRectangles(Composite parentComposite) {

		parentComposite.setLayout(new GridLayout(2, false));

		Map<String, Image> colorImages = ColorBrewer.getLastColorFamilyImages();
		for (String colorFamily : colorImages.keySet()) {
			Label familyLabel = new Label(parentComposite, SWT.NONE);
			familyLabel.setText(colorFamily);

			Image image = colorImages.get(colorFamily);
			Label imageLabel = new Label(parentComposite, SWT.NONE);
			imageLabel.setImage(image);
		}

	}

	//#end region

}
