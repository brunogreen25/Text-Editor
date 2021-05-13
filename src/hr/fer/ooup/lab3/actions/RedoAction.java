package hr.fer.ooup.lab3.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import hr.fer.ooup.lab3.command.UndoManager;
import hr.fer.ooup.lab3.observers.StackObserver;

public class RedoAction extends AbstractAction implements StackObserver {

	public RedoAction() {
		UndoManager.getInstance().registerNewStackObserver(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		UndoManager.getInstance().redo();
	}

	@Override
	public void undoStackEmpty() {}

	@Override
	public void undoStackNotEmpty() {}

	@Override
	public void redoStackEmpty() {
		setEnabled(false);
	}

	@Override
	public void redoStackNotEmpty() {
		setEnabled(true);
	}

}
