package controller.ui;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static controller.Controller.exit;
import static java.awt.Font.createFont;

public class Resources {

  private static final Toolkit DEFAULT_TOOLKIT = Toolkit.getDefaultToolkit();

  public static Image getImage(String name) {
    return DEFAULT_TOOLKIT.getImage(getPath(name));
  }

  public static Font getFont(String name) {
    try {
      return createFont(Font.TRUETYPE_FONT, new File(getPath(name)));
    } catch (FontFormatException | IOException e) {
      IllegalStateException ex = new IllegalStateException("Font creation failed");
      exit(ex);
      throw ex;
    }
  }

  private static String getPath(String file) {
    return String.format("res/%s", file);
  }

}
