package org.treez.core.console;

import java.io.IOException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;

public class TreezSpecificConsoleAppender extends AppenderSkeleton {

	//#region ATTRIBUTES

	private static IOConsole treezConsole;

	//#end region

	//#region CONSTRUCTORS

	public TreezSpecificConsoleAppender(IOConsole console) {
		super();
		treezConsole = console;
	}

	//#end region

	//#region METHODS

	@Override
	protected void append(LoggingEvent event) {

		Layout layout = this.getLayout();

		AbstractUiSynchronizingAtom.runNonUiTask("append", () -> {
			try (
					IOConsoleOutputStream out = treezConsole.newOutputStream();) {

				String message = "# unknown message #";
				if (layout != null) {
					message = layout.format(event);
				} else {
					Object eventMessage = event.getMessage();
					if (eventMessage != null) {
						message = eventMessage.toString();
					}
				}
				out.write(message);
			} catch (IOException exception) {
				throw new IllegalStateException("Could not append message.", exception);
			}
		});

	}

	@Override
	public void close() {
		//not used here
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	//#end region

}
