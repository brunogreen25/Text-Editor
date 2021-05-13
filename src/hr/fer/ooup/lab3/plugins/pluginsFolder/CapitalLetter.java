package hr.fer.ooup.lab3.plugins.pluginsFolder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import hr.fer.ooup.lab3.TextEditorModel;
import hr.fer.ooup.lab3.clipboardstack.ClipboardStack;
import hr.fer.ooup.lab3.command.UndoManager;
import hr.fer.ooup.lab3.plugins.Plugin;

public class CapitalLetter implements Plugin {
	
	private final static String name = "CapitalLetter Plugin";
	private final static String description = "Turns every first letter in a word to a capital letter.";

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
		Iterator<String> linesIter = model.allLines();
		List<String> newLines = new LinkedList<String>();
		
		String newLine;
		while(linesIter.hasNext()) {
			String line = linesIter.next();
			newLine = "";
			for(String word: line.split(" ")) {
				String cap = word.substring(0, 1).toUpperCase() + word.substring(1);
				newLine += cap + " ";
			}
			newLine = newLine.substring(0, newLine.length()-1);
			newLines.add(newLine);
		}
		model.setLines(newLines);
	}
}
