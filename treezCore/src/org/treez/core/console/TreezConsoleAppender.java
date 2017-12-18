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
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.monitor.TreezMonitor;

//note: class name need not match the @Plugin name.
@Plugin(name = "TreezConsoleAppender", category = "Core", elementType = "appender", printObject = true)
public final class TreezConsoleAppender extends AbstractAppender {

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

		String treezMonitorId = event.getContextStack().pop();
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
					String message = event.getMessage().getFormattedMessage();
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

	// Your custom appender needs to declare a factory method
	// annotated with `@PluginFactory`. Log4j will parse the configuration
	// and call this factory method to construct an appender instance with
	// the configured attributes.
	@PluginFactory
	public static TreezConsoleAppender createAppender(
			@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") final Filter filter) {
		if (name == null) {
			LOGGER.error("No name provided for TreezConsoleAppender");
			return null;
		}

		Layout<? extends Serializable> layoutOrDefault = null;
		if (layout == null) {
			layoutOrDefault = PatternLayout.createDefaultLayout();
		}
		return new TreezConsoleAppender(name, filter, layoutOrDefault, true);
	}

	/**
	 * If a non-null jobId is specified: returns the console for the given jobId or null if no corresponding console has
	 * been registered for the TreezMonitors. If the given jobId is null, the (single) TreezConsole is returned.
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
				}
			}

			//console does not already exist: create new one
			treezConsole = new MessageConsole(CONSOLE_NAME, null);
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
