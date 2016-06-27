import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.event.*;
// Kept in for future implementation of Undo/Redo
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/** TextNote.java is one of the two types of notes available in this package.
* TextNotes are "traditional" electronic sticky notes.
* They extend standard Notes by adding textual contents, a text color, and a
* font style.
*
* @author Sean Broestl
* @version 0.86
* @since 2016-05-6
**/

public class TextNote extends Note implements Serializable {

  // Stores the text of the note
  private String contents;
  // Likewise Font and Color
  private Color textColor;
  private Font textFont;
  // The display layer for the text
  JTextPane note = new JTextPane();
  // Keep a list of the NoteColors we can use for the JMenu
  public static final ArrayList<NoteColor> colors = JStickies.makeColorList();

	/**
	* Default empty TextNote constructor
	*/
	public TextNote() {
		super();
    // Build out all the menus we need and add them to the JMenuBar in the parent
    addMenu(buildEditMenu());
    addMenu(buildFormatMenu());
    buildAlarmMenu();
    // Turn our JTextPane into a ScrollPane so people can type longer than 250x250.
    JScrollPane scroll = new JScrollPane(note);
    // Set the bgcolor to a true sticky note color
    note.setBackground(new Color(252,250,118));
    // Set our size for new notes
    this.setSize(250,250);
    // Everything is set. Add the scrollpanel to the JFrame and make visible.
    this.add(scroll);
    this.setVisible(true);
	}

  /**
  * Implements Note's abstract method. Simply grabs the text stored in
  * the note object via getText().
  * @return a String of the text in TextNote.note.
  */
  public String getNoteText() {
    return this.note.getText();
  }

  /**
  * Method to update the TextNote's contents if needed.
  * Used primarily for the importNote() method.
  * @param s a String to import into the TextNote.note object.
  */
  public void setNoteText(String s) {
    this.note.setText(s);
  }

  /**
  * This method is used to update the lockstate of a Note.
  * This is implemented in each subclass, since the method is a little
  * different depending on the type of note.
  * @param b Boolean of what to set the lock state to - true or false.
  */
  public void setLockState(boolean b) {
    if(b = true) {
      System.out.println("Hit True");
      note.setEditable(false);
      lockState = true;
    }
    else {
      System.out.println("Hit False");
      note.setEditable(true);
      super.lockState = false;
    }
  }

  /**
  * Sets the font (foreground) color of the TextNote
  * @param Color c - the color to set the text to.
  */
  public void setFontColor(Color c) {
    note.setForeground(c);
  }

  /**
  * Actually does the business of locking the note. In the case of TextNotes
  * the method sets the JTextArea to setEditable(false) and updates the
  * lockstate via setLockState(). This is sufficient to prevent a note
  * from being accidentally edited. Also updates the Title of the window
  * with the LOCKTEXT variable to visually indicate that the note is locked.
  * @param Takes in a TextNote n since we need to access various parrts
  * of a note while we are trying to lock it via the menu or hotkey.
  */
  public void lockNote (TextNote n) {
    String title = n.getTitle();
    System.out.println(n.getLockState());
    if (n.getLockState() == false) {
      lockState = true;
      n.note.setEditable(false);
      n.setTitle(LOCKTEXT+title);
    }
    else {
      lockState = false;
      title = title.substring(LOCKTEXT.length(),title.length());
      n.note.setEditable(true);
      n.setTitle(title);
    }
  }

  /**
  * This method creates a JFileChooser to allow the user to save a note
  * out to disk. This discards all parameters except the note text unfortunately
  * Uses FileWriter and BufferedWriter to create the file object and then write
  * into it. The JFileChooser does the work of getting the path and filename
  * from the user.
  */
  public void exportNote() {
    JFileChooser fc = new JFileChooser();
    int returnVal = fc.showSaveDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      try {
        FileWriter fw = new FileWriter(fc.getSelectedFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(this.getNoteText());
        bw.close();
      }
      catch(IOException i)
      {
        i.printStackTrace();
      }
    }
    else if (returnVal == JFileChooser.CANCEL_OPTION) {
      return;
    }
  }

