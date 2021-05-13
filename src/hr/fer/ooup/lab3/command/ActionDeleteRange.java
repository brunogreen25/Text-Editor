package hr.fer.ooup.lab3.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import hr.fer.ooup.lab3.Location;
import hr.fer.ooup.lab3.LocationRange;
import hr.fer.ooup.lab3.TextEditorModel;

public class ActionDeleteRange implements EditAction {
	//stanje
	private List<String> linesBefore;
	private Location cursorLocationBefore;
	private LocationRange selectionRangeBefore;
		
	//stvari potrebne da se odradi konkretni algoritam
	private TextEditorModel textEditorModel;
	private LocationRange r;
	
	//ovdje trebamo primati referencu na kojoj nam se nalaze svi podaci (posto execute_do() ne prima nikakave parametre)
	public ActionDeleteRange(TextEditorModel textEditorModel, LocationRange r) {
		this.linesBefore = new LinkedList<String>();
		this.cursorLocationBefore = new Location(0, 0);
		this.selectionRangeBefore = null;
		this.r = r;
		
		this.textEditorModel = textEditorModel;
	}
	
	//popravit Ctrl+y, nesto je sa globalnin stanjima (hvata krivo, e_do bi triba 1. put iz tem,a svaki sljedeci globalno, cursorLoc je kriv, broj linesova je dobar)
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
		Location startLoc;
		Location endLoc;
		
		if (r.getBeginLocation().getY() < r.getEndLocation().getY() || (r.getBeginLocation().getY() == r.getEndLocation().getY() && r.getBeginLocation().getX() <= r.getEndLocation().getX())) {
			startLoc = r.getBeginLocation();
			endLoc = r.getEndLocation();
		} else {
			startLoc = r.getEndLocation();
			endLoc = r.getBeginLocation();
		}
		
		List<String> newText = new LinkedList<String>();
		
		String unEditedLine;
		for(int i=0;i<lines.size();i++) {
			if (i==startLoc.getY() && i==endLoc.getY()) {
				//za onaj red di je startLoc i di je endLoc
				unEditedLine = lines.get(i);
				unEditedLine = unEditedLine.substring(0, startLoc.getX()) + unEditedLine.substring(endLoc.getX());
				newText.add(unEditedLine);
				continue;
			}
			
			if (i < startLoc.getY() || i > endLoc.getY()) {
				//za onaj red di nema ograde
				unEditedLine = lines.get(i);
				newText.add(unEditedLine);
				continue;
			}
			if (i==startLoc.getY()) {
				//za onaj red di je startLocation
				unEditedLine = lines.get(i).substring(0, startLoc.getX());
				newText.add(unEditedLine);
				continue;
			}
			if (i==endLoc.getY()) {
				//za onaj red di je end location
				unEditedLine = lines.get(i).substring(endLoc.getX());
				
				int newTextLastIndex = newText.size()-1;
				String lastLine = newText.get(newTextLastIndex);
				newText.set(newText.size()-1, lastLine+unEditedLine);
				continue;
			}
			//ako nije u nikojem od ovih if-ova znaci da je cijeli redak oznacen i onda tu nemamo sto dodavati u newText
		}
		
		//izbrisi prosli text i dodaj novi
		lines.clear();
		lines.addAll(newText);
		
		textEditorModel.setSelectionRange(null);
		textEditorModel.setCursorLocation(new Location(startLoc));
		
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
