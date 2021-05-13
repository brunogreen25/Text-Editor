import java.awt.*;
import java.awt.Shape;
import java.awt.geom.*;
import java.awt.event.*;

import javax.swing.*;

import hr.fer.ooup.lab3.*;

public class TestSwing {
	public static void main(String[] args) throws Exception {
		//TextEditorModel tem = new TextEditorModel("Hello,\nmy name is Bruno.\nBest regards :)");
		TextEditorModel tem = new TextEditorModel("gle malu\nvocku poslije\nkise");
		SwingUtilities.invokeAndWait(()-> {
			TextEditor window = new TextEditor(tem);
			window.setLocation(20, 20);
			window.setVisible(true);
		});	
	}
}
