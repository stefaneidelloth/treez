package org.treez.core.scripting.java.classloader;

import java.util.Objects;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.treez.core.scripting.java.file.CompiledJavaFileContainer;

/**
 * Class loader for the in memory compilation. If the parent BundleClassLoader
 * can not find a class, this class loader will try to find it.
 */
public class InMemoryClassLoader extends ClassLoader {

	private static final Logger LOG = Logger
			.getLogger(InMemoryClassLoader.class);

	//#region ATTRIBUTES

	private CompiledJavaFileContainer classObject;

	//#end region

	//#region CONSTRUCTORS

	public InMemoryClassLoader(BundleClassLoader parent,
			CompiledJavaFileContainer classObject) {
		super(parent);
		Objects.requireNonNull(classObject, "classObject must not be null");
		this.classObject = classObject;
	}

	//#end region

	//#region METHODS

	@Override
	@SuppressWarnings("checkstyle:illegalcatch")
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		//LOG.debug("Finding class " + name);

		byte[] b = classObject.getBytes();
		Class<?> clazz = super.defineClass(name, classObject.getBytes(), 0,
				b.length);

		if (clazz == null) {
			String message = "Could not find class with MemoryClassLoader: '"
					+ name + "'";
			LOG.error(message);
			LOG.debug("Classes of parent class loader:");
			ClassLoader parentClassLoader = this.getParent();
			java.lang.reflect.Field field;
			try {
				field = parentClassLoader.getClass()
						.getDeclaredField("classes");
				field.setAccessible(true);
				@SuppressWarnings("unchecked")
				Vector<Class<?>> classes = (Vector<Class<?>>) field
						.get(parentClassLoader);
				for (Class<?> clazzz : classes) {
					LOG.debug(clazzz.getName());
				}
			} catch (Exception e) {
				throw new IllegalStateException(
						"Could not get classes from parent class loader.");
			}

			throw new IllegalStateException(message);
		}

		return clazz;
	}

	//#end region

}
