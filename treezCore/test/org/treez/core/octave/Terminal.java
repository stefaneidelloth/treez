package org.treez.core.octave;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

/**
 * Terminal example that uses the OctaveProcess class
 */
public class Terminal extends JFrame implements ActionListener, MouseListener, KeyListener {

	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(Terminal.class);

	//#region ATTRIBUTES

	private static final long serialVersionUID = -574505028092270523L;

	private static final String OCTAVE_PATH = "C:\\Octave\\Octave-3.8.1\\bin\\octave.exe --persist --interactive --quiet";

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");

	private OctaveProcess octaveProcess;

	private JTextPane outputTextPane;

	private JTextField textField;

	private List<String> history;

	private int historyIndex;

	private ServerSocket server;

	private boolean listeningForClients;

	//#end region

	//#region CONSTRUCTORS

	/**
	 * Constructor
	 *
	 * @param args
	 */
	public Terminal(String[] args) {

		PropertyConfigurator.configure("meta-inf/log4j.properties");
		LOG.setLevel(Level.DEBUG);

		createControl();

		OctaveProcessHandler processHandler = new OctaveProcessHandler() {

			@Override
			public void handleOutput(String outputString) {
				writeTextToTerminal(outputString);
			}

			@Override
			public void handleError(String errorString) {
				writeErrorToTerminal(errorString);
			}

		};

		octaveProcess = new OctaveProcess(OCTAVE_PATH, processHandler);
		history = new ArrayList<String>();

		evaluateCommandLineArguments(args);
		addClosingAction();
	}

	//#end region

	//#region METHODS

	/**
	 * Main method
	 *
	 * @param args
	 */
	@Test
	public static void startTerminalDemo(final String[] args) {
		//create Terminal
		Terminal terminal = new Terminal(args);

		//Listen to Socket
		terminal.startSocketListener();
	}

