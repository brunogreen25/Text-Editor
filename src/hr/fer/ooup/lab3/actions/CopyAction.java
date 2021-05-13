package hr.fer.ooup.lab3.actions;

import java.awt.event.ActionEvent;

import javax.swing.*;
import hr.fer.ooup.lab3.*;
import hr.fer.ooup.lab3.clipboardstack.ClipboardStack;
import hr.fer.ooup.lab3.observers.ClipboardObserver;
import hr.fer.ooup.lab3.observers.CursorObserver;
import hr.fer.ooup.lab3.observers.TextObserver;

public class CopyAction extends AbstractAction implements TextObserver, CursorObserver {
	
	private TextEditorModel textEditorModel;
	private ClipboardStack clipboardStack;
	
	public CopyAction(TextEditorModel textEditorModel, ClipboardStack clipboardStack) {
		this.textEditorModel = textEditorModel;
		this.clipboardStack = clipboardStack;
		
		textEditorModel.registerNewTextObserver(this);
		textEditorModel.registerNewCursorObserver(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String copiedText = textEditorModel.getSelectedTextString();
		clipboardStack.pushText(copiedText);		
		setEnabled(false);
	}
	
	@Override
	public void updateText() {
		if (textEditorModel.getSelectionRange() != null) {
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}

	@Override
	public void updateCursorLocation(Location loc) {
		setEnabled(false);
	}
}
