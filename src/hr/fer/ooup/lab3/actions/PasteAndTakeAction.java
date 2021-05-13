package hr.fer.ooup.lab3.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import hr.fer.ooup.lab3.TextEditorModel;
import hr.fer.ooup.lab3.clipboardstack.ClipboardStack;
import hr.fer.ooup.lab3.observers.ClipboardObserver;

public class PasteAndTakeAction extends AbstractAction implements ClipboardObserver {
	
	ClipboardStack clipboardStack;
	TextEditorModel textEditorModel;
	
	public PasteAndTakeAction(ClipboardStack clipboardStack, TextEditorModel textEditorModel) {
		this.clipboardStack = clipboardStack;
		this.textEditorModel = textEditorModel;
		
		clipboardStack.registerNewClipboardObserver(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		textEditorModel.insert(clipboardStack.popText());
	}
	
	@Override
	public void updateClipboard() {
		if(clipboardStack.hasText()) {
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}	
}
