import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;

/** GraphicNote.java is one of the two types of notes available in this package.
* TextNotes are "traditional" electronic sticky notes.
* They extend standard Notes by adding textual contents, a text color, and a
* font style.
*
* @author Sean Broestl
* @version 0.6
* @since 2016-05-6
**/

public class GraphicNote extends Note implements Serializable {
  public Graphics g;
  public JPanel note;
  private Color penColor;
  private Integer penSize;
  // The canvas we are actually drawing on
  public PaintCanvas PaintArea = new PaintCanvas();
  // Keep a list of the colors we support
  public static final ArrayList<NoteColor> colors = JStickies.makeColorList();

  /* Some variables to use while we are drawing to keep track of mouse
  * position, Color, and state
  */
  private Color currentColor;
  private int prevX, prevY;
  private boolean mouseBtnDepressed;
  private Graphics drawObject;

  /**
	* Default empty TextNote constructor. Sets up an empty paint object.
	*/
	public GraphicNote() {
		super();
    PaintArea.setPreferredSize(new Dimension(250,250));
    add(PaintArea);
    addMenu(buildEditMenu());
    addMenu(buildFormatMenu());
    this.setVisible(true);
    this.repaint();
    this.revalidate();
    this.pack();
	}

  /**
  * Creates the parts of the menu needed for an Edit menu. Exposes
  * the functionality of updateTitle() and lockNote();
  * @return The relevant JMenu entries for the Edit menu
  */
  private JMenu buildEditMenu() {

    JMenu menu = new JMenu("Edit");
    JMenuItem menuItem;

    menuItem = new JMenuItem("Set Title");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        updateTitle();
      }
    });
    menuItem.setAccelerator(KeyStroke.getKeyStroke('T', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
    menu.add(menuItem);

    menu.addSeparator();

    menuItem = new JMenuItem("Lock/Unlock note");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        lockNote(GraphicNote.this);
      }
    });
    menuItem.setAccelerator(KeyStroke.getKeyStroke('L', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
    menu.add(menuItem);

    return menu;
  }

  /**
  * Creates the parts of the menu needed for a Format menu.
  * In this case, just gives you controls for adjusting the color of the
  * pen.
  * @return The relevant JMenu entries for the Format menu
  */
  private JMenu buildFormatMenu() {
    JMenu menu = new JMenu("Format");
    JMenu submenu = new JMenu();

    submenu = new JMenu("Pen Color");
    menu.add(submenu);
    int count = 0;
    int[] keys = {KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9, KeyEvent.VK_0};
    Graphics g = getGraphics();
    for (NoteColor c : colors) {
      // NOTE: HAD TO MAKE CHANGES HERE BECAUSE OF JAVA7/8 differences on nice
      // Instead of final, this method call was in the setCurrentColor() method
      final Color menuColorOption = c.getColor();
      JMenuItem temp = new JMenuItem(c.getName(), c.getIcon());
      temp.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          setCurrentColor(menuColorOption);
        }
      });
      if (count < keys.length) temp.setAccelerator(KeyStroke.getKeyStroke(keys[count], Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
      submenu.add(temp);
      count++;
    }
    JMenuItem temp = new JMenuItem("Eraser");
    temp.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setCurrentColor(Color.WHITE);
      }
    });
    temp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
    submenu.add(temp);
    return menu;
  }

  /**
  * Updates the currentColor member variable so that when paint() is called
  * we use the correct color.
  * @param Color to set the pen to.
  */
  private void setCurrentColor(Color c) {
    currentColor = c;
  }

  /**
  * Actually does the business of locking the note. In the case of GraphicNotes
  * the method removes the ActionListener and updates the
  * lockstate via setLockState(). This is sufficient to prevent a note
  * from being accidentally edited. Also updates the Title of the window
  * with the LOCKTEXT variable to visually indicate that the note is locked.
  * @param Takes in a TextNote n since we need to access various parrts
  * of a note while we are trying to lock it via the menu or hotkey.
  */
  public void lockNote (GraphicNote n) {
    String title = n.getTitle();
    if (n.getLockState() == false) {
      lockState = true;
      n.setTitle(LOCKTEXT+title);
      // lockCanvas on the PaintArea inner Class removes the ActionListener
      PaintArea.lockCanvas();
    }
    else {
      lockState = false;
      title = title.substring(LOCKTEXT.length(),title.length());
      n.setTitle(title);
      PaintArea.unlockCanvas();
    }
  }


  public void openNoteFromFile() {
    JOptionPane.showMessageDialog(this, "Function not implemented", "Question",
        JOptionPane.INFORMATION_MESSAGE);
  }

  /** This function does not actually work. All it will give you is a picture
  * of the JFrame. I did some reading of stack overflow for information on writing
  * out a PNG, which lead me to the BufferedImage object. It works in terms of
  * demonstrating writing out a graphics object, but it seems like my edits
  * to the canvas aren't getting sucked up along with the rest of the Image
  * data.
  */
  public void exportNote() {
    JFileChooser fc = new JFileChooser();
    int returnVal = fc.showSaveDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      BufferedImage bi = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB);
      Graphics g = bi.getGraphics();
      PaintArea.paint(g);  //this == JComponent
      g.dispose();
      try {
        ImageIO.write(bi,"png",new File(fc.getSelectedFile()+".png"));
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
    else if (returnVal == JFileChooser.CANCEL_OPTION) {
      return;
    }
  }

  /**
  * Never got around to finishing import.
  */
  public void importNote() {
    JOptionPane.showMessageDialog(this, "Function not implemented", "Question",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public String getNoteText() {return null;}

  public class PaintCanvas extends JPanel implements MouseListener, MouseMotionListener {

  /**
  * Create the new PaintCanvas to draw on.
  */
  PaintCanvas() {
     setBackground(Color.WHITE);
     addMouseListener(this);
     addMouseMotionListener(this);
  }

  /**
  * These two methods are used to lock and unlock this type of note.
  * It's pretty simple to prevent edits to the canvas in a non-destructive
  * fashion by just keeping the mouse from doing anything until the user
  * flips the switch back on. Removes the MouseListeners
  */
  public void lockCanvas() {
    removeMouseListener(this);
    removeMouseMotionListener(this);
  }

  /**
  * Adds the MouseListeners back in
  */
  public void unlockCanvas() {
    addMouseListener(this);
    addMouseMotionListener(this);
  }


  public void paintComponent(Graphics g) {
    // Call the super.method()
    super.paintComponent(g);
  }

  /**
  * This method starts drawing a line at the x,y position of where the
  * mouse is clicked. Updates the method variables as necessary
  *
  */
  public void mousePressed(MouseEvent e) {
    // Get the coordinates where the user clicked
    int x = e.getX();
    int y = e.getY();

    /* Update the previous x,y for use by the mouseReleased event
    * we will need to know where the line drawing started.
    */
    prevX = x;
    prevY = y;
    // Set the member variable to true, the mouse button is down
    mouseBtnDepressed = true;
    // Get the graphics object.
    drawObject = getGraphics();
    // Set the color to whatever currentColor is (controlled by the JMenu)
    drawObject.setColor(currentColor);
  }


  /**
  * When the mouse button is released, finish up drawing by disposing
  * of the graphics object and resetting the state of the drawing object.
  */
  public void mouseReleased(MouseEvent e) {
    if (mouseBtnDepressed == false)
    return;  // Nothing to do because the user isn't drawing.
    mouseBtnDepressed = false;
    drawObject.dispose();
    drawObject = null;
  }


  /**
  * The mouse is moving. If it's depressed, we are using drawLine to make a new
  * line. Track the x,y position as we go.
  */
  public void mouseDragged(MouseEvent e) {
    if (mouseBtnDepressed == false) return;  // Nothing to do because the user isn't drawing.

    // x-coordinate of mouse at call of method
    int x = e.getX();
    // y-coordinate of mouse at call of method
    int y = e.getY();
    // Draw the line on the canvas
    drawObject.drawLine(prevX, prevY, x, y);
    // Continue to update the x,y variables as the mouse moves.
    prevX = x;
    prevY = y;

  }

  // Methods that must be created, but don't have to be filled in
  // as part of the Graphics interface.
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mouseClicked(MouseEvent e) {}
  public void mouseMoved(MouseEvent e) {}

  }
}
