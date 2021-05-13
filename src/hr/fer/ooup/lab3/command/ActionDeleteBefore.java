package hr.fer.ooup.lab3.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import hr.fer.ooup.lab3.Location;
import hr.fer.ooup.lab3.LocationRange;
import hr.fer.ooup.lab3.TextEditorModel;

public class ActionDeleteBefore implements EditAction {
	//stanje
	private List<String> linesBefore;
	private Location cursorLocationBefore;
	private LocationRange selectionRangeBefore;
		
	//stvari potrebne da se odradi konkretni algoritam
	private TextEditorModel textEditorModel;
	
	//ovdje trebamo primati referencu na kojoj nam se nalaze svi podaci (posto execute_do() ne prima nikakave parametre)
	public ActionDeleteBefore(TextEditorModel textEditorModel) {
		this.linesBefore = new LinkedList<String>();
		this.cursorLocationBefore = new Location(0, 0);
		this.selectionRangeBefore = null;
		
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
		String line = lines.get(lineIndex);
		
		if(cursorLocation.getX() == 0) {
			//ako je prvi char, onda mora obrisat zadnji char u redu prije
			if (cursorLocation.getY() == 0) {
				//samo da nije prvi char u prvom redu
				return;
			}
			lineIndex = lineIndex - 1;
			line = lines.get(lineIndex);
			
			int lineBeforeLength = lines.get(lineIndex).length(); 
			
			cursorLocation.setX(lineBeforeLength);
			cursorLocation.setY(cursorLocation.getY() - 1);
		}
		
		String newLine = line.substring(0, cursorLocation.getX() - 1) + line.substring(cursorLocation.getX());
		
		cursorLocation.setX(cursorLocation.getX() - 1);
		
		lines.set(lineIndex, newLine);
		textEditorModel.setLines(TextEditorModel.deleteEmptyLines(lines));
		
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
