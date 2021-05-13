package hr.fer.ooup.lab3.plugins.pluginsFolder;

import java.util.Iterator;

import javax.swing.JOptionPane;

import hr.fer.ooup.lab3.TextEditorModel;
import hr.fer.ooup.lab3.clipboardstack.ClipboardStack;
import hr.fer.ooup.lab3.command.UndoManager;
import hr.fer.ooup.lab3.plugins.Plugin;

public class Statistics implements Plugin {
	
	private final static String name = "Statistics Plugin";
	private final static String description = "Plugin which calculates how many lines, words and letters are in the text editor and shows it in JDialog.";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void execute(TextEditorModel model, UndoManager undoManager, ClipboardStack clipboardStack) {
		int lineCnt = 0;
		int wordCnt = 0;
		int letterCnt = 0;
		
		Iterator<String> linesIter = model.allLines();
		while(linesIter.hasNext()) {
			String line = linesIter.next();
			lineCnt++;
			
			for(String word: line.split(" ")) {
				wordCnt++;
				
				for(Character letter: word.toCharArray()) {
					if (Character.isAlphabetic(letter)) {
						letterCnt++;
					}
				}
			}	
		}
		String message = "There are " + lineCnt + " lines, " + wordCnt + " words and " + letterCnt + " letters in this document.";
		JOptionPane.showMessageDialog(null, message, getName(), JOptionPane.PLAIN_MESSAGE);
	}
}
