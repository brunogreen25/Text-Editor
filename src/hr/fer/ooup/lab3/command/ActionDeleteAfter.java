package hr.fer.ooup.lab3.command;

import java.util.*;

import hr.fer.ooup.lab3.*;

public class ActionDeleteAfter implements EditAction {
	
	//stanje
	private List<String> linesBefore;
	private Location cursorLocationBefore;
	private LocationRange selectionRangeBefore;
	
	private TextEditorModel textEditorModel;
	
	//ovdje trebamo primati referencu na kojoj nam se nalaze svi podaci (posto execute_do() ne prima nikakave parametre)
	public ActionDeleteAfter(TextEditorModel textEditorModel) {
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
		
		if (cursorLocation.getX() == line.length()) {
			//ako je zadnji char u redu, onda se mora obrisat prvi char u redu nakon
			if (cursorLocation.getY() == lines.size() - 1) {
				//samo da nije zadnji char u zadnjem redu
				return;
			}
			lineIndex = lineIndex + 1;
			line = lines.get(lineIndex);
			
			cursorLocation.setX(0);
			cursorLocation.setY(cursorLocation.getY() + 1);
			
			String newLine = line.substring(0, cursorLocation.getX()) + line.substring(cursorLocation.getX() + 1);
			
			lines.set(lineIndex, newLine);
			
			textEditorModel.notifyTextObservers();
			return;
		}
		
		String newLine = line.substring(0, cursorLocation.getX()) + line.substring(cursorLocation.getX() + 1);
		
		lines.set(lineIndex, newLine);
		
		textEditorModel.setLines(TextEditorModel.deleteEmptyLines(lines));
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
