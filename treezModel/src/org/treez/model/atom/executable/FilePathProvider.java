package org.treez.model.atom.executable;

/**
 * Provides a file path that can be used by other atoms.
 */
public interface FilePathProvider {

	/**
	 * Provides the file path. The file path might be null or it might not point to an existing file in some cases.
	 * Therefore, the calling class has to check the file path and perform some error handling.
	 *
	 * @return
	 */
	String provideFilePath();

}
