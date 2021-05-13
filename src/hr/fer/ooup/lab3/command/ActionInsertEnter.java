package hr.fer.ooup.lab3.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import hr.fer.ooup.lab3.*;

public class ActionInsertEnter implements EditAction {
	//stanje
	private List<String> linesBefore;
	private Location cursorLocationBefore;
	private LocationRange selectionRangeBefore;
	
	//stvari potrebne da se odradi konkretni algoritam
	private TextEditorModel textEditorModel;
	
	//ovdje trebamo primati referencu na kojoj nam se nalaze svi podaci (posto execute_do() ne prima nikakave parametre)
	public ActionInsertEnter(TextEditorModel textEditorModel) {
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
		int cX = cursorLocation.getX();
		int cY = cursorLocation.getY();
		
		if (selectionRange != null) {
			textEditorModel.notifyTextObservers();
			return;
		}
		
		String firstPart = lines.get(cY).substring(0, cX);
		String lastPart = lines.get(cY).substring(cX);
		
		lines.remove(cY);
		lines.add(cY, lastPart);
		lines.add(cY, firstPart);
		
		cursorLocation.setX(0);
		cursorLocation.setY(cY + 1);
		
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
