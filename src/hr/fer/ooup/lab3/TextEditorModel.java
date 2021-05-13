package hr.fer.ooup.lab3;

import hr.fer.ooup.lab3.command.*;
import hr.fer.ooup.lab3.iterator.*;
import hr.fer.ooup.lab3.observers.*;

import java.awt.Point;
import java.util.*;
import java.util.stream.Collectors;

public class TextEditorModel {
	
	//2.2
	private List<String> lines;
	private LocationRange selectionRange;
	private Location cursorLocation;
	
	private List<CursorObserver> cursorObservers;
	private List<TextObserver> textObservers;
	
	private UndoManager undoManager;
	
	public TextEditorModel(String text) {
		this.lines = new LinkedList<String>();
		
		this.cursorObservers = new LinkedList<CursorObserver>();
		this.textObservers = new LinkedList<TextObserver>();
		
		this.cursorLocation = new Location(0, 0);
		
		this.undoManager = UndoManager.getInstance();
		
		for (String line: text.split("\n")) {
			this.lines.add(line);
		}
	}
	
	//ITERATORI
	public Iterator<String> allLines() {
		return new MyIterator(this.lines);
	}
	
	public Iterator<String> linesRange(int index1, int index2) {
		return new MyIterator(this.lines, index1, index2);
	}
	
	//ZA POMICANJE CURSORA
	public boolean registerNewCursorObserver(CursorObserver newCursorObserver) {
		if (cursorObservers.contains(newCursorObserver)) {
			return false;
		}
		return this.cursorObservers.add(newCursorObserver);
	}
	
	public boolean unregisterCursorObserver(CursorObserver cursorObserver) {
		if (!cursorObservers.contains(cursorObserver)) {
			return false;
		}
		return this.cursorObservers.remove(cursorObserver);
	}
	
	public void notifyCursorObservers() {
		for(CursorObserver cursorObserver: this.cursorObservers) {
			cursorObserver.updateCursorLocation(this.cursorLocation);
		}
	}
	
	public boolean moveCursorLeft() {
		if (this.cursorLocation.getX() == 0) {
			//samo da nije prva linija
			if (this.cursorLocation.getY() == 0) {
				return false;
			}
			cursorLocation.setLocation(lines.get(cursorLocation.getY() - 1).length(), cursorLocation.getY() - 1);
		} else {
			cursorLocation.setX(cursorLocation.getX() - 1);
		}
		
		notifyCursorObservers();
		return true;
	}
	public boolean moveCursorRight() {
		int currentLineLength = lines.get(cursorLocation.getY()).length();
		
		if (this.cursorLocation.getX() == currentLineLength) {
			//samo da nije prva linija
			if (this.cursorLocation.getY() == lines.size() - 1) {
				return false;
			}
			cursorLocation.setLocation(0, cursorLocation.getY() + 1);
		} else {
			cursorLocation.setX(cursorLocation.getX() + 1);
		}
		
		notifyCursorObservers();
		return true;
	}
	public boolean moveCursorDown() {
		int textLinesSize = lines.size();
		
		if (this.cursorLocation.getY() == textLinesSize - 1) {
			//ako je zadnja linija ne moze se dalje
			return false;
		} else if (cursorLocation.getX() > lines.get(cursorLocation.getY() + 1).length()) {
			//ako donji red ima manje charova nego sta je trenutni polozaj cursorX-a
			cursorLocation.setLocation(lines.get(cursorLocation.getY() + 1).length(), cursorLocation.getY() + 1);
		} else {
			//inace samo povecaj y za 1
			cursorLocation.setY(cursorLocation.getY() + 1);
		}
		
		notifyCursorObservers();
		return true;
	}
	public boolean moveCursorUp() {

		if (this.cursorLocation.getY() == 0) {
			//samo da nije prva linija
			return false;
		} else if (cursorLocation.getX() > lines.get(cursorLocation.getY() - 1).length()) {
			//ako gornji red ima vise clanova nego sta je trenutni polozaj cursorY-a
			cursorLocation.setLocation(lines.get(cursorLocation.getY() - 1).length(), cursorLocation.getY() - 1);
		} else {
			//inace samo smanji y za 1
			cursorLocation.setY(cursorLocation.getY() - 1);

		}
		
		notifyCursorObservers();
		return true;
	}
	
	//ZA DELETEANJE
	public boolean registerNewTextObserver(TextObserver newTextObserver) {
		if (textObservers.contains(newTextObserver)) {
			return false;
		}
		return this.textObservers.add(newTextObserver);
	}
	
