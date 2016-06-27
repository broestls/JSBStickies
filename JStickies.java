import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.JFileChooser;
import java.io.*;

/**
* JStickies is my implementation of a Desktop sticky note application. Please
* see the accompanying document JStickies.pdf for full descriptions on the
* scope and ambition of this project. Otherwise you'll just be reading it
* twice.
*
* @author Sean Broestl
* @version 0.6
* @since 2016-05-6
*/

public class JStickies {

  /**
  * The application depends on having a number of defaults set up. These
  * variables are used to control the color of notes, and maintain a list of the
  * alarms that are created.
  */
  public static String platform = System.getProperty("os.name");
  public static Color[] colors = {Color.BLACK,Color.RED,Color.GREEN,Color.BLUE,Color.CYAN,Color.MAGENTA,Color.YELLOW};
  public static String[] colNames = {"Black","Red","Green","Blue","Cyan","Magenta","Yellow"};
  public static Color[] noteColors = {new Color(252,250,118), new Color(200,200,230)};
  public static ArrayList<NoteAlarm> alarmList = new ArrayList<NoteAlarm>();
  public static final String ALARMTEXT = "(Alarm)";

  /**
  * Create a new runnable and invoke the GUI on its own thread
  */
  public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
              createAndShowGUI();
          }
      });
  }

  /**
  * Create a new, empty note. The app currently depends on there being at least
  * one note open. If the last note is closed, the java process will kill it.
  * Future improvement is to keep the app open until the user chooses to end it.
  * Will probably involve creating a new "JStickies" instance to load.
  */
  private static void createAndShowGUI() {
    // Use the Mac OS X MenuBar if available.
    if(platform.equals("Mac OS X")) System.setProperty("apple.laf.useScreenMenuBar", "true");
    TextNote note1 = new TextNote();
    //GraphicNote note3 = new GraphicNote();
  }
  /**
  * This method builds a list of NoteColors for use by the Note classes to
  * set pen and text colors.
  * @return ArrayList<noteColor> a list of all the colors available for pen
  * colors. See NoteColor.class for details on that Object type.
  */
  public static ArrayList<NoteColor> makeColorList() {
    ArrayList<NoteColor> result = new ArrayList<NoteColor>();
    for(int i = 0; i < colors.length; i++) {
      result.add(new NoteColor(colors[i], colNames[i]));
    }
    return result;
  }

  /**
  * This method is used to attach a new NoteAlarm to a Note. When invoked
  * this method will prompt the user to enter the reminder text and a time in
  * minutes to pop the alarm up.
  * @param Note n - This is the Note having an alarm attached to it.
  * @return a new NoteAlarm.
  */
  public static NoteAlarm createNewAlarm(Note n) {
    NoteAlarm newAlarm = null;
    int alarmTime = 0;
    JTextField alarmTitle = new JTextField(20);
    JTextField time = new JTextField(4);

    // Creates a two-panel JPanel to attach to the JOptionPane
    JPanel alarmDialog = new JPanel();
    alarmDialog.setLayout(new BoxLayout(alarmDialog, BoxLayout.Y_AXIS));
    alarmDialog.add(new JLabel("Alarm message:"));
    alarmDialog.add(alarmTitle);
    alarmDialog.add(Box.createHorizontalStrut(15)); // a spacer
    alarmDialog.add(new JLabel("Time (in minutes)"));
    alarmDialog.add(time);

    // Pops the JOptionPane to collect the alarm parameters up on screen at
    // the note location
    int result = JOptionPane.showConfirmDialog(n, alarmDialog, "Create new alarm", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      /* Catch bad input on the Time field and remind the user of what it should
      * be. Don't create an alarm in that case.
      */
      try {
        alarmTime = Integer.parseInt(time.getText());
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(n, "Alarm time should be a number 1-60", "Question", JOptionPane.INFORMATION_MESSAGE);
        return null;
      }
      /* NoteAlarms store time in ms, so we multiply by 60 and 1000 to convert
      * the time the user entered as minutes into ms.
      */
      newAlarm = new NoteAlarm(alarmTitle.getText(), alarmTime*60*1000);
      n.setTitle(n.getTitle() + ALARMTEXT);
    }
    n.associateAlarm(newAlarm);
    return newAlarm;
  }
}
