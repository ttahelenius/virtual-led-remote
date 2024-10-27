package controller.ui.components;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

/**
 * JPanel that draws rectangular shadows under each of its subcomponents (or further subcomponents
 * recursively through JPanels until at {@code setSubComponentSearchDepth}).
 */
public class DropShadowPanel extends JPanel {

  private final int[] shadowAlphas;
  private final int umbraAlpha;
  private final int penumbraWidth;
  private final Point offset;
  private final int subComponentSearchDepth;

  private DropShadowPanel(LayoutManager layout, int umbraAlpha, int penumbraWidth, Point offset, int subComponentSearchDepth) {
    super(layout);

    this.umbraAlpha = umbraAlpha;
    this.penumbraWidth = penumbraWidth;
    this.offset = offset;
    this.subComponentSearchDepth = subComponentSearchDepth;

    // Approximate exponential decay:
    final double a = -0.5d;
    final int n = penumbraWidth;
    shadowAlphas = IntStream.range(1, n)
        .map(i -> {
          double factor = (Math.exp(a * i / n) - 1) / (Math.exp(a) - 1);
          return (int)((1d - factor) * umbraAlpha);
        })
        .toArray();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    ((Graphics2D)g).setComposite(AlphaComposite.SrcOver);

    List<Component> componentList = ComponentUtil.gatherComponents(this, subComponentSearchDepth);

    for (Component c : componentList) {
      int w = c.getWidth() - penumbraWidth;
      int h = c.getHeight() - penumbraWidth;
      Point position = SwingUtilities.convertPoint(c, offset.x + penumbraWidth / 2, offset.y + penumbraWidth / 2, this);
      for (int i = 1; i < penumbraWidth; i++) {
        ((Graphics2D) g).setPaint(new Color(0, 0, 0, shadowAlphas[i-1]));
        int arc = 2 * i;
        g.drawRoundRect(position.x - i, position.y - i, w - 1 + 2 * i, h - 1 + 2 * i, arc, arc);
      }
      g.setColor(new Color(0, 0, 0, umbraAlpha));
      g.fillRect(position.x, position.y, w, h);
    }
  }

  public static class Builder {
    private LayoutManager layout = new FlowLayout();
    private int umbraAlpha = 255;
    private Integer penumbraWidth;
    private Point offset;
    private int subComponentSearchDepth = 1;

    public Builder setLayout(LayoutManager layout) {
      this.layout = layout;
      return this;
    }

    public Builder setUmbraAlpha(int umbraAlpha) {
      this.umbraAlpha = umbraAlpha;
      return this;
    }

    public Builder setPenumbraWidth(int penumbraWidth) {
      this.penumbraWidth = penumbraWidth;
      return this;
    }

    public Builder setOffset(Point offset) {
      this.offset = offset;
      return this;
    }

    public Builder setSubComponentSearchDepth(int subComponentSearchDepth) {
      this.subComponentSearchDepth = subComponentSearchDepth;
      return this;
    }

    public DropShadowPanel create() {
      requireNonNull(penumbraWidth, "penumbraWidth is required");
      requireNonNull(offset, "offset is required");
      return new DropShadowPanel(layout, umbraAlpha, penumbraWidth, offset, subComponentSearchDepth);
    }
  }
}
