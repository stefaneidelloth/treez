package org.treez.core.octave;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.treez.core.quantity.Quantity;

/**
 * Runs an octave instance and can execute octave commands
 */
public class OctaveProcess {

	private static final Logger LOG = Logger.getLogger(OctaveProcess.class);

	//#region ATTRIBUTES

	/**
	 * Line separator
	 */
	private static final String LINE_SEPARATOR = "\n";

	/**
	 * Wait time [ms], e.g. for input/output streams to check for new data
	 */
	private static final int WAIT_TIME = 80;

	/**
	 * The octave output is checked in intervals of this time [ms]
	 */
	private static final int CHECK_INTERVAL_TIME = 1000;

	/**
	 * The path to the octave executable
	 */
	private String octavePath;

	private Process process;

	private OctaveProcessHandler processHandler;

	private BufferedReader outputReader;

	private InputStream errorStream;

	private BufferedReader errorReader;

	private InputStream outputStream;

	private PrintWriter printwriter;

	private OutputCheck outputCheck;

	private boolean waitingForOutputCheck;

	private int currentLineNumber;

	private String outputText;

	//#end region

	//#region CONSTRUCTORS

	@SuppressWarnings("checkstyle:magicnumber")
	public OctaveProcess(String octavePath,
			OctaveProcessHandler processHandler) {

		this.octavePath = octavePath;
		this.processHandler = processHandler;

		startOctave();
		connectToOctaveStreams();

		//wait a moment for the octave process
		sleep(20 * WAIT_TIME); //500

		//"Listen" to Octave output now and every 1000 ms
		outputCheck = new OutputCheck(this);
		new java.util.Timer().schedule(outputCheck, 0, CHECK_INTERVAL_TIME);

		//Wait for the Octave Welcome Output (it is slow)
		sleep(2 * WAIT_TIME);

		//System.out.println(""+waitingForOutputCheck);

		//Wait until octave is ready
		while (waitingForOutputCheck) {
			sleep(WAIT_TIME);
		}

		//turn off paging
		runOctaveCommand("more off;");
	}

	//#end region

	//#region METHODS

