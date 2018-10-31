package org.treez.core.console;

import java.io.IOException;
import java.io.Serializable;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.message.Message;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;

@Plugin(name = "TreezConsoleAppender", category = "Core", elementType = "appender", printObject = true)
public class TreezSpecificConsoleAppender extends AbstractAppender {

	//#region ATTRIBUTES

	private static IOConsole treezConsole;

	//#end region

	//#region CONSTRUCTORS

	protected TreezSpecificConsoleAppender(
			String name,
			Filter filter,
			Layout<? extends Serializable> layout,
			final boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions);
	}

	public TreezSpecificConsoleAppender(IOConsole console) {
		super("TreezConsoleAppender", null, null, false);
		treezConsole = console;
	}

	//#end region

	//#region METHODS

	@Override
	public void append(LogEvent event) {

		AbstractUiSynchronizingAtom.runNonUiTask("append", () -> {
			try (
					IOConsoleOutputStream out = treezConsole.newOutputStream();) {

				String message = "# unknown message #";

				Message eventMessage = event.getMessage();
				if (eventMessage != null) {
					message = eventMessage.getFormattedMessage();
				}

				out.write(message);
			} catch (IOException exception) {
				throw new IllegalStateException("Could not append message.", exception);
			}
		});

	}

	//#end region

}
