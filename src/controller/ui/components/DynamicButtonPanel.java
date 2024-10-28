package controller.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ButtonComponentGlowPanel} that attempts to distribute its components evenly among at most three vertical layers.
 */
public class DynamicButtonPanel extends ButtonComponentGlowPanel {

  private final int spaceBetween;
  private final int[] layerHeights;

  private int activeShelves = 1;
  private int totalWidth = 0;
  private final Box.Filler filler = (Box.Filler)Box.createRigidArea(new Dimension(0, 0));
  private final JPanel shelf1;
  private final JPanel shelf2;
  private final JPanel shelf3;
  private final List<Component> components;

  public DynamicButtonPanel(Color backgroundColor, int spaceBetween, int singleLayerHeight, int twoLayerHeight, int threeLayerHeight) {
    super();
    this.spaceBetween = spaceBetween;
    this.layerHeights = new int[]{singleLayerHeight, twoLayerHeight, threeLayerHeight};

    shelf1 = new JPanel();
    shelf2 = new JPanel();
    shelf3 = new JPanel();
    shelf1.setBorder(BorderFactory.createEmptyBorder(0, spaceBetween, 0, spaceBetween));
    shelf2.setBorder(BorderFactory.createEmptyBorder(0, spaceBetween, 0, spaceBetween));
    shelf3.setBorder(BorderFactory.createEmptyBorder(0, spaceBetween, 0, spaceBetween));
    components = new ArrayList<>();
    setPreferredSize(new Dimension(1, singleLayerHeight));
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    shelf1.setLayout(new BoxLayout(shelf1, BoxLayout.X_AXIS));
    shelf2.setLayout(new BoxLayout(shelf2, BoxLayout.X_AXIS));
    shelf3.setLayout(new BoxLayout(shelf3, BoxLayout.X_AXIS));
    setBackground(backgroundColor);
    Color emptyColor = new Color(0, 0, 0, 0);
    shelf1.setBackground(emptyColor);
    shelf2.setBackground(emptyColor);
    shelf3.setBackground(emptyColor);
    super.add(Box.createVerticalGlue());
    super.add(shelf1);
    super.add(shelf2);
    super.add(shelf3);
    super.add(Box.createVerticalGlue());

    this.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        refreshLayout();
        getRootPane().repaint();
      }
    });
  }

  public void refreshLayout() {
    int width = getWidth() - 5 * spaceBetween;
    if (width < 0)
      return;

    int previousActiveShelves = activeShelves;
    if (totalWidth / 2 < width - 2 * spaceBetween) {
      if (totalWidth < width - 2 * spaceBetween) {
        if (activeShelves != 1) {
          activeShelves = 1;
          shelf1.removeAll();
          shelf2.removeAll();
          shelf3.removeAll();
          for (int i = 0; i < components.size(); i++) {
            if (i == 0) {
              shelf1.add(filler);
              shelf1.add(Box.createRigidArea(new Dimension(spaceBetween, 0)));
            } else {
              shelf1.add(Box.createHorizontalGlue());
            }
            shelf1.add(components.get(i));
            if (i == components.size() - 1)
              shelf1.add(Box.createRigidArea(new Dimension(spaceBetween, 0)));
          }
        }
      }
      else if (activeShelves != 2) {
        activeShelves = 2;
        shelf1.removeAll();
        shelf2.removeAll();
        shelf3.removeAll();
        int threshold = (components.size() + 1) / 2;
        for (int i = 0; i < threshold; i++) {
          if (i == 0)
            shelf1.add(Box.createRigidArea(new Dimension(2 * spaceBetween, 0)));
          else
            shelf1.add(Box.createHorizontalGlue());
          shelf1.add(components.get(i));
        }
        for (int i = threshold; i < components.size(); i++) {
          shelf2.add(components.get(i));
          if (i == components.size() - 1)
            shelf2.add(Box.createRigidArea(new Dimension(2 * spaceBetween, 0)));
          else
            shelf2.add(Box.createHorizontalGlue());
        }
      }
    }
    else if (activeShelves != 3) {
      activeShelves = 3;
      shelf1.removeAll();
      shelf2.removeAll();
      shelf3.removeAll();
      int threshold = (components.size() + 1) / 3;
      for (int i = 0; i < threshold; i++) {
        if (i == 0)
          shelf1.add(Box.createRigidArea(new Dimension(2 * spaceBetween, 0)));
        else
          shelf1.add(Box.createHorizontalGlue());
        shelf1.add(components.get(i));
      }
      for (int i = threshold; i < threshold * 2; i++) {
        if (i == threshold)
          shelf2.add(Box.createRigidArea(new Dimension(spaceBetween, 0)));
        else
          shelf2.add(Box.createHorizontalGlue());
        shelf2.add(components.get(i));
        if (i == threshold * 2 - 1)
          shelf2.add(Box.createRigidArea(new Dimension(spaceBetween, 0)));
      }
      for (int i = threshold * 2; i < components.size(); i++) {
        shelf3.add(components.get(i));
        if (i == components.size() - 1)
          shelf3.add(Box.createRigidArea(new Dimension(2 * spaceBetween, 0)));
        else
          shelf3.add(Box.createHorizontalGlue());
      }
    }

    int w = ((width - totalWidth) % (components.size() - 1)) / 2;
    if (w > 0) {
      Dimension d = new Dimension(w, 0);
      filler.changeShape(d, d, d);
    }
    if (previousActiveShelves != activeShelves) {
      setPreferredSize(new Dimension(1, layerHeights[activeShelves-1]));
      revalidate();
    }
  }

  @Override
  public Component add(Component comp) {
    activeShelves = 1;
    if (getComponents().length > 0)
      shelf1.add(Box.createHorizontalGlue());
    components.add(comp);
    registerAddedComponent(comp);
    totalWidth += comp.getMinimumSize().width;
    return comp;
  }

}
