package org.treez.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractActivator {

	//#region ATTRIBUTES

	public static final String PLUGIN_ID = "org.treez.core";

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

	/**
	 * Returns the absolute path
	 *
	 * @return
	 */
	public static String getAbsolutePathStatic() {
		return getInstance().getAbsolutePath();
	}

	/**
	 * @param baseImage
	 * @param string
	 * @return
	 */
	public static Image getOverlayImageStatic(Image baseImage, String string) {
		return getInstance().getOverlayImage(baseImage, string);
	}

	/**
	 * @param imageName
	 * @return
	 */
	public static Image getImage(String imageName) {

		if (instance == null) {
			instance = new Activator();
		}

		return getInstance().getImageFromIconFolder(imageName);
	}

	/**
	 * Returns an image descriptor from an image in the icon folder
	 *
	 * @param name
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(String name) {
		return getInstance().getImageDescriptorFromInstance(name);
	}

	/**
	 * Returns the absolute help context id for the given relative help context id
	 *
	 * @param relativeHelpContextId
	 * @return
	 */
	public static String getAbsoluteHelpContextIdStatic(String relativeHelpContextId) {
		if (instance == null) {
			return null;
		}
		String absoluteHelpContextId = instance.getAbsoluteHelpContextId(relativeHelpContextId);
		return absoluteHelpContextId;
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Overloads the parent method
	 */
	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AbstractActivator getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Activator has not yet been created. Call constructor first.");
		}
		return instance;
	}

	@Override
	protected void setInstance(AbstractActivator abstractActivator) {
		instance = abstractActivator;

	}

	//#end region

}
