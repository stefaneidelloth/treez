package org.treez.model.atom.executable;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.log4j.Logger;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;
import org.treez.core.atom.uisynchronizing.AbstractUiSynchronizingAtom;
import org.treez.core.monitor.ObservableMonitor;

public class ExecutableExecutor {

	private static final Logger LOG = Logger.getLogger(ExecutableExecutor.class);

	//#region ATTRIBUTES

	private Executable executable;

	private boolean executionIsFinished = false;

	private String issueMessage = "";

	private String exceptionMessage = "";

	private String errorMessages = "";

	private String outputMessages = "";

	private OutputStream outputStream;

	private ErrorStream errorStream;

	//#end region

	//#region CONSTRUCTORS

	public ExecutableExecutor(Executable executable) {
		this.executable = executable;
	}

	//#end region

	//#region METHODS

	/**
	 * Executes the given command and returns true if it ended successfully Also updates the executionStatusInfo
	 */
	public boolean executeCommand(String command, ObservableMonitor executableMonitor) {

		executionIsFinished = false;

		exceptionMessage = "";
		errorMessages = "";
		outputMessages = "";

		MessageConsole console = executableMonitor.getConsole();
		IOConsoleOutputStream stream = console.newOutputStream();

		CommandLine cmdLine = CommandLine.parse(command);

		//create and configure executor
		DefaultExecutor executor = new DefaultExecutor();

		//get process handle to be able to catch exceptions and destroy the process
		ExecuteWatchdog watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
		executor.setWatchdog(watchdog);

		//define handling of output and error stream of the process

		errorStream = new ErrorStream(stream);

		outputStream = new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				stream.write(b);
				if (executableMonitor.isCanceled()) {
					watchdog.destroyProcess();
				}
			}
		};

		PumpStreamHandler executionStreamHandler = new PumpStreamHandler(outputStream, errorStream, System.in);

		executor.setStreamHandler(executionStreamHandler);

		//define post processing for finished process
		ExecuteResultHandler executionResultHandler = new ExecuteResultHandler() {

			@Override
			public void onProcessComplete(int exitValue) {
				AbstractUiSynchronizingAtom.runUiTaskNonBlocking(() -> {
					postProcessCompletedProcess(exitValue);
				});

				executionIsFinished = true;

			}

			@Override
			public void onProcessFailed(ExecuteException exception) {
				AbstractUiSynchronizingAtom.runUiTaskNonBlocking(() -> {
					postProcessFailedProcess(exception);
				});

				executionIsFinished = true;

			}
		};

		//execute command in extra thread

		executeCommandInExtraThreadAndWaitUntilFinished(command, cmdLine, executor, watchdog, executionResultHandler);

		//close streams
		closeStreams(outputStream, errorStream);
		executor = null;

		//return true if there are no issues and otherwise false
		boolean noIssues = issueMessage.isEmpty();
		return noIssues;

	}

	@SuppressWarnings("checkstyle:illegalcatch")
	private void executeCommandInExtraThreadAndWaitUntilFinished(
			String command,
			CommandLine cmdLine,
			DefaultExecutor executor,
			ExecuteWatchdog watchdog,
			ExecuteResultHandler executionResultHandler) {

		try {
			executor.execute(cmdLine, executionResultHandler);
		} catch (Exception e) {
			throw new IllegalStateException("Could not run command", e);
		}

		//wait for process to finish
		waitForExecutionToBeFinished(command, watchdog);
	}

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
					if (!errorData.contains("WARNING:")) {
						watchdog.destroyProcess();
						executionIsFinished = true;
						String message = "Error while executing system command '" + command + "':\n" + errorData;
						Exception exception = new IllegalStateException(message);
						postProcessFailedProcess(exception);
					}
				}

			} catch (InterruptedException exception) {
				String message = "Could not wait for command execution to be finish " + command;
				LOG.error(message, exception);
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
		if (emptyStatus) {
			statusMessage = "Finished execution";
		}
		final String executionStatus = statusMessage;
		AbstractUiSynchronizingAtom.runUiTaskNonBlocking(() -> executable.executionStatusInfo.set(executionStatus));

		boolean noIssues = currentIssueMessage.isEmpty();
		if (noIssues) {
			executable.resetError();
		} else {
			executable.highlightError();
		}

	}

	private void postProcessFailedProcess(Exception exception) {

		String statusMessage = "Process execution failed:";
		issueMessage = statusMessage;
		LOG.error(statusMessage, exception);
		AbstractUiSynchronizingAtom.runUiTaskNonBlocking(() -> executable.executionStatusInfo.set(statusMessage));
	}

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
