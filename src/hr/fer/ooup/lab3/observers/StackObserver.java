package hr.fer.ooup.lab3.observers;

public interface StackObserver {
	public void undoStackEmpty();
	public void undoStackNotEmpty();
	
	public void redoStackEmpty();
	public void redoStackNotEmpty();
}
