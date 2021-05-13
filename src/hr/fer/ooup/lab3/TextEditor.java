package hr.fer.ooup.lab3;

import java.awt.*;
import java.util.List;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import javax.swing.*;

import hr.fer.ooup.lab3.actions.*;
import hr.fer.ooup.lab3.clipboardstack.*;
import hr.fer.ooup.lab3.command.EditAction;
import hr.fer.ooup.lab3.command.UndoManager;
import hr.fer.ooup.lab3.observers.*;
import hr.fer.ooup.lab3.plugins.Plugin;
import hr.fer.ooup.lab3.plugins.PluginFactory;

public class TextEditor extends JFrame {
	private TextEditorModel textEditorModel;
	private TextEditorPanel textEditorPanel;
	
	private static final int windowHeight = 700;
	private static final int windowWidth = 800;
	
	private ClipboardStack clipboardStack;
	
	private UndoManager undoManager;
	
	public TextEditor(TextEditorModel textEditorModel) {
		this.clipboardStack = new ClipboardStack();
		this.undoManager = UndoManager.getInstance();
		
		initTextEditorModel(textEditorModel);
		setUpGUI();
	}
	
	public void initTextEditorModel(String text) {
		this.textEditorModel = new TextEditorModel(text);
		observeCursor();
		observeText();
	}
	
	private void initTextEditorModel(TextEditorModel textEditorModel) {
		this.textEditorModel = textEditorModel;
		observeCursor();
		observeText();
	}
	
	public void observeCursor() {
		textEditorModel.registerNewCursorObserver(new CursorObserver() {
			//ovdje se koristi anonimni razred koji implementira CursorObserver i ima sljedecu metodu:
			@Override
			public void updateCursorLocation(Location loc) {
				//ne moze se metoda paint pozvat (jer se nezna Graphics instanca)
				//ali se zato moze takva metoda pozvat u JPanelu (paintComponent)
				//a posto mi radimo crtanje, moramo nasljedit JPanel i tu overrideat metodu "paintComponent"
				//pozivom "repaint" se poziva "paintComponent"
				textEditorPanel.revalidate();	//ova se metoda uvijek mora zvat kad zelimo repaintat tako da damo doznanja JPanelu da mora opet crtat
				textEditorPanel.repaint();
			}
		});
	}
	
	public void observeText() {
		textEditorModel.registerNewTextObserver(new TextObserver() {
			@Override
			public void updateText() {
				//ne moze se metoda paint pozvat (jer se nezna Graphics instanca)
				//ali se zato moze takva metoda pozvat u JPanelu (paintComponent)
				//a posto mi radimo crtanje, moramo nasljedit JPanel i tu overrideat metodu "paintComponent"
				//pozivom "repaint" se poziva "paintComponent"
				textEditorPanel.revalidate();
				textEditorPanel.repaint();
			}
		});
	}
	
