import java.io.Serializable;

/** A class that was supposed in order to successfully serialize TextNotes.
* Ran out of time to implement
*/
class SavedTextNote implements Serializable {

    private String noteContents;
    private String title;
    private Integer noteID;
    //private Date createTime;
    //private Date lastEditTime;
    private NoteAlarm alarm;
    //private Dimension size = new Dimension(250,250);
    //private Color bgColor = new Color(242,240,118);
    public Boolean lockState;
    private static int lastX = 0;
    private static int lastY = 0;

    public SavedTextNote(TextNote n) {
      this.noteContents = n.getNoteText();
      this.title = n.getNoteTitle();
    }

  }
