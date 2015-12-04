package org.treez.core.console;

import java.io.IOException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

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

		//write formatted message to TreezConsole
		MessageConsole console = getConsole();
		if (console != null) {
			try (MessageConsoleStream out = console.newMessageStream();) {
				out.println(message);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
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
	 * Returns the TreezConsole
	 *
	 * @return
	 */
	public static MessageConsole getConsole() {
		if (treezConsole == null) {
			createTreezConsole();
		}
		return treezConsole;
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
				}
			}

			//console does not already exist: create new one
			treezConsole = new MessageConsole(CONSOLE_NAME, null);
			consoleManager.addConsoles(new IConsole[] { treezConsole });
		}
	}

	/**
	 * Gets the eclipse console manager
	 *
	 * @return
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
