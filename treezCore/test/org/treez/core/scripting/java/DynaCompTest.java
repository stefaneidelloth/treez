package org.treez.core.scripting.java;

import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.scripting.ModelProvider;

/**
 * Tests the in memory compilation
 */
public class DynaCompTest {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(DynaCompTest.class);

	/**
	 * Main
	 *
	 * @param args
	 * @throws Exception
	 */
	@Test
	@SuppressWarnings("checkstyle:illegalcatch")
	private static void testDynamicCompilation() {

		//Here we specify the source code of the class to be compiled
		StringBuilder src = new StringBuilder();
		src.append("package org.treez.examples;\n");
		src.append("import org.treez.core.atom.attribute.*;\n");
		src.append("import org.treez.core.scripting.*;\n");
		src.append("public class DynaClass extends ModelProvider {\n");
		src.append("    public Root createModel() {\n");
		src.append("        Root root = new Root(\"root\");\n");
		src.append("        return root;\n");
		src.append("    }\n");
		src.append("}\n");

		String javaCode = src.toString();

		sysLog.info(javaCode);

		//We get an instance of JavaCompiler. Then
		//we create a file manager
		//(our custom implementation of it)
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		try (StandardJavaFileManager standardfileManager = compiler.getStandardFileManager(null, null, null);
				InMemoryClassFileManager fileManager = new InMemoryClassFileManager(
						javaCode,
						standardfileManager,
						DynaCompTest.class.getClassLoader());) {
			//Dynamic compiling requires specifying
			//a list of "files" to compile. In our case
			//this is a list containing one "file" which is in our case
			//our own implementation (see details below)
			List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
			jfiles.add(new InMemoryJavaFileObject(javaCode));

			//We specify a task to the compiler. Compiler should use our file
			//manager and our list of "files".
			//Then we run the compilation with call()
			compiler.getTask(null, fileManager, null, null, null, jfiles).call();

			//Creating an instance of our compiled class and
			//running its toString() method
			String fullClassName = fileManager.getFullClassName();
			ClassLoader classLoader = fileManager.getClassLoader(null);
			Object instance = classLoader.loadClass(fullClassName).newInstance();
			ModelProvider modelProvider = (ModelProvider) instance;
			AbstractAtom root = modelProvider.createModel();
			sysLog.info("Root: " + root.getName());
		} catch (Exception e) {
			sysLog.error("Compilation did not work.", e);
		}
	}
}
