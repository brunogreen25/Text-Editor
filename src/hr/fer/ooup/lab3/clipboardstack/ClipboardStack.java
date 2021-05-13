package hr.fer.ooup.lab3.clipboardstack;

import java.util.*;

import hr.fer.ooup.lab3.observers.*;

public class ClipboardStack {
	private Stack<String> texts;
	private List<ClipboardObserver> clipboardObservers;
	
	public ClipboardStack() {
		texts = new Stack<String>();
		clipboardObservers = new LinkedList<ClipboardObserver>();
	}
	
	//metode za observere
	public boolean registerNewClipboardObserver(ClipboardObserver newClipboardObserver) {
		if (clipboardObservers.contains(newClipboardObserver)) {
			return false;
		}
		return clipboardObservers.add(newClipboardObserver);
	}
	
	public boolean unregisterClipboardObserver(ClipboardObserver clipboardObserver) {
		if (!clipboardObservers.contains(clipboardObserver)) {
			return false;
		}
		return clipboardObservers.remove(clipboardObserver);
	}
	
	public void notifyClipboardObservers() {
		for(ClipboardObserver clipboardObserver: clipboardObservers) {
			clipboardObserver.updateClipboard();
		}
	}
	
	//popratne metode
	public void pushText(String text) {
		texts.push(text);
		notifyClipboardObservers();
	}
	
	public String popText() {
		if (texts.isEmpty()) {
			return "";
		}
		
		String text = texts.pop();
		
		notifyClipboardObservers();
		return text;
	}
	
	public String peekText() {
		if (texts.empty()) {
			return "";
		}
		
		return texts.peek();
	}
	
	public boolean hasText() {
		return !texts.isEmpty();
	}
	
	public void deleteStack() {
		texts = new Stack<String>();
	}
}
