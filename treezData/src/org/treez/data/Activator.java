package org.treez.data;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.treez.core.AbstractActivator;

/**
 * Activates the plugin
 */
public class Activator extends AbstractActivator {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(Activator.class);

	// #region ATTRIBUTES

	/**
	 * The shared activator instance
	 */
	private static AbstractActivator instance;

	//#end region

	// #region CONSTRUCTORS

	/**
	 * The constructor
	 */
	public Activator() {
		super();
	}

	// #end region

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
		AbstractActivator activator;
		try {
			activator = getInstance();
		} catch (IllegalStateException exception) {
			String message = "Could not get image since Activator has not yet been constructed. "
					+ "Returning dummy image instead.";
			sysLog.warn(message);
			Image image = createDefaultImage();
			return image;
		}

		return activator.getImageFromIconFolder(imageName);
	}

	/**
	 * Returns an image descriptor from an image in the icon folder
	 *
	 * @param name
	 * @return
	 */
	public static ImageDescriptor getImageDescriptorStatic(String name) {
		return getInstance().getImageDescriptorFromInstance(name);
	}

	/**
	 * Returns the preference store
	 *
	 * @return
	 */
	public static IPreferenceStore getPreferenceStoreStatic() {
		return getInstance().getPreferenceStore();
	}

	//#end region

	// #region ACCESSORS

	/**
	 * Overloads the parent method
	 */
	@Override
	public String getPluginId() {
		return "org.treez.data";
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
