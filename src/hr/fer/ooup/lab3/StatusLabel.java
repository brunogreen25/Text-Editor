package hr.fer.ooup.lab3;

import javax.swing.JLabel;

import hr.fer.ooup.lab3.observers.*;

public class StatusLabel extends JLabel implements CursorObserver, TextObserver {
	
	private TextEditorModel textEditorModel;
	private int linesNumber;
	private Location cursorPosition;
	
	public StatusLabel(TextEditorModel textEditorModel) {
		this.textEditorModel = textEditorModel;
		
		this.cursorPosition = new Location(0, 0);
		this.linesNumber = textEditorModel.getLines().size();
		
		textEditorModel.registerNewCursorObserver(this);
		textEditorModel.registerNewTextObserver(this);
		
		updateStatusLabel();
	}

	@Override
	public void updateText() {
		linesNumber = textEditorModel.getLines().size();
		
		updateStatusLabel();
	}

	@Override
	public void updateCursorLocation(Location loc) {
		this.cursorPosition.setX(loc.getX());
		this.cursorPosition.setY(loc.getY());
		
		updateStatusLabel();
	}
	
	private void updateStatusLabel() {
		this.setText("Cursor position: " + "(" + cursorPosition.getX() + ", " + cursorPosition.getY() + ")" + " | " + "Lines number: " + linesNumber);
	}
}
