package controller.ui;

import java.awt.*;
import java.util.List;

import static controller.ui.Button.InputSaving.SAVES_INPUT;

public class Button {

  static final Color DEFAULT_BACKGROUND_COLOR = Color.LIGHT_GRAY;
  private static final Gradient DEFAULT_BACKGROUND = singleColor(DEFAULT_BACKGROUND_COLOR);
  private static final Font DEFAULT_FONT = new Font("SansSerif", Font.BOLD, 28);

  enum InputSaving {
    SAVES_INPUT,
    DONT_SAVE_INPUT
  }

  public final String label;
  public final Font font;
  public final Gradient background;
  final String remoteCommand;
  final boolean savesInput;

  Button(Color color, String remoteCommand) {
    this.label = null;
    this.font = DEFAULT_FONT;
    this.background = singleColor(color);
    this.remoteCommand = remoteCommand;
    this.savesInput = true;
  }

  Button(String label, String remoteCommand) {
    this.label = label;
    this.font = DEFAULT_FONT;
    this.background = DEFAULT_BACKGROUND;
    this.remoteCommand = remoteCommand;
    this.savesInput = true;
  }

  Button(String label, String remoteCommand, InputSaving inputSaving) {
    this.label = label;
    this.font = DEFAULT_FONT;
    this.background = DEFAULT_BACKGROUND;
    this.remoteCommand = remoteCommand;
    this.savesInput = inputSaving == SAVES_INPUT;
  }

  Button(String label, Font font, String remoteCommand, InputSaving inputSaving) {
    this.label = label;
    this.font = font;
    this.background = DEFAULT_BACKGROUND;
    this.remoteCommand = remoteCommand;
    this.savesInput = inputSaving == SAVES_INPUT;
  }

  Button(String label, Color color, String remoteCommand) {
    this.label = label;
    this.font = DEFAULT_FONT;
    this.background = singleColor(color);
    this.remoteCommand = remoteCommand;
    this.savesInput = true;
  }

  Button(String label, Color color, String remoteCommand, InputSaving inputSaving) {
    this.label = label;
    this.font = DEFAULT_FONT;
    this.background = singleColor(color);
    this.remoteCommand = remoteCommand;
    this.savesInput = inputSaving == SAVES_INPUT;
  }

  Button(String label, Gradient gradient, String remoteCommand) {
    this.label = label;
    this.font = DEFAULT_FONT;
    this.background = gradient;
    this.remoteCommand = remoteCommand;
    this.savesInput = true;
  }

  private static Gradient singleColor(Color color) {
    return new Gradient(color).discrete();
  }

  public static class Gradient {
    public final List<Color> colors;
    public boolean discrete;

    Gradient(Color... colors) {
      this.colors = List.of(colors);
    }

    Gradient discrete() {
      this.discrete = true;
      return this;
    }
  }

}