  /**
  * This method creates a JFileChooser to allow the user to import a plain
  * text file into the JStickies app. Uses FileReader to open the filehandle
  * BufferedReader to read in the text, creates a new TextNote object via
  * the constructor and uses setNoteText() to update the text with the imported
  * stuff.
  */
  public void importNote() {
    String line = "";
    StringBuilder sb = new StringBuilder();
    JFileChooser fc = new JFileChooser();
    int returnVal = fc.showOpenDialog(this);
    try {
      FileReader fr = new FileReader(fc.getSelectedFile());
      BufferedReader br = new BufferedReader(fr);
      while((line = br.readLine()) != null) {
        sb.append(line);
      }
      TextNote result = new TextNote();
      result.setNoteText(sb.toString());
    }
    catch(IOException i)
    {
      System.out.println("Error reading file.");
      i.printStackTrace();
      return;
    }
    return;
  }


  /**
  * Creates the parts of the menu needed for the Format menu. This includes
  * font changing functionality and functions to change the font color.
  * The Text Color portion makes use of the NoteColors ArrayList we
  * made as part of a new instance. That object is looped over along with
  * some hotkeys to fill the menu and add JMenuItems with Icons.
  * @return The relevant JMenu entries for the Format menu
  */
  private JMenu buildFormatMenu() {
    JMenu menu = new JMenu("Format");
    JMenu submenu = new JMenu();
    JMenuItem menuItem = new JMenuItem();
    submenu = new JMenu("Font");
    menuItem = new JMenuItem("Helvetica");
    submenu.add(menuItem);
    menuItem = new JMenuItem("Marker Felt");
    submenu.add(menuItem);
    menuItem = new JMenuItem("Font Type 3");
    submenu.add(menuItem);
    menu.add(submenu);

    submenu = new JMenu("Text Color");
    menu.add(submenu);
    //submenu.setMnemonic(KeyEvent.VK_S);
    int count = 0;
    int[] keys = {KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9, KeyEvent.VK_0};
    for (NoteColor c : colors) {
      // NOTE: HAD TO MAKE CHANGES HERE BECAUSE OF JAVA7/8 differences on nice
      // Instead of final, c.getColor() method call was in the setCurrentColor() method
      final Color menuColorOption = c.getColor();
      JMenuItem temp = new JMenuItem(c.getName(), c.getIcon());
      temp.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          setFontColor(menuColorOption);
        }
      });
      if (count < keys.length) temp.setAccelerator(KeyStroke.getKeyStroke(keys[count], Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
      submenu.add(temp);
      count++;
    }
    return menu;
  }

  /**
  * Creates the parts of the menu needed for an Edit menu. This includes
  * the Cut/Copy/Paste actions (implemented via java's built in definitions -
  * cutAction, copyAction, pasteAction. Also exposes the functionality of
  * updateTitle() and lockNote();
  * @return The relevant JMenu entries for the Edit menu
  */
  private JMenu buildEditMenu() {
    JMenu menu = new JMenu("Edit");
    JMenuItem menuItem = new JMenuItem();
    Action cutAction = new DefaultEditorKit.CutAction();
    cutAction.putValue(Action.NAME, "Cut");
    menuItem.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
    menu.add(cutAction);

    Action copyAction = new DefaultEditorKit.CopyAction();
    copyAction.putValue(Action.NAME, "Copy");
    menuItem.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
    menu.add(copyAction);

    Action pasteAction = new DefaultEditorKit.PasteAction();
    pasteAction.putValue(Action.NAME, "Paste");
    menuItem.setAccelerator(KeyStroke.getKeyStroke('P', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
    menu.add(pasteAction);

    menu.addSeparator();
    menuItem = new JMenuItem("Set Title");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        updateTitle();
      }
    });
    menuItem.setAccelerator(KeyStroke.getKeyStroke('T', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
    menu.add(menuItem);

    menuItem = new JMenuItem("Lock/Unlock note");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        lockNote(TextNote.this);
      }
    });
    menuItem.setAccelerator(KeyStroke.getKeyStroke('L', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
    menu.add(menuItem);

    return menu;
  }
}
