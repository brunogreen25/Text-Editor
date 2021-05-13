package hr.fer.ooup.lab3.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import hr.fer.ooup.lab3.Location;
import hr.fer.ooup.lab3.LocationRange;
import hr.fer.ooup.lab3.TextEditorModel;

public class ActionInsertChar implements EditAction {
	//stanje
	private List<String> linesBefore;
	private Location cursorLocationBefore;
	private LocationRange selectionRangeBefore;
	
	//stvari potrebne da se odradi konkretni algoritam
	private TextEditorModel textEditorModel;
	private char c;
	
	//ovdje trebamo primati referencu na kojoj nam se nalaze svi podaci (posto execute_do() ne prima nikakave parametre)
	public ActionInsertChar(TextEditorModel textEditorModel, char c) {
		this.linesBefore = new LinkedList<String>();
		this.cursorLocationBefore = new Location(0, 0);
		this.selectionRangeBefore = null;
		this.c = c;
		
		this.textEditorModel = textEditorModel;
	}
		
	@Override
	public void execute_do() {
		//dobivanje potrebnih podataka za algoritam i spremanje stanja
		Location cursorLocation = textEditorModel.getCursorLocation();
		List<String> lines = textEditorModel.getLines();
		LocationRange selectionRange = textEditorModel.getSelectionRange();
			
		//spremanje stanja
		cursorLocationBefore = new Location(cursorLocation);
		linesBefore = new ArrayList<String>(lines);
		if (selectionRange != null) {
			selectionRangeBefore = new LocationRange(selectionRange);
		}

		//algoritam
		int lineIndex = cursorLocation.getY();
		int cursorPosX = cursorLocation.getX();
		
		if (selectionRange != null) {
			EditAction action = new ActionDeleteRange(textEditorModel, selectionRange);
			action.execute_do();
			
			//ovo ne koristi, jer ce ovo pozvat akciju, a to znaci dodatno spremanje na undo stog
			//textEditorModel.deleteRange(selectionRange);
			lines = textEditorModel.getLines();
			cursorPosX = selectionRange.getBeginLocation().getX();
			
			String newLine = lines.get(selectionRange.getBeginLocation().getY());
			newLine = newLine.substring(0, cursorPosX) + c + newLine.substring(cursorPosX);
			
			lines.set(selectionRange.getBeginLocation().getY(), newLine);
			
			cursorLocation.setX(cursorPosX + 1);
			cursorLocation.setY(selectionRange.getBeginLocation().getY());
			textEditorModel.setLines(lines);
			
			//textEditorModel.notifyCursorObservers()
			//textEditorModel.notifyTextObservers();
			return;
		}
		
		//ako je prvi element
		if (lines.size() == 0) {
			lines.add(""+c);
			cursorLocation.setX(cursorPosX + 1);
			
			textEditorModel.setLines(lines);
			
			textEditorModel.notifyCursorObservers();
			//textEditorModel.notifyTextObservers();
			
			return;
		}
		
		String newLine = lines.get(lineIndex);
		newLine = newLine.substring(0, cursorPosX) + c + newLine.substring(cursorPosX);
		
		lines.remove(lineIndex);
		lines.add(lineIndex, newLine);
		
		cursorLocation.setX(cursorPosX + 1);
		textEditorModel.setLines(lines);
		
		textEditorModel.notifyCursorObservers();
		//textEditorModel.notifyTextObservers();
	}

	@Override
	public void execute_undo() {
		//vrati stanje
		textEditorModel.setLines(linesBefore);
		textEditorModel.setCursorLocation(cursorLocationBefore);
		textEditorModel.setSelectionRange(selectionRangeBefore);
			
		//textEditorModel.notifyTextObservers();
	}
}
