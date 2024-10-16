package controller.ui.components;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ButtonComponentGlowPanel} that attempts to preserve the given aspect ratio. Adjacent
 * {@link AspectRatioPreservingPanel}s may be registered using {@link #registerSibling(AspectRatioPreservingPanel)}
 * in order to preserve proportions among all of them.
 */
public class AspectRatioPreservingPanel extends ButtonComponentGlowPanel {

  private final List<AspectRatioPreservingPanel> siblings;
  private final float aspectRatio;
  private final int margin;

  public AspectRatioPreservingPanel(LayoutManager layout, float aspectRatio, int margin) {
    super(layout);
    this.aspectRatio = aspectRatio;
    this.margin = margin;
    this.siblings = new ArrayList<>();
  }

  @Override
  public Dimension getPreferredSize() {
    float w = calculatePreferredWidth();
    for (AspectRatioPreservingPanel sibling : siblings) {
      w = Math.min(w, sibling.calculatePreferredWidth());
    }
    if ((int) w == 0)
      return new Dimension(0, 0);
    float h = w / aspectRatio;
    return new Dimension((int) w - margin, (int) h - margin);
  }

  public void registerSibling(AspectRatioPreservingPanel sibling) {
    siblings.add(sibling);
  }

  private float calculatePreferredWidth() {
    Dimension d = getParent().getSize();
    if (d.width > aspectRatio * d.height)
      return d.height * aspectRatio;
    return d.width;
  }

}
