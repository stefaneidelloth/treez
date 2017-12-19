package org.treez.core.scripting;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * A writer that redirects its output to a Logger.
 */
public class LoggingWriter extends Writer {

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
	public LoggingWriter(final Logger log, final Level level) throws NullPointerException {
		Objects.requireNonNull(log, "Logger must be not null.");
		Objects.requireNonNull(level, "Log level must be not null.");
		this.log = log;
		this.level = level;
	}

	//#end region

	//#region METHODS

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		if (cbuf == null) {
			throw new NullPointerException();
		} else {
			boolean offOutOfBounds = (off < 0) || (off > cbuf.length);
			boolean endOutOfBounds = ((off + len) > cbuf.length) || ((off + len) < 0);
			boolean wrongIndex = offOutOfBounds || (len < 0) || endOutOfBounds;
			if (wrongIndex) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return;
			}
		}

		doWrite(cbuf, off, len);

	}

	@Override
	public void flush() throws IOException {
		//nothing to do here

	}

	@Override
	public void close() throws IOException {
		//nothing to do here

	}

	@Override
	public void write(char[] chars) {
		String message = new String(chars);
		log.log(level, message);
		allLoggedData += message;
	}

	private void doWrite(char[] b, int off, int len) {
		char[] subBytes = new char[len];
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
