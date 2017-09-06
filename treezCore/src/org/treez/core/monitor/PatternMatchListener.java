package org.treez.core.monitor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.texteditor.ITextEditor;

public class PatternMatchListener implements IPatternMatchListener {

	//#region ATTRIBUTES

	private TextConsole console;

	private IProject project;

	//#end region

	//#region CONSTRUCTORS

	//#end region

	//#region METHODS

	@Override
	public void connect(TextConsole console) {
		this.console = console;

		ILaunchConfiguration launchConfig = getLaunchConfiguration(console);
		project = getProject(launchConfig);

	}

	@Override
	public void disconnect() {
		console = null;
	}

	@Override
	public void matchFound(PatternMatchEvent event) {
		try {
			String fileReferenceText = console.getDocument().get(event.getOffset(), event.getLength());
			int separatorIndex = fileReferenceText.lastIndexOf(":");

			String fileName = fileReferenceText.substring(1, separatorIndex);
			int lineNumber = Integer
					.parseInt(fileReferenceText.substring(separatorIndex + 1, fileReferenceText.length() - 1));

			IHyperlink hyperlink = makeHyperlink(fileName, lineNumber); // a link to any file
			console.addHyperlink(hyperlink, event.getOffset(), event.getLength());

		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}

	}

	@Override
	public String getPattern() {
		String fileNamePattern = "[\\w,\\s-]+\\.java";
		String lineNumberPattern = "\\d+";
		return "\\(" + fileNamePattern + "\\:" + lineNumberPattern + "\\)";
	}

	@Override
	public int getCompilerFlags() {
		return 0;
	}

	@Override
	public String getLineQualifier() {
		return null;
	}

	private IHyperlink makeHyperlink(String fileName, int lineNumber) {
		return new IHyperlink() {

			@Override
			public void linkExited() {}

			@Override
			public void linkEntered() {}

			@Override
			public void linkActivated() {

				String className = fileName.replace("\\.java", "");
				JavaProject javaProject = (JavaProject) project;
				try {
					IType type = javaProject.findType(className);
					IEditorPart editor = JavaUI.openInEditor(type);

					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					goToLine(editor, lineNumber);
				} catch (Exception exception) {

				}

			}
		};
	}

	private static void goToLine(IEditorPart editorPart, int lineNumber) {
		if (editorPart instanceof ITextEditor) {
			ITextEditor textEditor = (ITextEditor) editorPart;
			IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());

			if (document != null) {
				IRegion region = null;

				try {
					region = document.getLineInformation(lineNumber - 1);
				} catch (BadLocationException exception) {}

				if (region != null) {
					textEditor.selectAndReveal(region.getOffset(), region.getLength());
				}
			}
		}
	}

	private static ILaunchConfiguration getLaunchConfiguration(TextConsole console) {
		ILaunchConfiguration launchConfig = null;

		if (console instanceof IAdaptable) {
			Object o = ((IAdaptable) console).getAdapter(ILaunchConfiguration.class);

			if (o instanceof ILaunchConfiguration) {
				launchConfig = (ILaunchConfiguration) o;
			}
		}

		if (launchConfig == null && console instanceof org.eclipse.debug.ui.console.IConsole) {
			ILaunch launch = ((org.eclipse.debug.ui.console.IConsole) console).getProcess().getLaunch();
			launchConfig = launch.getLaunchConfiguration();
		}

		return launchConfig;
	}

	private static IProject getProject(ILaunchConfiguration launchConfig) {
		try {
			if (launchConfig == null || launchConfig.getMappedResources() == null) {
				return null;
			}

			for (IResource resource : launchConfig.getMappedResources()) {
				if (resource instanceof IProject) {
					return (IProject) resource;
				} else if (resource.getProject() != null) {
					return resource.getProject();
				}
			}
		} catch (CoreException ex) {
			return null;
		}

		return null;
	}

	//#end region

	//#region ACCESSORS

	//#end region

}
