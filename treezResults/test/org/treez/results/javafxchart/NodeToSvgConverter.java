package org.treez.results.javafxchart;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.shape.Shape;

/**
 * Converts a JavaFx Node to SVG code (without svg header and svg end tag)
 */
public class NodeToSvgConverter extends AbstractNodeToSvgConverter<Node> {

	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(JavaFxNodeToSvgConverter.class);

	//#region CONSTRUCTORS

	//#end region

	//#region METHODS

	/**
	 * Creates SVG code from the given JavaFx Node. The given initial comment is included as a prefix.
	 */
	@Override
	public String extendCode(String initialSvgString, Node node) {
		String svgContentString = nodeToSvgContentString(node);
		String svgString = initialSvgString + svgContentString;
		return svgString;
	}

	/**
	 * Converts the node to SVG code (without SVG header and end tags). The type hierarchy of Node is as follows:
	 *
	 * <pre>
	 *  * Node
	 *    ** Camera
	 *    ** Canvas
	 *    ** ImagaView
	 *    ** LightBase
	 *       *** AmbientLight
	 *       *** PointLight
	 *    ** MediaView
	 *    ** Parent (x)
	 *       *** Group (x)
	 *       *** Region (x)
	 *           **** Control
	 *                ***** Label (x)
	 *                ***** ...
	 *           **** ...
	 *       *** WebView
	 *    ** Printable
	 *    ** Shape (x)
	 *       *** Arc (x)
	 *       *** Circle (x)
	 *       *** CubicCurve (x)
	 *       *** Ellipse (x)
	 *       *** Line (x)
	 *       *** Path (x)
	 *       *** Polygon (x)
	 *       *** QuadCurve (x)
	 *       *** Rectangle (x)
	 *       *** SVGPath (x)
	 *       *** Text (x)
	 *    ** Shape3D
	 *    ** SubScene
	 *    ** SwingNode
	 * </pre>
	 *
	 * @param startNode
	 * @return
	 */
	private String nodeToSvgContentString(Node node) {

		boolean isControl = node instanceof Control;

		if (isControl) {
			//handle special case of Node=>Parent=>Region=>Control
			Control control = (Control) node;
			String message = "A node of class " + node.getClass().getName() + " is a Control";
			LOG.info(message);
			String svgString = getSvgStringForControl(control);
			return svgString;
		} else {
			//handle all other types of Nodes
			String svgString = getSvgStringForNonControl(node);
			return svgString;
		}
	}

	/**
	 * Converts a Control to SVG code (without SVG header and end tags)
	 *
	 * @param control
	 * @return
	 */
	private String getSvgStringForControl(Control control) {

		boolean isLabel = control instanceof Label;
		if (isLabel) {
			//handle Node=>Parent=>Region=>Control=>Label
			Label label = (Label) control;
			LabelToSvgConverter labelConverter = new LabelToSvgConverter();
			labelConverter.setIndentation(indentation);
			String svgString = labelConverter.extendCode("", label);
			indentation = labelConverter.getIndentation();
			return svgString;
		}

		String message = "Controls of type '" + control.getClass().getName() + "' are not yet implemented.";
		throw new IllegalArgumentException(message);
	}

	/**
	 * Converts a Node (that is not a control) to SVG code
	 *
	 * @param node
	 * @param hasChildren
	 * @return
	 */
	private String getSvgStringForNonControl(final Node node) {

		//check if the node is an instance of specific inheriting classes and apply the
		//corresponding data
		Boolean isShape = node instanceof Shape;
		if (isShape) {
			//handle Node=>Shape
			Shape shape = (Shape) node;
			ShapeToSvgConverter shapeConverter = new ShapeToSvgConverter();
			shapeConverter.setIndentation(indentation);
			String svgString = shapeConverter.extendCode("", shape);
			return svgString;

		} else {
			Boolean isParent = node instanceof Parent;
			if (isParent) {
				//handle Node=>Parent (also includes Region, Group, WebView)
				Parent parent = (Parent) node;
				ParentToSvgConverter parentConverter = new ParentToSvgConverter();
				parentConverter.setIndentation(indentation);
				String svgString = parentConverter.extendCode("", parent);
				return svgString;

			} else {
				//handle Node=>Canvas
				Boolean isCanvas = node instanceof Canvas;
				if (isCanvas) {
					String comment = "Warning: the Canvas class is not yet implemented";
					String svgString = indentation + "<!--" + comment + " -->\n";
					LOG.warn(comment);
					return svgString;
				} else {
					//handle Node=> remaining ?xyz?
					String classString = node.getClass().getName();
					String comment = "Warning: the class '" + classString + "' is not yet implemented";
					String svgString = indentation + "<!--" + comment + " -->\n";
					LOG.warn(comment);
					return svgString;
				}
			}
		}
	}

	//#end region

}
