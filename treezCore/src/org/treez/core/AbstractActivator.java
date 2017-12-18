package org.treez.core;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.treez.core.standallone.StandAloneWorkbench;

/**
 * The activator class controls the plug-in life cycle
 */
@SuppressWarnings("restriction")
public abstract class AbstractActivator extends AbstractUIPlugin {

	private static final Logger LOG = LogManager.getLogger(AbstractActivator.class);

	//#region CONSTRUCTORS

	public AbstractActivator() {}

	//#end region

	//#region METHODS

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		setInstance(this);
		initializeLog4j();
	}

	/**
	 * Sets the shared instance
	 *
	 * @param abstractActivator
	 */
	protected abstract void setInstance(AbstractActivator abstractActivator);

	/**
	 * Initializes log4j
	 */
	protected void initializeLog4j() {

		try {
			LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
			URL log4j2xml = this.getClass().getClassLoader().getResource("META-INF/log4j2.xml");
			loggerContext.setConfigLocation(log4j2xml.toURI());
		} catch (URISyntaxException exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		setInstance(null);
		super.stop(context);
	}

	/**
	 * Get absolute path
	 *
	 * @return
	 */
	public String getAbsolutePath() {

		String pathSeparator = "\\";

		String pluginId = getPluginId();

		if (isRunningInEclipse()) {

			Bundle bundle = Platform.getBundle(pluginId);
			return getAbsoluteFilePathWithFileLocator(pathSeparator, bundle);
		} else {
			return StandAloneWorkbench.getAbsolutePluginPath(pluginId);
		}

	}

	private static String getAbsoluteFilePathWithFileLocator(String pathSeparator, Bundle bundle) {
		org.eclipse.core.runtime.Path path = new org.eclipse.core.runtime.Path("/");
		URL url;
		try {
			url = FileLocator.find(bundle, path, null);
			url = FileLocator.toFileURL(url);

			try {
				File file = new File(url.toURI());
				file = file.getAbsoluteFile();
				String pathWithRelativeContent = file.getAbsolutePath();
				IPath ipath = new org.eclipse.core.runtime.Path(pathWithRelativeContent);
				IPath absPath = ipath.makeAbsolute();
				String pathToPluginDirAbsolute = absPath.toString();
				pathToPluginDirAbsolute = pathToPluginDirAbsolute.replace("/", pathSeparator);
				return pathToPluginDirAbsolute;
			} catch (URISyntaxException e) {
				throw new IllegalStateException("Could not get absolute path");
			}
		} catch (IOException e1) {
			throw new IllegalStateException("Could not get absolute path");
		}
	}

	/**
	 * Creates a new image from two images of the icon folder. The second image is put on top of the first image to
	 * create the new image.
	 *
	 * @param baseImageName
	 * @param overlayImageName
	 * @return
	 */
	public Image getOverlayImage(String baseImageName, String overlayImageName) {
		Image baseImage = getImageFromIconFolder(baseImageName);
		Image newImage = getOverlayImage(baseImage, overlayImageName);
		return newImage;
	}

	/**
	 * Creates a new image from the given image and an image from the icon folder. The second image is put on top of the
	 * first image to create the new image.
	 *
	 * @param baseImage
	 * @param overlayImageName
	 * @return
	 */
	public Image getOverlayImage(Image baseImage, String overlayImageName) {
		Image overlayImage = getImageFromIconFolder(overlayImageName);
		ImageDescriptor overlayImageDescriptor = ImageDescriptor.createFromImage(overlayImage);
		ImageDescriptor[] overlaysArray = new ImageDescriptor[] { overlayImageDescriptor };

		DecorationOverlayIcon overlayIcon = new DecorationOverlayIcon(baseImage, overlaysArray);
		Image newImage = overlayIcon.createImage(true);
		return newImage;
	}

	/**
	 * Gets an image from the icon folder
	 *
	 * @param imageName
	 * @return
	 */
	public Image getImageFromIconFolder(String imageName) {

		Image image;
		if (isRunningInEclipse()) {

			IWorkbench workbench = PlatformUI.getWorkbench();

			//try to get image from eclipse shared images
			image = workbench.getSharedImages().getImage(imageName);

			if (image == null) {

				//try to get the image from icons folder of the eclipse plugin
				Display display = Display.getCurrent();
				String path = getAbsolutePath() + "\\icons\\" + imageName;
				//LOG.debug("Loading image from path '" + path + "'.");
				try {
					image = new Image(display, path);
				} catch (IllegalArgumentException | SWTException | SWTError exception) {
					//use error image as a default and log message
					image = createDefaultImage();
					String message = "Could not load image";
					LOG.error(message, exception);
				}
			}

		} else {

			//assuming stand alone mode
			image = StandAloneWorkbench.getImage(imageName, this);

		}

		return image;
	}

	@SuppressWarnings("restriction")
	public static boolean isRunningInEclipse() {
		boolean isRunningInEclipse = false;
		try {
			Workbench.getInstance();
			isRunningInEclipse = true;
		} catch (NoClassDefFoundError error) {
			//nothing to do here
		}
		return isRunningInEclipse;
	}

	/**
	 * Creates a default image to replace images that could not be found
	 *
	 * @return
	 */
	public static Image createDefaultImage() {
		Display display = Display.getCurrent();
		final int imageSize = 16;
		Image image = new Image(display, imageSize, imageSize);
		GC gc = new GC(image);
		gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.fillOval(0, 0, imageSize, imageSize);
		gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
		gc.drawLine(0, 0, imageSize, imageSize);
		gc.drawLine(imageSize, 0, 0, imageSize);
		gc.dispose();
		return image;
	}

	/**
	 * Returns an image descriptor from an image in the icon folder
	 *
	 * @param imageName
	 * @return
	 */
	public ImageDescriptor getImageDescriptorFromInstance(String imageName) {
		Image image = getImageFromIconFolder(imageName);
		return ImageDescriptor.createFromImage(image);
	}

	/**
	 * Returns an image descriptor for the given image
	 *
	 * @param image
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(Image image) {
		return ImageDescriptor.createFromImage(image);
	}

	//#region HELP CONTEXTS

	/**
	 * Registers the given relative help id for the given control. The absolute help id already contains the plugin id
	 * in front of the relative help id, e.g. org.treez.Data.MY_RELATIVE_HELP_CONTEXT_ID
	 *
	 * @param relativeHelpContextId
	 */
	public void registerRelativeHelpId(String relativeHelpContextId, Control helpControl) {
		String absoluteHelpContextId = getAbsoluteHelpContextId(relativeHelpContextId);
		registerAbsoluteHelpId(absoluteHelpContextId, helpControl);
	}

	/**
	 * Registers the given absolute help id for the given control. The absolute help id already contains the plugin id
	 * in front of the relative help id, e.g. org.treez.Data.MY_RELATIVE_HELP_CONTEXT_ID
	 *
	 * @param helpContextId
	 */
	public static void registerAbsoluteHelpId(String helpContextId, Control helpControl) {

		if (isRunningInEclipse()) {
			IWorkbenchHelpSystem helpSystem = getHelpSystem();
			if (helpSystem != null) {
				applzHelpId(helpContextId, helpControl, helpSystem);
			} else {
				throw new IllegalStateException("Could not get help system.");
			}
		}
	}

	private static void applzHelpId(String helpContextId, Control helpControl, IWorkbenchHelpSystem helpSystem) {
		helpSystem.setHelp(helpControl, helpContextId);

		Display display = Display.getDefault();
		if (display == null) {
			display = Display.getCurrent();
		}

		if (display != null) {
			Shell shell = display.getActiveShell();
			if (shell != null) {
				shell.setData("org.eclipse.ui.help", helpContextId);
			}
		}
	}

	/**
	 * Shows the help view which will automatically navigate to the help page for this control adaption if a help id has
	 * been registered and a corresponding help page exists.
	 */
	public static void showDynamicHelp() {
		IWorkbenchHelpSystem helpSystem = getHelpSystem();
		if (helpSystem != null) {
			helpSystem.displayDynamicHelp();
		}
	}

	/**
	 * Shows the help view and navigates to the page that corresponds to the given help context id.
	 */
	public void showHelpForRelativeHelpContextId(String relativeHelpContextId) {
		String absoluteHelpContextId = getAbsoluteHelpContextId(relativeHelpContextId);
		IWorkbenchHelpSystem helpSystem = getHelpSystem();
		if (helpSystem != null) {
			helpSystem.displayHelp(absoluteHelpContextId);
		}
	}

	/**
	 * Returns the absolute help context id for the given relative help context id. The absolute help context id
	 * includes the plugin id.
	 *
	 * @param relativeHelpContextId
	 * @return
	 */
	public String getAbsoluteHelpContextId(String relativeHelpContextId) {
		String pluginId = getPluginId();
		String absoluteHelpContextId = pluginId + "." + relativeHelpContextId;
		return absoluteHelpContextId;
	}

	/**
	 * Returns the help system if it exists or null.
	 *
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private static IWorkbenchHelpSystem getHelpSystem() {

		IWorkbench workbench = null;
		try {
			workbench = PlatformUI.getWorkbench();
		} catch (Exception exception) {
			LOG.error("Could not get workbench.", exception);
		}

		IWorkbenchHelpSystem helpSystem = null;
		if (workbench != null) {
			helpSystem = workbench.getHelpSystem();
		}
		return helpSystem;
	}

	//#end region

	//#end region

	//#region ACCESSORS

	/**
	 * Returns the plugin id, must be overloaded (hidden) by implementing class
	 *
	 * @return
	 */
	public abstract String getPluginId();

	//#end region
}
