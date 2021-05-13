package hr.fer.ooup.lab3.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import hr.fer.ooup.lab3.command.UndoManager;
import hr.fer.ooup.lab3.observers.StackObserver;

public class UndoAction extends AbstractAction implements StackObserver {
	
	public UndoAction() {
		UndoManager.getInstance().registerNewStackObserver(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		UndoManager.getInstance().undo();
	}

	@Override
	public void undoStackEmpty() {
		setEnabled(false);
	}

	@Override
	public void undoStackNotEmpty() {
		setEnabled(true);
		
	}

	@Override
	public void redoStackEmpty() {}
	@Override
	public void redoStackNotEmpty() {}
}
