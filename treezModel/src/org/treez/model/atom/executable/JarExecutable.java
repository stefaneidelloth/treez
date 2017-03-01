package org.treez.model.atom.executable;

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
import org.treez.core.attribute.Consumer;
import org.treez.core.attribute.Wrap;
import org.treez.model.Activator;

/**
 * Represents an external executable that can be executed with additional command line arguments and file paths
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
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
	protected
			void
			createExecutableSection(Page dataPage, Consumer updateStatusListener, String executableHelpContextId) {
		Section executable = dataPage
				.createSection("javaExecutable", executableHelpContextId)
				.setLabel("Java executable");

		Image resetImage = Activator.getImage("resetJobIndex.png");
		executable.createSectionAction("resetJobIndex", "Reset the job index to 1", () -> resetJobIndex(), resetImage);
		executable.createSectionAction("action", "Run external executable", () -> execute(treeViewRefreshable));

		FilePath filePath = executable.createFilePath(executablePath, this, "Path to java.exe",
				"D:/EclipseJava/App/jdk1.8/bin/java.exe");
		filePath.addModificationConsumer("updateStatus", updateStatusListener);

	}

	private void modifyModel() {

		AbstractAtom<?> root = this.getModel();
		Page dataPage = (Page) root.getChild("data");

		Consumer updateStatusConsumer = () -> refreshStatus();

		createClassPathSection(dataPage, updateStatusConsumer, null);
		createJvmArgumentsSection(dataPage, updateStatusConsumer, null);

	}

	private void createClassPathSection(Page dataPage, Consumer updateStatusListener, String executableHelpContextId) {

		Section section = dataPage
				.createSection("classPath", executableHelpContextId) //
				.setLabel("Jar (class path)");
		section.moveAtom(1);

		FileOrDirectoryPath classPathChooser = section.createFileOrDirectoryPath(jarPath, this,
				"Path to jar file (that provides main class)", "");
		classPathChooser.addModificationConsumer("updateStatus", updateStatusListener);

		TextField fullClassNameField = section.createTextField(fullClassName, this, "");
		fullClassNameField.setLabel("Full name of main class");
		fullClassNameField.addModificationConsumer("updateStatus", updateStatusListener);
	}

	private void createJvmArgumentsSection(
			Page dataPage,
			Consumer updateStatusListener,
			String executableHelpContextId) {

		Section section = dataPage
				.createSection("jvmArguments", executableHelpContextId) //
				.setLabel("JVM arguments");
		section.moveAtom(2);

		TextArea jvmField = section.createTextArea(jvmArgument, this);
		jvmField.setLabel("Arguments for tweaking Java Virtual Maschine");

		jvmField.addModificationConsumer("updateStatus", updateStatusListener);
		jvmField.setHelpId("org.eclipse.ui.ide.jvmArguments");

	}

	@Override
	public Image provideImage() {
		return Activator.getImage("java.png");
	}

	@Override
	protected String buildCommand() {
		String command = "\"" + executablePath.get() + "\"";

		command = addJavaArguments(command);
		command = addInputArguments(command);
		command = addOutputArguments(command);
		command = addLoggingArguments(command);

		return command;
	}

	private String addJavaArguments(String commandToExtend) {
		String command = commandToExtend;
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
