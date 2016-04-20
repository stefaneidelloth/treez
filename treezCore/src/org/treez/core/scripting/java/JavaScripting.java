package org.treez.core.scripting.java;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.scripting.AbstractScripting;
import org.treez.core.scripting.LoggingWriter;
import org.treez.core.scripting.ModelProvider;

/**
 * Class for interpreting java code
 */
public class JavaScripting extends AbstractScripting {

	private static final Logger LOG = Logger.getLogger(JavaScripting.class);

	//#region ATTRIBUTES

	/**
	 * An object instance of the compiled java class
	 */
	private Object instance = null;

	//#end region

	//#region CONSTRUCTORS

	public JavaScripting() {

	}

	//#end region

	//#region METHODS

	/**
	 * Executes the given java code
	 *
	 * @param javaCode
	 */
	@Override
	@SuppressWarnings("checkstyle:illegalcatch")
	public void execute(String javaCode) {

		//LOG.debug("Executing following java code: \n" + javaCode);

		//get an instance of the JavaCompiler.
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		Objects.requireNonNull(compiler,
				"Java compiler must not be null. Please start Eclipse with JDK instead of JRE "
						+ "(specify path to JDK with -vm option in eclipse.ini).");

		//get the class loader from this class to use it as
		//the parent class loader for the class loaders that are
		//created by the InMemoryClassFileManager
		ClassLoader parentClassLoader = this.getClass().getClassLoader();
		Objects.requireNonNull(parentClassLoader,
				"The parent class loader must not be null.");

		//Create the InMemoryClassFileManager and use it to
		//compile the class in memory.
		//Also get the full class name and create a new class loader that
		//contains the compiled class.
		ClassLoader memoryClassLoader;
		String fullClassName;
		try (StandardJavaFileManager standardFileManager = compiler
				.getStandardFileManager(null, null, null);
				InMemoryClassFileManager initialFileManager = new InMemoryClassFileManager(
						javaCode, standardFileManager, parentClassLoader);) {

			//get full class name
			fullClassName = initialFileManager.getFullClassName();

			//compile class and create an in memory class loader that
			//contains the compiled class
			memoryClassLoader = compileClassAndCreateInMemoryClassLoader(
					compiler, initialFileManager);

		} catch (Exception exception) {
			String message = "Could not create InMemoryClassFileManager.";
			LOG.error(message, exception);
			throw new IllegalArgumentException(message, exception);
		}

		//Create an object instance using the in memory class loader
		instance = createObjectInstanceFromCompiledClass(fullClassName,
				memoryClassLoader);
		Objects.requireNonNull(instance, "Instance must not be null.");

	}

	/**
	 * Compile the class and create a new file manager that contains the
	 * compiled class and a corresponding in memory class loader. Finaly return
	 * the new class loader.
	 *
	 * @param compiler
	 * @param initialFileManager
	 * @return
	 */
	private static ClassLoader compileClassAndCreateInMemoryClassLoader(
			JavaCompiler compiler,
			InMemoryClassFileManager initialFileManager) {

		ClassLoader memoryClassLoader;
		try (JavaFileManager memoryFileManager = compileClass(
				initialFileManager, compiler);) {

			//Get final class loader from our fileManager (the argument
			//null is not used here; it is just required to implement the
			//file manager interface)
			memoryClassLoader = memoryFileManager.getClassLoader(null);
			Objects.requireNonNull(memoryClassLoader,
					"Could not create memory class loader. Please check if an import is missing in your Treez java file.");

		} catch (SecurityException | IllegalStateException
				| IOException exception) {
			String message = "Could not create memory file manager and memory class loader.";
			LOG.error(message, exception);
			throw new IllegalArgumentException(message, exception);
		}
		return memoryClassLoader;
	}

	/**
	 * Tries to get the root adaptable object (=model) from the scripted java
	 * class object instance. Returns null if the root could not be retrieved.
	 * This method has to be called after the method execute.
	 *
	 * @return
	 */
	@Override
	public AbstractAtom getRoot() {
		//check instance
		Objects.requireNonNull(instance,
				"The document has to be executed before calling this method.");

		//try to cast the class instance to a ModelProvider
		ModelProvider modelProvider = castInstanceToModelProvider();

		//try to retrieve the model (=root) from the ModelProvider
		AbstractAtom root = retrieveModelFromModelProvider(modelProvider);

		return root;
	}

