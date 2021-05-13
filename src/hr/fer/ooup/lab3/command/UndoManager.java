package hr.fer.ooup.lab3.command;

import java.util.*;

import hr.fer.ooup.lab3.observers.*;

public class UndoManager {
	
	private Stack<EditAction> undoStack;
	private Stack<EditAction> redoStack;
	
	private List<StackObserver> stackObservers;
	
	//ZA SINGLETON
	private static UndoManager instance = null;
	
	private UndoManager() {
		undoStack = new Stack<EditAction>();
		redoStack = new Stack<EditAction>();
		
		stackObservers = new LinkedList<StackObserver>();
	}
	
	public static UndoManager getInstance() {
		if (instance == null) {
			instance = new UndoManager();
		}
		return instance;
	}
	
	
	public void undo() {
		//skidaj sa undoStack, pushaj na redoStack i izvrsi
		if (!undoStack.isEmpty()) {
			EditAction editAction = undoStack.pop();
			redoStack.push(editAction);
			editAction.execute_undo();
		}
		
		notifyStackObserversAboutUndo();
		notifyStackObserversAboutRedo();
	}
	
	public void redo() {
		if (!redoStack.isEmpty()) {
			EditAction editAction = redoStack.pop();
			editAction.execute_do();
			undoStack.push(editAction);
		}
		
		notifyStackObserversAboutUndo();
		notifyStackObserversAboutRedo();
	}
	
	public void push(EditAction c) {
		//obrisi redoStack, pushaj naredbu na undoStack
		
		//provjeri je li ovo dobro
		redoStack.clear();
		undoStack.push(c);
		
		notifyStackObserversAboutUndo();
		notifyStackObserversAboutRedo();
	}
	
	//Stack Observer
	public boolean registerNewStackObserver(StackObserver newStackObserver) {
		if (stackObservers.contains(newStackObserver)) {
			return false;
		}
		return this.stackObservers.add(newStackObserver);
	}
	
	public boolean unregisterStackObserver(StackObserver stackObserver) {
		if (!stackObservers.contains(stackObserver)) {
			return false;
		}
		return this.stackObservers.remove(stackObserver);
	}
	
	public void notifyStackObserversAboutUndo() {
		for(StackObserver stackObserver: this.stackObservers) {
			if (undoStack.isEmpty()) {
				stackObserver.undoStackEmpty();
			} else {
				stackObserver.undoStackNotEmpty();
			}
		}
	}
	
	public void notifyStackObserversAboutRedo() {
		for(StackObserver stackObserver: this.stackObservers) {
			if (redoStack.isEmpty()) {
				stackObserver.redoStackEmpty();
			} else {
				stackObserver.redoStackNotEmpty();
			}
		}
	}
	
	
}
