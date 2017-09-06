package org.treez.model.atom.executable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.treez.core.monitor.TreezMonitor;

/**
 * An output stream that redirects its output to a Logger. http://stackoverflow.com/questions/6995946/
 * log4j-how-do-i-redirect-an-outputstream-or-writer-to-loggers-writers/28579006#28579006 The logged data accumulates in
 * this logger. If you want to log a huge anount of data you might need to reset this logger from time to time to free
 * the memory.
 */
public class ErrorStream extends OutputStream {

	//#region ATTRIBUTES

	private IOConsoleOutputStream stream;

	private String allLoggedData = "";

	//#end region

	//#region CONSTRUCTORS

	public ErrorStream(IOConsoleOutputStream stream) {
		Objects.requireNonNull(stream, "Logger must be not null.");
		this.stream = stream;
	}

	//#end region

	//#region METHODS

	@Override
	public void write(byte[] bytes) {

		Color originalColor = stream.getColor();
		stream.setColor(TreezMonitor.RED);
		try {
			stream.write(bytes);
		} catch (IOException exception) {
			throw new IllegalStateException("Could not write to stream", exception);
		}
		stream.setColor(originalColor);

		String message = new String(bytes);
		allLoggedData += message;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else {
			boolean offOutOfBounds = (off < 0) || (off > b.length);
			boolean endOutOfBounds = ((off + len) > b.length) || ((off + len) < 0);
			boolean wrongIndex = offOutOfBounds || (len < 0) || endOutOfBounds;
			if (wrongIndex) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return;
			}
		}

		doWrite(b, off, len);
	}

	private void doWrite(byte[] b, int off, int len) {
		byte[] subBytes = new byte[len];
		for (int i = 0; i < len; i++) {
			subBytes[i] = b[off + i];
		}

		write(subBytes);
	}

	/**
	 * Writes the specified byte to this output stream.
	 *
	 * @param b
	 *            the byte to write
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	@Override
	public void write(final int b) throws IOException {
		String message = "The logging of individual bytes is not allowed in this output stream implementation.";
		throw new IllegalStateException(message);
	}

	/**
	 * Resets the stored data
	 */
	public void reset() {
		allLoggedData = "";
	}

	@Override
	public void close() throws IOException {
		super.close();
		reset();
	}

	//#end region

	//#region ACCESSORS

	/**
	 * Returns true if the buffer currently contains data
	 */
	public boolean hasData() {
		return !allLoggedData.isEmpty();
	}

	/**
	 * Returns the currently buffered data as String
	 */
	public String getDataAsString() {
		return allLoggedData;
	}

	//#end region

}
