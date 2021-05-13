package hr.fer.ooup.lab3.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import hr.fer.ooup.lab3.Location;
import hr.fer.ooup.lab3.LocationRange;
import hr.fer.ooup.lab3.TextEditorModel;

public class ActionInsertString implements EditAction {
	//stanje
	private List<String> linesBefore;
	private Location cursorLocationBefore;
	private LocationRange selectionRangeBefore;
	
	//stvari potrebne da se odradi konkretni algoritam
	private TextEditorModel textEditorModel;
	private String text;
	
	//ovdje trebamo primati referencu na kojoj nam se nalaze svi podaci (posto execute_do() ne prima nikakave parametre)
	public ActionInsertString(TextEditorModel textEditorModel, String text) {
		this.linesBefore = new LinkedList<String>();
		this.cursorLocationBefore = new Location(0, 0);
		this.selectionRangeBefore = null;
		this.text = text;
		
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
		int chars = text.length();
		
		if (selectionRange != null) {
			EditAction action = new ActionDeleteRange(textEditorModel, selectionRange);
			action.execute_do();
			
			//ovo ne koristi, jer ce ovo pozvat akciju, a to znaci dodatno spremanje na undo stog
			//textEditorModel.deleteRange(selectionRange);
		}
		
		String[] newLines = text.split("\n");
		String firstPart = lines.get(lineIndex).substring(0, cursorPosX);
		String lastPart = lines.get(lineIndex).substring(cursorPosX);
		
		for (int i=newLines.length-1;i>=0; i--) {
			if (newLines.length - 1 == 0) {
				//ovo je ako novi red dodajemo u isti (samo ce ovde uc jednom)
				String line = firstPart + newLines[i] + lastPart;
				
				lines.remove(lineIndex);
				lines.add(lineIndex, line);
				
				cursorLocation.setX(cursorPosX + newLines[i].length());
				continue;
			}
			
			if(i == newLines.length-1) {
				//prvi put dodaj tu
				String line = newLines[i] + lastPart;
				
				lines.remove(lineIndex);
				lines.add(lineIndex, line);
				
				cursorLocation.setX(newLines[i].length());
				continue;
			}
			if (i == 0) {
				//dodaj na kraj
				String line = firstPart + newLines[i];
				
				lines.add(lineIndex, line);
				
				continue;
			}
			lines.add(lineIndex, newLines[i]);
		}
		cursorLocation.setY(cursorLocation.getY() + newLines.length - 1);
		
		//textEditorModel.notifyTextObservers();
		textEditorModel.notifyTextObservers();
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
