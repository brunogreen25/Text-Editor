package hr.fer.ooup.lab3.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import hr.fer.ooup.lab3.observers.ClipboardObserver;
import hr.fer.ooup.lab3.*;
import hr.fer.ooup.lab3.clipboardstack.ClipboardStack;

public class PasteAction extends AbstractAction implements ClipboardObserver {
	
	private ClipboardStack clipboardStack;
	private TextEditorModel textEditorModel;
	
	public PasteAction(ClipboardStack clipboardStack, TextEditorModel textEditorModel) {
		this.clipboardStack = clipboardStack;
		this.textEditorModel = textEditorModel;
		
		clipboardStack.registerNewClipboardObserver(this);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String copiedText = clipboardStack.peekText();
		if (copiedText == "") {
			return;
		}
		textEditorModel.insert(copiedText);
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
