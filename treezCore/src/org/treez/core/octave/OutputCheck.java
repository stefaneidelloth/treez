package org.treez.core.octave;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

/**
 * Checks the output of an octave process
 */
public class OutputCheck extends TimerTask {

	private static final Logger LOG = LogManager.getLogger(OutputCheck.class);

	//#region ATTRIBUTES

	/**
	 * Wait time [ms], e.g. for input/output streams to check for new data
	 */
	private static final int WAIT_TIME = 10;

	/**
	 * regular expression pattern for the octave row label
	 */
	private final Pattern octaveLabelPattern = Pattern
			.compile("octave:\\d+\\>");

	/**
	 * Number of characters before line number
	 */
	private final int octaveLabelPatternPreLength = 7;

	/**
	 * The octave process this output check belongs to
	 */
	private OctaveProcess octaveProcess;

	/**
	 * The raw text output from octave
	 */
	private String rawOctaveOutputText;

	/**
	 * The raw error output from octave
	 */
	private String rawOctaveErrorText;

	//#end region

	//#region CONSTRUCTORS

	public OutputCheck(OctaveProcess octaveProcess) {
		this.octaveProcess = octaveProcess;
		this.rawOctaveOutputText = "";
		this.rawOctaveErrorText = "";
	}

	//#end region

	//#region METHODS

	@Override
	public void run() {
		boolean processIsWaiting = octaveProcess.isWaitingForOutputCheck();
		if (!processIsWaiting) {
			octaveProcess.startWaitingForOutputCheck();
			handleRawOctaveOutput();
			octaveProcess.stopWaitingForOutputCheck();
		}
	}

	/**
	 * Handles the raw octave output
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private void handleRawOctaveOutput() {

		boolean outputAvailable = true;

		try {

			while (outputAvailable) {
				waitForConstantOutputSize();
				readOctaveOutput();
				waitForConstantErrorOutputSize();
				readOctaveErrorOutput();
				outputAvailable = checkIfOutputIsAvailable();
			}
			postprocessOutput();
			postprocessErrors();

		} catch (Exception e) {
			throw new IllegalStateException("Octave output handling failed!",
					e);
		}
	}

	/**
	 * Post processes the raw error text
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private void postprocessErrors() {
		if (rawOctaveErrorText.length() > 0) {
			try {
				String errorText = trimOctaveSeparator(rawOctaveErrorText);
				errorText = errorText.trim();
				octaveProcess.handleErrorOutput(errorText);
				rawOctaveErrorText = "";
			} catch (Exception e) {
				LOG.error("Could not postprocess error", e);
				throw e;
			}
		}
	}

	/**
	 * Post processes the the raw output text
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private void postprocessOutput() {

		//LOG.debug("postprocess output");

		if (rawOctaveOutputText.length() > 0) {

			try {

				String outputText = trimOctaveSeparator(rawOctaveOutputText);
				outputText = outputText.trim();

				//LOG.debug("output:\n#'" + outputText + "'#" );

				List<OctaveLabelInformation> lineInfo = extractLabelInformation();
				int numberOfLabels = lineInfo.size();
				switch (numberOfLabels) {
					case 0 :
						//LOG.debug("0 labels");
						octaveProcess.handleOutput(outputText);
						break;
					case 1 :
						//LOG.debug("1 labels");
						octaveProcess.setCurrentLineNumber(
								lineInfo.get(0).getLineNumber());
						outputText = trimOctaveLabel(outputText,
								lineInfo.get(0));
						octaveProcess.handleOutput(outputText);
						break;
					default :
						LOG.warn(
								"Several octave labels. Following output is not handled:\n"
										+ rawOctaveOutputText);
						//throw new IllegalStateException("Cannot handle several outputs at once. "+
						//"The output handling code has to be corrected for\n" + rawOctaveOutputText);
				}

				rawOctaveOutputText = "";
			} catch (Exception e) {
				LOG.error("Could not postprocess output", e);
				throw e;
			}

		}
	}

	/**
	 * @return
	 */
	private List<OctaveLabelInformation> extractLabelInformation() {

		Matcher patternMatcher = octaveLabelPattern
				.matcher(rawOctaveOutputText);
		List<OctaveLabelInformation> labelInfoList = new ArrayList<>();
		while (patternMatcher.find()) {
			int startIndex = patternMatcher.start();
			int endIndex = patternMatcher.end();
			int lineNumber = Integer.parseInt(rawOctaveOutputText.substring(
					startIndex + octaveLabelPatternPreLength, endIndex - 1));
			labelInfoList.add(new OctaveLabelInformation(lineNumber, startIndex,
					endIndex));
		}

		return labelInfoList;
	}