	public boolean unregisterTextObserver(TextObserver textObserver) {
		if (!textObservers.contains(textObserver)) {
			return false;
		}
		return this.textObservers.remove(textObserver);
	}
	
	public void notifyTextObservers() {
		for(TextObserver textObserver: this.textObservers) {
			textObserver.updateText();
		}
	}
	
	public static LinkedList<String> deleteEmptyLines(List<String> lines) {
		String lastLine = lines.get(lines.size() - 1);
		LinkedList<String> newList = lines.stream().filter(item->!item.equals("")).collect(Collectors.toCollection(LinkedList::new));
		if (lastLine.equals("")) {
			newList.add(lastLine);
		}
		
		return newList;
	}
	
	public void deleteBefore() {
		EditAction editAction = new ActionDeleteBefore(this);
		editAction.execute_do();
		undoManager.push(editAction);
	}
	
	public void deleteAfter() {
		EditAction editAction = new ActionDeleteAfter(this);
		editAction.execute_do();
		undoManager.push(editAction);
	}
	
	public void deleteRange(LocationRange r) {
		EditAction editAction = new ActionDeleteRange(this, r);
		editAction.execute_do();
		undoManager.push(editAction);
	}
	
	
	//ZA UPDATE-ANJE SELECTIONA
	//zbog jednostavnosti, Shift moze ic samo u jednom smjeru
	public boolean addSelectionRight() {
		if (selectionRange == null) {
			//ako je ovo prvi selection, napravi novi sa cursorLocation-om kao pocetnom pozicijom
			selectionRange = new LocationRange(cursorLocation, cursorLocation);
		}
		
		if (cursorLocation.getY() == lines.size() - 1 && cursorLocation.getX() == lines.get(cursorLocation.getY()).length()) {
			//ako je ovo zadnji char u zadnjoj liniji, ne mozes dalje
			return false;
		}
		
		if (cursorLocation.getX() == lines.get(cursorLocation.getY()).length()) {
			//ako je ovo zadnji char, ali ima jos linija nakon
			cursorLocation.setX(0);
			cursorLocation.setY(cursorLocation.getY() + 1);
			
			selectionRange.setEndLocation(cursorLocation);
			
			notifyTextObservers();
			return true;
		}
		
		//inace samo je normalni selection prema desno i samo onda povecaj selectionRange.endLocation.x za 1
		cursorLocation.setX(cursorLocation.getX() + 1);
		selectionRange.setEndLocation(cursorLocation);
		
		notifyTextObservers();
		return true;
	}
	
	public boolean addSelectionLeft() {
		if (selectionRange == null) {
			//ako je ovo prvi selection, napravi novi sa cursorLocation-om kao pocetnom pozicijom
			selectionRange = new LocationRange(cursorLocation, cursorLocation);
		}
		
		if (cursorLocation.getY() == 0 && cursorLocation.getX() == 0) {
			//ako je ovo prvi char u prvoj liniji, ne mozes dalje
			return false;
		}
		
		if (cursorLocation.getX() == 0) {
			//ako je ovo prvi char, ali ima jos linija prije
			cursorLocation.setX(lines.get(cursorLocation.getY() - 1).length());
			cursorLocation.setY(cursorLocation.getY() - 1);
			
			selectionRange.setBeginLocation(cursorLocation);
			
			notifyTextObservers();
			return true;
		}
		
		//inace samo je normalni selection prema desno i samo onda smanji selectionRange.beginLocation.x za 1
		cursorLocation.setX(cursorLocation.getX() - 1);
		selectionRange.setBeginLocation(cursorLocation);
		
		notifyTextObservers();
		return true;
	}
	
	public boolean addSelectionDown() {
		if (selectionRange == null) {
			//ako je ovo prvi selection, napravi novi sa cursorLocation-om kao pocetnom pozicijom
			selectionRange = new LocationRange(cursorLocation, cursorLocation);
		}
		
		if (cursorLocation.getY() == lines.size() - 1) {
			//ako je ovo zadnji red, onda nemozes nista selection-at
			return false;
		}
		
		if (cursorLocation.getX() > lines.get(cursorLocation.getY() + 1).length()) {
			//ako sljedeci red ima manje znakova nego sto je x od naseg trenutnog
			cursorLocation.setX(lines.get(cursorLocation.getY() + 1).length());
			cursorLocation.setY(cursorLocation.getY() + 1);
			
			selectionRange.setEndLocation(cursorLocation);
			
			notifyTextObservers();
			return true;
		}
		
		//inace samo je normalni selection prema dolje
		cursorLocation.setY(cursorLocation.getY() + 1);
		selectionRange.setEndLocation(cursorLocation);
		
		notifyTextObservers();
		return true;
	}
	
