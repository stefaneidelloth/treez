package org.treez.model.atom.executable;

public interface InputPathProvider {

	boolean getIncludeDateInInputFolder();

	boolean getIncludeDateInInputSubFolder();

	boolean getIncludeJobIndexInInputFile();

	boolean getIncludeJobIndexInInputFolder();

	boolean getIncludeJobIndexInInputSubFolder();

	boolean getIncludeDateInInputFile();

	String getJobName();

}
