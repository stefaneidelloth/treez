package org.treez.core.scripting.java;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.treez.core.scripting.java.classloader.BundleClassLoader;
import org.treez.core.scripting.java.classloader.InMemoryClassLoader;
import org.treez.core.scripting.java.file.CompiledJavaFileContainer;
import org.treez.core.scripting.java.file.JavaFileToBeCompiledInMemory;

/**
 * File manger for in memory compilation. Serves as factory for the
 * BundleClassLoader and its child MemoryClassLoader. First the Memory
 */
public class InMemoryClassFileManager
		extends
			ForwardingJavaFileManager<JavaFileManager> {

	private static final Logger LOG = Logger
			.getLogger(InMemoryClassFileManager.class);

	//#region ATTRIBUTES

	/**
	 * Represents the in memory java file to compile
	 */
	private JavaFileToBeCompiledInMemory javaFileToBeCompiled;

	/**
	 * stores the compiled byte code of our class
	 */
	private CompiledJavaFileContainer compiledJavaFileContainer = null;

	/**
	 * the parent class loader
	 */
	private ClassLoader parentClassLoader;

	//#region CONSTRUCTORS

	/**
	 * Will initialize the manager with the specified standard java file manager
	 *
	 * @param javaCode
	 * @param standardManager
	 * @param parentClassLoader
	 */

	public InMemoryClassFileManager(String javaCode,
			StandardJavaFileManager standardManager,
			ClassLoader parentClassLoader) {
		super(standardManager);
		this.javaFileToBeCompiled = new JavaFileToBeCompiledInMemory(javaCode);
		this.parentClassLoader = parentClassLoader;
	}

	//#end region

	//#region METHODS

	/**
	 * Returns an in memory class loader that is able to provide the compiled
	 * class. (The argument "location" is not used by this in memory
	 * implementation.)
	 */
	@Override
	public ClassLoader getClassLoader(Location location) {
		Objects.requireNonNull(javaFileToBeCompiled,
				"javaFileObject must not be null.");

		//Create a class loader that loads classes from other Eclipse plugins.
		//This will be used as parent class loader for the in memory
		//class loader.
		Set<String> bundleIds = javaFileToBeCompiled.getBundleIds();
		BundleClassLoader bundleLoader = new BundleClassLoader(
				parentClassLoader, bundleIds);

		if (compiledJavaFileContainer == null) {
			return null;
		}

		//create a class loader that loads the compiled class
		ClassLoader memoryLoader = new InMemoryClassLoader(bundleLoader,
				compiledJavaFileContainer);

		return memoryLoader;

	}

	/**
	 * Gives the compiler an CompiledJavaFileContainer so that the compiler can
	 * write the compiled byte code into it. (The arguments "location" and
	 * "sibling" are not used here.)
	 */
	@Override
	public JavaFileObject getJavaFileForOutput(Location location,
			String className, Kind kind, FileObject sibling)
			throws IOException {

		compiledJavaFileContainer = new CompiledJavaFileContainer(className,
				kind);
		return compiledJavaFileContainer;
	}

	/**
	 * Returns compilation options for a compilation task: include the class
	 * path
	 *
	 * @return
	 */
	public List<String> getCompileOptions() {
		List<String> options = new ArrayList<String>();
		String classPath = getClassPath();
		options.addAll(Arrays.asList("-classpath", classPath));
		return options;
	}

	/**
	 * Returns a list that contains the JavaFileObject for a compilation task:
	 * include our InMemoryJavaFileObject
	 *
	 * @return
	 */
	public List<JavaFileObject> getJavaFileObjects() {
		//create a "list of java file objects" to be dynamically compiled in
		//memory. In
		//our case this will only contain a single in memory java file object.
		List<JavaFileObject> javaFileObjects = new ArrayList<JavaFileObject>();
		javaFileObjects.add(javaFileToBeCompiled);
		return javaFileObjects;
	}

	/**
	 * Retrieves the class path from the given fileManger There are different
	 * possibilities how this JavaScripting is executed and these possibilities
	 * might be associated with different class loading mechanisms. Launching
	 * with a run configuration or as JUnit test uses a so called
	 * AppClassLoader. In those cases the class path can simply be taken from
	 * the system property of the JVM. If this JavaScripting is executed in an
	 * Eclipse plugin, an EquinoxClassLoader is used and the class path from the
	 * JVM has to be extended using information from that class loader.
	 *
	 * @return
	 */
	private String getClassPath() {

		//retrieve current class path from JVM
		String classPath = System.getProperty("java.class.path");

		//get type of class loader and extend class path if required
		ClassLoader classLoader = this.getClass().getClassLoader();
		String classLoaderType = classLoader.getClass().getSimpleName();
		switch (classLoaderType) {
			case "AppClassLoader" :
				//LOG.debug("Using AppClassLoader");
				//nothing to do here
				break;
			case "EquinoxClassLoader" :
				//LOG.debug("Using EquinoxClassLoader");
				Set<String> bundleIds = javaFileToBeCompiled.getBundleIds();
				bundleIds = extendBundleIdsWithDependencies(bundleIds);

				classPath = extendClassPathWithBundleIds(classPath, bundleIds);
				break;

			default :

				//close file manager
				try {
					fileManager.close();
				} catch (IOException exception) {
					//nothing to do here
				}

				//throw exception
				String message = "The class loader type '" + classLoaderType
						+ "' is not yet implemented.";
				throw new IllegalStateException(message);
		}

		//LOG.debug("classpath: " + classPath);

		return classPath;
	}

	private static Set<String> extendBundleIdsWithDependencies(
			Set<String> bundleIds) {

		Set<String> extendedBundleIds = new HashSet<>(bundleIds);

		for (String bundleId : bundleIds) {
			Bundle coreBundle = Platform.getBundle(bundleId);
			if (coreBundle != null) {
				Dictionary<String, String> manifestInfo = coreBundle
						.getHeaders();
				String requiredBundlesString = manifestInfo
						.get("Require-Bundle");
				if (requiredBundlesString != null) {
					String[] requiredBundleIds = requiredBundlesString
							.split(",");
					List<String> extraBundleIds = Arrays
							.asList(requiredBundleIds);
					extendedBundleIds.addAll(extraBundleIds);
				}
			}
		}
		return extendedBundleIds;
	}

	/**
	 * Extends the class path with the help of a given list of bundle ids (e.g.
	 * "org.treez.core").
	 *
	 * @param classPath
	 * @param bundleIds
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private static String extendClassPathWithBundleIds(String classPath,
			Set<String> bundleIds) {

		String newClassPath = classPath;
		for (String bundleId : bundleIds) {
			//get path to bundle and add it to the class path
			try {
				Bundle coreBundle = Platform.getBundle(bundleId);
				String bundleLocation = coreBundle.getLocation();
				newClassPath = appendClassPathWithBundleLocation(newClassPath,
						bundleLocation);
			} catch (Exception exception) {
				String message = "Could not extend classpath with bundleId "
						+ bundleId;
				LOG.error(message, exception);
			}
		}
		return newClassPath;
	}

	private static String appendClassPathWithBundleLocation(String classPath,
			String location) {

		boolean isJarLocation = location.contains(".jar");
		if (isJarLocation) {
			String newClassPath = appendClassPathWithJarBundleLocation(
					classPath, location);
			return newClassPath;
		} else {
			String newClassPath = appendClassPathWithBinBundleLocation(
					classPath, location);
			return newClassPath;
		}

	}

	/**
	 * Appends a given jar path to the class path
	 *
	 * @param classPath
	 * @param location
	 * @return
	 */
	private static String appendClassPathWithJarBundleLocation(String classPath,
			String location) {
		String newClassPath = classPath;

		String prefix = "reference:file:";
		int startIndex = prefix.length();
		String relativePluginLocation = location.substring(startIndex,
				location.length());

		String eclipsePath = getEclipsePath();

		String pluginJarPath = eclipsePath + relativePluginLocation;

		pluginJarPath = pluginJarPath.replace("/", "\\");

		newClassPath = newClassPath + ";" + pluginJarPath;
		return newClassPath;

	}

	private static String getEclipsePath() {

		String eclipsePath = Platform.getInstallLocation().getURL().toString();

		eclipsePath = eclipsePath.replace("file:/", "");

		return eclipsePath;

	}

	/**
	 * Determines the path to a bin folder from the given bundle location and
	 * adds the path to the bin folder to the class path
	 *
	 * @param classPath
	 * @param location
	 * @return
	 */
	private static String appendClassPathWithBinBundleLocation(String classPath,
			String location) {

		boolean isSystemBundle = location.equals("System Bundle");
		if (isSystemBundle) {
			return classPath;
		}
		String prefix = "reference:file:/";
		int startIndex = prefix.length();
		String pluginClassPath = location.substring(startIndex,
				location.length());
		String pluginBinClassPath = pluginClassPath + "bin";
		File binFolder = new File(pluginBinClassPath);

		String newClassPath = classPath;
		if (binFolder.exists()) {
			String pluginBinSubFolderPath = pluginBinClassPath + "\\.";
			pluginBinSubFolderPath = pluginBinSubFolderPath.replace("/", "\\");
			//LOG.debug("Appending to classpath: " +
			//pluginBinSubFolderPath);
			newClassPath = newClassPath + ";" + pluginBinSubFolderPath;
		}
		return newClassPath;
	}

	/**
	 * Returns the full class name
	 *
	 * @return
	 */
	public String getFullClassName() {
		return javaFileToBeCompiled.getFullClassName();
	}

	//#end region
}