	/**
	 * Compiles the javaCode that is contained in the given file manager and
	 * updates the file manager with the result. The updated file manger
	 * provides a new class loader that can be used to obtain the compiled class
	 * (use the method "getClassLoader").
	 *
	 * @param compiler
	 * @param fileManager
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private static JavaFileManager compileClass(
			InMemoryClassFileManager fileManager, JavaCompiler compiler) {

		//check arguments
		Objects.requireNonNull(compiler, "Java compiler must not be null.");
		Objects.requireNonNull(fileManager, "File manger must not be null.");

		//get compile options (including class path)
		List<String> options = fileManager.getCompileOptions();

		//create a "list of java file objects" to be dynamically compiled in
		//memory. In
		//our case this will only contain a single in memory java file.
		List<JavaFileObject> javaFileObjects = fileManager.getJavaFileObjects();

		//create CompilationTask
		LoggingWriter compileLogger = new LoggingWriter(LOG, Level.ERROR);
		DiagnosticListener<? super JavaFileObject> diagnosticListener = null;
		Iterable<String> classes = null;
		CompilationTask compilationTask = compiler.getTask(compileLogger,
				fileManager, diagnosticListener, options, classes,
				javaFileObjects);
		Objects.requireNonNull(compilationTask,
				"Compilation task must not be null.");

		//run CompilationTask (this will update the wrapped file manager)
		try {
			compilationTask.call();
		} catch (Exception exception) {
			String message = "Could not compile class "
					+ fileManager.getFullClassName();
			LOG.error(message, exception);
			throw new IllegalArgumentException(message, exception);
		}

		//check if compile errors have been logged
		boolean compileErrorsExist = compileLogger.hasData();
		if (compileErrorsExist) {
			String message = "Could not compile class '"
					+ fileManager.getFullClassName() + "\n"
					+ "------------------\n" //
					+ compileLogger.getDataAsString() //
					+ "------------------\n"
					+ "This issue might be due to missing import statements. If so, please add "
					+ "the missing imports to the Treez java file. Please do so even if the imports"
					+ " are not direcly used in your code and you get an Eclipse warning "
					+ "'The import xy is never used'). The imports are actually needed by Treez to"
					+ " indentify Eclipse plugin dependencies. A line 'import org.treez.model.atom.Models;' "
					+ "tells Treez for example that the Eclipse plugin 'org.treez.model' needs to"
					+ " be included in the class path.";
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}

		//return modified file manager
		return fileManager;
	}

	/**
	 * Tries to cast the class object instance to a ModelProvider
	 *
	 * @return
	 */
	private ModelProvider castInstanceToModelProvider() {
		ModelProvider modelProvider = null;
		try {
			modelProvider = (ModelProvider) instance;
		} catch (ClassCastException exception) {
			String message = "Could not cast the scripted java class to a ModelProvider.";
			LOG.error(message, exception);
		}
		return modelProvider;
	}

	/**
	 * Tries to retrieve the model from the ModelProvider
	 *
	 * @param modelProvider
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private static AbstractAtom retrieveModelFromModelProvider(
			ModelProvider modelProvider) {
		AbstractAtom root = null;
		if (modelProvider != null) {
			try {
				root = modelProvider.createModel();
			} catch (Exception exception) {
				String message = "Could not retrive model from scripted java class. Please check the method 'createModel'.";
				LOG.error(message, exception);
			}
		}
		return root;
	}

	/**
	 * Creates an object instance from the compiled class with the given name.
	 * Returns null if the object instance cannot be created
	 *
	 * @param fullClassName
	 * @param classLoader
	 * @return
	 */
	private static Object createObjectInstanceFromCompiledClass(
			String fullClassName, ClassLoader classLoader) {

		//check arguments
		Objects.requireNonNull(fullClassName, "Class name must not be null");
		Objects.requireNonNull(classLoader, "Class loader must not be null");

		//get compiled class
		Class<?> clazz = null;
		try {
			//LOG.debug("Loading class " + fullClassName);
			clazz = classLoader.loadClass(fullClassName);
			//LOG.debug("Loaded class " + fullClassName);
		} catch (ClassNotFoundException exception) {
			String message = "Could not load class " + fullClassName;
			LOG.error(message, exception);
		}

		//create object instance
		Object objectInstance = null;
		if (clazz != null) {
			try {
				//LOG.debug("Creating object instance for class " +
				//fullClassName);
				objectInstance = clazz.newInstance();
				//LOG.debug("Created object instance for class " +
				//fullClassName);
			} catch (InstantiationException exception) {
				String message = "Could not instanciate an object from the class "
						+ fullClassName;
				LOG.error(message, exception);
			} catch (IllegalAccessException e) {
				String message = "Could not instanciate an object from the class "
						+ fullClassName;
				LOG.error(message, e);
			}
		}

		return objectInstance;
	}

	//#end region
}
