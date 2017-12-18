package org.treez.core.console;

import java.io.IOException;
import java.io.Serializable;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.message.Message;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;

//note: class name need not match the @Plugin name.
@Plugin(name = "TreezConsoleAppender", category = "Core", elementType = "appender", printObject = true)
public final class TreezSpecificConsoleAppender extends AbstractAppender {

	//#region ATTRIBUTES

	private static MessageConsole treezConsole = null;

	//#end region

	//#region CONSTRUCTORS

	protected TreezSpecificConsoleAppender(
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

		AbstractUiSynchronizingAtom.runNonUiTask("append", () -> {
			try (
					IOConsoleOutputStream out = treezConsole.newOutputStream();) {

				Message messageObj = event.getMessage();
				if (messageObj != null) {
					String logMessage = messageObj.getFormattedMessage();
					if (logMessage != null) {
						out.write(logMessage);
					} else {
						out.write("# unknown message #");
					}
				} else {
					out.write("# unknown message #");
				}

			} catch (IOException exception) {
				throw new IllegalStateException("Could not append message.", exception);
			}
		});

	}

	@PluginFactory
	public static TreezSpecificConsoleAppender createAppender(
			@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") final Filter filter) {
		if (name == null) {
			LOGGER.error("No name provided for TreezSpecificConsoleAppender");
			return null;
		}
		Layout<? extends Serializable> layoutOrDefault = null;
		if (layout == null) {
			layoutOrDefault = PatternLayout.createDefaultLayout();
		}
		return new TreezSpecificConsoleAppender(name, filter, layoutOrDefault, true);
	}

	//#end region

}
