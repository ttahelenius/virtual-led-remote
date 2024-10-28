package controller.ui;

import controller.ui.Button.Gradient;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

import static controller.ui.Button.DEFAULT_BACKGROUND_COLOR;
import static controller.ui.Button.InputSaving.DONT_SAVE_INPUT;
import static java.awt.Color.*;
import static java.util.Arrays.asList;

class Buttons {

  private static final Gradient RGB_GRADIENT = new Gradient(RED, GREEN, BLUE);
  private static final Gradient ALL_GRADIENT = new Gradient(RED, YELLOW, GREEN, CYAN, BLUE, MAGENTA);
  private static final Gradient ALL_GRADIENT_FADE = new Gradient(RED, DARK_GRAY, YELLOW, DARK_GRAY, GREEN, DARK_GRAY);
  private static final Gradient RGB_GRADIENT_DISCRETE = new Gradient(RED, GREEN, BLUE).discrete();
  private static final Gradient ALL_GRADIENT_DISCRETE = new Gradient(RED, YELLOW, GREEN, CYAN, BLUE, MAGENTA).discrete();
  private static final Gradient GRADIENT_FADE = new Gradient(DEFAULT_BACKGROUND_COLOR, DARK_GRAY,
                                                              DEFAULT_BACKGROUND_COLOR, DARK_GRAY,
                                                              DEFAULT_BACKGROUND_COLOR, DARK_GRAY);

  private static final Font ON_OFF_FONT = Resources.getFont("Unicode_IEC_symbol.ttf")
                                                   .deriveFont(22f)
                                                   .deriveFont(AffineTransform.getTranslateInstance(1.4, 2.4));

  static final Button UNDO = new Button("⏮", null);
  static final Button REDO = new Button("⏭", null);

  static final List<Button> BACKLED_BUTTONS = asList(
      new Button("on",  "on",     DONT_SAVE_INPUT),
      new Button("off", "off",    DONT_SAVE_INPUT),
      new Button("▲",   "bright", DONT_SAVE_INPUT),
      new Button("▼",   "dim",    DONT_SAVE_INPUT),

      new Button(new Color(255, 0, 0),     "r"),
      new Button(new Color(0, 255, 0),     "g"),
      new Button(new Color(0, 0, 255),     "b"),
      new Button(new Color(161, 255, 230), "w"),

      new Button(new Color(255, 196, 0), "r2"),
      new Button(new Color(0, 255, 128), "g2"),
      new Button(new Color(34, 0, 255),  "b2"),
      new Button("⏵", ALL_GRADIENT,     "smooth"),

      new Button(new Color(238, 255, 0),  "r3"),
      new Button(new Color(0, 255, 187),  "g3"),
      new Button(new Color(94, 0, 255),   "b3"),
      new Button("⏵", ALL_GRADIENT_FADE, "fade"),

      new Button(new Color(162, 255, 0),      "r4"),
      new Button(new Color(0, 255, 217),      "g4"),
      new Button(new Color(132, 0, 255),      "b4"),
      new Button("⏵", RGB_GRADIENT_DISCRETE, "strobe"),

      new Button(new Color(123, 255, 0),      "r5"),
      new Button(new Color(0, 255, 247),      "g5"),
      new Button(new Color(170, 0, 255),      "b5"),
      new Button("⏵", ALL_GRADIENT_DISCRETE, "flash")
  );


