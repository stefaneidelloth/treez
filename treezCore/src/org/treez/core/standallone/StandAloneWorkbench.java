package org.treez.core.standallone;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.treez.core.AbstractActivator;

/**
 * Is used to simulate a workbench when starting Treez without Eclipse
 */
public final class StandAloneWorkbench {

	private static final Logger LOG = Logger
			.getLogger(StandAloneWorkbench.class);

	//#region ATTRIBUTES

	private static Map<String, IViewPart> views = new HashMap<>();

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction
	 */
	private StandAloneWorkbench() {
	}

	//#end region

	//#region ACCESSORS

	public static void registerView(String id, IViewPart view) {
		views.put(id, view);
	}

	public static IViewPart getView(String id) {
		return views.get(id);
	}

	/**
	 * Returns an image from the image folder
	 *
	 * @param imageName
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public static Image getImage(String imageName,
			AbstractActivator activator) {

		Display display = Display.getCurrent();
		String path = activator.getAbsolutePath() + "//icons//" + imageName;
		final String suffix = path.substring(path.length() - 4);
		if (!suffix.equals(".png")) {
			path += ".png";
		}

		try {
			Image image = new Image(display, path);
			return image;
		} catch (Exception exception) {
			String message = "Could not load image '" + path + "'";
			LOG.warn(message);
			return AbstractActivator.createDefaultImage();
		}

	}

	public static String getAbsolutePluginPath(String pluginId) {

		String[] pathItems = pluginId.split("\\.");
		String lastPathItem = pathItems[pathItems.length - 1];

		String pathSeparator = "//";

		File file = new File("");
		file = file.getAbsoluteFile();
		String pathWithRelativeContent = file.getAbsolutePath();
		IPath ipath = new org.eclipse.core.runtime.Path(
				pathWithRelativeContent);
		IPath absPath = ipath.makeAbsolute();

		String[] filePathItems = absPath.toString().split("/");

		String correctedPath = "";
		for (int index = 0; index < filePathItems.length - 1; index++) {
			correctedPath += filePathItems[index] + pathSeparator;
		}
		correctedPath += "Treez" + lastPathItem.toUpperCase();

		return correctedPath;

	}

	//#end region

}
