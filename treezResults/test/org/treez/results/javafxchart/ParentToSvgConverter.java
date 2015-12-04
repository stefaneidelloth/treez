package org.treez.results.javafxchart;

import java.util.List;

import org.apache.log4j.Logger;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;

/**
 * Converts a Parent to SVG code
 */
public class ParentToSvgConverter extends AbstractNodeToSvgConverter<Parent> {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(ParentToSvgConverter.class);

	//#region ATTRIBUTES

	//#end region

	//#region CONSTRUCTORS

	//#end region

	//#region METHODS

	/**
	 * Converts the Parent to SVG code (without SVG header and end tags). The type hierarchy of Parent is as follows:
	 *
	 * <pre>
	 *    ** Parent (x)
	 *       *** Group (x)
	 *       *** Region (x)
	 *           **** Control
	 *                ***** Label (x)
	 *                ***** ...
	 *           **** ...
	 *       *** WebView
	 * </pre>
	 *
	 * (The special case of a Control should already have been handled.)
	 *
	 * @return
	 */

	@Override
	public String extendCode(String initialSvgString, Parent parent) {

		addDataFromNode(parent);
		addDataFromParent(parent);

		//inheriting classes
		boolean isRegion = parent instanceof Region;
		if (isRegion) {
			//handle Node=>Parent=>Region
			Region region = (Region) parent;
			RegionToSvgConverter regionConverter = new RegionToSvgConverter();
			regionConverter.setIndentation(indentation);
			regionConverter.setSvgNodeProperties(svgNodeProperties);
			String svgString = regionConverter.extendCode(initialSvgString, region);
			return svgString;
		} else {

			boolean isGroup = parent instanceof Group;
			if (isGroup) {
				//handle Node=>Parent=>Group
				Group group = (Group) parent;
				GroupToSvgConverter groupConverter = new GroupToSvgConverter();
				groupConverter.setIndentation(indentation);
				groupConverter.setSvgNodeProperties(svgNodeProperties);
				String svgString = groupConverter.extendCode(initialSvgString, group);
				return svgString;
			} else {
				boolean isWebView = parent instanceof WebView;
				if (isWebView) {
					//handle Node=>Parent=>WebView

				}

				//handle Node=>Parent=> remaining ?xyz?

				String className = parent.getClass().getName();
				String comment = "Warning: The class '" + className
						+ "' does not derive from Region or Group and is not yet implemented";
				sysLog.warn(comment);
				String svgString = initialSvgString + indentation + "<!--" + comment + "-->\n";

				//create svg string from svg node properties
				svgString = svgString + createSvgString();

				//add svg text for child nodes and add the "g-end tag" if the node has children
				svgString = extendWithChildSvgCodeAndEndTag(svgString, parent);

				return svgString;

			}

		}

	}

	/**
	 * Extracts svg properties directly from the given Parent and applies them.
	 *
	 * @param nodeProperties
	 * @param node
	 */
	private void addDataFromParent(Parent parent) {

		//hasChildren
		List<Node> children = parent.getChildrenUnmodifiable();
		boolean hasChildren = !children.isEmpty();
		svgNodeProperties.setHasChildren(hasChildren);

	}

	//#end region

	//#region ACCESSORS

	//#end region

}
