package org.treez.model.atom.executable;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Helper class that actually executes the executable
 */
class ExecutableExecutor {

	/**
	 * Logger for this class
	 */
	private static Logger sysLog = Logger.getLogger(ExecutableExecutor.class);

	//#region ATTRIBUTES

	private Executable executable;

	private boolean executionIsFinished = false;

	private String issueMessage = "";

	private String exceptionMessage = "";

	private String errorMessages = "";

	private String outputMessages = "";

	private LoggingOutputStream outputStream;

	private LoggingOutputStream errorStream;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param executable
	 */
	public ExecutableExecutor(Executable executable) {
		this.executable = executable;
	}

	//#end region

	//#region METHODS

	/**
	 * Executes the given command and returns true if it ended successfully Also updates the executionStatusInfo
	 *
	 * @param command
	 * @return
	 */
	public boolean executeCommand(String command) {

		executionIsFinished = false;

		exceptionMessage = "";
		errorMessages = "";
		outputMessages = "";

		outputStream = new LoggingOutputStream(sysLog, Level.INFO);
		errorStream = new LoggingOutputStream(sysLog, Level.ERROR);

		CommandLine cmdLine = CommandLine.parse(command);

		//create and configure executor
		DefaultExecutor executor = new DefaultExecutor();

		//get process handle to be able to catch exceptions and destroy the process
		ExecuteWatchdog watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
		executor.setWatchdog(watchdog);

		//define handling of output and error stream of the process

		PumpStreamHandler executionStreamHandler = new PumpStreamHandler(outputStream, errorStream, System.in);
		executor.setStreamHandler(executionStreamHandler);

		//define post processing for finished process
		ExecuteResultHandler executionResultHandler = new ExecuteResultHandler() {

			@Override
			public void onProcessComplete(int exitValue) {
				postProcessCompletedProcess(exitValue);
				executionIsFinished = true;

			}

			@Override
			public void onProcessFailed(ExecuteException exception) {
				postProcessFailedProcess(exception);
				executionIsFinished = true;

			}
		};

		//execute command in extra thread

		executeCommandInExtraThread(command, cmdLine, executor, watchdog, executionResultHandler);

		//close streams
		closeStreams(outputStream, errorStream);

		//return true if there are no issues and otherwise false
		boolean noIssues = issueMessage.isEmpty();
		return noIssues;

	}

	@SuppressWarnings("checkstyle:illegalcatch")
	private void executeCommandInExtraThread(
			String command,
			CommandLine cmdLine,
			DefaultExecutor executor,
			ExecuteWatchdog watchdog,
			ExecuteResultHandler executionResultHandler) {
		Runnable executionRunnable = () -> {
			try {
				executor.execute(cmdLine, executionResultHandler);
			} catch (Exception exception) {
				String message = "Could not execut command " + command;
				sysLog.error(message, exception);
				exceptionMessage = exception.getMessage();
			}
		};

		Thread executionThread = new Thread(executionRunnable);
		executionThread.setName("Executable: ExecutionThread");
		executionThread.start();

		//wait for process to finish
		waitForExecutionToBeFinished(command, watchdog);
	}

	/**
	 * Waits for the process execution to be finished
	 *
	 * @param command
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private void waitForExecutionToBeFinished(String command, ExecuteWatchdog watchdog) {
		while (!executionIsFinished) {
			try {
				final int waitTime = 100;
				Thread.sleep(waitTime);

				try {
					watchdog.checkException();
				} catch (Exception exception) {
					executionIsFinished = true;
					postProcessFailedProcess(exception);
				}

				if (errorStream.hasData()) {
					String errorData = errorStream.getDataAsString();
					errorStream.reset();
					watchdog.destroyProcess();
					executionIsFinished = true;
					Exception exception = new Exception(errorData);
					postProcessFailedProcess(exception);
				}

			} catch (InterruptedException exception) {
				String message = "Could not wait for command execution to be finish " + command;
				sysLog.error(message, exception);
				exceptionMessage = exception.getMessage();
			}
		}
	}

	private void postProcessCompletedProcess(int exitValue) {

		if (exitValue != 0) {
			String message = "Process did not finish with expected exit code 0 but with " + exitValue;
			errorMessages += "\n" + message;
		}

		//post process output & error messages
		String currentIssueMessage = exceptionMessage + errorMessages;
		String statusMessage = outputMessages + currentIssueMessage;

		boolean emptyStatus = statusMessage.isEmpty();

		boolean noIssues = currentIssueMessage.isEmpty();
		if (noIssues) {
			executable.resetError();
		} else {
			executable.highlightError();
		}

		if (emptyStatus) {
			statusMessage = "Finished execution";
		}
		executable.executionStatusInfo.set(statusMessage);
		sysLog.info(statusMessage);

	}

	private void postProcessFailedProcess(Exception exception) {

		String statusMessage = "Process execution failed:";
		issueMessage = statusMessage;
		sysLog.error(statusMessage, exception);
		executable.runUiJobNonBlocking(() -> executable.executionStatusInfo.set(statusMessage));
	}

	/**
	 * Closes the streams
	 *
	 * @param out
	 * @param err
	 */
	private static void closeStreams(OutputStream out, OutputStream err) {
		try {
			out.flush();
			out.close();
		} catch (IOException exception) {
			String message = "Could not close output stream";
			throw new IllegalStateException(message, exception);
		}

		try {
			err.flush();
			err.close();
		} catch (IOException exception) {
			String message = "Could not close error stream";
			throw new IllegalStateException(message, exception);
		}
	}

	//#end region

}
