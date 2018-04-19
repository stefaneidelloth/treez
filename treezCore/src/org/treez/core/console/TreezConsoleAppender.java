package org.treez.core.console;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.monitor.TreezMonitor;

/**
 * For writing to the eclipse console
 */
public class TreezConsoleAppender extends AppenderSkeleton {

	//#region ATTRIBUTES

	private static final String CONSOLE_NAME = "TreezConsole";

	private static MessageConsole treezConsole = null;

	//#end region

	//#region CONSTRUCTORS

	//#end region

	//#region METHODS

	@Override
	protected void append(LoggingEvent event) {

		//get formatted message
		Layout layout = this.getLayout();
		String message = layout.format(event);

		String treezMonitorId = event.getNDC();
		MessageConsole console = getConsole(treezMonitorId);

		if (console != null) {

			AbstractUiSynchronizingAtom.runUiTaskNonBlocking(() -> {

				Level level = event.getLevel();

				try (
						MessageConsoleStream stream = console.newMessageStream();) {

					if (level.equals(Level.WARN)) {
						stream.setColor(TreezMonitor.ORANGE);
					} else if (level.equals(Level.ERROR)) {
						stream.setColor(TreezMonitor.RED);
					}

					stream.println(message);
				} catch (IOException exception) {
					exception.printStackTrace();
				}

				ThrowableInformation throwableInformation = event.getThrowableInformation();

				if (throwableInformation != null) {

					Throwable throwable = throwableInformation.getThrowable();

					try (
							MessageConsoleStream stream = console.newMessageStream();) {
						if (level.equals(Level.WARN)) {
							stream.setColor(TreezMonitor.ORANGE);
						} else if (level.equals(Level.ERROR)) {
							stream.setColor(TreezMonitor.RED);
						}

						throwable.printStackTrace(new PrintStream(stream));

					} catch (IOException exception) {
						exception.printStackTrace();
					}

				}

			});

		}

	}

	@Override
	public void close() {
		//not used here
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	/**
	 * If a non-null treezMonitorId is specified: returns the console for the given treezMonitorId or null if no
	 * corresponding console has been registered for the TreezMonitors. If the given treezMonitorId is null, the
	 * (single) TreezConsole is returned.
	 */
	private static MessageConsole getConsole(String treezMonitorId) {
		if (treezMonitorId == null) {
			if (treezConsole == null) {
				createTreezConsole();
			}
			return treezConsole;
		} else {
			return TreezMonitor.getConsole(treezMonitorId);
		}
	}

	/**
	 * Creates the console
	 */
	private static void createTreezConsole() {
		IConsoleManager consoleManager = getConsoleManager();
		if (consoleManager != null) {
			IConsole[] existingConsoles = consoleManager.getConsoles();
			//check if console already exists and save it if so
			for (IConsole currentConsole : existingConsoles) {
				String currentConsoleName = currentConsole.getName();
				boolean isWantedConsole = CONSOLE_NAME.equals(currentConsoleName);
				if (isWantedConsole) {
					treezConsole = (MessageConsole) currentConsole;
					return;
				}
			}

			//console does not already exist: create new one
			treezConsole = new MessageConsole(CONSOLE_NAME, null);
			treezConsole.setWaterMarks(80000, 80001);
			consoleManager.addConsoles(new IConsole[] { treezConsole });
		}
	}

	/**
	 * Gets the eclipse console manager
	 */
	private static IConsoleManager getConsoleManager() {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		if (plugin != null) {
			IConsoleManager consoleManager = plugin.getConsoleManager();
			return consoleManager;
		} else {
			return null;
		}

	}

	//#end region

}
