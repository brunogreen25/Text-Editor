package hr.fer.ooup.lab3.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import hr.fer.ooup.lab3.TextEditorModel;

public class CursorToDocumentStartAction extends AbstractAction {
	
	private TextEditorModel textEditorModel;
	
	public CursorToDocumentStartAction(TextEditorModel textEditorModel) {
		this.textEditorModel = textEditorModel;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		textEditorModel.setCursorBegin();
	}
}