	/**
	 * Sets the look and feel of the graphical user interface
	 */
	@SuppressWarnings({ "checkstyle:magicnumber", "checkstyle:illegalcatch" })
	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			UIManager.put("control", new Color(164, 195, 235));
			UIManager.put("info", new Color(255, 255, 206));
			UIManager.put("nimbusAlertYellow", new Color(246, 174, 6));
			UIManager.put("nimbusBase", new Color(54, 103, 165));
			UIManager.put("nimbusDisabledText", new Color(204, 204, 204));
			UIManager.put("nimbusFocus", new Color(137, 174, 214));
			UIManager.put("nimbusGreen", new Color(176, 179, 50));
			UIManager.put("nimbusInfoBlue", new Color(54, 103, 165));
			UIManager.put("nimbusLightBackground", new Color(255, 255, 255));
			UIManager.put("nimbusOrange", new Color(246, 174, 6));
			UIManager.put("nimbusRed", new Color(196, 36, 32));
			UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
			UIManager.put("nimbusSelectionBackground", new Color(54, 103, 165));
			UIManager.put("text", new Color(0, 0, 0));
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception ex) {
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception ex2) {
				throw new IllegalStateException("Could not set look and feel.");
			}
		}
	}

	/**
	 * Adds a closing action to the close button
	 */
	private void addClosingAction() {
		WindowListener wlisten = new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				close();
				System.exit(0);
			}
		};
		this.addWindowListener(wlisten);
	}

	/**
	 * Evaluates arguments passed from the command line as Octave commands
	 *
	 * @param args
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	private void evaluateCommandLineArguments(String[] args) {
		//
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				String command = args[i];
				try {
					octaveProcess.runOctaveCommand(command);
				} catch (Exception e) {
					writeErrorToTerminal("Could not evaluate following command line argument: \n" + args[i]);
					break;
				}
			}
		}
	}

	/**
	 * Creates the user interface
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	private void createControl() {
		//set look and feel
		setLookAndFeel();

		//create textarea
		outputTextPane = new JTextPane();

		//Set textarea's initial text and put it in a scroll pane
		String welcomeText = "Welcome to Octave Terminal!\nConnecting to Octave with following path:\n" + OCTAVE_PATH
				+ "\n...please wait...\n";
		outputTextPane.setContentType("text/html charset=EUC-JP");
		outputTextPane.addMouseListener(this);

		//outputTextPane.setEditorKit(new HTMLEditorKit());
		outputTextPane.setText(welcomeText);
		int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
		int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
		JScrollPane scroller = new JScrollPane(outputTextPane, v, h);

		//Create a content pane, set layout, add scroller to center
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.add(scroller, BorderLayout.CENTER);

		//Create TextField and add it
		textField = new JTextField("");
		textField.addActionListener(this);
		textField.addKeyListener(this);
		content.add(textField, BorderLayout.PAGE_END);

		//Add the content to the frame and set frame properties
		this.setContentPane(content);
		this.setTitle("Octave Terminal");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setLocation(100, 100);
		this.setSize(600, 500);

		//set frame icon
		String imagepath = "." + FILE_SEPARATOR + "#1guiconfig" + FILE_SEPARATOR + "tlogo.png";
		File ifile = new File(imagepath);
		if (ifile.exists()) {
			ImageIcon fimage = new ImageIcon(imagepath);
			this.setIconImage(fimage.getImage());
		}

		//set curser to text field
		textField.requestFocus();

		this.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		int pos = outputTextPane.viewToModel(event.getPoint());
		Element elem = ((StyledDocument) outputTextPane.getDocument()).getCharacterElement(pos);
		if (elem != null) {
			AttributeSet atset = elem.getAttributes();

			//open file
			String pathInfo = atset.getAttribute(HTML.Attribute.HREF).toString();

			LOG.info(pathInfo);

			String[] parts;

			parts = pathInfo.split("<br>");

			String command = "gedit('" + parts[0] + "'," + parts[1] + "," + parts[2] + ")";

			octaveProcess.runOctaveCommand(command.replaceAll("\\\\", "\\\\\\\\"));

		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		//not used here
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//not used here
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//not used here
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//not used here
	}

	/**
	 * Writes text to the terminal
	 *
	 * @param textToWrite
	 */
	public void writeTextToTerminal(String textToWrite) {
		try {
			//write text to terminal
			Document doc = outputTextPane.getDocument();
			doc.insertString(doc.getLength(), textToWrite + "\n", null);

			//move cursor to end of text
			outputTextPane.setCaretPosition(outputTextPane.getDocument().getLength());
		} catch (BadLocationException e) {
			LOG.error("Could not write text to terminal");
		}
	}

	/**
	 * Writes error text to the terminal
	 *
	 * @param textToWrite
	 */

	@SuppressWarnings({ "checkstyle:illegalcatch", "checkstyle:magicnumber" })
	public void writeErrorToTerminal(String textToWrite) {
		try {
			//define error value and style
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			//StyleConstants.setBold(attributes, true);
			//StyleConstants.setItalic(attributes, true);
			StyleConstants.setForeground(attributes, Color.red);
			Document doc = outputTextPane.getDocument();

			//error:   D:\SpiceGUI\javaclasses\go.m at line 1, column 0
			//TerminalErrorText

			//split error text in sinle lines
			String[] errorLines;
			errorLines = textToWrite.split("\n");

			for (int k = 0; k < errorLines.length; k++) {
				String textLine = errorLines[k];

				int idx = textLine.indexOf(" at line ");

				if (idx != -1) {

					int idx2 = textLine.indexOf(", column");

					String linkString = textLine.substring(9, textLine.length());
					String urlString1 = textLine.substring(9, idx);
					String urlString2 = textLine.substring(idx + 9, idx2);
					String urlString3 = textLine.substring(idx2 + 9, textLine.length());

					String urlString = urlString1 + "<br>" + urlString2 + "<br>" + urlString3;

					//write start
					doc.insertString(doc.getLength(), "error: ", attributes);

					//write Hyperlink
					writeHyperlinkToTerminal(linkString, urlString);

					//line break
					doc.insertString(doc.getLength(), "\n", attributes);

				} else {
					//write text to terminal
					doc.insertString(doc.getLength(), textLine + "\n", attributes);
				}
			}

			//move cursor to end of text
			outputTextPane.setCaretPosition(outputTextPane.getDocument().getLength());
		} catch (Exception e) {
			LOG.error("Could not write error text to terminal", e);
		}
	}

	/**
	 * Writes a hyperlink to the terminal
	 *
	 * @param representingText
	 * @param url
	 */
	public void writeHyperlinkToTerminal(String representingText, String url) {
		try {
			String insertText = representingText;
			if (representingText == null || representingText.isEmpty()) {
				insertText = url;
			}

			SimpleAttributeSet urlAttrs = new SimpleAttributeSet();
			urlAttrs.addAttribute(HTML.Attribute.HREF, url);

			StyleConstants.setForeground(urlAttrs, Color.blue);
			StyleConstants.setUnderline(urlAttrs, true);

			Document doc = outputTextPane.getDocument();
			doc.insertString(doc.getLength(), insertText, urlAttrs);
		} catch (BadLocationException e) {
			LOG.error("Could not write hyperlink to terminal.");
		}
	}

	/**
	 * Writes info text to terminal
	 *
	 * @param terminalErrorText
	 */
	@SuppressWarnings("checkstyle:illegalcatch")
	public void writeInfoToTerminal(String terminalErrorText) {
		try {
			//define info value and style
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			//StyleConstants.setBold(attributes, true);
			//StyleConstants.setItalic(attributes, true);
			StyleConstants.setForeground(attributes, Color.blue);

			//write text to terminal
			Document doc = outputTextPane.getDocument();
			doc.insertString(doc.getLength(), terminalErrorText + "\n", attributes);

			//move cursor to end of text
			outputTextPane.setCaretPosition(outputTextPane.getDocument().getLength());
		} catch (Exception e) {
			LOG.error("Could not write info text to terminal", e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		Object actionSource = evt.getSource();
		if (actionSource == textField) {
			//get text
			String commandString = textField.getText();

			//write command to text panel and to buffer
			//try {
			writeTextToTerminal("#" + octaveProcess.getLineNumber() + ">" + commandString);

			if (history.size() == 0) {
				history.add(commandString);
			} else {
				if (!commandString.equals(history.get(history.size() - 1))) {
					history.add(commandString);
				}
			}

			historyIndex = 0;
			//} catch (Exception e) {
			//	LOG.debug("Could not add command to history.");
			//}

			//run commandString as Octave Command
			octaveProcess.runOctaveCommand(commandString);

			//clear text field
			textField.setText("");

		}

	}

	@Override
	public void keyPressed(KeyEvent k) {
		int key = k.getKeyCode();
		if (key == KeyEvent.VK_UP) {
			if (historyIndex < history.size()) {
				historyIndex = historyIndex + 1;
				int index = history.size() - historyIndex;
				textField.setText(history.get(index));
			}
		}
		if (key == KeyEvent.VK_DOWN) {
			if (historyIndex > 1) {
				historyIndex = historyIndex - 1;
				int index = history.size() - historyIndex;
				textField.setText(history.get(index));
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent k) {
		//not used here
	}

	@Override
	public void keyTyped(KeyEvent k) {
		//not used here
	}

	@SuppressWarnings("checkstyle:magicnumber")
	private void startSocketListener() {

		//create Socket Server
		try {
			server = new ServerSocket(4444);
		} catch (IOException e) {
			LOG.error("Could not listen on port: 4444.");
			writeErrorToTerminal("Could not listen on port: 4444!");
		}

		//Wait to accept Clients
		while (listeningForClients) {
			ClientWorker w;
			try {
				w = new ClientWorker(server.accept());
				Thread t = new Thread(w);
				t.start();
			} catch (IOException e) {
				LOG.error("Accept failed: 4444");
				writeErrorToTerminal("Accept failed: 4444");
				break;
			}
		}
	}

	private void close() {
		//Clean up
		try {
			stopSocketListener();
			server.close();
			octaveProcess.close();
		} catch (IOException e) {
			LOG.error("Octave Terminal: Could not close Server.");
		}
	}

	private void stopSocketListener() {
		listeningForClients = false;
	}

	private class ClientWorker implements Runnable {

		private Socket client;

		ClientWorker(Socket client) {
			this.client = client;
		}

		@SuppressWarnings({ "synthetic-access", "checkstyle:illegalcatch" })
		@Override
		public void run() {
			String commandString = "OctaveClientError=1";
			try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					PrintWriter out = new PrintWriter(client.getOutputStream(), true);) {

				while (true) {
					try {
						//read data from client
						commandString = in.readLine();

						//exit loop for special command
						if (commandString.equals("OctaveTerminal_EndClient")) {
							break;
						}

						//execute Command
						executeCommand(commandString);
					} catch (IOException e) {
						break;
						//System.out.println("Octave Terminal Server: Failed to read or execute Octave Command!");
					}

					//Send data back to client
					out.println(commandString);
				}//end of while(true) loop

			} catch (IOException e) {
				LOG.error("Octave Terminal Server: Connection to in or out stream failed");
				writeErrorToTerminal("Octave Terminal Server: Connection to in or out stream failed!");
			}

			try {
				client.close();
			} catch (IOException e) {
				LOG.error("Octave Terminal Server: Failed to close Client!");
				writeErrorToTerminal("Octave Terminal Server: Failed to close Client!");
			}
		}

		@SuppressWarnings("checkstyle:illegalcatch")
		private void executeCommand(String commandString) {
			try {
				//execute Octave command
				octaveProcess.runOctaveCommand(commandString);
			} catch (Exception e) {
				LOG.error("Octave could not execute " + commandString + " :\n", e);
				writeErrorToTerminal("Error: Octave could not run following command: \n" + commandString);
			}
		}
	}

	//#end region

}
