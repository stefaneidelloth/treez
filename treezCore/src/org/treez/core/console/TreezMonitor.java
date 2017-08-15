package org.treez.core.console;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.treez.core.AbstractActivator;

public class TreezMonitor implements IProgressMonitor {

	//#region ATTRIBUTES

	private IOConsole console;

	private SubMonitor monitor;

	private Logger logger;

	//#end region

	//#region CONSTRUCTORS

	public TreezMonitor(String name, SubMonitor parentMonitor) {
		initialize(name, parentMonitor);
	}

	public TreezMonitor(Logger logger, SubMonitor parentMonitor) {
		monitor = parentMonitor.newChild(1);
		this.logger = logger;

	}

	//#end region

	//#region METHODS

	private synchronized void initialize(String name, SubMonitor parentMonitor) {
		//monitor = parentMonitor.newChild(1);
		//console = getOrCreateConsole(name);
	}

	public void info(Object message) {
		/*
				try (
						IOConsoleOutputStream stream = console.newOutputStream();) {
					stream.write(message.toString());
				} catch (IOException e) {
					throw new IllegalStateException("Could not log info", e);
				}
		*/
	}

	public void info(Object message, Throwable throwable) {

		try (
				IOConsoleOutputStream stream = console.newOutputStream();) {
			stream.write(message.toString());
		} catch (IOException e) {
			throw new IllegalStateException("Could not log info", e);
		}

	}

	@Override
	public void done() {
		//monitor.done();
	}

	@Override
	public void internalWorked(double work) {
		//monitor.internalWorked(work);
	}

	@Override
	public boolean isCanceled() {
		//return monitor.isCanceled();
		return false;
	}

	@Override
	public void setCanceled(boolean value) {
		//monitor.setCanceled(value);
	}

	@Override
	public void setTaskName(String name) {
		//monitor.setTaskName(name);
	}

	@Override
	public void subTask(String name) {
		//monitor.subTask(name);
	}

	@Override
	public void worked(int work) {
		//monitor.worked(work);
	}

	@Override
	public void beginTask(String name, int totalWork) {
		//monitor.beginTask(name, totalWork);
	}

	private static synchronized IOConsole getOrCreateConsole(String consoleName) {
		IOConsole console = getConsole(consoleName);

		if (console == null) {
			Image consoleImage = org.treez.core.Activator.getImage("tree.png");
			ImageDescriptor consoleImageDescriptor = AbstractActivator.getImageDescriptor(consoleImage);
			console = new IOConsole(consoleName, consoleImageDescriptor);
			IConsole[] consoles = new IConsole[1];
			consoles[0] = console;
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(consoles);
		}
		return console;
	}

	private static IOConsole getConsole(String consoleName) {
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();

		IConsole[] consoles = consoleManager.getConsoles();
		for (IConsole console : consoles) {
			if (console.getName().equals(consoleName)) {
				return (IOConsole) console;
			}
		}
		return null;
	}

	//#end region

}
