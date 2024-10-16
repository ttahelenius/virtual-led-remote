package controller.ui.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComponentUtil {

  /**
   * Searches recursively all components through JPanels starting at {@code component}.
   * @param component starting point
   * @return all non-JPanel components under {@code component} (inclusive).
   */
  public static java.util.List<Component> gatherComponents(Component component) {
    return gatherComponents(component, Integer.MAX_VALUE);
  }

  /**
   * Searches recursively all components through JPanels starting at {@code component} until at level {@code depth}.
   * Note: at level {@code depth} also JPanels will be included.
   * @param component starting point
   * @param depth Maximum level for recursion: 1 searches only direct children.
   * @return all components under {@code component} (inclusive) up to level {@code depth} excluding intermediate JPanels.
   */
  public static java.util.List<Component> gatherComponents(Component component, int depth) {
    if (depth == 0 || !(component instanceof JPanel))
      return Collections.singletonList(component);

    List<Component> components = new ArrayList<>();
    for (Component c : ((JPanel)component).getComponents()) {
      components.addAll(gatherComponents(c, depth-1));
    }
    return components;
  }

}
