package org.treez.testutils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * Provides some static utility methods that help testing
 */
public final class TestUtils {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(TestUtils.class);

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

		} catch (URISyntaxException e) {
			throw new IllegalStateException("Could not initialize logging");
		}

	}

	/**
	 * Returns an image from the image folder
	 *
	 * @param imageName
	 * @return
	 */
	public static Image getImage(String imageName) {
		URL binUrl = TestUtils.class.getClassLoader().getResource(".");
		try {
			URI binUri = binUrl.toURI();
			URI imageUri = binUri.resolve("../icons/" + imageName);

			try {
				URL imageUrl = imageUri.toURL();
				sysLog.debug("Loading test image from " + imageUrl);
				ImageDescriptor imageDescriptor = ImageDescriptor
						.createFromURL(imageUrl);
				Image image = imageDescriptor.createImage();
				return image;
			} catch (MalformedURLException e) {
				throw new IllegalStateException(
						"Could not get image " + imageName);
			}

		} catch (URISyntaxException e) {
			throw new IllegalStateException("Could not get image " + imageName);
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