  static final List<Button> FRONTLED_BUTTONS = asList(
      new Button("⏻", ON_OFF_FONT,                         "onoff",     DONT_SAVE_INPUT),
      new Button("⏯",                                      "playpause", DONT_SAVE_INPUT),
      new Button("\uD83D\uDD06", new Color(225, 225, 225), "bright",    DONT_SAVE_INPUT),
      new Button("\uD83D\uDD05", new Color(157, 157, 157), "dim",       DONT_SAVE_INPUT),

      new Button(new Color(183, 43, 0),    "r"),
      new Button(new Color(23, 197, 27),   "g"),
      new Button(new Color(0, 0, 255),     "b"),
      new Button(new Color(120, 243, 222), "w"),

      new Button(new Color(162, 132, 0),   "r2"),
      new Button(new Color(0, 255, 81),    "g2"),
      new Button(new Color(47, 0, 255),    "b2"),
      new Button(new Color(138, 221, 231), "w2"),

      new Button(new Color(137, 152, 0),   "r3"),
      new Button(new Color(0, 255, 149),   "g3"),
      new Button(new Color(64, 0, 255),    "b3"),
      new Button(new Color(100, 255, 229), "w5"), // w5 and w3 switched, fits better that way

      new Button(new Color(109, 210, 0),   "r4"),
      new Button(new Color(0, 255, 178),   "g4"),
      new Button(new Color(85, 0, 255),    "b4"),
      new Button(new Color(176, 255, 240), "w4"),

      new Button(new Color(140, 255, 0),   "r5"),
      new Button(new Color(100, 255, 229), "g5"),
      new Button(new Color(132, 0, 255),   "b5"),
      new Button(new Color(154, 131, 255), "w3"), // w5 and w3 switched, fits better that way

      new Button("⏵", RGB_GRADIENT,          "fade3"),
      new Button("⏵", ALL_GRADIENT,          "fade7"),
      new Button("⏵", RGB_GRADIENT_DISCRETE, "jump3"),
      new Button("⏫",                       "quick", DONT_SAVE_INPUT),

      new Button("⏵", ALL_GRADIENT_DISCRETE,   "jump7"),
      new Button("⟳", ALL_GRADIENT,             "auto"),
      new Button("☆", new Color(100, 255, 229), "flash"),
      new Button("⏬",                          "slow",  DONT_SAVE_INPUT),

      new Button("▲", new Color(183, 43, 0), "rup", DONT_SAVE_INPUT),
      new Button("▲", new Color(8, 255, 0),  "gup", DONT_SAVE_INPUT),
      new Button("▲", new Color(0, 0, 255),  "bup", DONT_SAVE_INPUT),
      null,

      new Button("▼", new Color(2, 220, 220),  "rdown", DONT_SAVE_INPUT),
      new Button("▼", new Color(222, 12, 222), "gdown", DONT_SAVE_INPUT),
      new Button("▼", new Color(229, 229, 25), "bdown", DONT_SAVE_INPUT),
      null,

      new Button("💾", "diy1"),
      new Button("💾", "diy2"),
      new Button("💾", "diy3"),
      null,

      new Button("💾", "diy4"),
      new Button("💾", "diy5"),
      new Button("💾", "diy6"),
      new Button("\uD83D\uDD27", new Color(206, 148, 128), "calibrate", DONT_SAVE_INPUT)
  );

  static final List<Button> POTLED_BUTTONS = asList(
      new Button("on",  "on",     DONT_SAVE_INPUT),
      new Button("off", "off",    DONT_SAVE_INPUT),
      new Button("▲",   "bright", DONT_SAVE_INPUT),
      new Button("▼",   "dim",    DONT_SAVE_INPUT),

      new Button(new Color(255, 0, 0),     "r"),
      new Button(new Color(0, 255, 0),     "g"),
      new Button(new Color(0, 0, 255),     "b"),
      new Button(new Color(161, 255, 230), "w"),

      new Button(new Color(255, 196, 0),      "r2"),
      new Button(new Color(0, 255, 128),      "g2"),
      new Button(new Color(34, 0, 255),       "b2"),
      new Button("⏵", RGB_GRADIENT_DISCRETE, "smooth"),

      new Button(new Color(238, 255, 0),  "r3"),
      new Button(new Color(0, 255, 187),  "g3"),
      new Button(new Color(94, 0, 255),   "b3"),
      new Button("⏵", ALL_GRADIENT,      "fade"),

      new Button(new Color(162, 255, 0),  "r4"),
      new Button(new Color(0, 255, 217),  "g4"),
      new Button(new Color(132, 0, 255),  "b4"),
      new Button("⏵", GRADIENT_FADE,     "strobe"),

      new Button(new Color(123, 255, 0),      "r5"),
      new Button(new Color(0, 255, 247),      "g5"),
      new Button(new Color(170, 0, 255),      "b5"),
      new Button("⏵", ALL_GRADIENT_DISCRETE, "flash"),

      null,
      null,
      null,
      new Button("\uD83D\uDD27", new Color(206, 148, 128), "calibrate", DONT_SAVE_INPUT)
  );

