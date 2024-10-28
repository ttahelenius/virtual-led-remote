package controller.ui;

import controller.Controller;
import controller.Device;
import controller.ui.components.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

import static controller.Controller.exit;
import static controller.ui.Buttons.REDO;
import static controller.ui.Buttons.UNDO;
import static javax.swing.BorderFactory.createRaisedBevelBorder;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class UI {

  private static final Image ICON_IMAGE = Resources.getImage("icon.png");
  private static final String UI_PROPERTIES = "ui.properties";

  private static final Color EMPTY_COLOR = new Color(0, 0, 0, 0);
  private static final Color REMOTE_COLOR = new Color(197, 199, 206);
  private static final Color BUTTONPANE_COLOR = new Color(128, 129, 136);
  private static final Color CONTROL_PANEL_COLOR = new Color(138, 142, 155);

  private static final Supplier<JLabel> BUTTON_PLACEHOLDER_SUPPLIER = JLabel::new;

  public static void show() {
    SwingUtilities.invokeLater(() -> {
      final JFrame window = TransparentFrameGenerator.createFrame();
      window.setIconImage(ICON_IMAGE);

      Container contentPane = window.getContentPane();
      contentPane.setLayout(new BorderLayout());
      contentPane.add(createCompoundButtonPanel(), BorderLayout.SOUTH);
      contentPane.add(createControllers(), BorderLayout.CENTER);

      window.setSize(885, 860);
      window.setMinimumSize(new Dimension(600, 640));
      window.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setWindowLocationFromPropertiesFileOrElse(window, UI_PROPERTIES, () -> window.setLocationRelativeTo(null));
      window.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
          storeWindowLocationToPropertiesFile(window, UI_PROPERTIES);
        }
      });

      window.setVisible(true);
    });
  }

  public static void showError(String message) {
    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  public static void confirmOrExit(String message) {
    int chosen = JOptionPane.showConfirmDialog(null, message, "Proceed?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    if (chosen == JOptionPane.NO_OPTION)
      exit();
  }

  private static void setWindowLocationFromPropertiesFileOrElse(JFrame window, String propertiesFile, Runnable otherwise) {
    String[] pos = readProperty(propertiesFile, "LastWindowPos", "-1 -1").split(" ");
    Point point = new Point(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    for (GraphicsDevice gd : ge.getScreenDevices()) {
      Rectangle bounds = gd.getDefaultConfiguration().getBounds();
      if (bounds.contains(point)) {
        window.setLocation(point);
        return;
      }
    }
    otherwise.run();
  }

  private static void storeWindowLocationToPropertiesFile(JFrame window, String propertiesFile) {
    writeProperty(propertiesFile, "LastWindowPos", window.getX() + " " + window.getY());
  }

  private static String readProperty(String propertiesFile, String property, String defaultValue) {
    Properties properties = new Properties();
    try (FileInputStream in = new FileInputStream(propertiesFile)) {
      properties.load(in);
    } catch (IOException ignored) {
      return defaultValue;
    }
    return properties.getProperty(property, defaultValue);
  }

  private static void writeProperty(String propertiesFile, String property, String value) {
    Properties properties = new Properties();
    properties.setProperty(property, value);
    try (FileOutputStream out = new FileOutputStream(propertiesFile)) {
      properties.store(out, null);
    } catch (IOException ignored) {
    }
  }

  private static JPanel createControllers() {
    AspectRatioPreservingPanel backLedController = new AspectRatioPreservingPanel(new GridLayout(6, 4, -4, -4), 0.65f, 5);
    AspectRatioPreservingPanel frontLedController = new AspectRatioPreservingPanel(new GridLayout(12, 4, -4, -4), 0.35f, 5);
    AspectRatioPreservingPanel potLedController = new AspectRatioPreservingPanel(new GridLayout(7, 4, -4, -4), 0.56f, 5);
    backLedController.registerSibling(frontLedController);
    backLedController.registerSibling(potLedController);
    frontLedController.registerSibling(backLedController);
    frontLedController.registerSibling(potLedController);
    potLedController.registerSibling(backLedController);
    potLedController.registerSibling(frontLedController);

    populateController(backLedController, Device.BACKLED, Buttons.BACKLED_BUTTONS);
    populateController(frontLedController, Device.FRONTLED, Buttons.FRONTLED_BUTTONS);
    populateController(potLedController, Device.POTLED, Buttons.POTLED_BUTTONS);

    Border subBorder1 = BorderFactory.createCompoundBorder(createRaisedBevelBorder(), createRaisedBevelBorder());
    Border subBorder2 = BorderFactory.createEmptyBorder(20, 10, 40, 10);
    Border border = BorderFactory.createCompoundBorder(subBorder1, subBorder2);

    backLedController.setBackground(REMOTE_COLOR);
    backLedController.setBorder(border);
    frontLedController.setBackground(REMOTE_COLOR);
    frontLedController.setBorder(border);
    potLedController.setBackground(REMOTE_COLOR);
    potLedController.setBorder(border);

    Container backLedControllerArea = new JPanel(new GridBagLayout()); // Using GridBagLayout yields vertical center alignment
    backLedControllerArea.setBackground(EMPTY_COLOR);
    backLedControllerArea.add(backLedController);
    Container frontLedControllerArea = new JPanel(new GridBagLayout());
    frontLedControllerArea.setBackground(EMPTY_COLOR);
    frontLedControllerArea.add(frontLedController);
    Container potLedControllerArea = new JPanel(new GridBagLayout());
    potLedControllerArea.setBackground(EMPTY_COLOR);
    potLedControllerArea.add(potLedController);

    Container controllers = new JPanel(new GridLayout(1, 3, 40, 0));
    controllers.add(potLedControllerArea);
    controllers.add(backLedControllerArea);
    controllers.add(frontLedControllerArea);
    controllers.setBackground(EMPTY_COLOR);

    Container controlPanel = createControlPanel();

    Container commonArea = createCommonArea(controllers, controlPanel);

    JPanel dropShadowPanel = new DropShadowPanel.Builder()
        .setLayout(new BorderLayout())
        .setOffset(new Point(16, 30))
        .setUmbraAlpha(90)
        .setPenumbraWidth(15)
        .setShadowCastingSubComponents(backLedController, frontLedController, potLedController, controlPanel)
        .create();
    dropShadowPanel.add(commonArea, BorderLayout.CENTER);

    dropShadowPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    dropShadowPanel.setBackground(EMPTY_COLOR);

    return dropShadowPanel;
  }

  private static Container createControlPanel() {
    ButtonComponentGlowPanel controlPanel = new ButtonComponentGlowPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

    Border border = BorderFactory.createCompoundBorder(createRaisedBevelBorder(), createRaisedBevelBorder());
    controlPanel.setBorder(border);
    controlPanel.setBackground(CONTROL_PANEL_COLOR);

    ButtonComponent undo = new ButtonComponent(UNDO, controlPanel);
    undo.addActionListener(e -> Controller.undo());
    undo.setEnabled(false);
    undo.setPreferredSize(new Dimension(40, 40));
    ButtonComponent redo = new ButtonComponent(REDO, controlPanel);
    redo.addActionListener(e -> Controller.redo());
    redo.setEnabled(false);
    redo.setPreferredSize(new Dimension(40, 40));

    Controller.addStateChangeListener(() -> {
      undo.setEnabled(Controller.canUndo());
      redo.setEnabled(Controller.canRedo());
    });

    controlPanel.add(undo);
    controlPanel.add(redo);

    controlPanel.setPreferredSize(new Dimension(120, 60));

    return controlPanel;
  }

  private static Container createCommonArea(Container controllers, Container controlPanel) {
    JLayeredPane layered = new JLayeredPane();
    layered.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        controllers.setBounds(0, 0, layered.getWidth(), layered.getHeight());
        Dimension d = controlPanel.getPreferredSize();
        controlPanel.setBounds(0, layered.getHeight() - d.height, d.width, d.height);
      }
    });
    layered.setBackground(EMPTY_COLOR);
    layered.add(controllers);
    layered.add(controlPanel);
    return layered;
  }

  private static void populateController(ButtonComponentGlowPanel panel, Device device, List<Button> buttons) {
    for (Button button : buttons) {
      if (button == null) {
        panel.add(BUTTON_PLACEHOLDER_SUPPLIER.get());
        continue;
      }

      JButton buttonComponent = new ButtonComponent(button, panel);
      buttonComponent.addActionListener(e -> {
        Controller.pressButton(device, button.remoteCommand, button.savesInput, true);
        if (button.savesInput)
          Controller.saveStateToHistory();
      });

      SpringLayout layout = new SpringLayout();
      JPanel container = new JPanel(layout);
      container.setBackground(EMPTY_COLOR);
      Spring pw = layout.getConstraint(SpringLayout.WIDTH,  container);
      Spring ph = layout.getConstraint(SpringLayout.HEIGHT, container);
      SpringLayout.Constraints c = layout.getConstraints(buttonComponent);
      float margin = 0.05f;
      c.setX(Spring.scale(pw, margin));
      c.setY(Spring.scale(ph, margin));
      c.setWidth(Spring.scale(pw,  1f - margin*2f));
      c.setHeight(Spring.scale(ph, 1f - margin*2f));
      container.add(buttonComponent);
      panel.add(container);
    }
  }

  private static JPanel createCompoundButtonPanel() {
    DynamicButtonPanel buttonPanel = new DynamicButtonPanel(BUTTONPANE_COLOR, 10, 80, 120, 160);
    Border subBorder = BorderFactory.createCompoundBorder(createRaisedBevelBorder(), createRaisedBevelBorder());
    buttonPanel.setBorder(subBorder);

    populateCompoundButtonPanel(buttonPanel, Buttons.COMPOUND_BUTTONS);

    buttonPanel.refreshLayout();

    return buttonPanel;
  }

  private static void populateCompoundButtonPanel(DynamicButtonPanel panel, List<CompundButton> buttons) {
    for (CompundButton button : buttons) {
      JButton buttonComponent = new ButtonComponent(button, panel);

      buttonComponent.setPreferredSize(new Dimension(50, 50));
      buttonComponent.setMinimumSize(buttonComponent.getPreferredSize());
      buttonComponent.setMaximumSize(buttonComponent.getPreferredSize());

      buttonComponent.addActionListener(e -> {
        for (CompundButton.DeviceCommand deviceCommand : button.deviceCommands) {
          Controller.pressButton(deviceCommand.device, deviceCommand.remoteCommand, true, false);
        }
        Controller.saveStateToHistory();
      });
      panel.add(buttonComponent);
    }
  }

}
