package hr.fer.ooup.lab3.actions;

import java.util.List;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import hr.fer.ooup.lab3.Location;
import hr.fer.ooup.lab3.TextEditorModel;
import hr.fer.ooup.lab3.clipboardstack.ClipboardStack;
import hr.fer.ooup.lab3.observers.ClipboardObserver;
import hr.fer.ooup.lab3.observers.CursorObserver;
import hr.fer.ooup.lab3.observers.TextObserver;

public class CutAction extends AbstractAction implements TextObserver, CursorObserver {
	
	public ClipboardStack clipboardStack;
	public TextEditorModel textEditorModel;
	
	public CutAction(ClipboardStack clipboardStack, TextEditorModel textEditorModel) {
		this.clipboardStack = clipboardStack;
		this.textEditorModel = textEditorModel;
		
		textEditorModel.registerNewTextObserver(this);
		textEditorModel.registerNewCursorObserver(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		clipboardStack.pushText(textEditorModel.getSelectedTextString());
		textEditorModel.deleteRange(textEditorModel.getSelectionRange());
		
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
