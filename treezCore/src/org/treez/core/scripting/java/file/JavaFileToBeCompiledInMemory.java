package org.treez.core.scripting.java.file;

import java.net.URI;
import java.util.Set;

import javax.tools.SimpleJavaFileObject;

import org.treez.core.scripting.java.ScriptingPropertyExtractor;

/**
 * Represents a java file that should be compiled in memory
 */
public class JavaFileToBeCompiledInMemory extends SimpleJavaFileObject {

	//#region ATTRIBUTES

	/**
	 * CharSequence representing the source code to be compiled
	 */
	private CharSequence content;

	/**
	 * The full name of the class
	 */
	private String fullClassName;

	/**
	 * A set of ids of the bundles this java file depends on
	 */
	private Set<String> bundleIds = null;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * This constructor will store the source code in the internal "content" variable and register it as a source code,
	 * using a URI containing the class full name
	 *
	 * @param content
	 *            source code to compile
	 */
	public JavaFileToBeCompiledInMemory(CharSequence content) {
		super(createUri(content), Kind.SOURCE);
		this.content = content;
		String javaCode = content.toString();
		this.fullClassName = ScriptingPropertyExtractor.extractFullClassName(javaCode);
		this.bundleIds = ScriptingPropertyExtractor.extractBundleIds(javaCode);
	}

	//#end region

	//#region METHODS

	/**
	 * Creates the java file object URI
	 * 
	 * @param content
	 * @return
	 */
	private static URI createUri(CharSequence content) {
		String fullClassName = ScriptingPropertyExtractor.extractFullClassName(content.toString());
		URI uri = URI.create("string:///" + fullClassName.replace('.', '/') + Kind.SOURCE.extension);
		return uri;
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Answers the CharSequence to be compiled. It will give the source code stored in variable "content"
	 */
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return content;
	}

	/**
	 * Returns the ids of the bundles this java file object depends on
	 *
	 * @return
	 */
	public Set<String> getBundleIds() {
		return bundleIds;
	}

	/**
	 * Returns the full class name
	 *
	 * @return
	 */
	public String getFullClassName() {
		return fullClassName;
	}

	//#end region
}
