package hr.fer.ooup.lab3.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import hr.fer.ooup.lab3.TextEditor;
import hr.fer.ooup.lab3.TextEditorModel;

public class OpenAction extends AbstractAction {
	
	private TextEditor textEditor;
	private Path openedFilePath;
	private TextEditorModel textEditorModel;
	
	public OpenAction(Path openedFilePath, TextEditor textEditor, TextEditorModel textEditorModel) {
		this.textEditor = textEditor;
		this.openedFilePath = openedFilePath;
		this.textEditorModel = textEditorModel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Choose the file You wish to open");
		if(fileChooser.showOpenDialog(textEditor) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		Path path = fileChooser.getSelectedFile().toPath();
		if(!Files.isReadable(path)) {
			JOptionPane.showMessageDialog(textEditor, "The file you have provided is not readable", "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		byte[] bytes = null;
		try {
			bytes = Files.readAllBytes(path);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(textEditor, "While reading file:" + e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		textEditorModel.setLines(new String(bytes, StandardCharsets.UTF_8));
		openedFilePath = path;
	}
}