	private void setUpGUI() {
		setSize(windowWidth, windowHeight);
		setTitle("The BEST Text Editor");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		this.textEditorPanel = new TextEditorPanel();
		textEditorPanel.setFocusable(false);
		add(textEditorPanel, BorderLayout.CENTER);
		
		setUpMenu();
		setUpToolbar();
		setUpStatusbar();
		
		//adding key listeners
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_LEFT:
						if (e.isShiftDown()) {
							textEditorModel.addSelectionLeft();
						} else {
							textEditorModel.setSelectionRange(null);
							textEditorModel.moveCursorLeft();
						}
						break;

					case KeyEvent.VK_RIGHT:
						if (e.isShiftDown()) {
							textEditorModel.addSelectionRight();
						} else {
							textEditorModel.setSelectionRange(null);
							textEditorModel.moveCursorRight();
						}
						break;
					
					case KeyEvent.VK_UP:
						if (e.isShiftDown()) {
							textEditorModel.addSelectionUp();
						} else {
							textEditorModel.setSelectionRange(null);
							textEditorModel.moveCursorUp();
						}
						break;
					
					case KeyEvent.VK_DOWN:
						if (e.isShiftDown()) {
							textEditorModel.addSelectionDown();
						} else {
							textEditorModel.setSelectionRange(null);
							textEditorModel.moveCursorDown();
						}
						break;
						
					case KeyEvent.VK_BACK_SPACE:
						if(textEditorModel.getSelectionRange() != null) {
							textEditorModel.deleteRange(textEditorModel.getSelectionRange());
						} else {
							textEditorModel.deleteBefore();
						}
						break;
						
					case KeyEvent.VK_DELETE:
						if(textEditorModel.getSelectionRange() != null) {
							textEditorModel.deleteRange(textEditorModel.getSelectionRange());
						} else {
							textEditorModel.deleteAfter();
						}
						break;
					case KeyEvent.VK_ENTER:
						textEditorModel.insertEnter();
						break;
					
					default:
						if (e.getKeyCode() == (int)'C' && e.isControlDown() && textEditorModel.getSelectionRange() != null) {
							String copiedText = textEditorModel.getSelectedTextString();
							clipboardStack.pushText(copiedText);
							
							break;
						}
						
						if (e.getKeyCode() == (int)'X' && e.isControlDown() && textEditorModel.getSelectionRange() != null) {
							String copiedText = textEditorModel.getSelectedTextString();
							clipboardStack.pushText(copiedText);
							
							textEditorModel.deleteRange(textEditorModel.getSelectionRange());
							break;
						}
						
						if (e.getKeyCode() == (int)'V' && e.isControlDown() && e.isShiftDown()) {
							String copiedText = clipboardStack.popText();
							
							if (copiedText.equals("")) {
								break;
							}
							
							if (textEditorModel.getSelectionRange() != null) {
								textEditorModel.deleteRange(textEditorModel.getSelectionRange());
							}
							
							textEditorModel.insert(copiedText);
							break;
						}
						
						if (e.getKeyCode() == (int)'V' && e.isControlDown()) {
							String copiedText = clipboardStack.peekText();
							
							if (copiedText.equals("")) {
								break;
							}
							
							if (textEditorModel.getSelectionRange() != null) {
								textEditorModel.deleteRange(textEditorModel.getSelectionRange());
							}
							
							textEditorModel.insert(copiedText);
							break;
						}
						
						if (e.getKeyCode() == (int)'Z' && e.isControlDown()) {
							undoManager.undo();
							break;
						}
						
						if (e.getKeyCode() == (int)'Y' && e.isControlDown()) {
							undoManager.redo();
							break;
						}
						
						if(!e.isControlDown() && !e.isShiftDown() && e.getKeyCode() != KeyEvent.VK_CAPS_LOCK) {
							textEditorModel.insert(e.getKeyChar());
							//textEditorModel.insert("BRUNOJEKRALJ");
						}
						break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyTyped(KeyEvent e) {}
		});

	}
	
	private void setUpMenu() {
		//create Actions
		manageActions();
		
		//create JMenuBar
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		//FILE ITEM
		JMenu file = new JMenu("File");
		menuBar.add(file);
		file.add(new JMenuItem(openAction));
		file.add(new JMenuItem(saveAction));
		file.add(new JMenuItem(exitAction));
		//
		
		//EDIT ITEM
		JMenu edit = new JMenu("Edit");
		menuBar.add(edit);
		edit.add(new JMenuItem(undoAction));
		edit.add(new JMenuItem(redoAction));
		edit.add(new JMenuItem(cutAction));
		edit.add(new JMenuItem(copyAction));
		edit.add(new JMenuItem(pasteAction));
		edit.add(new JMenuItem(pasteAndTakeAction));
		edit.add(new JMenuItem(deleteSelectionAction));
		edit.add(new JMenuItem(clearDocumentAction));
		//
				
		//MOVE ITEM
		JMenu move = new JMenu("Move");
		menuBar.add(move);
		move.add(new JMenuItem(cursorToDocumentStartAction));
		move.add(new JMenuItem(cursorToDocumentEndAction));
		
		//PLUGIN ITEMS
		JMenu pluginMenu = new JMenu("Plugins");
		menuBar.add(pluginMenu);
		
		List<Plugin> plugins = PluginFactory.loadPlugins();
		for(Plugin plugin: plugins) {
			
			Action action = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					plugin.execute(textEditorModel, undoManager, clipboardStack);
				}
			};
			
			action.putValue(Action.NAME, plugin.getName());
			action.putValue(Action.LONG_DESCRIPTION, plugin.getDescription());
			
			pluginMenu.add(new JMenuItem(action));
		}
	}
	
	
	//ACTIONS
	private Action clearDocumentAction;
	private Action copyAction;
	private Action cursorToDocumentEndAction;
	private Action cursorToDocumentStartAction;
	private Action cutAction;
	private Action deleteSelectionAction;
	private Action exitAction;
	private Action openAction;
	private Action pasteAction;
	private Action pasteAndTakeAction;
	private Action redoAction;
	private Action saveAction;
	private Action undoAction;
	Path openedFile;
	
	private void manageActions() {
		
		clearDocumentAction = new ClearDocumentAction(textEditorModel);
		copyAction = new CopyAction(textEditorModel, clipboardStack);
		cursorToDocumentEndAction = new CursorToDocumentEndAction(textEditorModel);
		cursorToDocumentStartAction = new CursorToDocumentStartAction(textEditorModel);
		cutAction = new CutAction(clipboardStack, textEditorModel);
		deleteSelectionAction = new DeleteSelectionAction(textEditorModel);
		exitAction = new ExitAction();
		openAction = new OpenAction(openedFile, this, textEditorModel);
		pasteAction = new PasteAction(clipboardStack, textEditorModel);
		pasteAndTakeAction = new PasteAndTakeAction(clipboardStack, textEditorModel);
		redoAction = new RedoAction();
		saveAction = new SaveAction(openedFile, textEditorModel, this);
		undoAction = new UndoAction();
		
		
		openAction.putValue(Action.NAME, "Open");
		
		saveAction.putValue(Action.NAME, "Save");
		
		exitAction.putValue(Action.NAME, "Exit");
		
		undoAction.putValue(Action.NAME, "Undo");
		undoAction.setEnabled(false);
		
		redoAction.putValue(Action.NAME, "Redo");
		redoAction.setEnabled(false);
		
		cutAction.putValue(Action.NAME, "Cut");
		cutAction.setEnabled(false);
		
		copyAction.putValue(Action.NAME, "Copy");
		copyAction.setEnabled(false);
		
		pasteAction.putValue(Action.NAME, "Paste");
		pasteAction.setEnabled(false);
		
		pasteAndTakeAction.putValue(Action.NAME, "Paste and Take");
		pasteAndTakeAction.setEnabled(false);
		
		deleteSelectionAction.putValue(Action.NAME, "Delete selection");
		deleteSelectionAction.setEnabled(false);
		
		clearDocumentAction.putValue(Action.NAME, "Clear document");
		
		cursorToDocumentStartAction.putValue(Action.NAME, "Cursor to document start");
		
		cursorToDocumentEndAction.putValue(Action.NAME, "Cursor to document end");
	}
	
	private void setUpToolbar() {
		JToolBar toolbar = new JToolBar();
		
		JButton undoButton = new JButton(undoAction);
		undoButton.setFocusable(false);
		toolbar.add(undoButton);
		
		JButton redoButton = new JButton(redoAction);
		redoButton.setFocusable(false);
		toolbar.add(redoButton);
		
		JButton cutButton = new JButton(cutAction);
		cutButton.setFocusable(false);
		toolbar.add(cutButton);
		
		JButton copyButton = new JButton(copyAction);
		copyButton.setFocusable(false);
		toolbar.add(copyButton);
		
		JButton pasteButton = new JButton(pasteAction);
		pasteButton.setFocusable(false);
		toolbar.add(pasteButton);
		
		add(toolbar, BorderLayout.NORTH);
	}
	
	private void setUpStatusbar() {
		StatusLabel statusLabel = new StatusLabel(textEditorModel);
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusLabel.setBackground(Color.WHITE);
		add(statusLabel, BorderLayout.SOUTH);
	}
	
	class TextEditorPanel extends JPanel {
		//s obzirom da mu treba referenca na textEditorModel (za dobit linije i to), najbolje je da ovo bude ugnjezdjena klasa (da se osiguramo da ne bude sphagetti code)
		
		LocationRange selectionRange;
		Iterator<String> linesIter;
		Location cursorLocation;
		
		List<String> text;
		
		public TextEditorPanel() {
			text = new LinkedList<String>();
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			//this.requestFocusInWindow();
			super.paintComponent(g);
			
			Graphics2D g2d = (Graphics2D)g;
			
			setBackground(Color.GRAY);
			
			cursorLocation = textEditorModel.getCursorLocation();
			linesIter = textEditorModel.allLines();
			selectionRange = textEditorModel.getSelectionRange();
			
			Font font = new Font("TimesRoman", Font.PLAIN, 30);
			g2d.setFont(font);
			
			String newLine, leftPart, rightPart;
			
			if (!linesIter.hasNext()) {
				//ako nema elemenata, nacrtaj samo cursor
				g2d.drawString("|", 10, 50);
			}
			
	        for(int i=0;linesIter.hasNext();i++) {
	        	String line = linesIter.next();
	        	
	        	if(i == cursorLocation.getY()) {
	        		leftPart = line.substring(0, cursorLocation.getX());
	        		rightPart = line.substring(cursorLocation.getX());
	        		
	        		newLine = leftPart + "|" + rightPart;
	        	} else {
	        		//ovo je linija bez |
	        		newLine = line;
	        	}
	        	
	        	//add begin location for "selection range"
	        	if (selectionRange != null) {
	        		newLine = newLine.replace("|", "");
	        	}
	        	
	        	if (selectionRange != null && selectionRange.getBeginLocation().getY() == i) {
	        		//int numColored = newLine.substring(selectionRange.getBeginLocation().getX()).length();
	        		//int numNotColored = newLine.substring(0, selectionRange.getBeginLocation().getX()).length();
	        		
	        		//g2d.setColor(Color.BLUE);
	        		//g2d.fillRect(10+ 15*(numNotColored), 50+(i-1)*font.getSize(), numColored*15, font.getSize());
	        		
	        		newLine = newLine.substring(0, selectionRange.getBeginLocation().getX()) + "[" + newLine.substring(selectionRange.getBeginLocation().getX());
	        	}
	        	
	        	
	        	if (selectionRange != null && selectionRange.getEndLocation().getY() - i > selectionRange.getBeginLocation().getY()) {
	        		//preskoci; ovo je za one redove koji su cijeli oznaceni (mijenjati ako se bude bojalo)
	        	}
	        	
	        	if (selectionRange != null && selectionRange.getEndLocation().getY() == i) {
	        		//int numColored = newLine.substring(selectionRange.getBeginLocation().getX()).length();
	        		//int numNotColored = newLine.substring(0, selectionRange.getBeginLocation().getX()).length();
	        		//g2d.setColor(Color.BLUE);
	        		//g2d.fillRect(10+ 15*(numNotColored), 50+(i-1)*font.getSize(), numColored*15, font.getSize());
	        		
	        		if (selectionRange != null && selectionRange.getBeginLocation().getY() == i) {
	        			//ako su begin i end u istoj liniji
	        			newLine = newLine.substring(0, selectionRange.getEndLocation().getX()+1) + "]" + newLine.substring(selectionRange.getEndLocation().getX()+1);
	        		} else {	
	        			newLine = newLine.substring(0, selectionRange.getEndLocation().getX()) + "]" + newLine.substring(selectionRange.getEndLocation().getX());
	        		}
	        	}
	        	
	        	g2d.setColor(Color.BLACK);
	        	g2d.drawString(newLine, 10, 50+i*font.getSize());
	        }
		}
	}
}
