package org.treez.core.console;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
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
@Plugin(name = "TreezConsoleAppender", category = "Core", elementType = "appender", printObject = true)
public class TreezConsoleAppender extends AbstractAppender {

	//#region ATTRIBUTES

	private static final String CONSOLE_NAME = "TreezConsole";

	private static MessageConsole treezConsole = null;

	//#end region

	//#region CONSTRUCTORS

	protected TreezConsoleAppender(
			String name,
			Filter filter,
			Layout<? extends Serializable> layout,
			final boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions);
	}

	//#end region

	//#region METHODS

	@Override
	public void append(LogEvent event) {

		String message = event.getMessage().getFormattedMessage();

		String treezMonitorId = event.getContextData().getValue("id");
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

				Throwable throwable = event.getThrown();

				if (throwable != null) {

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