	/**
	 * Removes the octave label, e.g "octave.exe:#>" from the given string
	 *
	 * @param outputText
	 * @param outputInfo
	 * @return
	 */
	private static String trimOctaveLabel(String outputText,
			OctaveLabelInformation outputInfo) {

		String trimmedOutput = outputText
				.substring(0, outputInfo.getStartIndex()).trim();

		return trimmedOutput;

	}

	/**
	 * Removes the octave separator from the given string
	 *
	 * @param rawText
	 * @return
	 */
	private static String trimOctaveSeparator(String rawText) {
		String outputText = rawText.substring(0, rawText.length() - 1);
		return outputText;
	}

	/**
	 * Checks if the output stream or error stream contains available data
	 *
	 * @return
	 * @throws IOException
	 */
	private boolean checkIfOutputIsAvailable() throws IOException {
		boolean outputAvailable;
		outputAvailable = octaveProcess.getOutputStream().available() > 0;
		if (!outputAvailable) {
			outputAvailable = octaveProcess.getErrorStream().available() > 0;
			//LOG.debug("error available:" + outputAvailable);
		}
		return outputAvailable;
	}

	/**
	 * Reads the octave output
	 *
	 * @throws IOException
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	private void readOctaveOutput() throws IOException {

		boolean outputAvailable = octaveProcess.getOutputStream()
				.available() > 0;
		if (outputAvailable) {

			byte[] bufferArray = new byte[128];

			octaveProcess.getOutputStream().read(bufferArray); //puts output in bufferArray
			String outputString = new String(bufferArray);

			rawOctaveOutputText = rawOctaveOutputText + outputString;
		}

	}

	@SuppressWarnings("checkstyle:magicnumber")
	private void readOctaveErrorOutput() throws IOException {
		//LOG.debug("read octave error output");

		boolean outputIsAvailable = octaveProcess.getErrorStream()
				.available() > 0;
		if (outputIsAvailable) {
			byte[] bufferArray = new byte[128];
			octaveProcess.getErrorStream().read(bufferArray); //puts output in bufferArray
			String errorString = new String(bufferArray);
			rawOctaveErrorText = rawOctaveErrorText + errorString;
		}
	}

	/**
	 * Waits in steps of WAITING_TIME until the size of the available octave
	 * output stays constant
	 *
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void waitForConstantOutputSize()
			throws IOException, InterruptedException {
		int outputSizeBeforeWaiting = 0;
		int outputSizeAfterWaiting = 1;
		while (outputSizeBeforeWaiting < outputSizeAfterWaiting) {
			outputSizeBeforeWaiting = octaveProcess.getOutputStream()
					.available();
			Thread.sleep(WAIT_TIME);
			outputSizeAfterWaiting = octaveProcess.getOutputStream()
					.available();
		}
	}

	/**
	 * Waits in steps of WAIT_TIME until the size of the available octave error
	 * output stays constant
	 *
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void waitForConstantErrorOutputSize()
			throws IOException, InterruptedException {
		int outputSizeBeforeWaiting = 0;
		int outputSizeAfterWaiting = 1;
		while (outputSizeBeforeWaiting < outputSizeAfterWaiting) {
			outputSizeBeforeWaiting = octaveProcess.getErrorStream()
					.available();
			Thread.sleep(WAIT_TIME);
			outputSizeAfterWaiting = octaveProcess.getErrorStream().available();
		}
	}

	//#end region

}
