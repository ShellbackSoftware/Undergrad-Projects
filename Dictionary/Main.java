import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;

import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.util.Comparator;

public class Main implements Runnable, KeyListener, ListSelectionListener {
	private static String windowTitle = "CS 311: Dictionary";
	private static int windowWidth = 400;
	private static int windowHeight = 800;
	private JFrame mainWindow;
	private JTextField searchField;
	private JList<String> resultList;
	private DefaultListModel<String> resultListModel;
	private JTextArea definitionArea;

	private HybridTST<String> trie = new HybridTST<String>();

	/**
	 * Don't do any Swing GUI stuff here. That needs to be executed from run
	 * which is invoked in the correct event thread from main
	 */
	public Main() {

		String infile = "dictionary.json";
		JsonReader jsonReader;
		JsonObject jobj = null;
		try {
			jsonReader = Json.createReader(new FileReader(infile));
			jobj = jsonReader.readObject();
		} catch (FileNotFoundException e) {
			System.out.println("Could not find the file to read: ");
			e.printStackTrace();
		} catch (JsonParsingException e) {
			System.out
					.println("There is a problem with the JSON syntax; could not parse: ");
			e.printStackTrace();
		} catch (JsonException e) {
			System.out.println("Could not create a JSON object: ");
			e.printStackTrace();
		} catch (IllegalStateException e) {
			System.out
					.println("JSON input was already read or the object was closed: ");
			e.printStackTrace();
		}
		if (jobj == null)
			return;

		Iterator<Map.Entry<String, JsonValue>> it = jobj.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, JsonValue> me = it.next();
			String word = me.getKey().toLowerCase();
			String definition = me.getValue().toString();
			trie.put(word, definition);
		}
	}

	/**
	 * Setup GUI in the correct thread. Required by the Runnable interface
	 */
	public void run() {
		setupUI();
	}

	/**
	 * Setup the user interface
	 */
	private void setupUI() {
		mainWindow = new JFrame(windowTitle);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// The user enters their search query here
		searchField = new JTextField();
		searchField.setBorder(BorderFactory.createTitledBorder("Search"));
		searchField.addKeyListener(this);
		searchField.setFocusTraversalKeysEnabled(false); // to capture VK_TAB or
															// VK_ENTER key
															// press
		// tab can mean complete this word while enter can mean a different kind
		// of search

		// Search results are displayed in a JList
		// (so we can let them click one and figure out which one was selected)
		resultListModel = new DefaultListModel<String>();
		resultListModel.addElement("No results yet");
		resultListModel.addElement("Input query, then press 'Enter' for general search.");
		resultListModel.addElement("Input query with a '.' in the desired location, ");
		resultListModel.addElement("        " + "then press 'Tab' for wildcard search.");
		resultList = new JList<String>(resultListModel);
		resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultList.addListSelectionListener(this);
		resultList.setVisibleRowCount(10);
		JScrollPane scrollPane = new JScrollPane(resultList);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Results"));

		// text area to display the definition of a word
		definitionArea = new JTextArea();
		definitionArea.setLineWrap(true);
		JScrollPane scrollPane2 = new JScrollPane(definitionArea);
		scrollPane2.setBorder(BorderFactory.createTitledBorder("Definition"));

		// layout
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.add(scrollPane);
		mainPanel.add(scrollPane2);

		Container cp = mainWindow.getContentPane();
		cp.add(searchField, BorderLayout.NORTH);
		cp.add(mainPanel, BorderLayout.CENTER);

		mainWindow.setSize(windowWidth, windowHeight);
		mainWindow.setVisible(true);
	}

	/**
	 * Application entry point.
	 * 
	 * @param args
	 *            This application takes no command line arguments
	 */
	public static void main(String[] args) {
		Main main = new Main();
		// Setup GUI on Swing's event thread
		SwingUtilities.invokeLater(main);
	}

	/**
	 * Method required to fulfill the KeyListener contract.
	 *
	 * @param e
	 *            The event object.
	 */
	public void keyTyped(KeyEvent e) {

	}

	/**
	 * Method required to fulfill the KeyListener contract.
	 *
	 * @param e
	 *            The event object.
	 */
	public void keyPressed(KeyEvent e) {
		String currentChar = searchField.getText() + e.getKeyChar();
		String currentString = searchField.getText().toLowerCase();
		if (currentChar.length() <= 1) {
			return;
		}
		resultListModel.clear();
		ArrayList<String> prefixes = new ArrayList<String>();
		for (String str : trie.keysWithPrefix(currentString)) {
			prefixes.add(str);
		}

		class customCompare implements Comparator<String> {
			@Override
			public int compare(String o1, String o2) {
				if (o1.length() != o2.length()) {
					return o1.length() - o2.length();
				}
				return o1.compareTo(o2);
			}
		}

		Collections.sort(prefixes, new customCompare());

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			for (String s : prefixes)
				resultListModel.addElement(s);

			if (prefixes.size() == 0) {
				resultListModel.addElement("Input '" + currentString
						+ "' is not in the dictionary.");
				return;
			}
			definitionArea.setText(trie.get(prefixes.get(0)));
		}

		if (currentChar.length() >= 3) {
			ArrayList<String> keys = new ArrayList<String>();
			for (String s : trie.keysWithPrefix(currentChar)) {
				resultListModel.addElement(s);
			}
			for (String s : trie.keysThatMatch(currentString)) {
				keys.add(s);
			}
			Collections.sort(keys, new customCompare());
			if (e.getKeyCode() == KeyEvent.VK_TAB) {
				for (String s : keys)
					resultListModel.addElement(s);
				if (keys.size() == 0) {
					return;
				}
			}
		}
	}

	/**
	 * Method required to fulfill the KeyListener interface
	 *
	 * @param e
	 *            The event object.
	 */
	public void keyReleased(KeyEvent e) {
		String currentString = searchField.getText().toLowerCase();

		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if (currentString.length() == 0) {
				resultListModel.clear();
				resultListModel.addElement("Please enter a query.");
				resultListModel.addElement("Input query, then press 'Enter' for general search.");
				resultListModel.addElement("Input query with a '.' in the desired location, ");
				resultListModel.addElement("        " + "then press 'Tab' for wildcard search.");
				definitionArea.setText("");				
				return;
			}
			for (String s : trie.keysWithPrefix(currentString))
				resultListModel.addElement(s);
		}
	}

	/**
	 * Method required to fulfill the ListSelectionListener interface
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			if (resultList.getSelectedIndex() == -1) {
				// no selection is made
			} else {
				int rowSelected = resultList.getSelectedIndex();
				if (rowSelected >= 0 && rowSelected < resultListModel.size()) {
					String wordSelected = resultListModel
							.elementAt(rowSelected);
					// Get the definition and put it in the text area
					definitionArea.setText(trie.get(wordSelected));
				}
			}
		}
	}

} // end class Main