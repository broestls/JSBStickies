import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.event.*;
import javax.swing.event.MenuKeyListener;

/**
* Note is the abstract class that defines my primary data type. Notes
* can be extended into two child types - TextNote and GraphicNote.
* Notes also combine both the display and data layer, so they
* are pretty concise and quick to create and manipulate. This has
* other problems associated with it. Please see wrap-up doc.
*
* @author Sean Broestl
* @version 1.895545
* @since 2016-05-6
*/

public abstract class Note extends JFrame implements Serializable {

  // Define the string we use to mark the title of locked notes.
  public final String LOCKTEXT = "(locked) ";

  /* Even if i don't use them yet, I think it's worth tracking my own
  * IDs of the notes that get created.
  */
  private Integer noteID;
  // Keep track of creation Date
  private Date createTime;
  // Keep track of last edit Date (not implemented)
  private Date lastEditTime;
  // References alarm associated with this note, if present
  private NoteAlarm alarm;
  // Track whether this note is editible
  public Boolean lockState;
  // Keep an internal counter of notes between them
  private static Integer serialNum = 0;
  // Create the JMenuBar for a Note
  private JMenuBar noteMenu = new JMenuBar();

  /* Track where the last note was at creation to work around a JDK bug
  * See setNetWindowLocation() for further detail.
  */
  private static int lastX = 0;
  private static int lastY = 0;

  /** Default constructor for Note objects. Abstract, but we define
  * properties common to all Note types.
  */
  Note() {
    this.noteID = serialNum;
    // Increment the Note counter
    serialNum++;
    this.createTime = new Date();
    this.lockState = false;
    // Create JFrame-type properties
    this.createMenuBar();
    this.setJMenuBar(noteMenu);
    this.setTitle("New Note");
    setNetWindowLocation();
    // Update the position variables on the member variables
    this.lastX += 23;
    this.lastY += 23;
    // Want to close window on X click, not close whole app
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }

  /**
  * Works around JDK-8025130
  * https://bugs.openjdk.java.net/browse/JDK-8025130
  * The setLocationByPlatform directive is awesome! It's currently broken
  * on certain versions of Mac OS and the JDK. To get similar window placement
  * on those platforms, we track the X,Y position of newly-created windows
  * and try to emulate that cascading Window-open behavior.
  */
  public void setNetWindowLocation() {
    if (!JStickies.platform.equals("Mac OS X")) this.setLocationByPlatform(true);
    else this.setLocation(lastX,lastY);
  }

  /**
  * getNoteTitle returns the Title of the JFrame window.
  * @return String of text from JFrame.getTitle();
  */
  public String getNoteTitle() {
    return this.getTitle();
  }

  /**
  * getLockState returns the lock state of the note.
  * @return lockstate of the note, a Boolean.
  */
  public Boolean getLockState() {
		return lockState;
	}

  /**
  * addMenu adds a JMenu to the Class' JMenuBar
  * Used so that child classes can add their distinct JMenus to a standard
  * JMenuBar.
  * @param JMenu that a child class creates.
  */
  public void addMenu(JMenu m) {
    noteMenu.add(m);
  }

  /**
  * associateAlarm simply ties an alarm created outside the class context
  * to the member variable this.alarm.
  * @param a NoteAlarm that has been created and is running.
  */
  public void associateAlarm(NoteAlarm alarm) { this.alarm = alarm;}

  /** This is the method used to create the primary JMenuBar object and
  * functions common to all types of Notes. This includes creating
  * JMenuItems for Open, Close, Import, Export, and Quit. These
  * methods are either general to the entire app or are implemented
  * as abstract methods.
  */
  public void createMenuBar() {
    // Create generic JMenu objects to be populated
    JMenu menu, submenu;
    JMenuItem menuItem;

    // Create the first menu.
    menu = new JMenu("File");
    noteMenu.add(menu);

    /** The following defines the template for how menu items are added.
    * It definitely should have been looped, but I didn't get around to doing it.
    * Each item is created to fill the menuItem variable, has an ActionListener
    * added as an inner class, then we also use setAccelerator to assign a
    * hotkey, since that is how I wanted to use the app myself.
    */
    menuItem = new JMenuItem("New Text Note");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Note newNote = new TextNote();
      }
    });
    menuItem.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
    menu.add(menuItem);

    menuItem = new JMenuItem("New Sketch Note");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Note newNote = new GraphicNote();
      }
    });
    menuItem.setAccelerator(KeyStroke.getKeyStroke('K', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
    menu.add(menuItem);

    menu.addSeparator();

    menuItem = new JMenuItem("Import...");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        importNote();
      }
    });
    menuItem.setAccelerator(KeyStroke.getKeyStroke('I', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
    menu.add(menuItem);

    menuItem = new JMenuItem("Export...");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exportNote();
      }
    });
    menuItem.setAccelerator(KeyStroke.getKeyStroke('E', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
    menu.add(menuItem);

    menu.addSeparator();

    menuItem = new JMenuItem("Close note");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
    menuItem.setAccelerator(KeyStroke.getKeyStroke('W', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
    menu.add(menuItem);

    menuItem = new JMenuItem("Exit");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    menuItem.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

    menu.add(menuItem);
  }

  /**
  * This method follows the same template as the above code. A JMenu is
  * created, then populated with a JMenuItem =. Added straight to the
  * JMenuBar since we are already in the Class which holds it.
  */
  public void buildAlarmMenu() {
    JMenu menu;

    menu = new JMenu("Alarms");
    noteMenu.add(menu);

    JMenuItem menuItem = new JMenuItem("Attach new alarm...");
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // Use the method we defined in JStickies to create alarms
        alarm = JStickies.createNewAlarm(Note.this);
        /* Add the alarm to the list of alarms - hope to be able to
        * list and edit alarms later on.
        */
        JStickies.alarmList.add(alarm);
      }
    });
    menu.add(menuItem);
  }

  /**
  * This method allows the user to update the title of their notes so that
  * they don't have to have "New Note" staring them down as their sticky
  * title forever and ever. Doesn't take parameters or a return. However
  * it does pop a JOptionPane and uses this to update the JFrame title using
  * setTitle().
  */
  public void updateTitle() {
    String newTitle = JOptionPane.showInputDialog("Please enter a title:",getTitle());
    setTitle(newTitle);
  }

  // Abstract methods that must be defined by the child classes.
  public abstract void exportNote();
  public abstract void importNote();
  public abstract String getNoteText();

}
