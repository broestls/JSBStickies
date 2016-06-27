import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/** NoteAlarm is an object meant to be attached to Note classes. The meat
* here is that alarms have a Timer object attached to them. Once they
* are invoked and the .start() method is called (which this method does upon)
* object creation, a counter will go from the user-designated time to zero.
* When the timer expires, an ActionEvent fires, which will bring up a
* JOptionPane to alert the user that the time is up.
*
* @author Sean Broestl
* @version Last modified 4_11_2016
**/

public class NoteAlarm implements Serializable {

  private String title;
  private String alarmMessage;
  private Timer countDown;
  private Boolean alarmExpired;

  /**
  * The constructor for a NoteAlarm object. Requires an alarm text
  * and a time to count down from.
  * @param message The text to present on the fired alarm at time expiration
  * @param time The time to elapse before the alarm should fire
  * @return a new NoteAlarm.
  */
  public NoteAlarm (String message, int time) {
    countDown = new Timer(time, new AlarmActionListener());
    alarmMessage = message;
    countDown.start();
    this.alarmExpired = false;
  }

  /**
  * The custom ActionListener that fires when alarms expire.
  * Upon timer expiration, the user is presented with a JOptionPane
  * displaying the text contained in alarmMessage. The Timer object
  * is stopped via the Timer.stop() method. alarmExpired is set to true,
  * which marks it for removal from the alarmList in the main class.
  */
  class AlarmActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      JOptionPane alarmPrompt = new JOptionPane();
      alarmPrompt.showMessageDialog(null, alarmMessage);
      countDown.stop();
      alarmExpired = true;
    }
  }

  /**
  * This method simply checks if the alarm is marked as expired or not.
  * Used for cleanup of expired alarms. (Which may or may not have
  * been implemented in time).
  * @return boolean indicating if the alarm has fired or not.
  */
  public boolean isAlarmExpired() {return alarmExpired;}
}
