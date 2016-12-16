package org.treez.model.atom.executable;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.treez.core.atom.attribute.FileOrDirectoryPath;
import org.treez.core.atom.attribute.FilePath;
import org.treez.core.atom.attribute.InfoText;
import org.treez.core.atom.attribute.Page;
import org.treez.core.atom.attribute.Section;
import org.treez.core.atom.attribute.TextArea;
import org.treez.core.atom.attribute.TextField;
import org.treez.core.atom.base.AbstractAtom;
import org.treez.core.attribute.Attribute;
import org.treez.core.attribute.Wrap;
import org.treez.model.Activator;

/**
 * Represents an external executable that can be executed with additional command line arguments and file paths
 */
@SuppressWarnings({ "checkstyle:visibilitymodifier", "checkstyle:classfanoutcomplexity" })
public class JarExecutable extends Executable {

	//#region ATTRIBUTES
	public final Attribute<String> jvmArgument = new Wrap<>();

	public final Attribute<String> jarPath = new Wrap<>();

	public final Attribute<String> fullClassName = new Wrap<>();
	//#end region

	//#region CONSTRUCTORS

	public JarExecutable(String name) {
		super(name);

		modifyModel();

	}

	//#end region

	//#region METHODS

	@Override
	protected void createExecutableSection(
			Page dataPage,
			ModifyListener updateStatusListener,
			String executableHelpContextId) {
		Section executable = dataPage
				.createSection("javaExecutable", executableHelpContextId)
				.setLabel("Java executable");

		Image resetImage = Activator.getImage("resetJobIndex.png");
		executable.createSectionAction("resetJobIndex", "Reset the job index to 1", () -> resetJobIndex(), resetImage);
		executable.createSectionAction("action", "Run external executable", () -> execute(treeViewRefreshable));

		FilePath filePath = executable.createFilePath(executablePath, this, "Java executable",
				"D:/EclipseJava/App/jdk1.8/bin/java.exe");
		filePath.addModifyListener("updateStatus", updateStatusListener);

	}

	private void modifyModel() {

		AbstractAtom<?> root = this.getModel();
		Page dataPage = (Page) root.getChild("data");

		ModifyListener updateStatusListener = (ModifyEvent e) -> refreshStatus();

		createJvmArgumentsSection(dataPage, updateStatusListener, null);
		createClassPathSection(dataPage, updateStatusListener, null);

	}

	private void createJvmArgumentsSection(
			Page dataPage,
			ModifyListener updateStatusListener,
			String executableHelpContextId) {

		Section section = dataPage.createSection("jvmArguments", executableHelpContextId).setLabel("JVM Arguments");
		section.moveAtom(1);

		TextArea jvmField = section.createTextArea(jvmArgument, this);
		jvmField.setLabel("JVM arguments");
		jvmField.addModifyListener("updateStatus", updateStatusListener);
		jvmField.setHelpId("org.eclipse.ui.ide.jvmArguments");

	}

	private void createClassPathSection(
			Page dataPage,
			ModifyListener updateStatusListener,
			String executableHelpContextId) {

		Section section = dataPage.createSection("classPath", executableHelpContextId).setLabel("Class Path");
		section.moveAtom(2);

		FileOrDirectoryPath classPathChooser = section.createFileOrDirectoryPath(jarPath, this, "", "");
		classPathChooser.addModifyListener("updateStatus", updateStatusListener);

		TextField classFullName = section.createTextField(fullClassName, this, "");
		classFullName.setLabel("Class Full Name");
		classFullName.addModifyListener("updateStatus", updateStatusListener);
	}

	@Override
	public Image provideImage() {
		return Activator.getImage("java.png");
	}

	@Override
	protected String buildCommand() {
		String command = "\"" + executablePath.get() + "\"";

		// Check the Executable arguments before the ones from this class
		boolean inputArgsIsEmpty = inputArguments.get().isEmpty();
		if (!inputArgsIsEmpty) {
			String modifiedInputArguments = injectStudyAndJobInfo(inputArguments);
			command += " " + modifiedInputArguments;
		}

		boolean inputPathIsEmpty = inputPath.get().isEmpty();
		if (!inputPathIsEmpty) {
			command += " " + inputPath;
		}

		boolean outputArgsIsEmpty = outputArguments.get().isEmpty();
		if (!outputArgsIsEmpty) {
			command += " " + outputArguments;
		}

		boolean outputPathIsEmpty = outputPath.get().isEmpty();
		if (!outputPathIsEmpty) {
			modifiedOutputPath = provideFilePath();
			command += " " + modifiedOutputPath;
		}

		boolean logArgsIsEmpty = logArguments.get().isEmpty();
		if (!logArgsIsEmpty) {
			command += " " + logArguments;
		}

		boolean logFilePathIsEmpty = logFilePath.get().isEmpty();
		if (!logFilePathIsEmpty) {
			command += " " + logFilePath;
		}
		boolean jvmArgumentsIsEmpty = jvmArgument.get().isEmpty();
		if (!jvmArgumentsIsEmpty) {
			command += " " + jvmArgument.get();
		}

		boolean classPathArgsIsEmplty = jarPath.get().isEmpty();
		if (!classPathArgsIsEmplty) {
			command += " -cp " + jarPath.get();
		}

		boolean classFullNameArgsIsEmpty = fullClassName.get().isEmpty();
		if (!classFullNameArgsIsEmpty) {
			command += " " + fullClassName.get();
		}

		return command;
	}

	@Override
	public void refreshStatus() {
		this.runUiJobNonBlocking(() -> {
			String infoTextMessage = buildCommand();
			// LOG.debug("Updating info text: " + infoTextMessage);
			commandInfo.set(infoTextMessage);

			Wrap<String> infoTextWrap = (Wrap<String>) executionStatusInfo;
			InfoText executionStatusInfoText = (InfoText) infoTextWrap.getAttribute();
			executionStatusInfoText.resetError();
			executionStatusInfoText.set("Not yet executed");

			jobIndexInfo.set("" + getJobId());
		});

	}

	//#end region

	//#end region

}
