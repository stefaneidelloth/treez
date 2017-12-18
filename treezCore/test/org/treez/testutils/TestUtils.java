package org.treez.testutils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

/**
 * Provides some static utility methods that help testing
 */
public final class TestUtils {

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction.
	 */
	private TestUtils() {}

	//#end region

	//#region METHODS

	/**
	 * Initializes the log4j logging
	 */
	public static void initializeLogging() {
		URL binUrl = TestUtils.class.getClassLoader().getResource(".");
		try {
			URI binUri = binUrl.toURI();
			initializeLoggingWithBinUri(binUri);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Could not initialize logging");
		}
	}

	@SuppressWarnings("checkstyle:illegalcatch")
	private static void initializeLoggingWithBinUri(URI binUri) {
		LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
		URI log4jUri = binUri.resolve("../META-INF/log4j2.xml");
		try {
			URL log4jUrl = log4jUri.toURL();
			String filePath = log4jUrl.toString().substring(6);
			File file = new File(filePath);

			if (file.exists()) {
				loggerContext.setConfigLocation(file.toURI());
			} else {
				log4jUri = binUri.resolve("../classes/log4j2.xml");
				try {
					log4jUrl = log4jUri.toURL();
					loggerContext.setConfigLocation(file.toURI());
				} catch (Exception exception) {
					throw new IllegalStateException("Could not initialize logging");
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException("Could not initialize logging");
		}
	}

	/**
	 * Provides a preview window
	 *
	 * @return
	 */
	public static PreviewWindow getPreviewWindow() {
		return new PreviewWindow();
	}

	//#end region

}
