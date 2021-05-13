package hr.fer.ooup.lab3.iterator;

import java.util.*;

public class MyIterator implements Iterator<String> {
	
	private List<String> lines;
	private int index;
	private int endIndex;
	
	public MyIterator(List<String> lines) {
		this.lines = lines;
		this.index = 0;
		this.endIndex = lines.size();
	}
	
	public MyIterator(List<String> lines, int beginIndex, int endIndex) {
		this.lines = lines;
		this.index = beginIndex;
		this.endIndex = endIndex;
	}

	@Override
	public boolean hasNext() {
		if (index < endIndex && lines.get(index) != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String next() throws NoSuchElementException {
		if (hasNext() == true) {
			return lines.get(index++);
		} else {
			throw new NoSuchElementException("List of Strings has been iterated");
		}
	}
	
	
	
}
