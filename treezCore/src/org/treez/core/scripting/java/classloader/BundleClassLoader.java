package org.treez.core.scripting.java.classloader;

import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * A class loader that is able to load classes from other Eclipse plugins. If
 * this class loader is not able to find a class, its child class loader will
 * try to do so. We use the BundleClassLoader as parent of a MemoryClassLoader.
 * The class loaders are created by the InMemoryClassFileManager.
 */
public class BundleClassLoader extends ClassLoader {

	/**
	 * Logger for this class
	 */
	@SuppressWarnings("unused")
	private static Logger sysLog = Logger.getLogger(BundleClassLoader.class);

	//#region ATTRIBUTES

	/**
	 * The bundleIds of the eclipse plugins this class loader is table to load
	 * classes from.
	 */
	private Set<String> bundleIds;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param parent
	 * @param bundleIds
	 */
	public BundleClassLoader(ClassLoader parent, Set<String> bundleIds) {
		super(parent);
		this.bundleIds = bundleIds;
	}

	//#end region

	//#region METHODS

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		//sysLog.debug("Searching class '" + name + "' with BundleClassLoader.");

		//search the bundles for the requested class
		Class<?> clazz = null;
		for (String bundleId : bundleIds) {
			Bundle bundle = Platform.getBundle(bundleId);
			boolean continueLoop = (clazz == null) && (bundle != null);
			if (continueLoop) {
				try {
					//sysLog.debug("Checking bundle " + bundleId + "...");
					clazz = bundle.loadClass(name);
					//sysLog.debug("Found class " + name + ".");
				} catch (ClassNotFoundException exception) {
					//nothing to do here
				}
			}
		}

		if (clazz == null) {
			String message = "Could not find class '" + name
					+ "' with BundleClassLoader.";
			//sysLog.debug(message);
			throw new ClassNotFoundException(message); //after throwing this, the child class loaders will continue the search.
		}

		return clazz;
	}

	//#end region

}
