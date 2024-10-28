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
    if (!(component instanceof JPanel))
      return Collections.singletonList(component);

    List<Component> components = new ArrayList<>();
    for (Component c : ((JPanel)component).getComponents()) {
      components.addAll(gatherComponents(c));
    }
    return components;
  }

}
