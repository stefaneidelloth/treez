package org.treez.core.scripting.java;

import java.util.Objects;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Class loader for the in memory compilation
 */
public class MemoryClassLoader extends ClassLoader {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(MemoryClassLoader.class);

	//#region ATTRIBUTES

	private JavaClassObject classObject;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param parent
	 * @param classObject
	 */
	public MemoryClassLoader(ClassLoader parent, JavaClassObject classObject) {
		super(parent);
		Objects.requireNonNull(classObject, "classObject must not be null");
		this.classObject = classObject;
	}

	//#end region

	//#region METHODS

	@Override
	@SuppressWarnings("checkstyle:illegalcatch")
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		//sysLog.debug("Finding class " + name);

		byte[] b = classObject.getBytes();
		Class<?> clazz = super.defineClass(name, classObject.getBytes(), 0, b.length);

		if (clazz == null) {
			String message = "Could not find class with MemoryClassLoader: '" + name + "'";
			sysLog.error(message);
			sysLog.debug("Classes of parent class loader:");
			ClassLoader parentClassLoader = this.getParent();
			java.lang.reflect.Field field;
			try {
				field = parentClassLoader.getClass().getDeclaredField("classes");
				field.setAccessible(true);
				@SuppressWarnings("unchecked")
				Vector<Class<?>> classes = (Vector<Class<?>>) field.get(parentClassLoader);
				for (Class<?> clazzz : classes) {
					sysLog.debug(clazzz.getName());
				}
			} catch (Exception e) {
				throw new IllegalStateException("Could not get classes from parent class loader.");
			}

			throw new IllegalStateException(message);
		}

		return clazz;
	}

	//#end region

}