	public boolean addSelectionUp() {
		if (selectionRange == null) {
			//ako je ovo prvi selection, napravi novi sa cursorLocation-om kao pocetnom pozicijom
			selectionRange = new LocationRange(cursorLocation, cursorLocation);
		}
		
		if (cursorLocation.getY() == 0) {
			//ako je ovo prvi red, onda ne mozes nista selection-at
			return false;
		}
		
		if (cursorLocation.getX() > lines.get(cursorLocation.getY() - 1).length()) {
			//ako prethodni red ima manje znakova nego sto je x od naseg trenutnog
			cursorLocation.setX(lines.get(cursorLocation.getY() - 1).length());
			cursorLocation.setY(cursorLocation.getY() - 1);
			
			selectionRange.setBeginLocation(cursorLocation);
			
			notifyTextObservers();
			return true;
		}
		
		//inace je samo normalni selection prema gore
		cursorLocation.setY(cursorLocation.getY() - 1);
		selectionRange.setBeginLocation(cursorLocation);
		
		notifyTextObservers();
		return true;
	}
	
	public List<String> getSelectedTextList() {
		List<String> selectedText = new LinkedList<String>();
		
		int start = selectionRange.getBeginLocation().getY();
		int end = selectionRange.getEndLocation().getY();
		
		int beginIndex = selectionRange.getBeginLocation().getX();
		int endIndex = selectionRange.getEndLocation().getX();
		
		for(int i=start;i<=end;i++) {
			String line = lines.get(i);
			
			if(start == end) {
				selectedText.add(line.substring(beginIndex, endIndex));
				continue;
			}
			
			if (i == start) {
				selectedText.add(line.substring(beginIndex));
				continue;
			}
			if (i == end) {
				selectedText.add(line.substring(0, endIndex));
				continue;
			}
			
			selectedText.add(line);
		}
		
		return selectedText;
	}
	
	public String getSelectedTextString() {
		List<String> textList = getSelectedTextList();
		String text = "";
		for (String t: textList) {
			text += t;
			text += "\n";
		}
		text = text.substring(0, text.length() - 1);
		return text;
	}
	
	//ZA UNOS ZNAKA/ZNAKOVA/ENTERA
	public void insert(char c) {
		EditAction editAction = new ActionInsertChar(this, c);
		editAction.execute_do();
		undoManager.push(editAction);
	}
	
	public void insert(String text) {
		EditAction editAction = new ActionInsertString(this, text);
		editAction.execute_do();
		undoManager.push(editAction);
	}
	
	public void insertEnter() {
		EditAction editAction = new ActionInsertEnter(this);
		editAction.execute_do();
		undoManager.push(editAction);
	}
	
	public List<String> getLines() {
		return this.lines;
	}
	
	public LocationRange getSelectionRange() {
		return this.selectionRange;
	}
	
	public void setSelectionRange(LocationRange selectionRange) {
		this.selectionRange = selectionRange;
	}
	
	public Location getCursorLocation() {
		return this.cursorLocation;
	}
	
	public void setLines(List<String> lines) {
		this.lines = lines;
		notifyTextObservers();
	}
	
	public void setCursorLocation(Location cursorLocation) {
		this.cursorLocation = cursorLocation;
		notifyCursorObservers();
	}
	
	//obrisi cijeli dokument
	public void clear() {
		Location beginDocument = new Location(0, 0);
		Location endDocument = new Location(lines.get(lines.size()-1).length(), lines.size()-1);
		
		LocationRange newSelectionRange = new LocationRange(beginDocument, endDocument);
		//setSelectionRange(newSelectionRange);
		
		deleteRange(newSelectionRange);
	}
	
	public void setCursorBegin() {
		setCursorLocation(new Location(0, 0));
		notifyCursorObservers();
	}
	
	public void setCursorEnd() {
		Location endDocument = new Location(lines.get(lines.size()-1).length(), lines.size()-1);
		setCursorLocation(endDocument);
		notifyCursorObservers();
	}


	public void setLines(String string) {
		List<String> lines = new LinkedList<String>();
		for (String line: string.split("\n")) {
			lines.add(line);
		}
		setLines(lines);
	}
}