	/**
	 * Waits some time
	 *
	 * @param time
	 */
	private static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			LOG.error("Could not get sleep");
		}
	}

	@SuppressWarnings("checkstyle:illegalcatch")
	private void connectToOctaveStreams() {
		try {
			//Connect to Octave input, output and error stream
			printwriter = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(process.getOutputStream())),
					true);
			outputStream = process.getInputStream();
			outputReader = new BufferedReader(
					new InputStreamReader(outputStream));
			errorStream = process.getErrorStream();
			errorReader = new BufferedReader(
					new InputStreamReader(errorStream));

		} catch (Exception e) {
			throw new IllegalStateException(
					"Error connecting to Octave streams: \n" + e);
		}

	}

	/**
	 * Tries to start octave with the current octave path
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private void startOctave() {
		Runtime runtime = Runtime.getRuntime();
		try {
			process = runtime.exec(octavePath);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Could not start octave.exe at " + octavePath, e);
		}
	}

	/**
	 * Start waiting for output check to be finished
	 */
	protected void startWaitingForOutputCheck() {
		this.waitingForOutputCheck = true;
	}

	/**
	 * Stop waiting for output check
	 */
	protected void stopWaitingForOutputCheck() {
		this.waitingForOutputCheck = false;
	}

	/**
	 * @param outputText
	 */
	void handleOutput(String outputText) {
		this.outputText = outputText;
		if (processHandler != null) {
			processHandler.handleOutput(outputText);
		}
	}

	void handleErrorOutput(String errorText) {
		LOG.error(errorText);
		if (processHandler != null) {
			processHandler.handleError(errorText);
		}

	}

	/**
	 * Executes a given octave command
	 *
	 * @param command
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public void runOctaveCommand(String command) {

		checkForExitCommand(command);

		try {
			//execute command
			printwriter.println(command);

			//Check Octave output
			outputCheck.run();

			//Wait until Octave Output is finished
			while (waitingForOutputCheck) {
				sleep(WAIT_TIME);
			}
		} catch (Exception e) {
			LOG.error("Could not execute Octave Command\n" + command + "\n", e);
		}

	}

	/**
	 * Executes a given octave command and returns the output
	 *
	 * @param command
	 * @return
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public String execute(String command) {
		outputText = null;

		checkForExitCommand(command);

		try {
			//Wait until Octave Output is ready
			outputCheck.run();
			while (waitingForOutputCheck) {
				sleep(WAIT_TIME);
			}

			//execute command
			printwriter.println(command);

			//Wait until Octave Output is ready
			outputCheck.run();
			while (waitingForOutputCheck) {
				sleep(WAIT_TIME);
			}
			sleep(WAIT_TIME); //50

			return outputText;

		} catch (Exception e) {
			LOG.error("Could not execute Octave Command\n" + command + "\n", e);
			return "";
		}

	}

	/**
	 * Executes a given octave command and returns the result without assignment
	 * part, e.g ans = 66 => a = 1 => 1
	 *
	 * @param command
	 * @return
	 */
	public String eval(String command) {
		String result = execute(command);

		boolean isEmptyResult = result == null || result.equals("");
		if (isEmptyResult) {
			return "";
		}

		boolean isCorruptedResult = result.contains("octave");
		if (isCorruptedResult) {
			String message = "Something went wrong with the octave evaluation. "
					+ "The result string contains 'octave' which should be filterd out. "
					+ "Please check the regular expression octaveLabelPattern in OutputCheck.";
			throw new IllegalStateException(message);
		}

		if (result.contains("=")) {
			//assignment
			int startIndex = result.indexOf("=") + 1;
			return result.substring(startIndex, result.length()).trim();
		} else {
			//no assignment
			return result;
		}
	}

	/**
	 * @param command
	 * @return
	 */
	public Quantity evalQuantity(String command) {

		final String valueLineSeparator = "\n";

		//LOG.debug("Evaluating '" + command + "'.");
		String result = eval(command);
		//LOG.debug("Result: '" + result + "'.");
		String value = "";
		String unit = "";
		if (result.contains(LINE_SEPARATOR)) {
			String[] lines = result.split(LINE_SEPARATOR);

			//use first line without brackets and * as unit, e.g. [m^1]* => m^2
			unit = lines[0].substring(0, lines[0].length() - 1);

			//use all other lines as "filePath"
			value = "";
			for (int lineIndex = 1; lineIndex < lines.length; lineIndex++) {
				value = value + lines[lineIndex].trim() + valueLineSeparator;
			}
			value = value.substring(0,
					value.length() - valueLineSeparator.length());

		} else {
			value = result;
		}

		return new Quantity(value, unit);
	}

	/**
	 * Checks if the command equals exit end triggers the close action
	 *
	 * @param command
	 */
	private void checkForExitCommand(String command) {
		if (command.equals("exit")) {
			close();
		}
	}

	/**
	 * Destroys the process
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public void close() {
		try {
			process.destroy();
		} catch (Exception e) {
			throw new IllegalStateException("Could not destroy process.");
		}

	}

	//#end region

	//#region ACCESSORS

	/**
	 * Get output reader
	 *
	 * @return the outputReader
	 */
	public BufferedReader getOutputReader() {
		return outputReader;
	}

	/**
	 * Get error reader
	 *
	 * @return the errorReader
	 */
	public BufferedReader getErrorReader() {
		return errorReader;
	}

	/**
	 * Return true if the process is waiting for the output check to be finished
	 *
	 * @return the waitingForOutputCheck
	 */
	public boolean isWaitingForOutputCheck() {
		return waitingForOutputCheck;
	}

	/**
	 * @return the outputStream
	 */
	public InputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * @return the errorStream
	 */
	public InputStream getErrorStream() {
		return errorStream;
	}

	/**
	 * Sets the current octave line number
	 *
	 * @param currentLineNumber
	 */
	void setCurrentLineNumber(int currentLineNumber) {
		this.currentLineNumber = currentLineNumber;

	}

	/**
	 * Returns the current octave line number
	 *
	 * @return
	 */
	public int getLineNumber() {
		return currentLineNumber;
	}

	//#end region

}
