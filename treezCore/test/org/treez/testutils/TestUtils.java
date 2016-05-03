package org.treez.testutils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;

/**
 * Provides some static utility methods that help testing
 */
public final class TestUtils {

	//#region CONSTRUCTORS

	/**
	 * Private Constructor that prevents construction.
	 */
	private TestUtils() {
	}

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
		URI log4jUri = binUri.resolve("../META-INF/log4j.properties");
		try {
			URL log4jUrl = log4jUri.toURL();
			if (new File(log4jUrl.toString()).exists()) {
				PropertyConfigurator.configure(log4jUrl);
			} else {
				log4jUri = binUri.resolve("../classes/log4j.properties");
				try {
					log4jUrl = log4jUri.toURL();
					PropertyConfigurator.configure(log4jUrl);
				} catch (Exception exception) {
					throw new IllegalStateException(
							"Could not initialize logging");
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
