package controller.ui.components;

import controller.ui.Resources;
import controller.ui.UI;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import static controller.Controller.exit;

/**
 * Creates a partially transparent JFrame with a customized Metal LaF and sets Nimbus LaF for its contents.
 * The transparency is turned off while window is being resized, to prevent white flashing associated with
 * window transparency in JRE.
 */
public class TransparentFrameGenerator extends DefaultMetalTheme {

  private final static Color EMPTY_COLOR = new Color(0, 0, 0, 0);
  private final static Color BG_COLOR = new Color(68, 81, 91, 140);
  private final static Color BG_COLOR2 = new Color(68, 81, 91, 250);
  private final static Color BORDER_COLOR = new Color(66, 113, 143, 150);
  private final static Color BORDER_COLOR_ACTIVE = new Color(60, 106, 140);

  private static final Image ICONIFY_IMAGE = Resources.getImage("iconify.png");
  private static final Image MAXIMIZE_IMAGE = Resources.getImage("max.png");
  private static final Image MINIMIZE_IMAGE = Resources.getImage("min.png");
  private static final Image CLOSE_IMAGE = Resources.getImage("close.png");

  public static JFrame createFrame() {
    JFrame.setDefaultLookAndFeelDecorated(true);

    MetalLookAndFeel.setCurrentTheme(new TransparentFrameGenerator());

    try {
      UIManager.setLookAndFeel(new MetalLookAndFeel());
    } catch (UnsupportedLookAndFeelException e) {
      UI.showError(e.getMessage());
      exit();
    }

    UIManager.getDefaults().put("InternalFrame.iconifyIcon", new ImageIcon(ICONIFY_IMAGE));
    UIManager.getDefaults().put("InternalFrame.maximizeIcon", new ImageIcon(MAXIMIZE_IMAGE));
    UIManager.getDefaults().put("InternalFrame.minimizeIcon", new ImageIcon(MINIMIZE_IMAGE));
    UIManager.getDefaults().put("InternalFrame.closeIcon", new ImageIcon(CLOSE_IMAGE));

    JFrame frame = createFrameComponent();

    try {
      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
             UnsupportedLookAndFeelException e) {
      UI.showError(e.getMessage());
      exit();
    }

    return frame;
  }

  private static JFrame createFrameComponent() {
    return new CustomMetalFrame();
  }

  @Override
  protected ColorUIResource getSecondary1() {
    return new ColorUIResource(EMPTY_COLOR);
  }

  @Override
  public ColorUIResource getMenuBackground() {
    return new ColorUIResource(EMPTY_COLOR);
  }

  @Override
  public ColorUIResource getControl() {
    return null;
  }

  @Override
  public ColorUIResource getPrimaryControl() {
    return null;
  }

  @Override
  public ColorUIResource getPrimaryControlDarkShadow() {
    return new ColorUIResource(EMPTY_COLOR);
  }

  @Override
  public ColorUIResource getControlHighlight() {
    return new ColorUIResource(EMPTY_COLOR);
  }

  @Override
  public ColorUIResource getPrimaryControlHighlight() {
    return new ColorUIResource(EMPTY_COLOR);
  }

  @Override
  public ColorUIResource getWindowTitleBackground() {
    return new ColorUIResource(BORDER_COLOR_ACTIVE);
  }

  @Override
  public ColorUIResource getWindowTitleInactiveBackground() {
    return new ColorUIResource(BORDER_COLOR);
  }

  @Override
  public FontUIResource getWindowTitleFont() {
    return new FontUIResource(new Font(null, Font.PLAIN, 16));
  }

  @Override
  public ColorUIResource getWindowBackground() {
    return new ColorUIResource(BORDER_COLOR_ACTIVE);
  }

  private static class CustomMetalFrame extends JFrame implements ActionListener {
    private boolean mouseDown = false;

    private final Timer transparencyRepaintTimer;

    private CustomMetalFrame() {
      setBackground(EMPTY_COLOR);
      getRootPane().setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 3));
      JPanel panel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
          super.paintComponent(g);
          Graphics2D g2d = (Graphics2D) g;
          g2d.setPaint(new GradientPaint(0f, 0f, BG_COLOR, 0f, getHeight() * .8f, BG_COLOR2));
          g2d.fillRect(0, 0, getWidth(), getHeight());
        }
      };
      setContentPane(panel);

      transparencyRepaintTimer = new Timer(40, this);
      transparencyRepaintTimer.setRepeats(false);
      addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
          if (getBackground() == EMPTY_COLOR)
            setBackground(BORDER_COLOR_ACTIVE);
          if (transparencyRepaintTimer.isRunning())
            transparencyRepaintTimer.restart();
          else
            transparencyRepaintTimer.start();
          if (getExtendedState() == MAXIMIZED_BOTH)
            setShape(new Rectangle2D.Double(0d, 0d, getWidth(), getHeight()));
          else
            setShape(new RoundRectangle2D.Double(0d, 0d, getWidth(), getHeight(), 20d, 20d));
        }
      });

      addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          mouseDown = true;
          super.mousePressed(e);
        }
        @Override
        public void mouseReleased(MouseEvent e) {
          mouseDown = false;
          super.mouseReleased(e);
        }
      });
    }

    @Override
    public Cursor getCursor() {
      return Cursor.getDefaultCursor();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (mouseDown) {
        transparencyRepaintTimer.restart();
        return;
      }
      setBackground(EMPTY_COLOR);
      repaint();
    }
  }
}
