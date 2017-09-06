package org.treez.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.treez.core.AbstractActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractActivator {

	//#region ATTRIBUTES

	public static final String PLUGIN_ID = "org.treez.views";

	/**
	 * The shared activator instance
	 */
	private static AbstractActivator instance;

	//#end region

	//#region CONSTRUCTORS

	public Activator() {
		super();
	}

	//#end region

	//#region METHODS

	public static String getAbsolutePathStatic() {
		return getInstance().getAbsolutePath();
	}

	public static Image getOverlayImageStatic(Image baseImage, String string) {
		return getInstance().getOverlayImage(baseImage, string);
	}

	public static Image getImage(String imageName) {
		return getInstance().getImageFromIconFolder(imageName);
	}

	/**
	 * Returns an image descriptor from an image in the icon folder
	 */
	public static ImageDescriptor getImageDescriptorStatic(String name) {
		return getInstance().getImageDescriptorFromInstance(name);
	}

	//#end region

	//#region ACCESSORS

	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}

	public static AbstractActivator getInstance() {
		if (instance == null) {
			instance = new Activator();
		}
		return instance;
	}

	@Override
	protected void setInstance(AbstractActivator abstractActivator) {
		instance = abstractActivator;
	}

	//#end region

}
