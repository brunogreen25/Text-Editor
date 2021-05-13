package hr.fer.ooup.lab3.plugins;

import hr.fer.ooup.lab3.*;
import hr.fer.ooup.lab3.clipboardstack.*;
import hr.fer.ooup.lab3.command.*;

public interface Plugin {
	public String getName();
	public String getDescription();
	public void execute(TextEditorModel model, UndoManager undoManager, ClipboardStack clipboardStack);
}
