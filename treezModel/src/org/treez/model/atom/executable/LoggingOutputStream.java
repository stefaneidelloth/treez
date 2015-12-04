package org.treez.model.atom.executable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * An output stream that redirects its output to a Logger. http://stackoverflow.com/questions/6995946/
 * log4j-how-do-i-redirect-an-outputstream-or-writer-to-loggers-writers/28579006#28579006
 */
public class LoggingOutputStream extends OutputStream {

	//#region ATTRIBUTES

	/**
	 * The logger to write to.
	 */
	private Logger log;

	/**
	 * The log level.
	 */
	private Level level;

	private String allLoggedData = "";

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Creates the Logging instance to flush to the given logger.
	 *
	 * @param log
	 *            the Logger to write to
	 * @param level
	 *            the log level
	 * @throws NullPointerException
	 *             in case if one of arguments is null.
	 */
	public LoggingOutputStream(final Logger log, final Level level) throws NullPointerException {
		Objects.requireNonNull(log, "Logger must be not null.");
		Objects.requireNonNull(level, "Log level must be not null.");
		this.log = log;
		this.level = level;
	}

	//#end region

	//#region METHODS

	@Override
	public void write(byte[] bytes) {
		String message = new String(bytes);
		log.log(level, message);
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

	//#end region

	//#region ACCESSORS

	/**
	 * Returns true if the buffer currently contains data
	 *
	 * @return
	 */
	public boolean hasData() {
		return !allLoggedData.isEmpty();
	}

	/**
	 * Returns the currently buffered data as String
	 *
	 * @return
	 */
	public String getDataAsString() {
		return allLoggedData;
	}

	//#end region

}