  static final List<CompundButton> COMPOUND_BUTTONS = asList(
      new CompundButton(RED,   "r", RED,   "r", RED,   "r"),
      new CompundButton(GREEN, "g", GREEN, "g", GREEN, "g"),
      new CompundButton(BLUE,  "b", BLUE,  "b", BLUE,  "b"),
      new CompundButton(new Color(162, 255, 0), "r4",
                        new Color(238, 255, 0), "r3",
                        new Color(137, 152, 0), "r3"),
      new CompundButton(new Color(0, 255, 128), "g2",
                        new Color(0, 255, 187), "g3",
                        new Color(0, 255, 178), "g4"),
      new CompundButton(new Color(170, 0, 255),   "b5",
                        new Color(94, 0, 255),    "b3",
                        new Color(154, 131, 255), "w3"),
      new CompundButton(new Color(255, 196, 0), "r2",
                        new Color(255, 196, 0), "r2",
                        new Color(208, 89, 0),  "diy4"),
      new CompundButton(new Color(255, 196, 0),   "r2",
                        new Color(170, 0, 255),   "b5",
                        new Color(134, 152, 255), "diy2"),
      new CompundButton(new Color(0, 255, 128),   "g2",
                        new Color(255, 196, 0),   "r2",
                        new Color(154, 131, 255), "w3"),
      new CompundButton(new Color(94, 0, 255),  "b3",
                        new Color(34, 0, 255),  "b2",
                        new Color(0, 255, 149), "g3"),
      new CompundButton(new Color(161, 255, 230), "w",
                        new Color(238, 255, 0),   "r3",
                        new Color(208, 89, 0),    "diy4"),
      new CompundButton(new Color(255, 196, 0), "r2",
                        new Color(0, 255, 128), "g2",
                        new Color(196, 0, 151),  "diy3"),
      new CompundButton(new Color(162, 255, 0), "r4",
                        new Color(255, 196, 0), "r2",
                        new Color(111, 145, 125),  "diy1"),
      new CompundButton(new Color(255, 0, 0),   "r",
                        new Color(94, 0, 255),  "b3",
                        new Color(137, 152, 0), "r3"),
      new CompundButton(new Color(161, 255, 230), "w",
                        new Color(162, 255, 0),   "r4",
                        new Color(210, 144, 0),   "r2"),
      new CompundButton(new Color(94, 0, 255),  "b3",
                        new Color(255, 196, 0), "r2",
                        new Color(0, 0, 255),   "b"),
      new CompundButton(new Color(123, 255, 0), "r5",
                        new Color(238, 255, 0), "r3",
                        new Color(208, 89, 0),  "diy4"),
      new CompundButton(new Color(161, 255, 247), "w",
                        new Color(0, 239, 175),   "g4",
                        new Color(196, 0, 151),   "diy3"),
      new CompundButton(new Color(161, 255, 247), "w",
                        new Color(255, 0, 0),     "r",
                        new Color(120, 243, 222), "w"),
      new CompundButton(new Color(123, 255, 0), "r5",
                        new Color(94, 0, 255),  "b3",
                        new Color(208, 89, 0),  "diy4"),
      new CompundButton(new Color(0, 0, 255),   "b",
                        new Color(255, 196, 0), "r2",
                        new Color(196, 0, 151), "diy3"),
      new CompundButton(new Color(255, 0, 0),   "r",
                        new Color(255, 0, 0),   "r",
                        new Color(196, 0, 151), "diy3"),
      new CompundButton(new Color(0, 255, 247), "g5",
                        new Color(0, 255, 128), "g2",
                        new Color(132, 0, 255), "b5"),
      new CompundButton(new Color(161, 255, 230), "w",
                        new Color(132, 0, 255),   "b4",
                        new Color(162, 132, 0),   "r2"),
      new CompundButton(new Gradient(RED, YELLOW, GREEN, CYAN, BLUE, MAGENTA,
                                     RED, YELLOW, GREEN, CYAN, BLUE, MAGENTA,
                                     RED, YELLOW, GREEN, CYAN, BLUE, MAGENTA).discrete(),
                                     "flash", "flash", "jump7")
  );

}
