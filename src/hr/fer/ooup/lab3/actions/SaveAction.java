package hr.fer.ooup.lab3.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import hr.fer.ooup.lab3.TextEditor;
import hr.fer.ooup.lab3.TextEditorModel;

public class SaveAction extends AbstractAction {
	
	private Path openedFilePath;
	private TextEditorModel textEditorModel;
	private TextEditor textEditor;
	
	public SaveAction(Path openedFilePath, TextEditorModel textEditorModel, TextEditor textEditor) {
		this.openedFilePath = openedFilePath;
		this.textEditor = textEditor;
		this.textEditorModel = textEditorModel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(openedFilePath == null) {
			//ako je nije otvorena datoteka vec, onda moras odabrat gdje je zelis spremit
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Save your file");
			if(fileChooser.showSaveDialog(textEditor) != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(textEditor, "The saving was not approved", "ERROR", JOptionPane.ERROR_MESSAGE);
				return;
			}
			openedFilePath = fileChooser.getSelectedFile().toPath();
		}
		
		Iterator<String> linesIter = textEditorModel.allLines();
		String lines = "";
		while(linesIter.hasNext()) {
			lines += linesIter.next() + "\n";
		}
		lines = lines.substring(0, lines.length()-1);
		
		byte[] text = lines.getBytes(StandardCharsets.UTF_8);
		try {
			Files.write(openedFilePath, text);
		} catch (IOException exc) {
			JOptionPane.showMessageDialog(textEditor, "There was an error while writing to file", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
}
