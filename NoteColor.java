import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
* NoteColor is a class designed to deal with my hate of the default Color
* class in Java. I feel that at least the Color constants should be able to
* store a color name as a String field. Or that user created colors should
* be able to have a name attached. This Class corrects that deficiency and
* also adds an additional field to store a color icon in the assets/
* subdirectory of the application.
*
* @author Sean Broestl
* @version 1.0
* @since 2016-05-4
*/
  public class NoteColor implements Serializable {

    private Color col;
    private String name;
    private ImageIcon cIcon;

    /**
    * NoteColor is the primary constructor for the NoteColor class.
    * At creation, you need to provide a Color object and the name
    * by which you want to refer to the color. A reference is also
    * created to an assets/<ColorName>.png Icon. Please place a
    * 16x16 png representing your color in that directory to have it used
    * automatically in color choosers in the app.
    * @param color The color to create
    * @param name The name of the color
    */
    public NoteColor(Color color, String name) {
      this.col = color;
      this.name = name;
      cIcon = new ImageIcon("assets/"+name+".png");
    }

    // Standard getters

    /** getIcon returns the reference to the color's ImageIcon
    * @return ImageIcon of the NoteColor
    */
    public ImageIcon getIcon() {return cIcon;}
    /** getName returns the NoteColor's name
    * @return String name of the NoteColor
    */
    public String getName() {return name;}
    /** getColor returns Color object stored in the NoteColor
    * @return Color object 
    */
    public Color getColor() {return col;}

  }
